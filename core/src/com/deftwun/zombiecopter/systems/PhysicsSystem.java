package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.box2dJson.PhysicsScene.PhysicsSceneListener;
import com.deftwun.zombiecopter.components.HealthComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.StickyComponent;

public class PhysicsSystem extends IteratingSystem implements ContactListener, EntityListener, PhysicsSceneListener {
 	
	public World world;
	private final float gravity = -14;

	private Logger logger = new Logger("PhysicsSystem",Logger.INFO);
	
	//Collision Maps
	private ObjectMap<Fixture,Array<Fixture>> fixture_fixtures_map = new ObjectMap<Fixture,Array<Fixture>>();
	private ObjectMap<Fixture,Array<Body>> fixture_bodies_map = new ObjectMap<Fixture,Array<Body>>();
	private ObjectMap<Fixture,Array<PhysicsComponent>> fixture_physics_map = new ObjectMap<Fixture,Array<PhysicsComponent>>();
	private ObjectMap<Body,Array<Fixture>> body_fixtures_map = new ObjectMap<Body,Array<Fixture>>();
	private ObjectMap<Body,Array<Body>> body_bodies_map = new ObjectMap<Body,Array<Body>>();
	private ObjectMap<Body,Array<PhysicsComponent>> body_physics_map = new ObjectMap<Body,Array<PhysicsComponent>>();
	private ObjectMap<PhysicsComponent,Array<Fixture>> physics_fixtures_map = new ObjectMap<PhysicsComponent,Array<Fixture>>();
	private ObjectMap<PhysicsComponent,Array<Body>> physics_bodies_map = new ObjectMap<PhysicsComponent,Array<Body>>();
	private ObjectMap<PhysicsComponent,Array<PhysicsComponent>> physics_physics_map = new ObjectMap<PhysicsComponent,Array<PhysicsComponent>>();
	
	//Collision Methods
	public Array<Fixture> getFixturesTouching(Fixture f){if (f==null) return null; return fixture_fixtures_map.get(f,null);}
	public Array<Fixture> getFixturesTouching(Body b){if (b==null) return null; return body_fixtures_map.get(b,null);}
	public Array<Fixture> getFixturesTouching(PhysicsComponent p){if (p==null) return null;return physics_fixtures_map.get(p,null);}
	public Array<Body> getBodiesTouching(Fixture f){if (f==null) return null;return fixture_bodies_map.get(f,null);}
	public Array<Body> getBodiesTouching(Body b){if (b==null) return null;return body_bodies_map.get(b,null);}
	public Array<Body> getBodiesTouching(PhysicsComponent p){if (p==null) return null;return physics_bodies_map.get(p,null);}
	public Array<PhysicsComponent> getPhysicsComponentsTouching(Fixture f){if (f==null) return null;return fixture_physics_map.get(f,null);}
	public Array<PhysicsComponent> getPhysicsComponentsTouching(Body b){if (b==null) return null;return body_physics_map.get(b,null);}
	public Array<PhysicsComponent> getPhysicsComponentsTouching(PhysicsComponent p){if (p==null) return null;return physics_physics_map.get(p,null);}
		
	public int getContactCount(Fixture f){	
		if (f == null) return 0;
		Array<Fixture> list = fixture_fixtures_map.get(f,null);
		return (list == null) ? 0 : list.size;
	}
	public int getContactCount(Body b){
		if (b == null) return 0;
		Array<Fixture> list = body_fixtures_map.get(b,null);
		return (list == null) ? 0 : list.size;
	}
	public int getContactCount(PhysicsComponent p){
		if (p == null) return 0;
		Array<Fixture> list = physics_fixtures_map.get(p,null);
		return (list == null) ? 0 : list.size;
	}
	
	@SuppressWarnings("unchecked")
	public PhysicsSystem(){
		super(Family.all(PhysicsComponent.class).get());
		logger.debug("initializing");
		world = new World(new Vector2(0,gravity),true);
		world.setContactListener(this);
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		world.step(deltaTime,4,4);	
	}
		
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent physics = App.engine.mappers.physics.get(entity);
		HealthComponent health = App.engine.mappers.health.get(entity);
		StickyComponent sticky = App.engine.mappers.sticky.get(entity);
		
		float collisionForce = physics.getCollisionNormal();
		
