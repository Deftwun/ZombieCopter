package com.deftwun.zombiecopter;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;


public class Box2dHelper{
	
	public static BodyDef bodyDef(BodyDef.BodyType type, Vector2 position){
		BodyDef bd = new BodyDef();
		bd.type = type;
		bd.position.set(position);
		return bd;
	}
	
	public static FixtureDef fixtureDef(float density,float friction,float restitution, boolean isSensor){
		return fixtureDef(density,friction,restitution,null,isSensor);
	}
	
	public static FixtureDef fixtureDef(float density,float friction,float restitution, Shape shape, boolean isSensor){
		FixtureDef fd = new FixtureDef();
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		fd.shape = shape;
		fd.isSensor = isSensor;
		return fd;
	}
	
	public static FixtureDef circleFixtureDef(float radius, Vector2 position, float density, float friction, float restitution, boolean isSensor){
		CircleShape s = new CircleShape();
		s.setPosition(position);
		s.setRadius(radius);
		FixtureDef fd = fixtureDef(density,friction,restitution,s,isSensor);
		return fd;
	}
	
	public static Fixture circleFixture(Body b, float radius, float density, float friction, float restitution, boolean isSensor){
		return circleFixture(b,radius,0,0,density,friction,restitution,isSensor);
	}
	
	public static Fixture circleFixture(Body b, float radius, float x, float y, float density, float friction, float restitution, boolean isSensor){
		CircleShape s = new CircleShape();
		s.setPosition(new Vector2(x,y));
		s.setRadius(radius);
		FixtureDef fd = fixtureDef(density,friction,restitution,s,isSensor);
		Fixture f = b.createFixture(fd);
		s.dispose();
		return f;
	}
		
	public static FixtureDef rectangleFixtureDef(Vector2 position, Vector2 dimensions, float density,float friction,float restitution,boolean isSensor){
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(dimensions.x,dimensions.y,new Vector2(position.x,position.y),0);
		FixtureDef fd = fixtureDef(density,friction,restitution,shape,isSensor);
		return fd;
	}
	public static Fixture rectangleFixture(Body b, Vector2 dimensions, float density, float friction, float restitution, boolean isSensor){
		return rectangleFixture(b,new Vector2(0,0),dimensions,density,friction,restitution,isSensor);
	}
	

	
	public static Fixture rectangleFixture(Body b, Vector2 position, Vector2 dimensions, float density, float friction, float restitution, boolean isSensor){
		PolygonShape s = new PolygonShape();
		s.setAsBox(dimensions.x, dimensions.y, position, 0);
		FixtureDef fd = fixtureDef(density,friction,restitution,s,isSensor);
		Fixture f = b.createFixture(fd);
		s.dispose();
		return f;
	}
	
	public static Fixture triangleFixture(Body b, Vector2 dimensions, float density, float friction, float restitution, boolean isSensor){
		FixtureDef fd = triangleFixtureDef(dimensions, density, friction, restitution, isSensor);
		Fixture f = b.createFixture(fd);
		return f;
	}
	
	public static FixtureDef triangleFixtureDef(Vector2 dimensions, float density, float friction, float restitution, boolean isSensor) {
		/*
		float[] verts = {-dimensions.y/2, dimensions.x/2,
                		 -dimensions.y/2,-dimensions.x/2,
                		  dimensions.y/2,0};
		*/
		float[] verts = {dimensions.x,dimensions.y,
						 -dimensions.x,dimensions.y,
						 0,-dimensions.y};
		

		PolygonShape s = new PolygonShape();
		s.set(verts);
		return fixtureDef(density,friction,restitution,s,isSensor);
	}
	
	public static Body circleBody(World world, BodyDef.BodyType type, Vector2 position, float radius, float density,float friction,float restitution,boolean isSensor){
		Body b = world.createBody(bodyDef(type,position));
		circleFixture(b,radius,density,friction,restitution,isSensor);
		return b;
	}
	
