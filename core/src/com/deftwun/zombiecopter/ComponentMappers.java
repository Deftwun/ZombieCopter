package com.deftwun.zombiecopter;
import com.badlogic.ashley.core.ComponentMapper;
import com.deftwun.zombiecopter.components.*;

public class ComponentMappers{
	public ComponentMapper<GunComponent> gun = ComponentMapper.getFor(GunComponent.class);
	public ComponentMapper<JumpComponent> jump = ComponentMapper.getFor(JumpComponent.class);
    public ComponentMapper<LedgeHangComponent> ledge = ComponentMapper.getFor(LedgeHangComponent.class);
    public ComponentMapper<LookComponent> look = ComponentMapper.getFor(LookComponent.class);
	public ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);
    public ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public ComponentMapper<ThrusterComponent> thrust = ComponentMapper.getFor(ThrusterComponent.class);
	public ComponentMapper<WalkComponent> walk = ComponentMapper.getFor(WalkComponent.class);
	public ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);
	public ComponentMapper<TimeToLiveComponent> timeToLive = ComponentMapper.getFor(TimeToLiveComponent.class);
	public ComponentMapper<BulletComponent> bullet = ComponentMapper.getFor(BulletComponent.class);
	public ComponentMapper<HelicopterComponent> helicopter = ComponentMapper.getFor(HelicopterComponent.class);
	public ComponentMapper<SpriteComponent> sprite = ComponentMapper.getFor(SpriteComponent.class);
	public ComponentMapper<TeamComponent> team = ComponentMapper.getFor(TeamComponent.class);
	public ComponentMapper<BrainComponent> brain = ComponentMapper.getFor(BrainComponent.class);
	public ComponentMapper<Collector> collector = ComponentMapper.getFor(Collector.class);
	public ComponentMapper<Collectable> collectable = ComponentMapper.getFor(Collectable.class);
	public ComponentMapper<VehicleComponent> vehicle = ComponentMapper.getFor(VehicleComponent.class);
	public ComponentMapper<VehicleOperatorComponent> vehicleOperator = ComponentMapper.getFor(VehicleOperatorComponent.class);
	public ComponentMapper<CarComponent> car = ComponentMapper.getFor(CarComponent.class);
	public ComponentMapper<ControllerComponent> controller = ComponentMapper.getFor(ControllerComponent.class);
	public ComponentMapper<LeaderComponent> leader = ComponentMapper.getFor(LeaderComponent.class);
	public ComponentMapper<ChildComponent> child = ComponentMapper.getFor(ChildComponent.class);	
	public ComponentMapper<StickyComponent> sticky = ComponentMapper.getFor(StickyComponent.class);	
	public ComponentMapper<MeleeComponent> melee = ComponentMapper.getFor(MeleeComponent.class);		
	
}

