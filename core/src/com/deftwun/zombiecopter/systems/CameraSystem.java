package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.components.PhysicsComponent;

public class CameraSystem extends EntitySystem implements EntityListener{

	private Vector2 mouseVector = new Vector2();
	private int LOG_LEVEL = Logger.INFO;
	private Logger logger = new Logger("CameraSystem",LOG_LEVEL);
	private float speed = 3;
	private ExtendViewport viewport;
	private OrthographicCamera camera;
	private Entity targetObject;
	private PhysicsComponent targetPhysics;
	private Rectangle camRect;
	
	public CameraSystem(){
		float viewWidth = App.engine.viewWidth,
			  viewHeight = App.engine.viewHeight;
		logger = new Logger("CameraSystem",LOG_LEVEL);
		logger.debug("initializing");
		logger.debug("ViewSize is " + viewWidth + "x" + viewHeight);
		camera = new OrthographicCamera();		
		viewport = new ExtendViewport(viewWidth,viewHeight,0,0,camera);
		this.setProcessing(false);
		
		camera.zoom = 1.1f;
		/*
		camera.position.x = viewport.getWorldWidth() / 2;
		camera.position.y = 200;
		*/
		camRect = new Rectangle();
		
		
	}
	
	public ExtendViewport getViewport(){
		return viewport;
	}
		
	public OrthographicCamera getCamera(){
		return camera;
	}
	
	public Rectangle getCameraRect(){
		float w = App.engine.viewWidth,
			  h = App.engine.viewHeight,
			  x = camera.position.x - w/2,
			  y = camera.position.y - h/2;
		return camRect.set(x,y,w,h);
	}
	
	public Rectangle getCameraRect(float scale){
		Rectangle r = getCameraRect();
		return r.set(r.x*scale,r.y*scale,r.width*scale,r.height*scale);
	}
	
	public Vector2 unproject(Vector2 coords){
		return viewport.unproject(coords);
	}
	
	public void setFollow(Entity e){
		logger.debug("Following Entity#" + e.getId());
		targetObject = e;
		targetPhysics = App.engine.mappers.physics.get(targetObject);
	}
		
	public void update(float deltaTime){
		if (targetPhysics != null){
			float cameraLookDistance = 65;
			mouseVector.set(Gdx.input.getX() - viewport.getWorldWidth() / 2,-Gdx.input.getY() + viewport.getWorldHeight() / 2).nor().scl(cameraLookDistance);
			Vector2 pixPos = targetPhysics.getPosition().scl(App.engine.PIXELS_PER_METER);
			Vector2 pixPosDelta = mouseVector.add(pixPos).sub(camera.position.x,camera.position.y);
			//Vector2 pixPos = targetPhysics.getPosition().scl(App.engine.PIXELS_PER_METER),
			//		pixPosDelta = mouseVector.add(pixPos).sub(camera.position.x,camera.position.y);
			float distance = pixPosDelta.len();
			
			camera.translate(pixPosDelta.nor().scl(distance * deltaTime * speed));
			//camera.position.set(pixPosition.x,pixPosition.y,camera.position.z);
		}
		Rectangle bounds = App.engine.entityBounds;
		float units = App.engine.PIXELS_PER_METER;
		bounds.setCenter(camera.position.x / units,camera.position.y / units);
		camera.update();
	}
	
	public void resize(float w,float h){
		logger.debug("Resize to " + w + "x" + h);
		viewport.update((int)w,(int)h);
	}

	@Override
	public void entityAdded(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entityRemoved(Entity entity) {
		if (entity == targetObject){
			targetPhysics = null;
		}
		
	}
}