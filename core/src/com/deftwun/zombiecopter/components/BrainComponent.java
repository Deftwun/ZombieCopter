package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.deftwun.zombiecopter.AI.BrainState;

public class BrainComponent extends Component implements Poolable{
	
	public float time = 0, thinkTime = 1, 
				 desiredRange = 0,
				 rangeTolerance = 1; 
	public Entity closestLeader,
			      //target,
				  closestEnemy,
				  closestFriend;
	public Vector2 myPosition = new Vector2(),
				   myVelocity = new Vector2();//,
				   //desiredPosition = new Vector2(),
			       //pointOfInterest = new Vector2();
	
	public BrainState state = BrainState.IDLE;
	
	
	@Override
	public void reset() {
		time = 0;
		thinkTime = .1f;
		desiredRange = 0;
		rangeTolerance = 1;
		closestLeader = null;
		myPosition.set(0,0);
		myVelocity.set(0,0);
		//desiredPosition.set(0,0);
		//pointOfInterest.set(0,0);
		state = BrainState.IDLE;
	}
	
}	