package com.deftwun.zombiecopter.box2dJson;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

public class BodyModel{
	private transient final Logger logger = new Logger("BodyModel",Logger.INFO);
	public String name = "body";
	public boolean active,awake,allowSleep,fixedRotation,bullet;
	public float angle,linearDamping,angularDamping,angularVelocity,gravityScale;
	public Vector2 position = new Vector2(),linearVelocity = new Vector2();
	public BodyType type = BodyType.DynamicBody;
	public Array<FixtureModel> fixtures = new Array<FixtureModel>();

	public BodyModel(){
		logger.debug("Initializing");
	}
	
	public BodyModel(String theName){
		logger.debug("Initializing with name:" + theName);
		name = theName;
	}
	
	public BodyModel(Body b){
		logger.debug("Initializing with body");
		this.fromBody(b);
	}
	
	public BodyModel(BodyDef bd){
		logger.debug("Initializing with bodyDef");
		this.fromBodyDef(bd);
	}
	
	public BodyModel(String theName, BodyDef bd){
		logger.debug("Initializing with bodyDef. name: " + theName);
		name = theName;
		this.fromBodyDef(bd);
	}

	public BodyModel(String theName, Body b) {
		logger.debug("Initializing with body. name: " + theName);
		name = theName;
		this.fromBody(b);
	}
	
	public Body toBody(World world){
		logger.debug("to body");
		return world.createBody(this.toBodyDef());
	}
	
	public BodyDef toBodyDef(){
		logger.debug("to bodyDef");
		BodyDef bd = new BodyDef();
		bd.type = type;
		bd.active = active;
		bd.allowSleep = allowSleep;
		bd.angle = angle;
		bd.angularDamping = angularDamping;
		bd.angularVelocity = angularVelocity;
		bd.awake = awake;
		bd.bullet = bullet;
		bd.fixedRotation = fixedRotation;
		bd.gravityScale = gravityScale;
		bd.linearDamping = linearDamping;
		bd.linearVelocity.set(linearVelocity);
		bd.position.set(position);
		return bd;
	}
	
	private void fromBodyDef(BodyDef bd){
		logger.debug("from bodyDef");
		active = bd.active;
		awake = bd.awake;
		allowSleep = bd.allowSleep;
		fixedRotation = bd.fixedRotation;
		bullet = bd.bullet;
		angle = bd.angle;
		gravityScale = bd.gravityScale;
		angularVelocity = bd.angularVelocity;
		angularDamping = bd.angularDamping;
		linearDamping = bd.linearDamping;
		linearVelocity.set(bd.linearVelocity);
		position.set(bd.position);
		type = bd.type;
	}
	
	
	public void fromBody(Body b){
		logger.debug("from body");
		active = b.isActive();
		awake = b.isAwake();
		allowSleep = b.isSleepingAllowed();
		fixedRotation = b.isFixedRotation();
		bullet = b.isBullet();
		angle = b.getAngle();
		gravityScale = b.getGravityScale();
		linearDamping = b.getLinearDamping();
		angularVelocity = b.getAngularVelocity();
		angularDamping = b.getAngularDamping();
		linearVelocity.set(b.getLinearVelocity());
		position.set(b.getPosition());
		type = b.getType();
		this.addFixtures(b);
	}
	
	private void addFixtures(Body b){
		Array<Fixture> realFixtures = b.getFixtureList();
		logger.debug("add " + realFixtures.size + " fixtures");
		for (Fixture f : realFixtures){
			this.fixtures.add(new FixtureModel(f));
		}
	}	
}