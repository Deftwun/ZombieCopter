package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LedgeHangComponent extends Component implements Poolable{
	public boolean hanging,
				   climbing;
	public float climbSpeed;
	
	@Override
	public void reset() {
		hanging = false;
		climbing = false;
		climbSpeed = 1;
	}

}
