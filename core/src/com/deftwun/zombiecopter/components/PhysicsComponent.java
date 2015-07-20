package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.box2dJson.BodyModel;
import com.deftwun.zombiecopter.box2dJson.JointModel;
import com.deftwun.zombiecopter.box2dJson.PhysicsScene;
import com.deftwun.zombiecopter.box2dJson.PhysicsSceneModel;

/*

	This class contains more logic than most components. 
	Its built around a 'PhysicsScene' which is basically a container for
	Bodies, fixtures, and joints. 
	
*/
public class PhysicsComponent extends Component implements Serializable, Poolable{

	public Entity ownerEntity = null;
	public String collisionEffect = "";
	
	private PhysicsScene scene = new PhysicsScene(App.engine.systems.physics.world);
	private float collisionNormal;
	private Logger logger = new Logger("PhysicsComponent",Logger.INFO);
	
	public PhysicsComponent(){
		collisionEffect = "";
		collisionNormal = 0;
		ownerEntity = null;
		scene.setPhysicsSceneListener(App.engine.systems.physics);
	}
	
	//Add existing body
	public String addBody(String name,Body b){
		logger.debug("Add body");
		b.setUserData(this);
		return scene.addBody(name,b);
	}
	
	//Add Existing body
	public String addBody(Body b){
		logger.debug("Add body");
		b.setUserData(this);
		return scene.addBody(b);
	}
	
	//Create and add body using a BodyModel. A unique name is automatically generated and returned
	public String addBody(BodyModel bm){
		logger.debug("Add body model");
		Body b = scene.createBody(bm);
		b.setUserData(this);
		return bm.name;
	}
	
	public String addJoint(JointModel jm){
		logger.debug("Add joint model");
		logger.error("addJoint(JointModel jm) may not function correctly");
		scene.createJoint(jm);
		return jm.name;
	}
		
	public void addJoint(Joint j) {
		scene.addJoint(j);
	}	
	
	//Set the primary body
	public void setPrimaryBody(String name){
		scene.setPrimaryBody(name);
	}
	
	//Get the primary body
	public Body getPrimaryBody(){
		return scene.getPrimaryBody();
	}
	
	//Get the name of primary body
	public String getPrimaryBodyName(){
		return scene.getName(getPrimaryBody());
	}
		
	//Get body by name
	public Body getBody(String name){
		return scene.getBody(name);
	}
	
	//Get an array of all attached bodies
	public Array<Body> getBodies(){
		return scene.getBodies();
	}
	
	//Get a specific joint
	public Joint getJoint(String name){
		return scene.getJoint(name);
	}
	
	//Get all joints
	public Array<Joint> getJoints(){
		return scene.getJoints();
	}
	//Get fixture by name
	public Fixture getFixture(String name){
		return scene.getFixture(name);
	}
	
	//Applies filter to ALL fixtures that have been added so far
	public void setFilter(Filter filter){
		for (Fixture f : scene.getFixtures()){
			f.setFilterData(filter);
		}
	}
	
	//Applies filter to ALL fixtures that have been added so far
	public void setFilter(short category, short mask){
		for (Fixture f : scene.getFixtures()){
			Filter filter = f.getFilterData();
			filter.categoryBits = category;
			filter.maskBits = mask;
			f.setFilterData(filter);
		}
	}
	
	//Get the position of the primary body
	//TODO maybe sum the body AABB's together and return the center of that?
	public Vector2 getPosition(){
		if (scene.getPrimaryBody() != null) return scene.getPrimaryBody().getWorldCenter();
		return new Vector2();
	}
	
	//Set the position of the primary body. All other bodies are moved relative to that
	public void setPosition(Vector2 position) {
		if (scene.getPrimaryBody() == null) return;
		Vector2 deltaP = new Vector2(position).sub(scene.getPrimaryBody().getWorldCenter());
		for (Body b : scene.getBodies()){
			b.setTransform(b.getWorldCenter().add(deltaP), b.getAngle());
		}		
	}	
	
	//Set the rotation of the primary body. All other bodies are rotated relative to that. (NOT YET!)
	// TODO: OTHER BODIES ARE NOT ALTERED
	public void setRotation(float angleDegrees){
		if (scene.getPrimaryBody() != null){
			scene.getPrimaryBody().setTransform(scene.getPrimaryBody().getWorldCenter().x,scene.getPrimaryBody().getWorldCenter().y,(float) Math.toRadians(angleDegrees));
		}
	}
	
	//Get the rotation of the primary body
	public float getRotation(){
		if (scene.getPrimaryBody() != null)
			return (float) Math.toDegrees(scene.getPrimaryBody().getAngle());
		return 0;
	}
	
	//Set velocity of all bodies
	public void setLinearVelocity(Vector2 vel){
		for (Body b : scene.getBodies()){
			b.setLinearVelocity(vel);
		}
	}
	
	//Get velocity of primary body
	public Vector2 getLinearVelocity(){
		if (scene.getPrimaryBody() != null){
			return scene.getPrimaryBody().getLinearVelocity();
		}
		return new Vector2();
	}
	
	//Get the combined mass of all bodies 
	public float getMass(){
		float mass = 0;
		for (Body b : scene.getBodies())
			mass += b.getMass();
		return mass;
	}
	
	//Destroy everything
	public void destroy(){
		logger.debug("Destroy Everything");
		scene.destroy();
		collisionNormal = 0;
	}
	
	//Destroy specific body
	public void destroyBody(String name){	
		logger.debug("Destroy body: " + name);
		scene.destroyBody(name);
	}
	
	//Destroy specific fixture 
	public void destroyFixture(String name){
		logger.debug("Destroy fixture: " + name);
		scene.destroyFixture(name);
	}
	
	//Destroy Joint
	public void destroyJoint(String name){
		logger.debug("Destroy Joint: " + name);
		scene.destroyJoint(name);
	}
	
	//Get the total impact force that has been applied since last update
	public float getCollisionNormal(){
		return collisionNormal;
	}
	
	//Used by physics system during BeginContact
	public void addCollisionNormal(float normal) {
		collisionNormal += normal;
	}
	
	//Used by physics system during update
	public void clearCollisionNormal(){
		collisionNormal = 0;
	}
	
	//Json Write
	@Override
	public void write(Json json) {		
		logger.debug("Serializing..");
		PhysicsConfig config = new PhysicsConfig();
		config.collisionEffect = collisionEffect;
		config.sceneModel = scene.toSceneModel();
		json.writeFields(config);
	}
	
	//Json read
	@Override
	public void read(Json json, JsonValue jsonData) {
		logger.debug("Deserializing");
		PhysicsConfig config = json.fromJson(PhysicsConfig.class, jsonData.toString());
		collisionEffect = config.collisionEffect;
		this.buildFromModel(config.sceneModel);
	}

	//Pooled component reset
	@Override
	public void reset() {
		collisionEffect = "";
		collisionNormal = 0;
		ownerEntity = null;
		scene.setPhysicsSceneListener(App.engine.systems.physics);
		this.destroy();		
	}
	
	//Create everything from a SceneModel. (SceneModels are easily serialized)
	private void buildFromModel(PhysicsSceneModel physicsModel){
		logger.debug("Build from physics scene model");
		scene.createFromModel(physicsModel);
		for (Body b : scene.getBodies()){
			b.setUserData(this);
		}
		
	}

	//Used internally for serialization / deserialization
	private static class PhysicsConfig{
		protected String collisionEffect = "";
		protected PhysicsSceneModel sceneModel; 
	}


}
