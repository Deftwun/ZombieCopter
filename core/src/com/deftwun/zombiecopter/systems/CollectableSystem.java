package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.components.Collectable;
import com.deftwun.zombiecopter.components.Collector;
import com.deftwun.zombiecopter.components.PhysicsComponent;

public class CollectableSystem extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	public CollectableSystem() {
		super(Family.all(PhysicsComponent.class,Collector.class).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ComponentMappers maps =App.engine.mappers;
		PhysicsComponent physics = maps.physics.get(entity);
		Collector collector = maps.collector.get(entity);
		
		for (Entity e : App.engine.getEntitiesFor(Family.all(Collectable.class).get())){
			Collectable c = maps.collectable.get(e);
			PhysicsComponent p = maps.physics.get(e);
			if (c!= null && p != null){
				if (p.getPosition().sub(physics.getPosition()).len() < c.pickupRange){
					if (c.type == Collectable.ItemType.Civilian && collector.civilians < collector.maxCivilians) {
						collector.civilians += 1;
						App.engine.removeEntity(p.ownerEntity);
					}
				}
			}
		}
	}

}
