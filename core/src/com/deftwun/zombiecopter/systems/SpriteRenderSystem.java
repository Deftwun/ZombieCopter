package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.SpriteLayer;
import com.deftwun.zombiecopter.components.PhysicsComponent;
import com.deftwun.zombiecopter.components.SpriteComponent;

public class SpriteRenderSystem extends IteratingSystem implements EntityListener{
	private Logger logger;
	private int LOG_LEVEL = Logger.INFO;
	
	private SpriteBatch batch;
	private ObjectMap<SpriteLayer,ObjectSet<Sprite>> spriteLayers = new ObjectMap<SpriteLayer,ObjectSet<Sprite>>();
	//private SpriteLayer[] layerNames = SpriteLayer.values();
	
	@SuppressWarnings("unchecked")
	public SpriteRenderSystem() {		
		super(Family.all(PhysicsComponent.class,SpriteComponent.class).get());		
		logger = new Logger("SpriteRenderSystem",LOG_LEVEL);
		logger.debug("initializing");
		batch = new SpriteBatch();
		for (SpriteLayer l : SpriteLayer.values()){
			spriteLayers.put(l, new ObjectSet<Sprite>());
		}
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {		
		SpriteComponent spriteCom = App.engine.mappers.sprite.get(entity);
		PhysicsComponent physics = App.engine.mappers.physics.get(entity);
		
		Vector2 pixelPosition;
		float angleDegrees;
				
		for (ObjectMap.Entry<String,Sprite> entry: spriteCom.spriteMap.entries()){
			Body b = physics.getBody(entry.key);
			if (b == null){
				pixelPosition = physics.getPosition().scl(App.engine.PIXELS_PER_METER);
				angleDegrees = physics.getRotation();
			}
			else {
				pixelPosition = b.getWorldCenter().scl(App.engine.PIXELS_PER_METER);
				angleDegrees = b.getAngle() * MathUtils.radDeg;
			}
	
			Sprite s = entry.value;
			s.setCenter(pixelPosition.x,pixelPosition.y);
			s.setRotation(angleDegrees);
			//s.setFlip(spriteCom.flipX,spriteCom.flipY);
		}
	}
	
	public void render(){
		logger.debug("Render");
		
		batch.setProjectionMatrix(App.engine.systems.camera.getCamera().combined);
		batch.begin();
		
		for (SpriteLayer l : SpriteLayer.values()){
			for (Sprite s : spriteLayers.get(l)){
				//Camera Culling
				float spriteRadius = s.getWidth() < s.getHeight() ? s.getHeight() : s.getWidth();
				if (App.engine.systems.camera.getCamera().frustum.sphereInFrustum(s.getX(), s.getY(), 0, spriteRadius))
					{s.draw(batch);}
			}
		}
		batch.end();
	}

	@Override
	public void entityAdded(Entity entity) {
		SpriteComponent spriteCom = App.engine.mappers.sprite.get(entity);
		if (spriteCom == null) return;
		logger.debug("SpriteComponent added " + entity);
		for (Sprite s : spriteCom.spriteMap.values()){
			spriteLayers.get(spriteCom.layer).add(s);
		}
	}

	@Override
	public void entityRemoved(Entity entity) {
		SpriteComponent spriteCom = App.engine.mappers.sprite.get(entity);
		if (spriteCom == null) return;
		logger.debug("SpriteComponent removed " + entity);
		for (Sprite s : spriteCom.spriteMap.values()){
			spriteLayers.get(spriteCom.layer).remove(s);
		}
	}
	
}
