package com.deftwun.zombiecopter;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.deftwun.zombiecopter.box2dJson.*;
import com.deftwun.zombiecopter.components.*;
import com.deftwun.zombiecopter.components.TeamComponent.Team;
import com.deftwun.zombiecopter.EntityConfig;

public class EntityBuilder{
	private final Logger logger = new Logger("EntityBuilder",Logger.INFO);
	private final Vector2 tmpVector = new Vector2();

	public EntityBuilder(){
		logger.debug("Initializing");
	}
	
	public String serialize(Entity entity){
		Json json = new Json();
		logger.debug("Serialize Entity: " + entity.getId());
		return json.toJson(entity.getComponents().toArray()); 
	}
	
	public Entity deserialize(String data){
		Json json = new Json();
		logger.debug("Deserialize Entity");
		Array<Component> comps = json.fromJson(null,data);
		if (comps == null) {
			logger.error("Deserialization failed: " +  data);
			return null;
		}
		Entity e = App.engine.createEntity();
		for (Component c : comps){
			e.add(c);
		}
		logger.debug("Entity deserialized: " + e.getId());
		return e;
	}
	
	
	public Entity build(String name){return this.build(name, tmpVector.set(0,0),tmpVector,0);}
	public Entity build(String name, Vector2 position){return this.build(name, position,tmpVector.set(0,0), 0);}
	public Entity build(String name, Vector2 position, Vector2 velocity){return this.build(name, position,velocity, 0);}
	public Entity build(String name, Vector2 position, Vector2 velocity, float rotation){

		Entity e = null;
		EntityConfig cfg = App.assets.getEntityConfig(name);

		if (cfg == null) {
			logger.error("Config file not found for '" + name + "'");
			return null;
		}
		
		logger.debug(String.format("Building '%s' ; Model=%s",name,cfg.model)); 
		
		//Create physics & sprite model
		if (cfg.model.equals("humanoid")) e = createHumanoid(cfg);
		else if (cfg.model.equals("car")) e = createCar(cfg);
		else if (cfg.model.equals("helicopter")) e = createHelicopter(cfg);
		else if (cfg.model.equals("bullet")) e = createBullet(cfg);
		else if (cfg.model.equals("ball")) e = createBall(cfg);
		else {
			logger.error("Entity model '" + cfg.model + "' not recognized.");
			return null;
		}
		
		//Set physics position, velocity, & rotation 
		PhysicsComponent physics = App.engine.mappers.physics.get(e);
		if (physics != null){
			physics.collisionEffect = cfg.collisionEffect;
			physics.setPosition(position);
			physics.setLinearVelocity(velocity);
			physics.setRotation(rotation);
			logger.debug(String.format("  Set Position & Velocity : %f,%f / %f,%f ", 
										 physics.getPosition().x,physics.getPosition().y,
										 physics.getLinearVelocity().x,physics.getLinearVelocity().y));
		}

		//Controller (all entities have this)
		logger.debug("  Attach Controller");
		ControllerComponent controller = App.engine.createComponent(ControllerComponent.class);
		e.add(controller);
		
		
		//Player
		if (cfg.player){
			logger.debug("  Attach Player");
			PlayerComponent player = App.engine.createComponent(PlayerComponent.class);
			e.add(player);
		}
		
		//Brain
		if (cfg.brain)	{
			logger.debug("  Attach Brain");
			BrainComponent brain = App.engine.createComponent(BrainComponent.class);
			if (cfg.thinkTime > 0) brain.thinkTime = cfg.thinkTime;
			e.add(brain);
		}
		
		//Team
		if (!cfg.team.equals("")){
			TeamComponent team = App.engine.createComponent(TeamComponent.class);
			if (cfg.team.equals("player")) team.team = Team.PLAYER;
			else if (cfg.team.equals("bullet")) team.team = Team.BULLET;
			else if (cfg.team.equals("enemy")) team.team = Team.ENEMY;
			else if (cfg.team.equals("wild")) team.team = Team.WILD;
			else logger.error("  Team '" + cfg.team + "' not recognized.");
			e.add(team);
			logger.debug("  Attach Team : " + team.team);
		}
		
		//Weapons
		if (cfg.weapon != ""){
			WeaponConfig weaponCfg = App.assets.getWeaponConfig(cfg.weapon);
			if (weaponCfg != null){
				//Json json = new Json();
				logger.debug("  Attach Gun: " + cfg.weapon);
				GunComponent gun = App.engine.createComponent(GunComponent.class);
				gun.projectileName = weaponCfg.projectileName;
				gun.spreadAngle = weaponCfg.spreadAngle;
				gun.bulletSpeed = weaponCfg.bulletSpeed;
				gun.range = weaponCfg.range;
				gun.cooldown = weaponCfg.cooldown;
				gun.offset.set(cfg.weaponOffsetX,cfg.weaponOffsetY);
				e.add(gun);
			}
		}
		
		//Health
		if (cfg.health > 0){
			logger.debug("  Attach Health: " + cfg.health);
			HealthComponent health = App.engine.createComponent(HealthComponent.class);
			health.value = cfg.health;
			health.max = cfg.health;
			health.collisionDamageThreshold = cfg.collisionDamageThreshold;
			health.corpse = cfg.corpse;
			health.drop = cfg.drop;
			health.dropRate = cfg.dropRate;
			health.damageEffect = cfg.damageEffect;
			health.deathEffect = cfg.deathEffect;
			health.gibbedEffect = cfg.gibbedEffect;
			e.add(health);
		}
		
		//Time To live
		if (cfg.timeToLive > 0){
			logger.debug("  Attach TimeToLive: " + cfg.timeToLive);
			TimeToLiveComponent ttl = App.engine.createComponent(TimeToLiveComponent.class);
			ttl.timeLimit = cfg.timeToLive;
			e.add(ttl);
		}
		
		//Collectable
		if (cfg.collectable){
			logger.debug("  Attach Collectable");
			Collectable collectable = App.engine.createComponent(Collectable.class);
			e.add(collectable);
		}
		
		//Collector
		if (cfg.collector){
			logger.debug("  Attach Collector");
			Collector collector = App.engine.createComponent(Collector.class);
			e.add(collector);
		}
		
		//Look
		if (cfg.viewDistance > 0){
			logger.debug("  Attach Look");
			LookComponent look = App.engine.createComponent(LookComponent.class);
			look.controlSpriteFlip = cfg.lookControlSpriteFlip;
			e.add(look);
		}	
		
		//Walker
		if (cfg.walker){
			logger.debug("  Attach Walk");
			WalkComponent walk = App.engine.createComponent(WalkComponent.class);
			walk.topSpeed = cfg.speed;
			e.add(walk);
		}	
		
		//Jump
		if (cfg.jumpPower > 0){
			logger.debug("  Attach Jump");
			JumpComponent jump = App.engine.createComponent(JumpComponent.class);
			jump.power = cfg.jumpPower;
			jump.coolDown = cfg.jumpCooldown;
			e.add(jump);
		}	
		
		//Helicopter
		if (cfg.helicopter){
			logger.debug("  Attach Helicopter");
			HelicopterComponent copter = App.engine.createComponent(HelicopterComponent.class);
			copter.topSpeed = cfg.speed;
			copter.lateralPower = cfg.lateralPower;
			copter.verticalPower = cfg.verticalPower;
			copter.maxAltitude = cfg.maxAltitude;
			e.add(copter);
		}
		
		//Bullet
		if (cfg.damage > 0){
			logger.debug("  Attach Bullet: dmg=" + cfg.damage);
			BulletComponent bullet = App.engine.createComponent(BulletComponent.class);
			bullet.damage = cfg.damage;
			e.add(bullet);
		}
		
		//Vehicle
		if (cfg.vehicle){
			logger.debug("  Attach vehicle");
			VehicleComponent vehicle = App.engine.createComponent(VehicleComponent.class);
			e.add(vehicle);
		}
		
		//Vehicle Operator
		if (cfg.vehicleOperator){
			logger.debug("  Attach VehicleOperator");
			VehicleOperatorComponent operator = App.engine.createComponent(VehicleOperatorComponent.class);
			e.add(operator);
		}
		
		//Car 
		if (cfg.car){
			logger.debug("  Attach CarComponent");
			CarComponent car = App.engine.createComponent(CarComponent.class);
			car.speed = cfg.speed;
			car.downForce = cfg.downForce;
			car.frontWheelDrive = cfg.frontWheelDrive;
			car.rearWheelDrive = cfg.rearWheelDrive;
			if (!car.frontWheelDrive && !car.rearWheelDrive){
				car.frontWheelDrive = true;
			}
			e.add(car);
		}
		
		//Thruster
		if (cfg.thruster){
			logger.debug("  Attach ThrusterComponent");
			ThrusterComponent thruster = App.engine.createComponent(ThrusterComponent.class);
			thruster.power = cfg.thrustPower;
			thruster.duration = cfg.thrustDuration;
			thruster.delay = cfg.thrustDelay;
			thruster.topSpeed = cfg.thrustSpeed;
			e.add(thruster);
		}
		
		//Leader
		if (cfg.leader){
			logger.debug("  Attach LeaderComponent");
			LeaderComponent leader = App.engine.createComponent(LeaderComponent.class);
			e.add(leader);
		}
		
		//Sticky
		if (!cfg.stickyFixture.equals("")){
			logger.debug("  Attach StickyComponent");
			StickyComponent sticky = App.engine.createComponent(StickyComponent.class);
			sticky.fixtureName = cfg.stickyFixture;
			e.add(sticky);
		}
		
		//Melee
		if (cfg.meleeDamage > 0){
			logger.debug("  Attach MeleeComponent");
			MeleeComponent melee = App.engine.createComponent(MeleeComponent.class);
			melee.damage = cfg.meleeDamage;
			melee.range = cfg.meleeRange;
			melee.coolDown = cfg.meleeCoolDown;
			e.add(melee);
		}
		
		if (cfg.ledgeHanger){
			logger.debug("  Attach LedgeHang");
			LedgeHangComponent ledge = App.engine.createComponent(LedgeHangComponent.class);
			e.add(ledge);
		}
		
		App.engine.addEntity(e);
		logger.debug("Entity #" + e.getId() + " finished building.");
		return e;
	}

