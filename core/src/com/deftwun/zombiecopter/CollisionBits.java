package com.deftwun.zombiecopter;


//http://www.aurelienribon.com/blog/2011/07/box2d-tutorial-collision-filtering/
public class CollisionBits {
	
	public static final short 	
		Character	    = 0x0001,
 		Bullet			= 0x0002,
		Terrain 		= 0x0004,
		VisionSensor  	= 0x0008,
		TouchSensor     = 0x0010,
		Effects 		= 0x0020;
		
	public static final short 		
		Mask_Character	    = Bullet | Terrain | VisionSensor,
		Mask_Bullet 		= Character | Terrain | VisionSensor | Effects,
		Mask_Terrain 		= Character | Bullet | Terrain | VisionSensor | TouchSensor | Effects,
		Mask_VisionSensor	= Character | Bullet | Terrain,
		Mask_TouchSensor    = Terrain,
		Mask_Effects 		= Terrain | Bullet;
}