		//Sticky projectiles
		if (sticky != null && sticky.enabled){
			Fixture stickyFixture = physics.getFixture(sticky.fixtureName);
			if (stickyFixture != null){
				
				boolean colliding = getContactCount(stickyFixture) > 0;
				
				Array<Fixture> touchingFixtures = getFixturesTouching(stickyFixture);
				for (Fixture f : touchingFixtures){
					if (f.isSensor()) colliding = false;
					else {
						WeldJointDef jd = new WeldJointDef();
						Body b1 = f.getBody(),
						     b2 = stickyFixture.getBody();
						jd.initialize(b1,b2,b2.getWorldCenter());
						world.createJoint(jd);
						sticky.enabled = false;
						break;
					}
				}
				
				if (!colliding)	physics.setRotation(physics.getLinearVelocity().angle());

			}
		}
		
		//Collision Damage
		if (health != null && collisionForce >= health.collisionDamageThreshold){
			float dmg = collisionForce - health.collisionDamageThreshold;
			App.engine.systems.damage.dealDamage(entity,dmg);
			logger.debug(String.format("Collision damage: Entity#%d ; Force=%f ; Dmg=%f",
										entity.getId(),collisionForce,dmg));
		}
		physics.clearCollisionNormal();
		
		/*
		//Anything outside of the world bounds is destroyed
		if (!App.engine.entityBounds.contains(physics.getPosition()) &&
				entity != App.engine.getLevel().getEntity()){
			logger.debug("Destroy Entity #" + entity.getId() + ": outside of world bounds.");
			App.engine.removeEntity(physics.ownerEntity);
		}
		*/

	}
	
    @Override
    public void beginContact(Contact contact) { 
    	
		Fixture fixA = contact.getFixtureA(),
			    fixB = contact.getFixtureB();
		Body bodyA = fixA.getBody(), 
			 bodyB = fixB.getBody();
        PhysicsComponent physA = (PhysicsComponent)bodyA.getUserData(), 
                         physB = (PhysicsComponent)bodyB.getUserData();
		
		if ((physA == null) || (physB == null)) {logger.error("BEGIN CONTACT - Physics component is null"); return;}
		if (bodyA.getUserData() == physB || bodyB.getUserData() == physA) {logger.debug("BEGIN CONTACT - ignore PhysicsComponent self collisions"); return;}
		logger.debug("Begin contact between Entity #" + physA.ownerEntity.getId() + " & #" + physB.ownerEntity.getId());
		
		if (!fixA.isSensor() && !fixB.isSensor()){
			if (physA.collisionEffect != "")
				App.engine.systems.particle.addEffect(physA.collisionEffect,physA.getPosition(),physB.getRotation());
			if (physB.collisionEffect != "")
				App.engine.systems.particle.addEffect(physB.collisionEffect,physB.getPosition(),physB.getRotation());
		}
		fixture_fixtures_map.get(fixA).add(fixB);		
		fixture_fixtures_map.get(fixB).add(fixA);
		fixture_bodies_map.get(fixA).add(bodyB);
		fixture_bodies_map.get(fixB).add(bodyA);
		fixture_physics_map.get(fixA).add(physB);
		fixture_physics_map.get(fixB).add(physA);
		
		body_fixtures_map.get(bodyA).add(fixB);		
		body_fixtures_map.get(bodyB).add(fixA);
		body_bodies_map.get(bodyA).add(bodyB);
		body_bodies_map.get(bodyB).add(bodyA);
		body_physics_map.get(bodyA).add(physB);
		body_physics_map.get(bodyB).add(physA);
		
		physics_fixtures_map.get(physA).add(fixB);		
		physics_fixtures_map.get(physB).add(fixA);
		physics_bodies_map.get(physA).add(bodyB);
		physics_bodies_map.get(physB).add(bodyA);
		physics_physics_map.get(physA).add(physB);
		physics_physics_map.get(physB).add(physA);
    }
 
    @Override
    public void endContact(Contact contact) {
    	logger.debug("End contact");
		
		Fixture fixA = contact.getFixtureA(),
			    fixB = contact.getFixtureB();
		Body bodyA = fixA.getBody(), 
			 bodyB = fixB.getBody();
        PhysicsComponent physA = (PhysicsComponent)bodyA.getUserData(), 
                         physB = (PhysicsComponent)bodyB.getUserData();
        
		if ((physA == null) || (physB == null)) {logger.error("Physics component is null"); return;}
		if (bodyA.getUserData() == physB || bodyB.getUserData() == physA) {logger.debug("END CONTACT - ignore PhysicsComponent self collisions."); return;}
		logger.debug("End contact between Entity #" + physA.ownerEntity.getId() + " & #" + physB.ownerEntity.getId());
		
		if (fixture_fixtures_map.get(fixA) == null || fixture_fixtures_map.get(fixB) == null){
			//TODO: If the fixtures exist but haven't been added we just return
			logger.error("END CONTACT: One or both of the fixtures are no longer registered with the physics system.");
			return;
		}
		
		fixture_fixtures_map.get(fixA).removeValue(fixB, true);		
		fixture_fixtures_map.get(fixB).removeValue(fixA, true);
		fixture_bodies_map.get(fixA).removeValue(bodyB, true);
		fixture_bodies_map.get(fixB).removeValue(bodyA, true);
		fixture_physics_map.get(fixA).removeValue(physB, true);
		fixture_physics_map.get(fixB).removeValue(physA, true);
		
		body_fixtures_map.get(bodyA).removeValue(fixB, true);		
		body_fixtures_map.get(bodyB).removeValue(fixA, true);
		body_bodies_map.get(bodyA).removeValue(bodyB, true);
		body_bodies_map.get(bodyB).removeValue(bodyA, true);
		body_physics_map.get(bodyA).removeValue(physB, true);
		body_physics_map.get(bodyB).removeValue(physA, true);
		
		physics_fixtures_map.get(physA).removeValue(fixB, true);		
		physics_fixtures_map.get(physB).removeValue(fixA, true);
		physics_bodies_map.get(physA).removeValue(bodyB, true);
		physics_bodies_map.get(physB).removeValue(bodyA, true);
		physics_physics_map.get(physA).removeValue(physB, true);
		physics_physics_map.get(physB).removeValue(physA, true);     
    }
 
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
         
    }
 
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Body bodyA = contact.getFixtureA().getBody(), bodyB = contact.getFixtureB().getBody();
        PhysicsComponent physA = (PhysicsComponent)bodyA.getUserData(), 
                         physB = (PhysicsComponent)bodyB.getUserData();
        if (physA == null || physB == null) return;
        physA.addCollisionNormal(impulse.getNormalImpulses()[0]);
        physB.addCollisionNormal(impulse.getNormalImpulses()[0]);
    }

	@Override
	public void entityAdded(Entity entity) {
		logger.debug("Entity added: " + entity.getId());
		PhysicsComponent physics = App.engine.mappers.physics.get(entity);
		if (physics == null) return;
		physics.ownerEntity = entity;
		this.physicsAdded(physics);
	}

	@Override
	public void entityRemoved(Entity entity) {
		logger.debug("Entity removed: " + entity.getId());
		PhysicsComponent physics = App.engine.mappers.physics.get(entity);
		if (physics == null) return;
		physics.destroy();
		this.physicsRemoved(physics);
	}

	//Should be called when a new physics component is created
	public void physicsAdded(PhysicsComponent physics){
		logger.debug("Physics Component added");
		physics_fixtures_map.put(physics,new Array<Fixture>());
		physics_bodies_map.put(physics,new Array<Body>());
		physics_physics_map.put(physics,new Array<PhysicsComponent>());
	}
	
	//Should be called when a new fixture is created
	@Override
	public void fixtureAdded(Fixture fixture){
		logger.debug("Fixture Added");
		logger.debug("Fixture_Fixture_Map size = " + fixture_fixtures_map.size);
		fixture_fixtures_map.put(fixture,new Array<Fixture>());
		fixture_bodies_map.put(fixture,new Array<Body>());
		fixture_physics_map.put(fixture,new Array<PhysicsComponent>());
	}
	
	//Should be called when a new body is created
	@Override
	public void bodyAdded(Body body){
		logger.debug("Body Added");
		body_fixtures_map.put(body,new Array<Fixture>());
		body_bodies_map.put(body,new Array<Body>());
		body_physics_map.put(body,new Array<PhysicsComponent>());
	}
	
	//Should be called when an existing physics component is destroyed
	public void physicsRemoved(PhysicsComponent physics){
		logger.debug("Physics Component Removed");
		physics_fixtures_map.remove(physics);
		physics_bodies_map.remove(physics);
		physics_physics_map.remove(physics);
	}
	
	//Should be called when an existing fixture is destroyed
	@Override
	public void fixtureRemoved(Fixture fixture){
		logger.debug("Fixture Removed");
		fixture_fixtures_map.remove(fixture);
		fixture_bodies_map.remove(fixture);
		fixture_physics_map.remove(fixture);
	}

	//Should be called when an existing body is destroyed
	@Override
	public void bodyRemoved(Body body){
		logger.debug("Body Removed");
		body_fixtures_map.remove(body);
		body_bodies_map.remove(body);
		body_physics_map.remove(body);
	}
	@Override
	public void jointAdded(Joint j) {

		
	}
	@Override
	public void jointRemoved(Joint j) {

		
	}

}