	public Entity createHumanoid(EntityConfig config){
		Entity e = App.engine.createEntity();
		PhysicsComponent physics = App.engine.createComponent(PhysicsComponent.class);
		SpriteComponent sprite = App.engine.createComponent(SpriteComponent.class);
		
		//Physics
		float size = config.size > 0 ? config.size : .5f,
			  density = .1f, friction = .1f, restitution = 0;
		
		//Main body
		BodyDef bd = new BodyDef();
		bd.fixedRotation = true;
		bd.type = BodyType.DynamicBody;
		BodyModel body = new BodyModel("body",bd);
		
		//Vision Body
		bd.fixedRotation = false;
		BodyModel visionBody = new BodyModel("visionBody",bd);
		

		//Main Body Fixture
		float[] verts = {size/3,size,-size/3,size,0,-size};
		PolygonShape polygon = new PolygonShape();
		polygon.set(verts);
		FixtureDef fd = Box2dHelper.fixtureDef(density, friction, restitution, polygon ,false);
		fd.filter.categoryBits = CollisionBits.Character;
		fd.filter.maskBits = CollisionBits.Mask_Character;
		body.fixtures.add(new FixtureModel(fd));
		
		//Arm fixture
		/*
		bd.fixedRotation = false;
		polygon.setAsBox(size/4, size/8, new Vector2(size/8,size/16),0);
		fd.shape = polygon;
		visionBody.fixtures.add(new FixtureModel(fd));
		*/
		
		//Vision Sensor
		//fd = Box2dHelper.circleFixtureDef(config.viewDistance,new Vector2(),0,0,0,true);
		CircleShape circle = new CircleShape();
		circle.setRadius(config.viewDistance);
		fd.shape = circle;
		fd.isSensor = true;
		fd.density = 0;
		fd.filter.categoryBits = CollisionBits.VisionSensor;
		fd.filter.maskBits = CollisionBits.Mask_VisionSensor;
		visionBody.fixtures.add(new FixtureModel("visionSensor",fd));
		
		//Static Sensor Fixtures
		fd.shape = polygon;
		fd.filter.categoryBits = CollisionBits.TouchSensor;
		fd.filter.maskBits = CollisionBits.Mask_TouchSensor;
		
		polygon.setAsBox(.05f, size/3f,new Vector2(-size,0),0);
		body.fixtures.add(new FixtureModel("leftSensor",fd));
		polygon.setAsBox(.05f,size/3f,new Vector2(size,0),0);
		body.fixtures.add(new FixtureModel("rightSensor",fd));
		polygon.setAsBox(size/2,.05f,new Vector2(0,size),0);
		body.fixtures.add(new FixtureModel("topSensor",fd));
		polygon.setAsBox(size/2, .05f, new Vector2(0,-size),0);
		body.fixtures.add(new FixtureModel("bottomSensor",fd));
		polygon.setAsBox(size/2, .05f, new Vector2(-size,size),0);
		body.fixtures.add(new FixtureModel("topLeftSensor",fd));
		polygon.setAsBox(size/2, .05f, new Vector2(size,size),0);
		body.fixtures.add(new FixtureModel("topRightSensor",fd));
		
		polygon.setAsBox(size/2, .05f, new Vector2(-size,-size),0);
		body.fixtures.add(new FixtureModel("bottomLeftSensor",fd));
		polygon.setAsBox(size/2, .05f, new Vector2(size,-size),0);
		body.fixtures.add(new FixtureModel("bottomRightSensor",fd));

		//Add Bodies
		physics.addBody(body);
		physics.addBody(visionBody);
		physics.setPrimaryBody("body");		
		
		//Vision Joint
		/*
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.initialize(physics.getBody("body"),physics.getBody("visionBody"),physics.getPosition());
		physics.addJoint(new JointModel("visionJoint",jd,"body","visionBody"));
		*/
		
		//use 'images' field (deserializes to an objectMap<bodyName,stringName>)
		for (ObjectMap.Entry<String,String> bodySpritePair : config.images.entries()){
			String bodyName = bodySpritePair.key,
				   imgName = bodySpritePair.value;
			Texture t = App.assets.getTexture(imgName);
			if (t != null){
				Sprite s = new Sprite(t);		
				s.setOriginCenter();
				sprite.spriteMap.put(bodyName,s);
			}			
		}

		e.add(sprite)
		 .add(physics);
		
		return e;	
	}

