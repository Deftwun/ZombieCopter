package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.components.CarComponent;
import com.deftwun.zombiecopter.components.ControllerComponent;
import com.deftwun.zombiecopter.components.HelicopterComponent;
import com.deftwun.zombiecopter.components.JumpComponent;
import com.deftwun.zombiecopter.components.LedgeHangComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.SpriteComponent;
import com.deftwun.zombiecopter.components.ThrusterComponent;
import com.deftwun.zombiecopter.components.WalkComponent;

public class MoveSystem extends IteratingSystem {
	private int LOG_LEVEL = Logger.INFO;
	private Logger logger = new Logger("Move System",LOG_LEVEL);
	
	private Vector2 velocity = new Vector2(),
					desiredVel = new Vector2(),
					accel = new Vector2(),
					forceToApply = new Vector2(),
					centerOfMass = new Vector2();
	
	@SuppressWarnings("unchecked")
	public MoveSystem(){
		super(Family.all(PhysicsComponent.class,ControllerComponent.class)
				    .one(WalkComponent.class,
						 JumpComponent.class,
						 LedgeHangComponent.class,
						 ThrusterComponent.class,
						 HelicopterComponent.class,
						 CarComponent.class).get());
		logger.debug("initializing");
	}
	
	private Vector2 calculateForceNeeded(Body body, Vector2 desiredVelocity, float deltaTime){
		Vector2 accel = (desiredVelocity.sub(body.getLinearVelocity()).scl(1/deltaTime));
		return accel.scl(body.getMass());		
	}	
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		ComponentMappers mappers = App.engine.mappers;
	
		ControllerComponent controller = mappers.controller.get(entity);
		PhysicsComponent physics = mappers.physics.get(entity);
		SpriteComponent sprite = mappers.sprite.get(entity);
		WalkComponent walk = mappers.walk.get(entity);
		JumpComponent jump = mappers.jump.get(entity);
		LedgeHangComponent ledge = mappers.ledge.get(entity);
		ThrusterComponent thrust = mappers.thrust.get(entity);
		HelicopterComponent copter = mappers.helicopter.get(entity);
		CarComponent car = mappers.car.get(entity);
		
		//Physics variables
		Body body = physics.getPrimaryBody();
		velocity.set(body.getLinearVelocity());
		centerOfMass.set(body.getWorldCenter());
		PhysicsSystem physicsSystem = App.engine.systems.physics;
		
		
		//TODO: Raycasts better?
		boolean isBlockedBottom = physicsSystem.getContactCount(physics.getFixture("bottomSensor")) > 0;
		boolean isBlockedLeft = physicsSystem.getContactCount(physics.getFixture("leftSensor")) > 0;
		boolean isBlockedRight = physicsSystem.getContactCount(physics.getFixture("rightSensor")) > 0;
		boolean isBlockedTopLeft = physicsSystem.getContactCount(physics.getFixture("topLeftSensor")) > 0;
		boolean isBlockedTopRight = physicsSystem.getContactCount(physics.getFixture("topRightSensor")) > 0;
		boolean isBlockedBottomLeft = physicsSystem.getContactCount(physics.getFixture("bottomLeftSensor")) > 0;
		boolean isBlockedBottomRight = physicsSystem.getContactCount(physics.getFixture("bottomRightSensor")) > 0;
		boolean isOnTheGround = isBlockedBottom;
		
		
		//flip sprite
		Sprite s = sprite.spriteMap.get(physics.getPrimaryBodyName());
		if (s != null){
		//if (physics != null && sprite != null && controller.moveVector.x != 0) {
			//for (Sprite s : sprite.spriteMap.values()){
				Vector2 v = physics.getLinearVelocity();
				if (controller.moveVector.x < 0 && v.x < 0) s.setFlip(true, s.isFlipY());
				if (controller.moveVector.x > 0 && v.x > 0) s.setFlip(false, s.isFlipY());
			//}
		}
		
		
		//====  WALKING ====
		if (walk != null){
			desiredVel.set(0,velocity.y);
			if (controller.moveVector.x < 0 && !isBlockedTopLeft) desiredVel.x = -walk.topSpeed;
			if (controller.moveVector.x > 0 && !isBlockedTopRight) desiredVel.x = walk.topSpeed;
	
			forceToApply.set(calculateForceNeeded(body,desiredVel,deltaTime));
			if (isOnTheGround == false) forceToApply.scl(.5f);
			body.applyForce(forceToApply,centerOfMass, true);
		}
		
