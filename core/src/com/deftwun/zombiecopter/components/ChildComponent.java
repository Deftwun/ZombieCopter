package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ChildComponent extends Component implements Poolable{
	public Entity parentEntity;

	@Override
	public void reset() {
		parentEntity = null;
	}
}