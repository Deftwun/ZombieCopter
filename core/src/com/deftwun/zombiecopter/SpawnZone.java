package com.deftwun.zombiecopter;

import com.badlogic.gdx.math.Rectangle;

public class SpawnZone {
	public SpawnZone(Rectangle areaRect, String entType,int max, float dly){
		//position.set(pos);
		rectangle = areaRect;
		type = entType;
		maximum = max;
		delay = dly;
	}
	public Rectangle rectangle = new Rectangle();
	//public Vector2 	position = new Vector2();
	public String 	type = "none";
	public int 		count = 0, 
			   		maximum = 0; //value < 1 means spawn forever
	public float 	delay = 0,
				 	time = 0;
}
