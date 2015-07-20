package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.DropOffPoint;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.GameEngine;
import com.deftwun.zombiecopter.components.Collector;
import com.deftwun.zombiecopter.components.PhysicsComponent;


//Drop off points are areas that an entity with a 'collector' component can drop off civilians when within range. 
//TODO: Could be made to include different types of collectables ..
public class CivilianDropOffSystem extends EntitySystem {
	private Array<DropOffPoint> points = new Array<DropOffPoint>();
	private int totalCivsReturned = 0;
	
	public int getTotalCiviliansDroppedOff(){
		return totalCivsReturned;
	}
	
	public void add(DropOffPoint point){
		points.add(point);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		points.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(float deltaTime) {
		GameEngine engine = App.engine;
		ComponentMappers maps = engine.mappers;
		
		for (DropOffPoint point : points){
			for (Entity e : engine.getEntitiesFor(Family.all(Collector.class).get())){
				Collector c = maps.collector.get(e);
				PhysicsComponent phys = maps.physics.get(e);
				
				if (phys.getPosition().dst(point.position) < point.range){
					totalCivsReturned += c.civilians;
					c.civilians = 0;
				}
				
			}
		}

	}
	
}
