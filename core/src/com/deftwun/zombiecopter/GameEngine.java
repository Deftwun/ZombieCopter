package com.deftwun.zombiecopter;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Logger;

public class GameEngine{
	
	private final Logger logger = new Logger("Engine", Logger.INFO);
	
	private boolean 	paused = false;

	private float 		timestep = 1f/60f , 
						timeAccumulator = 0,
						timeModifier = 1;
	
	public final float PIXELS_PER_METER = 32,
			           viewWidth = 800, 
			           viewHeight = 600;
	
	private final int 	entityPoolInitialSize = 1, 
						entityPoolMaxSize = 50, 
						componentPoolInitialSize = 1, 
						componentPoolMaxSize = 100;
	
	private final PooledEngine entityEngine =
				new PooledEngine(entityPoolInitialSize, 
								 entityPoolMaxSize,
								 componentPoolInitialSize,
								 componentPoolMaxSize);

	private Level level;
	public final UserInterface ui = new UserInterface();
	public final Rectangle entityBounds = new Rectangle(0,0,viewWidth * 3 / PIXELS_PER_METER,viewHeight * 3 / PIXELS_PER_METER);
	public final ComponentMappers mappers = new ComponentMappers();
	public final EntityBuilder factory = new EntityBuilder();
	public Systems systems;
	
	
	// == Debugging stuff (Entity spawning / debug rendering) ==
	private final DebugRenderer debugRenderer = new DebugRenderer();
	private float debugEntitySpawnAcc = 0,
				  debugEntitySpawnRate = .1f;
	
	private class DebugConsole implements TextInputListener{
		public String myText = "";
		
		public void input(String text) {
			myText = text;
			String[] args = text.split(" ");
			if (args.length > 1){
				
				if (args[0].equals("level")){
					System.out.println(args[1]);
					App.engine.loadLevel(args[1]);
				}
			}	
		}
		
		public void canceled() {}

	}
	DebugConsole debugConsole = new DebugConsole();
	
	/*
	TextInputListener textInput =  new TextInputListener(){
		private String myText = "";
		public String toString() {return myText;}
		public void input(String text) {myText = text;}
		public void canceled() {}
	};
	*/
	
	
	public void initialize(){
		logger.debug("Initializing");
		systems = new Systems(entityEngine);
		Gdx.input.setInputProcessor(systems.player);
	}
	
	public void loadLevel(String fileName){
		logger.info("Loading level " + fileName);
		reset();
		level = new Level(fileName);
	}
	
	public void test_deserializeBug(Vector2 pos){
		/*
		Entity e = factory.build("jeep",pos);
		PhysicsComponent p = mappers.physics.get(e);
		WheelJoint j = (WheelJoint) p.getJoint("leftWheelJoint");
		logger.debug("Factory \n anchorA: " + j.getAnchorA() + "\n anchorB:" + j.getAnchorB() + " \n localAxis: "+ j.getLocalAxisA() + " \n damping: " + j.getSpringDampingRatio() + "\n frequency" + j.getSpringFrequencyHz());

		String data = factory.serialize(e);
		removeEntity(e);
		e = factory.deserialize(data);
		p = mappers.physics.get(e);
		j = (WheelJoint) p.getJoint("leftWheelJoint");
		logger.debug("Deserialized \n anchorA: " + j.getAnchorA() + "\n anchorB:" + j.getAnchorB() + " \n localAxis: "+ j.getLocalAxisA() + " \n damping: " + j.getSpringDampingRatio() + "\n frequency" + j.getSpringFrequencyHz());
		
		addEntity(e);
		*/
		World world = App.engine.systems.physics.world;
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		
		Body body = world.createBody(bd),
			 wheel = world.createBody(bd);
		
		PolygonShape rect = new PolygonShape();
		rect.setAsBox(2, 1);
		CircleShape circle = new CircleShape();
		circle.setRadius(1);
		
		body.createFixture(rect,1);
		wheel.createFixture(circle,1);
		
		Vector2 localAnchor = new Vector2(0,0),
				localAxis = new Vector2(0,1);
		
		WheelJointDef jd = new WheelJointDef();
		jd.initialize(body, wheel, localAnchor , localAxis);
		WheelJoint joint = (WheelJoint)world.createJoint(jd);

		System.out.println("Local Axis = " + joint.getLocalAxisA());
		System.out.println("Local Anchor A = " + joint.getLocalAnchorA());
		
		//joint.getLocalAxisA() seems to be returning localAnchorA
		
	}
	
