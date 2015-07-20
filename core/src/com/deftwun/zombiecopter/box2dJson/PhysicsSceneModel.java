package com.deftwun.zombiecopter.box2dJson;

import com.badlogic.gdx.utils.Array;

public class PhysicsSceneModel{
	public String primaryBody = "body";	
	public Array<BodyModel> bodyModels = new Array<BodyModel>();
	public Array<JointModel> jointModels = new Array<JointModel>();
}
