package com.deftwun.zombiecopter;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

public class RayCast implements RayCastCallback{
	
	public Fixture fixture = null;	
	public Body body = null;
	public PhysicsComponent physics = null;
	public Vector2 point = new Vector2(),
				   normal = new Vector2();
	private Vector2 tmp1 = new Vector2(),
					tmp2 = new Vector2();
	public float fraction;
	public boolean ignoreSensors = true;
	
	public boolean cast(World world,float x1,float y1,float x2,float y2){
		tmp1.set(x1,y1);
		tmp2.set(x2,y2);
		return cast(world,tmp1,tmp2);
	}
	
	public boolean cast(World world, Vector2 p1,float x2,float y2){
		tmp2.set(x2,y2);
		return cast(world,p1,tmp2);
	}
	
	public boolean cast(World world,PhysicsComponent phys0, PhysicsComponent phys1){
		reset();
		if (phys0.getPosition().dst(phys1.getPosition()) <= 0) return true;
		world.rayCast(this, phys0.getPosition(), phys1.getPosition());
		return fixture != null && physics == phys1;
	}
	
	public boolean cast(World world,PhysicsComponent phys0, Vector2 p1){
		reset();
		if (phys0.getPosition().dst(p1) <= 0) return true;
		world.rayCast(this, phys0.getPosition(), p1);
		return fixture == null;
	}
	
	public boolean cast(World world, Vector2 p1,PhysicsComponent phys2) {
		reset();
		return cast(world,p1,phys2.getPosition());
	}
	
	public boolean cast(World world,Vector2 p1, Vector2 p2){
		reset();
		if (p1.dst(p2) <= 0) return true;
		world.rayCast(this,p1,p2);
		return fixture == null;
	}
	
	public float reportRayFixture(Fixture f,Vector2 p,Vector2 n,float fra){
		if (f.isSensor() && ignoreSensors) return -1;
		fixture = f;
		body = f.getBody();
		physics = (PhysicsComponent)body.getUserData();
		point = p;
		normal = n;
		fraction = fra;
		return fra;
	}
	
	private void reset(){
		fixture = null;
		body = null;
		physics = null;
		point.set(0,0);
		normal.set(0,0);
		fraction = 0;
	}
}