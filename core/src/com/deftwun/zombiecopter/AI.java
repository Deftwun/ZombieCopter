package com.deftwun.zombiecopter;

public class AI{
	public enum BrainState{
		IDLE,
		PATROL,
		FOLLOW,
		APPROACH,
		ATTACK,
		FALLBACK,
		FLEE,
	}
}