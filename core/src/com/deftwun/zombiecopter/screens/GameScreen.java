package com.deftwun.zombiecopter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.App;
 
public class GameScreen implements Screen {
	private Logger logger;
	private int LOG_LEVEL = Logger.INFO;
	private String levelName = "data/levels/tilesetTest.tmx";
	
	private float resetCoolDown = 0;

	public GameScreen(){
		logger = new Logger("GameScreen",LOG_LEVEL);
		logger.debug("intitializing");
	}
	
    @Override
    public void render(float delta) {
		logger.debug("render");
	
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
       
        App.engine.render(delta);
    	App.engine.update(delta);
		
		resetCoolDown += delta;
		if (resetCoolDown > 3 && Gdx.input.isKeyPressed(Keys.ESCAPE)){
			resetCoolDown = 0;
			App.engine.loadLevel(levelName);
		}
    }
     
    @Override
    public void resize(int w, int h) {
		logger.debug("Resize to " + w + "x" + h);
		App.engine.windowResized(w,h);
    }
 
    @Override
	public void show() {
		logger.debug("Show");
		App.engine.loadLevel(levelName);
	}

	@Override
	public void pause() {
		logger.debug("pause");
	}

	@Override
	public void resume() {
		logger.debug("resume");
	}

	@Override
	public void hide() {
		logger.debug("hide");
	}

	@Override
	public void dispose() {
		logger.info("dispose");
	}
}