	public Entity createHelicopter(EntityConfig config){
		
		Entity e = App.engine.createEntity();
		PhysicsComponent physics = App.engine.createComponent(PhysicsComponent.class);
		SpriteComponent sprite = App.engine.createComponent(SpriteComponent.class);
		
		// Physics 
		float size = config.size > 0 ? config.size : 1, 
				     density = .2f, friction = .1f, restitution = 0, linearDamping = .65f;
		
		//Main Body	
		BodyDef bd = new BodyDef();
		bd.fixedRotation = true;
		bd.type = BodyType.DynamicBody;
		bd.linearDamping = linearDamping;
		BodyModel bodyModel = new BodyModel("body",bd);
		
		
		//Main Fixture
		//FixtureDef fd = Box2dHelper.circleFixtureDef(size,new Vector2(),density,friction,restitution,false);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size*2, size);
		FixtureDef fd = new FixtureDef();
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		fd.shape = shape;
		fd.filter.categoryBits = CollisionBits.Character;
		fd.filter.maskBits = CollisionBits.Mask_Character;
		bodyModel.fixtures.add(new FixtureModel("bodyFixture",fd));
		
		//Ground Sensor
		Vector2 sensorDimensions = new Vector2(size/2,.05f),
				sensorPosition = new Vector2(0,-size);
		fd = Box2dHelper.rectangleFixtureDef(sensorPosition,sensorDimensions,0,0,0,true);
		fd.filter.categoryBits = CollisionBits.TouchSensor;
		fd.filter.maskBits = CollisionBits.Mask_TouchSensor;
		bodyModel.fixtures.add(new FixtureModel("bottomSensor",fd));
		
		//Visual Sensor
		bd.type = BodyType.KinematicBody;
		bd.fixedRotation = false;
		BodyModel visionBodyModel = new BodyModel("visionBody",bd);
		fd = Box2dHelper.circleFixtureDef(config.viewDistance,new Vector2(),0,0,0,true); 
		fd.filter.categoryBits = CollisionBits.VisionSensor;
		fd.filter.maskBits = CollisionBits.Mask_VisionSensor;
		visionBodyModel.fixtures.add(new FixtureModel("visionSensor",fd));
		
		//Add bodies 
		physics.addBody(bodyModel);
		physics.addBody(visionBodyModel);
		physics.setPrimaryBody("body");

		//Vision Joint
		/*
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.initialize(physics.getBody("body"),physics.getBody("visionBody"),physics.getPosition());
		Joint j = App.engine.systems.physics.world.createJoint(jd);
		physics.addJoint(j);
		*
		*/
		//physics.addJoint(new JointModel("visionJoint",jd,"body","visionBody"));
		
		//Sprite 
		Texture t = App.assets.getTexture(config.image);
		if (t != null){
			Sprite s = new Sprite(t);		
			s.setOriginCenter();
			sprite.spriteMap.put("body", s);
		}
		
		e.add(sprite)
		 .add(physics);
		
		return e;	
	}

