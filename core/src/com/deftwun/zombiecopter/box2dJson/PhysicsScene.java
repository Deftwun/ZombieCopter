package com.deftwun.zombiecopter.box2dJson;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;


/*
	Manages multiple bodies, joints (..planned), and fixtures. Also able to be
	serialized and rebuilt from json data (..using PhysicsModel,BodyModel,FixtureModel,etc..)
*/

public class PhysicsScene{
	private Logger logger = new Logger("PhysicsScene",Logger.INFO);
	private static long uniqueId = 0;
	private Body primaryBody = null;
	private World world = null;
	private ObjectMap<String, Joint> joints = new ObjectMap<String,Joint>();
	private ObjectMap<String, Body> bodies = new ObjectMap<String,Body>();
	private ObjectMap<String, Fixture> fixtures = new ObjectMap<String,Fixture>();
	private PhysicsSceneListener listener = null;
	
	public interface PhysicsSceneListener{
		public void fixtureAdded(Fixture f);
		public void bodyAdded(Body b);
		public void jointAdded(Joint j);
		public void fixtureRemoved(Fixture f);
		public void bodyRemoved(Body b);
		public void jointRemoved(Joint j);
	}
	
	public PhysicsScene(World w){
		logger.debug("Creating scene using world");
		world = w;
	}
	
	public PhysicsScene(){
		logger.debug("Creating scene using new default world");
		world = new World(new Vector2(),true);
	}
	
	public void setPhysicsSceneListener(PhysicsSceneListener l){
		listener = l;
	}
	
	//Create everything from a PhysicsSceneModel
	public void createFromModel(PhysicsSceneModel model){
		logger.debug("create from model");
		this.destroy();
		
		for (BodyModel bm : model.bodyModels){
			createBody(bm);
		}
		for (JointModel jm : model.jointModels){
			createJoint(jm);
		}
		primaryBody = bodies.get(model.primaryBody,null);
		if (primaryBody == null && bodies.size > 0) 
			primaryBody = bodies.values().toArray().get(0);
	}
	
	public void setPrimaryBody(String name){
		logger.debug("Set primary body: " + name);
		primaryBody = bodies.get(name,null);
	}
	public Body getPrimaryBody(){
		return primaryBody;
	}

	//Create Body
	public Body createBody(BodyModel bm){
		logger.debug("create Body from model: " + bm.name);
		String name = uniqueBodyName(bm.name);
		Body body = bm.toBody(world);
		if (primaryBody == null){
			primaryBody = body;
		}
		bodies.put(name,body);
		if (listener != null) listener.bodyAdded(body);
		for (FixtureModel fm : bm.fixtures){
			Fixture f = fm.toFixture(body);
			fm.name = uniqueFixtureName(fm.name);
			fixtures.put(fm.name,f);
			if (listener != null) listener.fixtureAdded(f);
		}
		return body;
	}
	
	//Add Body
	public String addBody(Body b){
		String name = uniqueBodyName("body");
		this.addBody(name, b);
		return name;
	}
	
	//Add body 
	public String addBody(String name,Body b){
		logger.debug("Add body: " + name);
		if (primaryBody == null){
			primaryBody = b;
		}
		bodies.put(name,b);
		if (listener != null) listener.bodyAdded(b);
		for (Fixture f : b.getFixtureList()){
			String fixName = uniqueFixtureName("fixture");
			logger.debug("add fixture: " + fixName);
			fixtures.put(fixName,f);
			if (listener != null) listener.fixtureAdded(f);
		}
		return name;
	}
	
	//Create Joint
	public Joint createJoint(JointModel jm){
		
		String name = uniqueJointName(jm.name);
		logger.debug("Creating Joint from model: " + name + " = " + jm.bodyA + "+" + jm.bodyB);
		Body bodyA = bodies.get(jm.bodyA),
			 bodyB = bodies.get(jm.bodyB);
		
		if (bodyA == null) logger.error("Can't create Joint. Body: " + jm.bodyA + " not found in scene.");
		if (bodyB == null) logger.error("Can't create Joint. Body: " + jm.bodyB + " nof found in scene.");
		
		Joint joint = jm.toJoint(world,bodyA,bodyB);
		joints.put(name,joint);
		logger.debug("Joint count = " + joints.size);
		if (listener != null) listener.jointAdded(joint);
		return joint;
	}

	//Add joint
	public String addJoint(Joint j){
		String name = uniqueJointName("joint");
		return this.addJoint(name,j);
	}
	
	//Add joint
	public String addJoint(String name, Joint j){
		logger.debug("Joint added: " + name);
		
		joints.put(name,j);
		if (listener != null) listener.jointAdded(j);
		return name;
	}
	
	//Check if body exists in the scene
	public boolean hasBody(String name){
		return bodies.containsKey(name);
	}
	public boolean hasBody(Body b){
		return bodies.containsValue(b,true);
	}
	
	//Check if fixture exists in the scene
	public boolean hasFixture(String name){
		return fixtures.containsKey(name);
	}
	public boolean hasFixture(Fixture f){
		return fixtures.containsValue(f,true);
	}
	
	//Check if joint exists in the scene
	public boolean hasJoint(String name){
		return joints.containsKey(name);
	}
	public boolean hasJoint(Joint j){
		return joints.containsValue(j,true);
	}
	
	
	//Get name of body
	public String getName(Body b){
		for (ObjectMap.Entry<String,Body> entry : bodies){
			if (entry.value == b) return entry.key;
		}
		return "";
	}
	
