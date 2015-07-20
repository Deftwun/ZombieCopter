package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HelicopterComponent extends Component implements Poolable{
	public float verticalPower,
				 lateralPower,
				 topSpeed,
				 maxAltitude;
	//public Vector2 vector = new Vector2();
	
	@Override
	public void reset() {
		verticalPower = 0;
		lateralPower = 0;
		topSpeed = 0;
		//vector.set(0,0);
		maxAltitude = 0;
	}
	
	
}
