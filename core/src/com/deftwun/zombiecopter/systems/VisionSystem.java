package com.deftwun.zombiecopter.systems;
 
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.components.ControllerComponent;
import com.deftwun.zombiecopter.components.LookComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.SpriteComponent;

public class VisionSystem extends IteratingSystem{
     
    private Logger logger;
    private int LOG_LEVEL = Logger.INFO;
	
    @SuppressWarnings("unchecked")
    public VisionSystem(){
        super(Family.all(PhysicsComponent.class,LookComponent.class).get());    
        logger = new Logger("VisionSystem",LOG_LEVEL);
        logger.debug("initializing");
    }
     
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent phys = App.engine.mappers.physics.get(entity);
		ControllerComponent controller = App.engine.mappers.controller.get(entity);
        LookComponent look = App.engine.mappers.look.get(entity);
        SpriteComponent sprite = App.engine.mappers.sprite.get(entity);
				
        if (look.isSweeping){
        	if (look.sweepAcc >= look.sweepAngle){
        		look.reverseSweep = !look.reverseSweep;
        		look.sweepAcc = 0;
        	}
        	float rate = look.sweepRate * deltaTime;
        	if (look.reverseSweep) look.direction.rotate(rate * -1);
        	else look.direction.rotate(rate);
        	look.sweepAcc += rate;
        }
		else {
			if (controller != null)	look.direction.set(controller.lookVector).nor();
		}
        
        //Sprite flipping
        //TODO: Make sure no other systems flip the sprite after this
        if (look.controlSpriteFlip && sprite != null){
        	Sprite visionSprite = sprite.spriteMap.get("visionBody");
        	for (Sprite s : sprite.spriteMap.values()){
        		if (s == visionSprite){
        			if (look.direction.angle() > 90 && look.direction.angle() <  270)
        				visionSprite.setFlip(false, true);
        			else visionSprite.setFlip(false, false);
        		}
        		else {
        			if (look.direction.angle() > 90 && look.direction.angle() <  270)
        				s.setFlip(true,false);
        			else s.setFlip(false,false);
        		}
        	}
        }
        
        look.position.set(phys.getPosition());
		Body visionCone = phys.getBody("visionBody");
		if (visionCone != null){
			visionCone.setTransform(phys.getPosition(), look.direction.angleRad());
		}
    }
}