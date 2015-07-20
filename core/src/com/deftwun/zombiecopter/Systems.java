package com.deftwun.zombiecopter;

import com.badlogic.ashley.core.Engine;
import com.deftwun.zombiecopter.systems.*;

public class Systems {
	public CameraSystem camera = new CameraSystem();
	public MoveSystem move = new MoveSystem();
	public PhysicsSystem physics = new PhysicsSystem();
	public PlayerSystem player = new PlayerSystem();
	public WeaponSystem weapon = new WeaponSystem();
	public LifetimeSystem lifetime = new LifetimeSystem();
	public SpriteRenderSystem spriteRender = new SpriteRenderSystem();
	public VisionSystem vision = new VisionSystem();
//	public RangedGround_AI_System rangedGroundAI = new RangedGround_AI_System(); 
	//public CivilianGround_AI_System civilianGroundAI = new CivilianGround_AI_System();
	public TeamSystem team = new TeamSystem();
	public CollectableSystem collectable = new CollectableSystem();
	public SpawnSystem spawn = new SpawnSystem();
	public CivilianDropOffSystem dropoff = new CivilianDropOffSystem();
	public DamageSystem damage = new DamageSystem();
	public ParticleSystem particle = new ParticleSystem();
	//public Helicopter_AI_System helicopterAI = new Helicopter_AI_System();
	public VehicleSystem vehicle = new VehicleSystem();
	public AgentSystem agent = new AgentSystem();
	
	public Systems(Engine engine){

		engine.addSystem(physics);
		engine.addSystem(agent);
		engine.addSystem(camera);
		engine.addSystem(move);
		engine.addSystem(player);
		engine.addSystem(weapon);
		engine.addSystem(lifetime);
		engine.addSystem(vision);
		//engine.addSystem(rangedGroundAI);
		//engine.addSystem(civilianGroundAI);
		engine.addSystem(spriteRender);
		engine.addSystem(team);
		engine.addSystem(collectable);
		engine.addSystem(spawn);
		engine.addSystem(damage);
		engine.addSystem(particle);
		//engine.addSystem(helicopterAI);
		engine.addSystem(vehicle);
		
		engine.addEntityListener(camera);
		engine.addEntityListener(physics);
		engine.addEntityListener(player);
		engine.addEntityListener(spriteRender);

	}
}
