package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.GameEngine;
import com.deftwun.zombiecopter.components.ControllerComponent;
import com.deftwun.zombiecopter.components.HealthComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.PlayerComponent;
import com.deftwun.zombiecopter.components.TeamComponent;
import com.deftwun.zombiecopter.components.VehicleComponent;
import com.deftwun.zombiecopter.components.VehicleOperatorComponent;

public class VehicleSystem extends EntitySystem {
	private Logger logger = new Logger("VehicleSystem",Logger.INFO);
	
	public VehicleSystem() {
		logger.debug("Initializing");
	}
	
	private void enterVehicle(Entity o, Entity v){
		logger.debug("Enter Vehicle");
		GameEngine engine = App.engine;
		ComponentMappers mappers = engine.mappers;
		
		VehicleComponent vehicle = mappers.vehicle.get(v);		
		VehicleOperatorComponent operator = mappers.vehicleOperator.get(o);
		
		//Vehicle takes on operator team
		TeamComponent operatorTeam = mappers.team.get(o),
		              vehicleTeam = mappers.team.get(v);
		if (operatorTeam != null) {
			if (vehicleTeam == null){
				vehicleTeam = App.engine.createComponent(TeamComponent.class);
				vehicleTeam.team = operatorTeam.team;
				v.add(vehicleTeam);
			}
			else {
				vehicleTeam.team = operatorTeam.team;
			}
		}
		
		operator.enterVehicle = false;
		vehicle.occupantData = App.engine.factory.serialize(o);
		if (App.engine.systems.player.getPlayer() == o){
			PlayerComponent p = engine.createComponent(PlayerComponent.class);
			v.add(p);
			engine.systems.player.setPlayer(v);
		}
		engine.removeEntity(o);
	}
	
	private void ejectOccupant(Entity e){
		logger.debug("Eject occupant");
		
		
		GameEngine engine = App.engine;
		ComponentMappers mappers = engine.mappers;
		
		PhysicsComponent physics = mappers.physics.get(e);
		ControllerComponent controller = mappers.controller.get(e);
		VehicleComponent vehicle = mappers.vehicle.get(e);
		TeamComponent team = mappers.team.get(e);
		
		if (vehicle.occupantData.equals("")) return;
		vehicle.eject = false;
		
		//Remove team
		if (team != null) e.remove(TeamComponent.class); // remove team
		
		//Reset entity controller
		controller.reset();
				
		//Recreate occupant
		Entity occupant = engine.factory.deserialize(vehicle.occupantData);
		if (occupant == null) {
			logger.error("Could not deserialize : " + vehicle.occupantData);
			return;
		}
		PhysicsComponent occupantPhys = mappers.physics.get(occupant);
		if (occupantPhys != null){
			occupantPhys.setPosition(physics.getPosition());
			occupantPhys.setLinearVelocity(physics.getLinearVelocity());
		}
		engine.addEntity(occupant);
		App.engine.systems.player.setPlayer(occupant);
		
		vehicle.occupantData = "";
	}
	
	public void update(float deltaTime){
		
		GameEngine engine = App.engine;
		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> vehicles = engine.getEntitiesFor(Family.all(VehicleComponent.class).get()),
							   operators = engine.getEntitiesFor(Family.all(VehicleOperatorComponent.class).get());
		ComponentMappers mappers = App.engine.mappers;

		//Vehicles
		for (Entity v : vehicles){
			VehicleComponent vehicle = mappers.vehicle.get(v);
			HealthComponent health = mappers.health.get(v);

			if ((vehicle.eject && vehicle.occupantData != "") || (health != null && health.value <= 0))
			{
				ejectOccupant(v);
			}
		}
		
		//Operators
		for (Entity o : operators){
			VehicleOperatorComponent operator = mappers.vehicleOperator.get(o);
			PhysicsComponent operatorPhysics = mappers.physics.get(o);

			for (Entity v : vehicles){
				PhysicsComponent vehiclePhysics = mappers.physics.get(v);

				float vehicleRange = vehiclePhysics.getPosition().dst(operatorPhysics.getPosition());
				if (vehicleRange <= 3 && operator.enterVehicle){
					enterVehicle(o,v);
					break;	
				}
			}
		}
	}
}