	public Entity createCar(EntityConfig config){
		
		Entity e = App.engine.createEntity();
		PhysicsComponent physics = App.engine.createComponent(PhysicsComponent.class);
		SpriteComponent sprite = App.engine.createComponent(SpriteComponent.class);
		
		// Physics 
		float size = config.size, density = .1f, friction = .5f, restitution = .01f, linearDamping = 1;
		
		//Main Body
		BodyDef bd = new BodyDef();
		bd.fixedRotation = false;
		bd.type = BodyType.DynamicBody;
		bd.linearDamping = linearDamping;
		BodyModel bodyModel = new BodyModel("body",bd);
	
		
		//Trapezoid body
		FixtureDef fd = new FixtureDef();
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		PolygonShape polyShape = new PolygonShape();
		float[] verts = {-size/2,size/2,
				         -size * 1.7f,-size/2,
				         size * 1.7f,-size/2,
				         size/2,size/2};
		polyShape.set(verts);
		fd.shape = polyShape;
		fd.filter.categoryBits = CollisionBits.Character;
		fd.filter.maskBits = CollisionBits.Mask_Character;
		bodyModel.fixtures.add(new FixtureModel(fd));
		
		
		//Wheels
		fd.density = .7f;
		fd.friction = 1.5f;
		BodyModel leftWheelModel = new BodyModel("leftWheel",bd),
				  rightWheelModel = new BodyModel("rightWheel",bd);
		
		CircleShape shape = new CircleShape();
		shape.setPosition(new Vector2(-size * .75f,-size * .5f));
		shape.setRadius(size/2);
		fd.shape = shape;
		leftWheelModel.fixtures.add(new FixtureModel(fd));
		shape.setPosition(new Vector2(size * .75f,-size * .5f));
		fd.shape = shape;
		rightWheelModel.fixtures.add(new FixtureModel(fd));
	
		//Vision Body / Sensor
		bd.type = BodyType.KinematicBody;
		bd.fixedRotation = false;
		BodyModel visionBodyModel = new BodyModel("visionBody",bd);
		fd = Box2dHelper.circleFixtureDef(config.viewDistance,new Vector2(),0,0,0,true); 
		fd.filter.categoryBits = CollisionBits.VisionSensor;
		fd.filter.maskBits = CollisionBits.Mask_VisionSensor;
		visionBodyModel.fixtures.add(new FixtureModel("visionSensor",fd));
		
		//Create Physics
		physics.addBody(bodyModel);
		physics.addBody(leftWheelModel);
		physics.addBody(rightWheelModel);
		physics.addBody(visionBodyModel);

		physics.setPrimaryBody("body");

		//Wheel Joints
		WheelJointDef wjd = new WheelJointDef();
		wjd.maxMotorTorque = config.torque;
		wjd.frequencyHz = config.suspension;
		
		wjd.initialize(physics.getBody("body"), physics.getBody("leftWheel"), physics.getBody("leftWheel").getWorldCenter(),new Vector2(0,1));
		physics.addJoint(new JointModel("leftWheelJoint",wjd,"body","leftWheel"));
		
		wjd.initialize(physics.getBody("body"), physics.getBody("rightWheel"), physics.getBody("rightWheel").getWorldCenter(),new Vector2(0,1));
		physics.addJoint(new JointModel("rightWheelJoint",wjd,"body","rightWheel"));
		
		
		//Vision Joint
		/*
		RevoluteJointDef rjd = new RevoluteJointDef();
		rjd.initialize(physics.getBody("body"),physics.getBody("visionBody"),physics.getPosition());
		physics.addJoint(new JointModel("visionJoint",rjd,"body","visionBody"));
		*/
		
		//use 'images' field (deserializes to an objectMap<bodyName,stringName>)
		for (ObjectMap.Entry<String,String> bodySpritePair : config.images.entries()){
			String bodyName = bodySpritePair.key,
				   imgName = bodySpritePair.value;
			Texture t = App.assets.getTexture(imgName);
			if (t != null){
				Sprite s = new Sprite(t);		
				s.setOriginCenter();
				sprite.spriteMap.put(bodyName,s);
			}			
		}
	
		 e.add(sprite);
		 e.add(physics);
		
		return e;	
	}

