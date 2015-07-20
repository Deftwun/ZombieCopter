package com.deftwun.zombiecopter.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Logger;

public class MenuScreen implements Screen {

	private Logger logger;
	private int LOG_LEVEL = Logger.DEBUG;

	public MenuScreen(){
		logger = new Logger("MenuScreen",LOG_LEVEL);
		logger.info("intializing");
	}
	
	@Override
	public void show() {
		logger.debug("Show");

	}

	@Override
	public void render(float delta) {
		logger.debug("render");
	}

	@Override
	public void resize(int width, int height) {
		logger.debug("Resize to " + width + "x" + height);
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
