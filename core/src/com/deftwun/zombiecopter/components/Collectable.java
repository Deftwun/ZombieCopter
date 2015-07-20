package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Collectable extends Component implements Poolable{
	public enum ItemType{
		Civilian;
	}
	
	public ItemType type = ItemType.Civilian;
	public float pickupRange = 1;

	@Override
	public void reset() {
		type = ItemType.Civilian;
		pickupRange = 1;
	}
}
