package com.deftwun.zombiecopter;

import com.badlogic.gdx.utils.ObjectMap;

class EntityConfig{
	
	ObjectMap<String,String> images = new ObjectMap<String,String>();
	
	String model = "",
		   stickyFixture="",
		   name = "",
	       image = "",
		   team = "",
		   corpse = "",
		   drop = "",
		   damageEffect = "",
		   deathEffect = "",
		   collisionEffect ="",
		   gibbedEffect = "",
		   weapon = "";
		   
	boolean lookControlSpriteFlip,
			player,
			leader,
			thruster,
			car,
			frontWheelDrive,
			rearWheelDrive,
			brain,
			collectable,
			collector,
			walker,
			helicopter,
			bullet,
			ledgeHanger,
			vehicle,
			vehicleOperator;
			
	int	  min,
	  	  max;
	
	float thinkTime,
		  damage,
		  meleeDamage,
		  meleeRange,
		  meleeCoolDown,
		  dropRate=1,
		  timeToLive,
		  size,
		  speed,
		  torque,
		  suspension,
		  downForce,
		  lateralPower,
		  verticalPower,
		  jumpPower,
		  jumpCooldown,
		  maxAltitude,
		  viewDistance,
		  health,
		  collisionDamageThreshold = 5,
		  weaponOffsetX,
		  weaponOffsetY,
		  thrustDelay,
		  thrustPower,
		  thrustSpeed,
		  thrustDuration;

}