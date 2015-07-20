//Very specific to the game engine. Tight coupling

package com.deftwun.zombiecopter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.deftwun.zombiecopter.components.HealthComponent;
import com.deftwun.zombiecopter.components.PhysicsComponent;

public class DebugRenderer{

	public boolean drawPhysics = false,
				   drawText = true,
				   drawSpawnZones = false;

	private final Color spawnZoneColor = Color.WHITE,
						spawnZoneLegalColor = Color.GREEN,
						spawnZoneIllegalColor = Color.RED,
						entityBoundaryColor = Color.BLUE,
						cameraViewColor = Color.YELLOW,
						fontColor = Color.WHITE;
						
	private String infoString = "";
	private final SpriteBatch batch = new SpriteBatch();
	private final BitmapFont font = new BitmapFont();
	private final ShapeRenderer sr = new ShapeRenderer();
	private final Box2DDebugRenderer physicsRenderer = new Box2DDebugRenderer();
	private final Matrix4 projection = new Matrix4(),
						  scaledProjection = new Matrix4();
	private final Rectangle spawnRect = new Rectangle(),
							camRect = new Rectangle(), 
				            tmpRect = new Rectangle();
	

	public void render(){
		float units = App.engine.PIXELS_PER_METER;
		projection.set(App.engine.systems.camera.getCamera().combined);
        scaledProjection.set(projection).scale(units,units,1);
        
		if (drawText) renderInfoText();
		if (drawPhysics) renderPhysics();
		if (drawSpawnZones) renderSpawnZones();
		
	}
	
	public void update(){
		if (drawText) updateInfoText();
	}
	
	private void updateInfoText(){
		GameEngine engine = App.engine;
		Systems systems = engine.systems;
		ComponentMappers mappers = engine.mappers;
		
        Entity plyr = systems.player.getPlayer();
        if (plyr == null) return;
        PhysicsComponent p = mappers.physics.get(plyr);
        HealthComponent h = mappers.health.get(plyr);
         
        infoString = String.format("FPS: %d \nEntities: %d \n",
        										Gdx.graphics.getFramesPerSecond(),
        										engine.getEntityCount());
        if (h != null){
            infoString += "Health: " + (int)h.value + "/" + (int)h.max + "\n";
        }
        
        if (p != null){
        	int x = (int)p.getPosition().x,
        		y = (int)p.getPosition().y,
        		s = (int)p.getLinearVelocity().len();
        	
        	infoString += "Position: " + x + "," + y + "\n"+
        	              "Speed: " + s + "\n";
        }		
	}	
	
	private void renderInfoText(){
		font.setColor(fontColor);
	    batch.begin();
        font.draw(batch, infoString,0,App.engine.systems.camera.getCamera().viewportHeight - 20);
        batch.end();
	}
	
	private void renderPhysics(){
		World w = App.engine.systems.physics.world;
        physicsRenderer.render(w, scaledProjection);
	}

	private void renderSpawnZones(){
		GameEngine engine = App.engine;
		Systems systems = engine.systems;
		float units = engine.PIXELS_PER_METER;
		
		spawnRect.set(engine.entityBounds);
		camRect.set(systems.camera.getCameraRect(units));
		camRect.width /= systems.camera.getCamera().zoom;
		camRect.height /= systems.camera.getCamera().zoom;
		
		sr.setProjectionMatrix(scaledProjection);
		sr.begin(ShapeType.Line);

		//draw camera view rectangle (independent of camera zoom so you can zoom out and see it)
		sr.setColor(cameraViewColor);
		sr.rect(camRect.x,camRect.y,camRect.width,camRect.height);
		
		//draw the Entity boundary / spawnable area
		sr.setColor(entityBoundaryColor);
		sr.rect(spawnRect.x,spawnRect.y,spawnRect.width,spawnRect.height);
		
		//Spawn zones
		for (SpawnZone z : systems.spawn.getZones()){
			sr.setColor(spawnZoneColor);
			sr.rect(tmpRect.x,tmpRect.y,tmpRect.width,tmpRect.height);
			tmpRect.set(z.rectangle);

			//Draw the legal spawn area 			
			if (Intersector.intersectRectangles(z.rectangle,spawnRect,tmpRect)){
				if (camRect.overlaps(tmpRect)) sr.setColor(spawnZoneIllegalColor);
				else sr.setColor(spawnZoneLegalColor);
				sr.rect(tmpRect.x,tmpRect.y,tmpRect.width,tmpRect.height);
			}
		}	
		
		sr.end();
	}	
}	