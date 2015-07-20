package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.deftwun.zombiecopter.App;
import com.deftwun.zombiecopter.SpawnZone;

public class SpawnSystem extends EntitySystem {

	private Rectangle camRect,
					  boundsRect = new Rectangle(App.engine.entityBounds),
					  tmpRect = new Rectangle();
	
	private Vector2 cameraCenter = new Vector2(),
					spawnPoint = new Vector2();

	private Array<SpawnZone> points = new Array<SpawnZone>();
	private Array<SpawnZone> deadPoints = new Array<SpawnZone>();
	private final float units = App.engine.PIXELS_PER_METER;
	private final int maxEntities = 25;
	
	public void add(SpawnZone point){
		points.add(point);
	}

	public SpawnSystem(){
		this.setProcessing(true);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		points.clear();
		deadPoints.clear();
	}

	@Override
	public void update(float deltaTime) {
		if (App.engine.getEntityCount() > maxEntities) return;
		
		
		camRect = App.engine.systems.camera.getCameraRect(1/units);
		boundsRect.setCenter(camRect.getCenter(cameraCenter));
		
		for (SpawnZone z : points){
			z.time += deltaTime;
			
			//Spawn new entity
			if (z.time > z.delay && z.count < z.maximum){
				boolean withinBounds = Intersector.intersectRectangles(z.rectangle,boundsRect,tmpRect);
				boolean outsideCamera = !camRect.overlaps(z.rectangle);
				z.time = 0;
				
				if (withinBounds && outsideCamera){
					z.count++;
					spawnPoint.x = MathUtils.random(tmpRect.x,tmpRect.x+tmpRect.width);
					spawnPoint.y = MathUtils.random(tmpRect.y,tmpRect.y+tmpRect.height);
					App.engine.factory.build(z.type,spawnPoint);
				}
			}
			
			//Retire spawn point if it has reached max spawn count
			if (z.maximum > 0 && z.count > z.maximum){
				deadPoints.add(z);
			}
		}
		
		//Remove the retired spawn points
		for (SpawnZone p : deadPoints){
			points.removeValue(p, true);
		}
		deadPoints.clear();
	}

	public Array<SpawnZone> getZones() {
		return points;
	}
	
}