	//Get name of fixture
	public String getName(Fixture f){
		for (ObjectMap.Entry<String,Fixture> entry : fixtures){
			if (entry.value == f) return entry.key;
		}
		return "";
	}
	
	//Get name of joint
	public String getName(Joint j){
		for (ObjectMap.Entry<String,Joint> entry : joints){
			if (entry.value == j) return entry.key;
		}
		return "";
	}	
	
	//Get body
	public Body getBody(String name){
		return bodies.get(name);
	}
	
	//Get fixture
	public Fixture getFixture(String name){
		return fixtures.get(name);
	}
	
	//Get joint
	public Joint getJoint(String name){
		return joints.get(name);
	}
	
	//Get all fixtures
	public Array<Fixture> getFixtures(){
		return fixtures.values().toArray();
	}
	
	//Get all bodies
	public Array<Body> getBodies(){
		return bodies.values().toArray();
	}
	
	//Get all joints
	public Array<Joint> getJoints(){
		return joints.values().toArray();
	}
	
	//Destroy everything
	public void destroy(){
		logger.debug(String.format("Destroying: %d fixtures, %d bodies, & %d joints...",
									fixtures.size,bodies.size,joints.size));
		
		for (Joint j : joints.values()){
			world.destroyJoint(j);
			if (listener != null) listener.jointRemoved(j);
			logger.debug("Joint Destroyed");
		}
		
		for (Body b : bodies.values()){
			for (Fixture f : b.getFixtureList()){
				b.destroyFixture(f);
				if (listener != null) listener.fixtureRemoved(f);
				logger.debug("Fixture Destroyed");
			}
			world.destroyBody(b);
			if (listener != null) listener.bodyRemoved(b);
			logger.debug("Body Destroyed");
		}

	
		fixtures.clear();
		bodies.clear();
		joints.clear();
		uniqueId = 0;
		primaryBody = null;
		logger.debug("---Destroyed");
	}
	
	//Destroy body
	public void destroyBody(String name){
		logger.debug("Destroy body: " + name);
		Body b = bodies.get(name,null);
		if (b != null){
			//Check if its the primary body. If so, then automatically set a new primary
			if (b == primaryBody){
				if (bodies.size > 0)
					primaryBody = (Body) bodies.values().toArray().get(0);
				else primaryBody = null;
			}
			
			world.destroyBody(b);
			bodies.remove(name);
			if (listener != null) listener.bodyRemoved(b);
		}
		else logger.debug("Could not destroy body: " + name + " not found");
	}
	
	//Destroy Fixture
	public void destroyFixture(String name){
		logger.debug("Destroy fixture: " + name);
		Fixture f = fixtures.get(name);
		if (f != null){
			f.getBody().destroyFixture(f);
			fixtures.remove(name);
			if (listener != null) listener.fixtureRemoved(f);
		}
		else logger.debug("Could not destroy fixture: " + name + " not found");
		
	}
	
	//Destroy joint
	public void destroyJoint(String name){
		logger.debug("Destroy Joint: " + name);
		Joint j = joints.get(name);	
		if (j != null){
			world.destroyJoint(j);
			joints.remove(name);
			if (listener != null) listener.jointRemoved(j);
		}
		else logger.debug("Could not destroy joint: " + name + " not found");
	}
	
	//Create Scene model
	public PhysicsSceneModel toSceneModel(){
		logger.debug("Creating Scene Model");
		PhysicsSceneModel physicsModel = new PhysicsSceneModel();		
		
		//Bodies
		for (ObjectMap.Entry<String,Body> bodyEntry : bodies.entries()){
			BodyModel bodyModel = new BodyModel(bodyEntry.key,bodyEntry.value);
			//clear un-named fixtures. We'll manually add them with names included
			bodyModel.fixtures.clear();
			
			//Fixtures
			for (ObjectMap.Entry<String,Fixture> fixtureEntry : fixtures.entries()){
				if (fixtureEntry.value.getBody() == bodyEntry.value){
					FixtureModel fixModel = new FixtureModel(fixtureEntry.key,fixtureEntry.value);
					bodyModel.fixtures.add(fixModel);
				}
			}
			physicsModel.bodyModels.add(bodyModel);
		}
		
		//Joints
		for (ObjectMap.Entry<String,Joint> jointEntry : joints.entries()){
			Joint j = jointEntry.value;
			JointModel jointModel = new JointModel(jointEntry.key,jointEntry.value,getName(j.getBodyA()), getName(j.getBodyB()));
			physicsModel.jointModels.add(jointModel);
		}
		return physicsModel;
	}
	
	//generates a unique body name using the given prefix
	private String uniqueBodyName(String prefix){
		String s = prefix;
		while (bodies.containsKey(prefix))
			s = prefix + uniqueId++;
		return s;
	}
	//generates a unique fixture name using the given prefix
	private String uniqueFixtureName(String prefix){
		String s = prefix;
		while (fixtures.containsKey(s)) s = prefix + uniqueId++;
		return s;
	}
	//generates a unique joint name using the given prefix
	private String uniqueJointName(String prefix){
		String s = prefix;
		while (fixtures.containsKey(s)) s = prefix + uniqueId++;
		return s;
	}

}