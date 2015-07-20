package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.components.HealthComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.TimeToLiveComponent;

public class LifetimeSystem extends IteratingSystem{
	private Logger logger = new Logger("LifeTimeSystem",Logger.INFO);
	
	@SuppressWarnings("unchecked")
	public LifetimeSystem() {
		super(Family.all(PhysicsComponent.class)
				    .one(HealthComponent.class,TimeToLiveComponent.class).get());
		logger.debug("initializing");
	}		

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		ComponentMappers mappers = App.engine.mappers;
		TimeToLiveComponent timeToLive = mappers.timeToLive.get(entity);
	
		//Time to live (expiration)
		if (timeToLive != null){
			timeToLive.time += deltaTime;
			if (timeToLive.time >= timeToLive.timeLimit){
				logger.debug("Entity #" + entity.getId() + " has expired: Time to live exceeded");
				App.engine.removeEntity(entity);
			}
		}
	}
}
