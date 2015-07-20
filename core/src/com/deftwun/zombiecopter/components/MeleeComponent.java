package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

//Creates a weld joint when the defined fixture collides with another 
public class MeleeComponent extends Component implements Poolable{
	public float damage, 
				 range,
				 time,
				 coolDown;
	
	public boolean triggerPulled = false;
	public Entity target;
	
	@Override
	public void reset() {
		damage = 0;
		range = 0;
		time = 0;
		coolDown = 0;
		triggerPulled = false;	
		target = null;
	}
}