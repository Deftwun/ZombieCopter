package com.deftwun.zombiecopter.box2dJson;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class FixtureModel{
	public String name = "default";
	//public final FixtureDef fixtureDef = new FixtureDef();
	
	Filter filter = new Filter();
	boolean sensor;
	float friction,density,restitution;	
	public ShapeModel shapeModel = null;
	
	public FixtureModel(){}
	
	public FixtureModel(String theName){
		name = theName;
	}
	
	public FixtureModel(Fixture f){
		fromFixture(f);
	}
	
	public FixtureModel(FixtureDef fd){
		fromFixtureDef(fd);
	}
	
	public FixtureModel(String theName, FixtureDef fd){
		name = theName;
		fromFixtureDef(fd);
	}
	
	public FixtureModel(String theName, Fixture f){
		name = theName;
		fromFixture(f);
	}
	
	public Fixture toFixture(Body b){
		Fixture f = null;
		FixtureDef fd = this.toFixtureDef();
		if (fd != null) f = b.createFixture(fd);
		return f;
	}
	
	public FixtureDef toFixtureDef(){
		FixtureDef fd = null;
		if (shapeModel != null){
			fd = new FixtureDef();
			fd.shape = shapeModel.toShape();
			fd.isSensor= sensor;
			fd.density = density;
			fd.friction = friction;
			fd.filter.categoryBits = filter.categoryBits;
			fd.filter.groupIndex = filter.groupIndex;
			fd.filter.maskBits = filter.maskBits;	
		}	
		return fd;
	}
	
	public void fromFixture(Fixture f){
		shapeModel = new ShapeModel(f.getShape());
		Filter filterData = f.getFilterData();
		filter.categoryBits = filterData.categoryBits;
		filter.maskBits = filterData.maskBits;
		filter.groupIndex = filterData.groupIndex;
		sensor = f.isSensor();
		density = f.getDensity();
		friction = f.getFriction();
		restitution = f.getRestitution();
	}
	
	public void fromFixtureDef(FixtureDef fd){
		shapeModel = new ShapeModel(fd.shape);
		Filter filterData = fd.filter;
		filter.categoryBits = filterData.categoryBits;
		filter.maskBits = filterData.maskBits;
		filter.groupIndex = filterData.groupIndex;
		sensor = fd.isSensor;
		density = fd.density;
		friction = fd.friction;
		restitution = fd.restitution;
	}
	
}