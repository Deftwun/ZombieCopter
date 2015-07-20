package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class GunComponent extends Component implements Poolable{
	
	//public ObjectMap<String,Gun> availableGuns = new ObjectMap<String,Gun>();
	//public String selectedGun;	
	public float bulletSpeed = 5,
				 range = 5,
				 spreadAngle = 0,
				 time = 0,
		     	 cooldown = 2;	
	public String projectileName = "bullet";
	public Vector2 offset = new Vector2();
	public boolean triggerPulled = false;
	
	@Override
	public void reset() {
		range = 5;
		bulletSpeed = 5;
		spreadAngle = 0;
		time = 0;
		cooldown = 2;
		projectileName = "bullet";
		offset.set(0,0);
		triggerPulled = false;
	}  
	
}
