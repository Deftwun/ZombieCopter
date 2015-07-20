package com.deftwun.zombiecopter.systems;

import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.AI.BrainState;
import com.deftwun.zombiecopter.ComponentMappers;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.RayCast;
import com.deftwun.zombiecopter.components.BrainComponent;
import com.deftwun.zombiecopter.components.ControllerComponent;
import com.deftwun.zombiecopter.components.GunComponent;
import com.deftwun.zombiecopter.components.MeleeComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.TeamComponent;
import com.deftwun.zombiecopter.components.TeamComponent.Team;


//This AI system tries to dynamically determine how an entity should behave based on the components it contains
public class AgentSystem extends EntitySystem{	
	private Logger logger = new Logger("AgentSystem",Logger.INFO);

	private RayCast rayCast = new RayCast();
	private Family agents = Family.all(BrainComponent.class,ControllerComponent.class, PhysicsComponent.class).get();
	
	public AgentSystem(){
		logger.debug("Initializing");
	}
	
	public void update(float deltaTime){
		ImmutableArray<Entity> entities = App.engine.getEntitiesFor(agents);					
		ComponentMappers mappers = App.engine.mappers;
		for (Entity e : entities){
			BrainComponent brain = mappers.brain.get(e);
			brain.time += deltaTime;
			if (brain.time >= brain.thinkTime){
				brain.time = 0;
				processEntity(e,deltaTime);
			}
		}
	}
	
	private void processEntity(Entity e,float deltaTime){
		updateSpacialAwareness(e);
		determineState(e);
		handleMovement(e,deltaTime);
		handleWeapons(e,deltaTime);
		copterCollisionAvoidance(e,deltaTime);
		walkerCliffAvoidance(e,deltaTime);
	}
	
	private void walkerCliffAvoidance(Entity e, float deltaTime){
		ComponentMappers mappers = App.engine.mappers;
		
		//Only pertains to walkers
		if (!mappers.walk.has(e)) return;
		
		ControllerComponent controller = mappers.controller.get(e);
		PhysicsComponent physics = mappers.physics.get(e);
		BrainComponent brain = mappers.brain.get(e);
		
		World w = App.engine.systems.physics.world;
		controller.moveVector.nor();
		
		float x1 = brain.myPosition.x,
			  y1 = brain.myPosition.y,
			  speed = physics.getLinearVelocity().len(),
			  distance = physics.getLinearVelocity().scl(deltaTime).len(),
			  size = Box2DUtils.size(physics.getPrimaryBody()).len(),
			  height = speed * 5; //Dont walk off edge if nothing above this height
			  
		boolean noGroundToTheLeft = rayCast.cast(w,x1-distance,y1,x1,y1-height),
				noGroundToTheRight = rayCast.cast(w,x1+distance,y1,x1,y1-height);
		
		if (noGroundToTheLeft) controller.moveVector.add(1,0);
		if (noGroundToTheRight)controller.moveVector.add(-1,0);
		
	}
	
	private void copterCollisionAvoidance(Entity e, float deltaTime) {
		ComponentMappers mappers = App.engine.mappers;
		
		//Only pertains to helicopters
		if (!mappers.helicopter.has(e)) return;
		
		ControllerComponent controller = mappers.controller.get(e);
		PhysicsComponent physics = mappers.physics.get(e);
		BrainComponent brain = mappers.brain.get(e);
		
		World w = App.engine.systems.physics.world;
		controller.moveVector.nor();
		
		float x1 = brain.myPosition.x,
			  y1 = brain.myPosition.y,
			  s = Box2DUtils.size(physics.getPrimaryBody()).len(),
			  d = s + physics.getPrimaryBody().getLinearVelocity().scl(deltaTime).len() + brain.desiredRange * deltaTime;
		
		boolean clearBelow = rayCast.cast(w,x1,y1,x1,y1-d),
				clearAbove = rayCast.cast(w,x1,y1,x1,y1+d),
				clearLeft = rayCast.cast(w,x1,y1,x1-d,y1),
				clearRight = rayCast.cast(w,x1,y1,x1+d,y1),
				clearLowLeft = rayCast.cast(w,x1,y1,x1-d*.5f,y1-d),
				clearLowRight = rayCast.cast(w,x1,y1,x1+d*.5f,y1-d),
				clearUpLeft = rayCast.cast(w,x1,y1,x1-d*.5f,y1+d),
				clearUpRight = rayCast.cast(w,x1,y1,x1+d*.5f,y1+d);
		
		if (clearAbove) controller.moveVector.y = 0;
		if (!clearLowLeft)controller.moveVector.add(1,1);
		if (!clearLowRight)controller.moveVector.add(-1,1);
		if (!clearUpLeft)controller.moveVector.add(1,-1);
		if (!clearUpRight)controller.moveVector.add(-1,-1);
		
		if (!clearAbove) controller.moveVector.add(0,-1);
		if (!clearBelow) controller.moveVector.add(0,1);		
		if (!clearLeft) controller.moveVector.add(1,0);
		if (!clearRight) controller.moveVector.add(-1,0);
		
	}

