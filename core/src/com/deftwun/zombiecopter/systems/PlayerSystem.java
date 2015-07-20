package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.UserInterface;
import com.deftwun.zombiecopter.components.ControllerComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.VehicleComponent;
import com.deftwun.zombiecopter.components.VehicleOperatorComponent;

public class PlayerSystem extends EntitySystem implements EntityListener,InputProcessor{	
	private Logger logger = new Logger("PlayerSystem",Logger.INFO);	
	private Entity currentPlayer = null;
	
	public PlayerSystem(){
		logger.debug("initializing");
	}
	
	public void setPlayer(Entity e){
		currentPlayer = e;
		if (e != null)
			App.engine.systems.camera.setFollow(e);
	}
	
	public Entity getPlayer(){
		if (currentPlayer == null) logger.debug("currentPlayer == null");
		return currentPlayer;
	}
	
	@Override
	public void update(float deltaTime) {

		if (currentPlayer == null) return;
		ComponentMappers mappers = App.engine.mappers;
		ControllerComponent controller = mappers.controller.get(currentPlayer);
		PhysicsComponent physics = mappers.physics.get(currentPlayer);

		//Set entity controller inputs
		if (controller == null) return;
		
		UserInterface ui = App.engine.ui;		
		controller.moveVector.set(ui.getMoveVector());
		controller.attack = ui.isFiring();
		
		if (ui.isTouchScreen) controller.lookVector.set(ui.getFireVector());
		else {
			if (physics == null) return;
			Vector2 worldCoords = App.engine.systems.camera.unproject(ui.getTouchPosition());
			worldCoords.scl(1/App.engine.PIXELS_PER_METER);
			controller.lookVector.set(worldCoords.sub(physics.getPosition()).nor());
		}
		
		//Set entity boundary around player
		if (physics == null) return;
		App.engine.entityBounds.setCenter(physics.getPosition());
		
	}

	@Override
	public void entityAdded(Entity entity) {
		//If an current player is not set and a new entity is added with a player component,
		// then set the new entity as the current player
		/*
		if (currentPlayer == null){
			if (Platformer.engine.mappers.player.has(entity)){
				logger.debug("Player set to entity #" + entity.getId());
				Platformer.engine.systems.camera.setFollow(entity);
				currentPlayer = entity;
			}
		}
		*/
	}

	@Override
	public void entityRemoved(Entity entity) {
		//If the current player is removed then current player = null;
		if (currentPlayer == entity){
			logger.debug("Current player has been removed: Entity#" + currentPlayer.getId());
			currentPlayer = null;
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		if (currentPlayer == null) return false;
		if (keycode == Keys.E){
			//TODO: this shoudl be driven by tghe 'action' switch in the controller component
			logger.debug("Vehicle Key pressed");
			VehicleComponent vehicle = App.engine.mappers.vehicle.get(currentPlayer);
			VehicleOperatorComponent operator = App.engine.mappers.vehicleOperator.get(currentPlayer);
			if (vehicle != null) vehicle.eject = true;
			if (operator != null) operator.enterVehicle = true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (currentPlayer == null) return false;
		if (keycode == Keys.E){
			logger.debug("Vehicle Key depressed");
			VehicleComponent vehicle = App.engine.mappers.vehicle.get(currentPlayer);
			VehicleOperatorComponent operator = App.engine.mappers.vehicleOperator.get(currentPlayer);
			if (vehicle != null) vehicle.eject = false;
			if (operator != null) operator.enterVehicle = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
