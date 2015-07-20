package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ControllerComponent extends Component implements Poolable{
	
	public Vector2 moveVector = new Vector2(),
				   lookVector = new Vector2();
	public boolean attack,
				   action;
	
	@Override
	public void reset() {
		moveVector.set(0,0);
		lookVector.set(0,0);
		attack = false;
		action = false;
		
	}
	
}
