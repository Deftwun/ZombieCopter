package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.App;

public class ParticleSystem extends EntitySystem {
	private int LOG_LEVEL = Logger.INFO;
	private Logger logger = new Logger("ParticleSystem",LOG_LEVEL);
	
	SpriteBatch batch = new SpriteBatch();
	Array<ParticleEffect> effects = new Array<ParticleEffect>();
	Array<ParticleEffect> finishedEffects = new Array<ParticleEffect>();
	
	public ParticleSystem(){
		logger.debug("Initializing");
	}
	
	public void addEffect(String name, Vector2 position, float angle){
		logger.debug("Creating effect: " + name);
		ParticleEffect effect = App.assets.getEffect(name);
		if (effect == null) {
			logger.error("Couldn't create particle effect: " + name);
			return;
		}
		ParticleEffect e = new ParticleEffect(effect);
		for (ParticleEmitter emitter : e.getEmitters()){
			float a1 = emitter.getAngle().getHighMin(),
				  a2 = emitter.getAngle().getHighMax();
			
			emitter.getRotation().setHighMin(a1 + angle);
			emitter.getRotation().setHighMax(a2 + angle);
		}
		e.setPosition(position.x * App.engine.PIXELS_PER_METER, position.y * App.engine.PIXELS_PER_METER);
		e.start();
		effects.add(e);
		
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		for (ParticleEffect e : finishedEffects){
			effects.removeValue(e, true);
		}
		finishedEffects.clear();
	}
	
	public void render(float deltaTime){
		batch.setProjectionMatrix(App.engine.systems.camera.getViewport().getCamera().combined);
		batch.begin();
		for (ParticleEffect e : effects){
			if (e.isComplete()){
				finishedEffects.add(e);
				logger.debug("effect complete");
			}
			else
				e.draw(batch,deltaTime);
			
		}
		batch.end();
	}

}
