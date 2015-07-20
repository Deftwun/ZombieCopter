package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class WalkComponent extends Component implements Poolable{
	public float topSpeed;
	//public boolean left,right;
	
	/*
	public void stop(){left = false; right = false;}
	public void walkLeft(){right = false; left = true;}
	public void walkRight(){right = true; left = false;}
	*/
	
	@Override
	public void reset() {
		topSpeed = 0;
		//left = false;
		//right = false;
	}
}
