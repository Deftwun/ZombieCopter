package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class VehicleComponent extends Component implements Poolable {

	public String occupantData = "";
	public float time = 0,
				 coolDown = .5f;
	public boolean eject = false;
	
	
	@Override
	public void reset() {
		occupantData = "";
		coolDown = .5f;
		time = 0;
		eject = false;
	}
	
}
