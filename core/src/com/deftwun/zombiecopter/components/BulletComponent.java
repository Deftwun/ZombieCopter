package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BulletComponent extends Component implements Poolable{
	public float damage = 1;

	@Override
	public void reset() {
		damage = 1;
	}
	
	
}
