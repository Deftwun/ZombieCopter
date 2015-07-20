package com.deftwun.zombiecopter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Logger;

//Manages the player interaction data. 
//ie: keyboard/touch polling and events, on screen controls, Gestures, etc..

public class UserInterface {
	private Logger logger;
	private int LOG_LEVEL = Logger.INFO;
	
	public boolean isTouchScreen;
    private final Stage stage;
    private Touchpad moveStick,fireStick;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground, touchKnob;
    
    private Vector2 moveVector = new Vector2(),
    		        fireVector = new Vector2(),
    		        touchPosition = new Vector2(); //also mouse position
    
    //CTOR
    public UserInterface(){
		logger = new Logger("UserInterface",LOG_LEVEL);
		logger.debug("initializing");
	
		stage = new Stage();
		isTouchScreen = (Gdx.app.getType() == ApplicationType.Android || 
								 Gdx.app.getType() == ApplicationType.iOS);
		
		if (isTouchScreen) {
			logger.info("Touchscreen detected");
			createTouchControls();
		}
    }
 
    
    //Get the movement direction
    public Vector2 getMoveVector(){
    	if (isTouchScreen){
    		moveVector.set(moveStick.getKnobPercentX(),moveStick.getKnobPercentY());
    	}
    	else {
			moveVector.set(0,0);
			if (Gdx.input.isKeyPressed(Keys.A)) moveVector.add(-1,0);
	        if (Gdx.input.isKeyPressed(Keys.W)) moveVector.add(0,1);
	        if (Gdx.input.isKeyPressed(Keys.D)) moveVector.add(1,0);
	        if (Gdx.input.isKeyPressed(Keys.S)) moveVector.add(0,-1);  
    	}
		logger.debug("MoveVector = " + moveVector);
    	return moveVector;
    }
    
    //Get the firing direction
    public Vector2 getFireVector(){
    	if (isTouchScreen){
    		fireVector.set(fireStick.getKnobPercentX(),fireStick.getKnobPercentY());
    	}
    	else {
    		fireVector.set(0,0);
    	}
		return fireVector;
    }
    
    //Get the last touch / mouse position (screen coords)
    public Vector2 getTouchPosition(){
    	touchPosition.set(Gdx.input.getX(),Gdx.input.getY());
		return touchPosition;
    }
    
    public boolean isFiring(){
		boolean isFiring;
    	if (isTouchScreen) isFiring = fireVector.len() > 0;
    	else isFiring = Gdx.input.isButtonPressed(0);
		return isFiring;
    }
    
    //Create on screen controls for touchscreen
    private void createTouchControls(){
    	
    	logger.info("Creating Touchscreen controls");
    	
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", App.assets.getTexture("touchBackground.png"));
        touchpadSkin.add("touchKnob", App.assets.getTexture("touchKnob.png"));
        touchpadStyle = new TouchpadStyle();
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        moveStick = new Touchpad(10, touchpadStyle);
        moveStick.setBounds(15, 15, 200, 200);
        moveStick.setSize(200, 200);

        fireStick = new Touchpad(10, touchpadStyle);
        fireStick.setBounds(Gdx.graphics.getWidth() - 215, 15, 200, 200);
        fireStick.setSize(200, 200);
 
        //Create a Stage and add TouchPad
        //stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
        stage.addActor(moveStick);       
        stage.addActor(fireStick);
        Gdx.input.setInputProcessor(stage);
    }
    
    public void resize(int w, int h){
		logger.debug("Resize to " + w + "x" + h);
		stage.getViewport().update(w,h);
    }
    
    public void render() {        
        //Draw
		logger.debug("Render");
        stage.act(Gdx.graphics.getDeltaTime());        
        stage.draw();
    }
}
