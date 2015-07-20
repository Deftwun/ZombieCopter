package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Collector extends Component implements Poolable {
	public int civilians = 0;
	public int maxCivilians = 10;

	@Override
	public void reset() {
		civilians = 0;
	}
	
}
