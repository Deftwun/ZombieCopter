package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

//Creates a weld joint when the defined fixture collides with another 
public class StickyComponent extends Component implements Poolable{
	public String fixtureName = "";
	public boolean enabled = true;
	
	@Override
	public void reset() {
		enabled = true;
		fixtureName = "";
	}
}