	private boolean isEnemy(Team t1, Team t2){
		return App.engine.systems.team.getEnemies(t1).contains(t2,false);
	}
	
	private boolean isFriend(Team t1, Team t2){
		return t1 == t2;
	}
	
	//Recognize position & velocity in space & determine where things are
	private void updateSpacialAwareness(Entity e){
		ComponentMappers mappers = App.engine.mappers;
		PhysicsComponent physics = mappers.physics.get(e);
		BrainComponent brain = mappers.brain.get(e);
		TeamComponent team = mappers.team.get(e);
		
		brain.myPosition.set(physics.getPosition());
		brain.myVelocity.set(physics.getLinearVelocity());
		brain.closestEnemy = null;
		brain.closestFriend = null;
		brain.closestLeader = null;
		
		if (team == null) return;
		
		//Poll Physics entities in sight and Evaluate friend or foe etc...
		Fixture visionSensor = physics.getFixture("visionSensor");
		if (visionSensor != null){
			
			logger.debug("Looking around");
			
			Array<PhysicsComponent> visibleObjects = 
					App.engine.systems.physics.getPhysicsComponentsTouching(visionSensor);
			
			float closestEnemyRange = -1,
				  closestFriendRange = -1,
				  closestLeaderRange = -1;

			for (PhysicsComponent physicsInSight : visibleObjects){
				
				logger.debug("Physics entity in sight");
				Entity entityInSight = physicsInSight.ownerEntity;
				TeamComponent teamInSight = mappers.team.get(entityInSight);
				
				//Enemy
				if (teamInSight != null && isEnemy(team.team,teamInSight.team)){
					logger.debug("Entity is my ENEMY");
					float distance = physicsInSight.getPosition().dst(physics.getPosition());
					if (closestEnemyRange < 0) {
						closestEnemyRange = distance;
						brain.closestEnemy = entityInSight;
					}
					else if (distance < closestEnemyRange) {
						brain.closestEnemy = entityInSight;
					}
				}
				
				//tell brain which FRIEND is closest within sight
				else if (teamInSight != null && isFriend(team.team,teamInSight.team)){
					logger.debug("Entity is my FRIEND");
					
					float distance = physicsInSight.getPosition().dst(physics.getPosition());
					if (closestFriendRange < 0) {
						closestFriendRange = distance;
						brain.closestFriend = entityInSight;
					}
					else if (distance < closestFriendRange) {
						brain.closestFriend = entityInSight;	
					}
					
					if (mappers.leader.has(entityInSight)){
						//Closest FRIEND && LEADER
						logger.debug("Entity is my LEADER");
						
						if (closestLeaderRange < 0) {
							closestLeaderRange = distance;
							brain.closestLeader = entityInSight;
						}
						else if (distance < closestFriendRange) {
							brain.closestLeader = entityInSight;	
						}
					}
				}
			}
		}	
	}

	private void determineState(Entity e){
		BrainComponent brain = App.engine.mappers.brain.get(e);
		GunComponent gun = App.engine.mappers.gun.get(e);
		MeleeComponent melee = App.engine.mappers.melee.get(e);
		
		boolean enemyInSight = brain.closestEnemy != null,
				friendInSight = brain.closestFriend != null,
				leaderInSight = brain.closestLeader != null;
		
		//Follow the leader
		if (!enemyInSight && leaderInSight){
			brain.desiredRange = 1;
			brain.state = BrainState.FOLLOW;
		}
		
		//Attack or Flee 
		else if (enemyInSight){
			
			//Attack if we have a weapon
			if (gun != null){
				// lets try and stay within 80% of weapons range
				brain.desiredRange = gun.range * .5f; 
				brain.state = BrainState.ATTACK;
			}
			else if (melee != null){
				melee.target = brain.closestEnemy;
				brain.desiredRange = melee.range * .5f;
				brain.rangeTolerance = .1f;
				brain.state = BrainState.ATTACK;
			}
			
			//Flee if were defenseless
			else {
				brain.state = BrainState.FLEE;
			}
		}

		//Patrol (Look for leader / enemies)
		else if (!leaderInSight){
			brain.state = BrainState.PATROL;
		}
		
		logger.debug("Brain State = " + brain.state);
		
	}
	
