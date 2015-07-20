package com.deftwun.zombiecopter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TeamComponent extends Component implements Poolable{
	public enum Team{
		PLAYER,
		NEUTRAL,
		ENEMY, 
		BULLET, 
		WILD
	}
	
	public Team team = Team.NEUTRAL;

	@Override
    public void reset() {
		team = Team.NEUTRAL;
	}
}
