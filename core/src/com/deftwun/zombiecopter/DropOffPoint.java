package com.deftwun.zombiecopter;

import com.badlogic.gdx.math.Vector2;

public class DropOffPoint {
	public DropOffPoint(Vector2 pos, float minRange){
		position.set(pos);
		range = minRange;
	}
	public Vector2 	position = new Vector2();
	public float 	range = 1;
}