	public static Body rectangleBody(World world,BodyDef.BodyType type, Vector2 position, Vector2 dimensions, float density,float friction, float restitution,boolean isSensor){
		Body b = world.createBody(bodyDef(type,position));
		rectangleFixture(b,dimensions,density,friction,restitution,isSensor);
		return b;
	}
	
	public static Body triangleBody(World world,BodyDef.BodyType type, Vector2 position, Vector2 dimensions, float density,float friction, float restitution,boolean isSensor){
		Body b = world.createBody(bodyDef(type,position));
		triangleFixture(b,dimensions,density,friction,restitution,isSensor);
		return b;
	}
	
	public static Body boxBody(World world,BodyDef.BodyType type, Vector2 position, Vector2 dimensions, float thickness,float density, float friction, float restitution,boolean isSensor){
		Body b = world.createBody(bodyDef(type,position));
		PolygonShape s = new PolygonShape();
		            
		FixtureDef fd = fixtureDef(density,friction,restitution,s,isSensor);
		//top
		s.setAsBox(dimensions.x, thickness, new Vector2(position.x,position.y + dimensions.y), 0);
		fd.shape = s;
		b.createFixture(fd);
		
		//Bottom
		s.setAsBox(dimensions.x, thickness, new Vector2(position.x,position.y - dimensions.y), 0);
		fd.shape = s;
		b.createFixture(fd);
		
		//Left
		s.setAsBox(thickness, dimensions.y, new Vector2(position.x - dimensions.x,position.y), 0);
		fd.shape = s;
		b.createFixture(fd);
		
		//Right
		s.setAsBox(thickness, dimensions.y, new Vector2(position.x + dimensions.x,position.y), 0);
		fd.shape = s;
		b.createFixture(fd);
		
		s.dispose();
		
		return b;
	}
	
	private static float getRandomElevation(float prevElevation, float dY,float min, float max){
		float e = prevElevation;
		Random rand = new Random();
		switch (rand.nextInt(2)){
		case 0: e += dY; break;
		case 1: e -= dY; break;
		}
		if (e < min) e = min;
		if (e > max) e = max;
		return e;
	}
	
	public static Body randomCircleBody(World world,Vector2 position,float dX,float dY,float minY,float maxY,int randomness){	
		//TODO: Should use a logger class here
		if (360%dX != 0) System.out.println("Creating random planet: dx must be multiple of 360");
		
		assert(360%dX == 0); // must be multiple of 360
		int steps = (int) (360 / dX)-1;
		Random rand = new Random();
		float prevElevation = (minY + maxY) / 2;
		
		boolean randomModeToggle = false;
		ArrayList<Vector2> verts = new ArrayList<Vector2>();
		
		for (int x = 1; x < steps; x++){
			Vector2 h = new Vector2();
			if (randomModeToggle)
				h.set(0,rand.nextFloat() * (maxY - minY) + minY);
			else
				h.set(0,getRandomElevation(prevElevation,dY,minY,maxY));
			
			if (randomness!=0){
				if (x%randomness == 0){
					randomModeToggle = !randomModeToggle;
				}
			}
			
			prevElevation = h.y;
			h.rotate(x * dX);
			verts.add(h);
		}
		
		ChainShape shape = new ChainShape();
		float[] fVerts = new float[verts.size() * 2];
		for (int x=0; x < verts.size(); x++){
			fVerts[x*2] = verts.get(x).x;
			fVerts[(x*2) + 1] = verts.get(x).y;
		}
		shape.createLoop(fVerts);

		FixtureDef fd = new FixtureDef();
		fd.density = 10;
		fd.friction = 10;
		fd.restitution = 0f;
		fd.shape = shape;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyDef.BodyType.StaticBody;
		bd.position.set(position);
		
		Body body = world.createBody(bd);
		body.createFixture(fd);
		return body;
	}


}