	public Entity createBullet(EntityConfig config){
		
		Entity e = App.engine.createEntity();
		PhysicsComponent physics = App.engine.createComponent(PhysicsComponent.class);
		SpriteComponent sprite = App.engine.createComponent(SpriteComponent.class);
		
		//Physics
		float density = .1f, restitution = .1f, friction = .5f;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.bullet = true;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(config.size, config.size/4);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		fd.isSensor = false;
		fd.filter.categoryBits = CollisionBits.Bullet;
		fd.filter.maskBits = CollisionBits.Mask_Bullet;
		
		BodyModel bodyModel = new BodyModel("body",bd);
		bodyModel.fixtures.add(new FixtureModel("fixture",fd));
		physics.addBody(bodyModel);
		
		//Sprite
		Texture t = App.assets.getTexture(config.image);
		if (t != null){
			Sprite s = new Sprite(App.assets.getTexture(config.image));
			s.setOriginCenter();
			sprite.spriteMap.put("body",s);
		}
		
		e.add(sprite)
		 .add(physics);
		
		return e;	
	}
	
	public Entity createBall(EntityConfig config){
		
		Entity e = App.engine.createEntity();
		PhysicsComponent physics = App.engine.createComponent(PhysicsComponent.class);
		SpriteComponent sprite = App.engine.createComponent(SpriteComponent.class);
		
		//Physics
		float density = .6f, restitution = .1f, friction = .5f;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;

		CircleShape shape = new CircleShape();
		shape.setRadius(config.size/2);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		fd.isSensor = false;
		//fd.filter.categoryBits = CollisionBits;
		//fd.filter.maskBits = CollisionBits.Mask_Bullet;
		
		BodyModel bodyModel = new BodyModel("body",bd);
		bodyModel.fixtures.add(new FixtureModel("fixture",fd));
		physics.addBody(bodyModel);
		
		//Sprite
		Texture t = App.assets.getTexture(config.image);
		if (t != null){
			Sprite s = new Sprite(App.assets.getTexture(config.image));
			s.setOriginCenter();
			sprite.spriteMap.put("body",s);
		}
		
		e.add(sprite)
		 .add(physics);
		
		return e;	
	}
}