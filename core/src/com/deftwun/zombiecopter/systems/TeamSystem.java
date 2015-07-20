package com.deftwun.zombiecopter.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.deftwun.zombiecopter.components.TeamComponent.Team;


//This class only serves to determine enemies of a given team.
public class TeamSystem extends EntitySystem {
	private ObjectMap<Team,Array<Team>> enemyMap;
	
	public Array<Team> getEnemies(Team t){
		return enemyMap.get(t);
	}
	
	public TeamSystem(){
		enemyMap = new ObjectMap<Team,Array<Team>>();
		for (Team t : Team.values()){
			enemyMap.put(t, new Array<Team>());
		}
		enemyMap.get(Team.PLAYER).add(Team.ENEMY);
		enemyMap.get(Team.PLAYER).add(Team.WILD);
		
		enemyMap.get(Team.ENEMY).add(Team.PLAYER);
		enemyMap.get(Team.ENEMY).add(Team.WILD);
		
		enemyMap.get(Team.WILD).add(Team.ENEMY);
		enemyMap.get(Team.WILD).add(Team.PLAYER);
		enemyMap.get(Team.WILD).add(Team.NEUTRAL);

	}

}
