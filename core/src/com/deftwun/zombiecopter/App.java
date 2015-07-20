package com.deftwun.zombiecopter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.deftwun.zombiecopter.screens.GameScreen;

public class App extends Game {
	
	public static GameScreen gameScreen;
	public static GameEngine engine;
	public static Assets assets;
	
	@Override
	public void create() {	
		Gdx.app.setLogLevel(Logger.DEBUG);	
		assets = new Assets();
		engine = new GameEngine();
		engine.initialize();
		gameScreen = new GameScreen();
		setScreen(gameScreen);
	}

	
}
