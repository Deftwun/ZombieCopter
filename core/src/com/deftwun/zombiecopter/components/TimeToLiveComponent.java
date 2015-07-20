package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TimeToLiveComponent extends Component implements Poolable{
	public float time,timeLimit;
	

	@Override
	public void reset() {
		time = 0;
		timeLimit = 0;
	}
	
}
