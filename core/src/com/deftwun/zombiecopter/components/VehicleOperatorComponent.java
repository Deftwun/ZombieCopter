package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class VehicleOperatorComponent extends Component implements Poolable {

	public boolean enterVehicle = false;
	public float time = 0,
				 coolDown = .5f;
	
	@Override
	public void reset() {
		enterVehicle = false;
		time = 0;
		coolDown = .5f;
	}

}