	private void handleMovement(Entity e, float deltaTime){
		BrainComponent brain = App.engine.mappers.brain.get(e);
		ControllerComponent controller = App.engine.mappers.controller.get(e);
		
		switch (brain.state){
			//PATROL only controls movement along the x axis. Helicopters might not do so well unless I figure out a way to 
			// maintain desired altitude
			case PATROL: {
				
				controller.moveVector.y=0;
				
				//TODO: This should work off patrol distance or something
				//50% chance of changing direction
				if (MathUtils.randomBoolean(.5f)){
					logger.debug("Patrol: Changing direction");
					//33% percent chance to walk left,right or stop
					if (MathUtils.randomBoolean(.44f)){
						controller.moveVector.x = -1;
						logger.debug("Patrol: Move Left");
					}
					else if (MathUtils.randomBoolean(.88f)){
						controller.moveVector.x = 1;
						logger.debug("Patrol: Move Right");
					}
					else {
						controller.moveVector.x = 0;
						logger.debug("Patrol: Stay Put");
					}
				}
				
				//Look where your going
				controller.lookVector.set(controller.moveVector);
				
				break;
			}
			
			case ATTACK:{
				PhysicsComponent enemyPhysics = App.engine.mappers.physics.get(brain.closestEnemy);
				if (enemyPhysics == null) break;
				
				//Look at enemy
				controller.lookVector.set(enemyPhysics.getPosition()).sub(brain.myPosition).nor();
		
				//Don't move if were within desiredRange +- rangeTolerance
				
				float distance = brain.myPosition.dst(enemyPhysics.getPosition());
				if (distance < (brain.desiredRange + brain.rangeTolerance) && 
					distance > (brain.desiredRange - brain.rangeTolerance))
					controller.moveVector.set(0,0);
				
				//Move towards enemy
				else if (distance > brain.desiredRange){
					controller.moveVector.set(enemyPhysics.getPosition()).sub(brain.myPosition).nor();
				}
				//Move away from enemy
				else if (distance < brain.desiredRange){
					controller.moveVector.set(brain.myPosition).sub(enemyPhysics.getPosition()).nor();
				}

				break;
			}	
			
			case FLEE:{
				PhysicsComponent enemyPhysics = App.engine.mappers.physics.get(brain.closestEnemy);
				if (enemyPhysics == null) break;
				
				//Run away
				controller.moveVector.set(brain.myPosition).sub(enemyPhysics.getPosition()).nor();
				
				//Look where your going
				controller.lookVector.set(controller.moveVector);
				
				break;
			}	
			
			case FOLLOW:{
				PhysicsComponent leaderPhysics = App.engine.mappers.physics.get(brain.closestLeader);
				if (leaderPhysics == null) break;
				
				//Don't move if we're within desiredRange +- rangeTolerance
				float distance = brain.myPosition.dst(leaderPhysics.getPosition());
				if (distance < (brain.desiredRange + brain.rangeTolerance) && 
					distance > (brain.desiredRange - brain.rangeTolerance)) 
					controller.moveVector.set(0,0);
				
				//Move towards leader
				else if (distance > brain.desiredRange / 2){
					controller.moveVector.set(leaderPhysics.getPosition()).sub(brain.myPosition).nor();
					controller.lookVector.set(controller.moveVector);
					controller.moveVector.y = 0;
				}
				
				break;
			}
			
			default: brain.state = BrainState.PATROL;
		}
	}
	
	private void handleWeapons(Entity e, float deltaTime){
		
		ComponentMappers mappers = App.engine.mappers;
		
		if (!mappers.gun.has(e) && !mappers.melee.has(e)) return;
		
		logger.debug("Handle weapons");
		
		BrainComponent brain = mappers.brain.get(e);
		ControllerComponent controller = mappers.controller.get(e);
		PhysicsComponent physics = mappers.physics.get(e);
		
		switch (brain.state){
		
			case ATTACK:{
				World w = App.engine.systems.physics.world;
				PhysicsComponent enemyPhysics = mappers.physics.get(brain.closestEnemy);
				boolean clearShot = rayCast.cast(w,physics,enemyPhysics),
						inRange = brain.myPosition.dst(enemyPhysics.getPosition()) <= brain.desiredRange;
				logger.debug("Attacking? "+(inRange && clearShot) + " because ; inRange=" + inRange + " ; clearShot=" + clearShot);
				controller.attack = inRange && clearShot;
				break;
			}
			
			default: 
				controller.attack = false;
				break;
		}
	}
}
	