		//====  HELICOPTER ====
		if (copter != null){

			//TODO: Change this so helicopter rotates smoothly and not so artificially
			velocity.set(physics.getLinearVelocity());
			accel.set(controller.moveVector).nor();
			if (isOnTheGround) accel.x = 0;
			accel.x *= copter.lateralPower * deltaTime;
			accel.y *= copter.verticalPower * deltaTime;
			
			//Copter angle

			float angle = 0;
			if (accel.x < 0) {
				if (velocity.x > copter.topSpeed * -.3f ) angle = 15;
				else if (velocity.x > copter.topSpeed * -.6f) angle = 30;
				else angle = 45;
			}
			else if (accel.x > 0) {
				if (velocity.x < copter.topSpeed * .3f ) angle = 345;
				else if (velocity.x < copter.topSpeed * .6f) angle = 330;
				else angle = 315;
			}
			physics.setRotation(angle);

			
			//Cant thrust downward && below maxAltitude
			if (accel.y < 0 || physics.getPosition().y > copter.maxAltitude) accel.y = 0; 
			
			//Limit acceleration
			//TODO Possible bug. Gravity force is limited when moving left/right
			if (accel.len() > 0){
				desiredVel.set(velocity).add(accel.scl(deltaTime));
				forceToApply.set(calculateForceNeeded(body,desiredVel.limit(copter.topSpeed),deltaTime));
				body.applyForce(forceToApply,centerOfMass, true);
			}
		}
		
		//==== JUMPING ====
		if (jump != null){
			jump.timeSinceJump += deltaTime;
			//apply jump force
			if (isOnTheGround && controller.moveVector.y > 0 && jump.timeSinceJump > jump.coolDown){
				desiredVel.set(velocity.x,jump.power);
				forceToApply.set(calculateForceNeeded(body,desiredVel,deltaTime));
				body.applyForce(forceToApply, centerOfMass, true);
				jump.timeSinceJump = 0;
			}
			
		}
		
		//==== LEDGE HANGING ====
		if (ledge != null){
			int facing = 0;
			if (isBlockedRight && isBlockedBottomRight && !isBlockedTopRight && !isOnTheGround){
				facing = 1;
				ledge.hanging = true;
			}
			else if (isBlockedLeft && isBlockedBottomLeft && !isBlockedTopLeft && !isOnTheGround){
				facing = -1;
				ledge.hanging = true;
			}
			else ledge.hanging = false;
			
			if (ledge.hanging){
				if (jump != null) jump.timeSinceJump = jump.coolDown * .5f;
				desiredVel.set(facing * .5f,0);
				if (ledge.climbing){ 
					desiredVel.set(ledge.climbSpeed * facing,ledge.climbSpeed);
				}
				forceToApply.set(calculateForceNeeded(body,desiredVel,deltaTime));
				body.applyForce(forceToApply,centerOfMass, true);
			}
			
		}
		
		//==== THRUSTER ==== 
		//TODO: (Depracated?)
		
		if (thrust != null){
			
			//Reset thruster timer when we land 
			if (isOnTheGround){
				thrust.timeThrusting = 0;
				thrust.timeOffGround = 0;
			}
			
			//Apply thrusters force
			else {
				accel.set(thrust.vector).nor().scl(thrust.power * deltaTime);
				if (accel.len() > 0 && thrust.timeOffGround >= thrust.delay){
					thrust.timeOffGround += deltaTime;
					if (thrust.timeThrusting < thrust.duration){
						thrust.timeThrusting += deltaTime;
						desiredVel.set(velocity).add(accel.scl(deltaTime));
						forceToApply.set(calculateForceNeeded(body,desiredVel.limit(thrust.topSpeed),deltaTime));
						body.applyForce(forceToApply,centerOfMass, true);
					} 
				}
			}
		}
		
		// ==== CAR ====
		if (car != null){
			
			WheelJoint leftWheelJoint = (WheelJoint) physics.getJoint("leftWheelJoint"),
					   rightWheelJoint = (WheelJoint) physics.getJoint("rightWheelJoint");
			
			if (leftWheelJoint == null || rightWheelJoint == null) {
				logger.error("Car is missing wheel joints");
				return;
			}
			
			if(controller.moveVector.x != 0) {
				Body b = physics.getPrimaryBody();
				
				b.applyForce(new Vector2(0,-car.downForce * deltaTime), b.getWorldCenter(), true);
			}
			
			if (controller.moveVector.x > 0 ){
				rightWheelJoint.setMotorSpeed(-car.speed);
				leftWheelJoint.setMotorSpeed(-car.speed);
				if (car.frontWheelDrive) leftWheelJoint.enableMotor(true);
				else leftWheelJoint.enableMotor(false);
				if (car.rearWheelDrive) rightWheelJoint.enableMotor(true);
				else rightWheelJoint.enableMotor(false);
			}
			else if (controller.moveVector.x < 0) { 
				rightWheelJoint.setMotorSpeed(car.speed);
				leftWheelJoint.setMotorSpeed(car.speed);
				if (car.frontWheelDrive) rightWheelJoint.enableMotor(true);
				else rightWheelJoint.enableMotor(false);
				if (car.rearWheelDrive) leftWheelJoint.enableMotor(true);
				else leftWheelJoint.enableMotor(false);
			}
			else {
				leftWheelJoint.enableMotor(false);
				rightWheelJoint.enableMotor(false);
			}
		}
	}
		
}