	public void debugInput(float deltaTime){
		
		//
		debugEntitySpawnAcc += deltaTime;
		if (debugEntitySpawnAcc >= debugEntitySpawnRate){
			
			//Slo-mo
			if (Gdx.input.isKeyPressed(Keys.Q)){
				timeModifier = .1f;
			}
			else timeModifier = 1;
			
			
			Vector2 coords = new Vector2(Gdx.input.getX(),Gdx.input.getY()),
					worldCoords = systems.camera.unproject(coords).scl(1/PIXELS_PER_METER);
			
			if (Gdx.input.isKeyPressed(Keys.T)){
				test_deserializeBug(worldCoords);
			}
			
			
			if (Gdx.input.isKeyPressed(Keys.R)){
				debugRenderer.drawPhysics = !debugRenderer.drawPhysics;
			}
			
			if (Gdx.input.isKeyPressed(Keys.TAB)){
				Gdx.input.getTextInput(debugConsole, "Debug Console","", "");
			}
			
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
				factory.build(debugConsole.myText,worldCoords);
			}
			
			if (Gdx.input.isKeyPressed(Keys.NUM_1)){
				systems.camera.getCamera().zoom = 1;
			}
			
			if (Gdx.input.isKeyPressed(Keys.NUM_2)){
				systems.camera.getCamera().zoom = 2;
			}
			
			if (Gdx.input.isKeyPressed(Keys.NUM_3)){
				systems.camera.getCamera().zoom = 3;
			}
			
			if (Gdx.input.isKeyPressed(Keys.NUM_4)){
				systems.camera.getCamera().zoom = 4;
			}
			if (Gdx.input.isKeyPressed(Keys.NUM_5)){
				systems.camera.getCamera().zoom = 5;
			}
			if (Gdx.input.isKeyPressed(Keys.NUM_6)){
				systems.camera.getCamera().zoom = 6;
			}
			if (Gdx.input.isKeyPressed(Keys.P)){
				paused = !paused;
				logger.info("Paused = " + paused);
			}
			
				
			debugEntitySpawnAcc = 0;
		}
	}
	
	public void update(float deltaTime){
		
		debugInput(deltaTime);
		
		//Not doing this every render frame can cause flickering
		// note: This is updated during entityEngine.update also.
		// sshould it not?
		systems.camera.update(deltaTime);
		
		if (paused) return;
		
		//If frame rate drops to below half of update rate then we'll pause the simulation
		// while it tries to catch up. Basically we stop adding real time to the accumulator
		if (Gdx.graphics.getFramesPerSecond() < 1 / (timestep * 2)){	
			logger.debug("Low Frame Rate Detected. Waiting for simulation to catch up.");
		}
		else timeAccumulator += deltaTime;
		while (timeAccumulator >= timestep){
			entityEngine.update(timestep * timeModifier);
			timeAccumulator -= timestep;
			debugRenderer.update();
			
		}
	
	}
	
	
	public void render(float deltaTime){
		Matrix4 projection = systems.camera.getCamera().combined;
		level.render(projection);
		//level.render("background",projection);
		//level.render("midBack",projection);
		systems.spriteRender.render();
		systems.particle.render(deltaTime);
		//level.render("midFront",projection);
		//level.render("foreground",projection);
		ui.render();	
		debugRenderer.render();
	}
	
	public void windowResized(int w, int h){
		logger.debug("Window resized to " + w + "x" + h);
		systems.camera.resize(w,h);
	}
	
	public void addEntity(Entity e){
		logger.debug("Entity Added " + e.getId());
		entityEngine.addEntity(e);
	}
	
	public Entity getEntity(long id){
		return entityEngine.getEntity(id);
	}
	
	public int getEntityCount(){
		return entityEngine.getEntities().size();
	}
	
	public ImmutableArray<Entity> getEntitiesFor(Family family){
		return entityEngine.getEntitiesFor(family);
	}
	
	public void removeEntity(Entity e){
		logger.debug("Entity Removed: " + e.getId());
		entityEngine.removeEntity(e);
	}
	
	public void removeAllEntities(){
		logger.debug("Removing all entities");
		entityEngine.removeAllEntities();
	}
			
	public void reset(){
		logger.debug("Reset");
		removeAllEntities();
		entityEngine.clearPools();
		if (level != null) level.clear();
		level = null;
	}

	public Entity createEntity() {
		return entityEngine.createEntity();
	}
	
	public <T extends Component> T createComponent (Class<T> componentType) {
		return entityEngine.createComponent(componentType);
	}
	
	public Level getLevel(){
		return level;
	}
	
}