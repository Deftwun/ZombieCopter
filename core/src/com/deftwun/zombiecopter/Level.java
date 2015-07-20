package com.deftwun.zombiecopter;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.systems.SpawnSystem;

public class Level {
	private Logger logger = new Logger("Level",Logger.INFO);
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private SpriteBatch batch;
	private Entity entity;
	
	public Level() {
		logger.debug("initialising");
		batch = new SpriteBatch();
		map = null;
	}	

	public Level(String fileName){
		logger.debug("initialising");
		batch = new SpriteBatch();
		
		loadTiledMap(fileName);
	}
	
	public void clear(){
		logger.debug("Clearing");
		if (map != null) {
			map.dispose();
			map = null;
		}
		if (entity != null){
			App.engine.removeEntity(entity);
			entity = null;
		}
		if (mapRenderer != null){
			mapRenderer.dispose();
			mapRenderer = null;
		}	
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public void loadTiledMap(String mapName){
		logger.debug("Loading TiledMap: " + mapName);
		
		this.clear();
		
		try{
			map = new TmxMapLoader().load(mapName);
		}
		catch(Exception e){
			logger.error("Map load failed .. " + e.getMessage());
			return;
		}
		mapRenderer = new OrthogonalTiledMapRenderer(map,batch);
		
		createPhysics(map);
        createEntities(map);
        createSpawnZones(map);
        createDropOffPoints(map);
	}
	
	private void createPhysics(Map map) {
		
		logger.debug("Creating Physics");

		String layerName = "physics";

		//Layer objects
		MapLayer layer = map.getLayers().get(layerName);
		if (layer == null) {
			logger.error("layer " + layerName + " does not exist");
			return;
		}
		
		World world = App.engine.systems.physics.world;
		//world.dispose();
		
		
		Box2DMapObjectParser parser = new Box2DMapObjectParser(1/App.engine.PIXELS_PER_METER);
		parser.load(world,layer);
		logger.debug(parser.getBodies().size + " bodies were parsed in layer '" + layerName + "'");
		logger.debug(parser.getFixtures().size + " fixtures were parsed in layer '" + layerName + "'");
		
		//Level Entity (If it has physics then it needs to be associated with an entity,
		// 				other wise collisions won't be handled properly.)
		entity = App.engine.createEntity();
		
		PhysicsComponent physics = App.engine.createComponent(PhysicsComponent.class);
		for (ObjectMap.Entry<String,Body> entry :  parser.getBodies()){
			physics.addBody(entry.key,entry.value);
		}
		physics.setFilter(CollisionBits.Terrain,CollisionBits.Mask_Terrain);
		entity.add(physics);
		
		App.engine.addEntity(entity);
		
	}
	
	private void createDropOffPoints(Map map){
		logger.debug("Creating DropOffPoints");
		
		String layerName = "dropoff";
		
		//dropOffPoint map layer
		MapLayer layer = map.getLayers().get(layerName);
		if (layer == null) {
			logger.error("layer " + layerName + " does not exist");
			return;
		}

		//Layer objects
		MapObjects objects = layer.getObjects();
		for (MapObject mapObj : objects){
			
			//Object properties. 
			//name and position are set by the tiled editor. The rest are custom properties
			Vector2 position = new Vector2();
			float range = 1;
					
			MapProperties prop = mapObj.getProperties();
			Object x = prop.get("x"),
				   y = prop.get("y"),
				   r = prop.get("range");
			
			if (r != null) range = Float.parseFloat(r.toString());
			if (x != null && y != null)
				position.set((Float)x,(Float)y).scl(1/App.engine.PIXELS_PER_METER);					  
			App.engine.systems.dropoff.add(new DropOffPoint(position,range)); 	
		}		
	}
	
	private void createSpawnZones(Map map){
		logger.debug("Creating SpawnPoints");
		
		String layerName = "spawn";
		
		//spawnPoint map layer
		MapLayer layer = map.getLayers().get(layerName);
		if (layer == null) {
			logger.error("layer " + layerName + " does not exist");
			return;
		}

		//Layer objects
		float units = App.engine.PIXELS_PER_METER;
		MapObjects objects = layer.getObjects();
		for (MapObject mapObj : objects){
			logger.debug("found spawn zone");
			//Spawn area rectangle
			Rectangle rect;
			
			if (mapObj instanceof RectangleMapObject){
				rect = ((RectangleMapObject) mapObj).getRectangle();
				rect.height /= units;
				rect.width /= units;
				rect.x /= units;
				rect.y /= units;			
			}
			else {
				logger.error("spawn zones should only be rectangles");
				continue;
			}
			
			//Object properties. 
			//name and position are set by the tiled editor. The rest are custom properties
			String 	name = mapObj.getName();
			int 	maximum = 0;
			float 	delay = 3;
			
			logger.debug("Creating '" + name + "' spawn zone");
			
			MapProperties prop = mapObj.getProperties();
			Object max = prop.get("maximum"),
				   dly = prop.get("delay");
			
			if (max != null) maximum = Integer.parseInt(max.toString());
			if (dly != null) delay = Float.parseFloat(dly.toString());
			SpawnSystem spawner = App.engine.systems.spawn;
			spawner.add(new SpawnZone(rect,name,maximum,delay)); 	
		}		
	}
	
	private void createEntities(Map map) {
	
		logger.debug("Creating Entities");
		String layerName = "entities";
		
		MapLayer layer = map.getLayers().get(layerName);
		if (layer == null) {
			logger.error("layer " + layerName + " does not exist");
			return;
		}

		//Entity objects
		Vector2 position = new Vector2(),
		velocity = new Vector2();
		
		MapObjects objects = layer.getObjects();
		for (MapObject mapObj : objects){

			MapProperties prop = mapObj.getProperties();
			Object x = prop.get("x"),
				   y = prop.get("y"),
				   vx = prop.get("vx"),
				   vy = prop.get("vy");
			
			position.set(0,0);
			velocity.set(0,0);
			
			if (x != null && y != null)	
				position.set((Float)x,(Float)y).scl(1/App.engine.PIXELS_PER_METER);
			if (vx != null && y != null)
				velocity.set((Float)vx,(Float)vy);
			logger.debug(" -Create: " + mapObj.getName());
			Entity e = App.engine.factory.build(mapObj.getName(),position,velocity);
			if (mapObj.getName().equals("player")) App.engine.systems.player.setPlayer(e);
		}
	}
	
	public void render(Matrix4 projection){
		mapRenderer.setView((OrthographicCamera) App.engine.systems.camera.getCamera());
		mapRenderer.render();
	}
	
	public void renderLayer(String layerName, Matrix4 projection){
		mapRenderer.setView((OrthographicCamera) App.engine.systems.camera.getCamera());
		MapLayer layer = map.getLayers().get(layerName);
		if (layer != null) mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
	}
	
	public void renderLayer(int index, Matrix4 projection){
		mapRenderer.setView((OrthographicCamera) App.engine.systems.camera.getCamera());
		MapLayer layer = map.getLayers().get(index);
		if (layer != null) mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
	}
}
