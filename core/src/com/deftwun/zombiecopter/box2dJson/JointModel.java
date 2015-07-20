package com.deftwun.zombiecopter.box2dJson;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.utils.Logger;

public class JointModel{
	
	private transient Logger logger = new Logger("JointModel",Logger.INFO);
	
	public String name = "joint",
				  bodyA = "",
				  bodyB = "";
		
	JointType type;
	boolean collideConnected,
			enableLimit,
			enableMotor;
	
	Vector2 localAnchorA = new Vector2(),
			localAnchorB = new Vector2(),
			linearOffset = new Vector2(),
			target = new Vector2(),
			localAxisA = new Vector2(),
			groundAnchorA = new Vector2(),
	        groundAnchorB = new Vector2();
			
	Joint joint1,joint2;
	
	float dampingRatio,
	      frequencyHz,
		  length,
		  maxForce,
		  maxTorque,
		  ratio,
		  angularOffset,
	      correctionalFactor,
		  lowerTranslation,
	      maxMotorForce,
		  motorSpeed,
		  referenceAngle,
		  upperTranslation,
		  lengthA,
		  lengthB,
		  lowerAngle,
	      maxMotorTorque,
		  upperAngle,
		  maxLength;
	
	public JointModel(){
		
	}
	
	public JointModel(String theName){
		name = theName;
	}
	
	public JointModel(Joint j,String bodyName1, String bodyName2){
		this.fromJoint(j,bodyName1,bodyName2);
	}
	
	public JointModel(JointDef jd,String bodyName1, String bodyName2){
		this.fromJointDef(jd,bodyName1,bodyName2);
	}
	
	public JointModel(String theName, JointDef jd,String bodyName1, String bodyName2){
		name = theName;
		this.fromJointDef(jd,bodyName1,bodyName2);
	}

	public JointModel(String theName, Joint j, String bodyName1, String bodyName2) {
		name = theName;
		this.fromJoint(j,bodyName1,bodyName2);	
	}
	
	public Joint toJoint(World world,Body bodyA, Body bodyB){
		logger.debug("Creating Joint");
		JointDef jd = this.toJointDef(bodyA,bodyB);
		return world.createJoint(jd);

		/*
		switch (type){

		case DistanceJoint:{
			return world.createJoint((DistanceJointDef)jd);
		}
		
		case FrictionJoint:{
			return world.createJoint((FrictionJointDef)jd);
		}
		
		case GearJoint:{
			return world.createJoint((GearJointDef)jd);
		}
		
		case MotorJoint:{
			return world.createJoint((MotorJointDef)jd);
		}
		
		case MouseJoint:{
			return world.createJoint((MouseJointDef)jd);
		}
		
		case PrismaticJoint:{
			return world.createJoint((PrismaticJointDef)jd);
		}
		
		case PulleyJoint:{
			return world.createJoint((PulleyJointDef)jd);
		}
		
		case RevoluteJoint:{
			return world.createJoint((RevoluteJointDef)jd);
		}
		
		case RopeJoint:{
			return world.createJoint((RopeJointDef)jd);
		}
		
		case WeldJoint:{
			return world.createJoint((WeldJointDef)jd);
		}
		
		case WheelJoint:{
			return world.createJoint((WheelJointDef)jd);			
		}
		
		}
		return null;
		*/
	}

	public JointDef toJointDef(Body bodyA, Body bodyB){
		logger.debug("Creating joint def");
		JointDef jd = toJointDef(type);
		jd.bodyA = bodyA;
		jd.bodyB = bodyB;
		jd.collideConnected = collideConnected;
		return jd;
	}
	
	private JointDef toJointDef(JointType jt){
		switch (jt){
		case DistanceJoint:{
			logger.debug("Creating DistanceJointDef");
			DistanceJointDef jd = new DistanceJointDef();
			jd.dampingRatio = dampingRatio;
			jd.frequencyHz = frequencyHz;
			jd.length = length;
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			return jd;
		}
		
		case FrictionJoint:{
			logger.debug("Creating FrictionJointDef");
			FrictionJointDef jd = new FrictionJointDef();
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.maxForce = maxForce;
			jd.maxTorque = maxTorque;
			return jd;
		}
		
		case GearJoint:{
			logger.debug("Creating GearJointDef");
			GearJointDef jd = new GearJointDef();
			/*I dont know how to get the joints 1 & 2 easily from just the name
			jd.joint1 = getJoint(joint1);
			jd.joint2 = getJoint(joint2);
			*/
			jd.ratio = ratio;
			return jd;
		}
		
		case MotorJoint:{
			logger.debug("Creating MotorJointDef");
			MotorJointDef jd = new MotorJointDef();
			jd.angularOffset = angularOffset;
			jd.correctionFactor = correctionalFactor;
			jd.linearOffset.set(linearOffset);
			jd.maxForce = maxForce;
			jd.maxTorque = maxTorque;
			return jd;
		}
		
		case MouseJoint:{
			logger.debug("Creating MouseJointDef");
			MouseJointDef jd = new MouseJointDef();
			jd.dampingRatio = dampingRatio;
			jd.frequencyHz = frequencyHz;
			jd.maxForce = maxForce;
			jd.target.set(target);
			return jd;
		}
		
		case PrismaticJoint:{
			logger.debug("Creating PrismaticJointDef");
			PrismaticJointDef jd = new PrismaticJointDef();
			jd.enableLimit = enableLimit;
			jd.enableMotor = enableMotor;
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.localAxisA.set(localAxisA);
			jd.lowerTranslation = lowerTranslation;
			jd.maxMotorForce = maxMotorForce;
			jd.motorSpeed = motorSpeed;
			jd.referenceAngle = referenceAngle;
			jd.upperTranslation = upperTranslation;
			return jd;
		}
		
		case PulleyJoint:{
			logger.debug("Creating PulleyJointDef");
			PulleyJointDef jd = new PulleyJointDef();
			jd.groundAnchorA.set(groundAnchorA);
			jd.groundAnchorB.set(groundAnchorB);
			jd.lengthA = lengthA;
			jd.lengthB = lengthB;
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.ratio = ratio;
			return jd;
		}
		
		case RevoluteJoint:{
			logger.debug("Creating RevoluteJointDef");
			RevoluteJointDef jd = new RevoluteJointDef();
			jd.enableLimit = enableLimit;
			jd.enableMotor = enableMotor;
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.lowerAngle = lowerAngle;
			jd.maxMotorTorque = maxMotorTorque;
			jd.motorSpeed = motorSpeed;
			jd.referenceAngle = referenceAngle;
			jd.upperAngle = upperAngle;
			return jd;
		}
		
		case RopeJoint:{
			logger.debug("Creating RopeJointDef");
			RopeJointDef jd = new RopeJointDef();
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.maxLength = maxLength;
			return jd;
		}
		
		case WeldJoint:{
			logger.debug("Creating WeldJointDef");
			WeldJointDef jd = new WeldJointDef();
			jd.dampingRatio = dampingRatio;
			jd.frequencyHz = frequencyHz;
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.referenceAngle = referenceAngle;
			return jd;
		}
		
		case WheelJoint:{
			logger.debug("Creating WheelJointDef");
			WheelJointDef jd = new WheelJointDef();
			
			jd.dampingRatio = dampingRatio;
			jd.enableMotor = enableMotor;
			jd.frequencyHz = frequencyHz;
			jd.localAnchorA.set(localAnchorA);
			jd.localAnchorB.set(localAnchorB);
			jd.localAxisA.set(localAxisA);
			jd.maxMotorTorque = maxMotorTorque;
			jd.motorSpeed = motorSpeed;
			
			//TODO: get rid of this
			//jd.localAxisA.set(0,1);
			
			
			return jd;
		}
		
		default:
			logger.error("Failed to create JointDef: Unknown type '" + type + "'");
			return null;
		}
	}
	
	private void fromJointDef(JointDef def,String bodyName1, String bodyName2){
		
		type = def.type;
		bodyA = bodyName1;
		bodyB = bodyName2;
		collideConnected = def.collideConnected;
		
		switch (type){
		case DistanceJoint:{
			DistanceJointDef jd = (DistanceJointDef)def;
			dampingRatio = jd.dampingRatio;
			frequencyHz = jd.frequencyHz;
			length = jd.length;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			break;
		}
		
		case FrictionJoint:{
			FrictionJointDef jd = (FrictionJointDef)def;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			maxForce = jd.maxForce;
			maxTorque = jd.maxTorque;
			break;
		}
		
		case GearJoint:{
			logger.error("JointModel from GearJoint not fully implemented.");
			assert false: "JointModel: Gear joint not yet implemented.";
			GearJointDef jd = (GearJointDef)def;
			/*I dont know how to get the name of joints 1 & 2 easily
			joint1 = getJointName(jd.getJoint1());
			joint2 = getJointName(jd.getJoint2());
			*/
			ratio = jd.ratio;
			break;
		}
			
		case MotorJoint:{
			MotorJointDef jd = (MotorJointDef)def;
			angularOffset = jd.angularOffset;
			correctionalFactor = jd.correctionFactor;
			linearOffset.set(jd.linearOffset);
			maxForce = jd.maxForce;
			maxTorque = jd.maxTorque;
			break;
		}
		
		case MouseJoint:{
			MouseJointDef jd = (MouseJointDef)def;
			dampingRatio = jd.dampingRatio;
			frequencyHz = jd.frequencyHz;
			maxForce = jd.maxForce;
			target.set(jd.target);
			break;
		}	
		
		case PrismaticJoint:{
			PrismaticJointDef jd = (PrismaticJointDef)def;
			enableLimit = jd.enableLimit;
			enableMotor = jd.enableMotor;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			localAxisA.set(jd.localAxisA);
			lowerTranslation = jd.lowerTranslation;
			maxMotorForce = jd.maxMotorForce;
			motorSpeed = jd.motorSpeed;
			referenceAngle = jd.referenceAngle;
			upperTranslation = jd.upperTranslation;
			break;
		}
		case PulleyJoint:{
			PulleyJointDef jd = (PulleyJointDef)def;
			groundAnchorA.set(jd.groundAnchorA);
			groundAnchorB.set(jd.groundAnchorB);
			lengthA = jd.lengthA;
			lengthB = jd.lengthB;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			ratio = jd.ratio;
		}	break;
		
		case RevoluteJoint:{
			RevoluteJointDef jd = (RevoluteJointDef)def;
			enableLimit = jd.enableLimit;
			enableMotor = jd.enableMotor;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			lowerAngle = jd.lowerAngle;
			maxMotorTorque = jd.maxMotorTorque;
			motorSpeed = jd.motorSpeed;
			referenceAngle = jd.referenceAngle;
			upperAngle = jd.upperAngle;
			break;
		}
		
		case RopeJoint:{
			RopeJointDef jd = (RopeJointDef)def;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			maxLength = jd.maxLength;
			break;
		}
			
		case WeldJoint:{
			WeldJointDef jd = (WeldJointDef)def;
			dampingRatio = jd.dampingRatio;
			frequencyHz = jd.frequencyHz;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			referenceAngle = jd.referenceAngle;
			break;
		}
		case WheelJoint:
			WheelJointDef jd = (WheelJointDef)def;
			dampingRatio = jd.dampingRatio;
			enableMotor = jd.enableMotor;
			frequencyHz = jd.frequencyHz;
			localAnchorA.set(jd.localAnchorA);
			localAnchorB.set(jd.localAnchorB);
			localAxisA.set(jd.localAxisA);
			maxMotorTorque = jd.maxMotorTorque;
			motorSpeed = jd.motorSpeed;
			break;
		
		default:
			System.out.println("JointModel: Unknown joint type ");
			break;
		}
	}
	
	public void fromJoint(Joint j, String bodyName1, String bodyName2){
		type = j.getType();
		bodyA = bodyName1;
		bodyB = bodyName2;
		collideConnected = j.getCollideConnected();
		
		switch (type){
		case DistanceJoint:{
			DistanceJoint jd = (DistanceJoint)j;
			dampingRatio = jd.getDampingRatio();
			frequencyHz = jd.getFrequency();
			length = jd.getLength();
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			break;
		}
		
		case FrictionJoint:{
			FrictionJoint jd = (FrictionJoint)j;
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			maxForce = jd.getMaxForce();
			maxTorque = jd.getMaxTorque();
			break;
		}
		
		case GearJoint:{
			logger.error("JointModel from GearJoint not fully implemented.");
			assert false: "JointModel: GearJoint not yet implemented";
			GearJoint jd = (GearJoint)j;
			/*I dont know how to get the name of joints 1 & 2 easily
			joint1 = getJointName(jd.getJoint1());
			joint2 = getJointName(jd.getJoint2());
			*/
			ratio = jd.getRatio();
			break;
		}
			
		case MotorJoint:{
			MotorJoint jd = (MotorJoint)j;
			angularOffset = jd.getAngularOffset();
			correctionalFactor = jd.getCorrectionFactor();
			linearOffset.set(jd.getLinearOffset());
			maxForce = jd.getMaxForce();
			maxTorque = jd.getMaxTorque();
			break;
		}
			
		case MouseJoint:{
			MouseJoint jd = (MouseJoint)j;
			dampingRatio = jd.getDampingRatio();
			frequencyHz = jd.getFrequency();
			maxForce = jd.getMaxForce();
			target.set(jd.getTarget());
			break;
		}	
		
		case PrismaticJoint:{
			PrismaticJoint jd = (PrismaticJoint)j;
			enableLimit = jd.isLimitEnabled();
			enableMotor = jd.isMotorEnabled();
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			localAxisA.set(jd.getLocalAxisA());
			lowerTranslation = jd.getLowerLimit();
			maxMotorForce = jd.getMaxMotorForce();
			motorSpeed = jd.getMotorSpeed();
			referenceAngle = jd.getReferenceAngle();
			upperTranslation = jd.getUpperLimit();
			break;
		}
		
		case PulleyJoint:{
			PulleyJoint jd = (PulleyJoint)j;
			groundAnchorA.set(jd.getGroundAnchorA());
			groundAnchorB.set(jd.getGroundAnchorB());
			lengthA = jd.getLength1();
			lengthB = jd.getLength2();
			//This methods (getLocalAnchor) dont exist and this should fail until i figure this out
			localAnchorA.set(jd.getAnchorA());
			localAnchorB.set(jd.getAnchorB());
			ratio = jd.getRatio();
			break;
		}
		
		case RevoluteJoint:{
			RevoluteJoint jd = (RevoluteJoint)j;
			enableLimit = jd.isLimitEnabled();
			enableMotor = jd.isMotorEnabled();
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			lowerAngle = jd.getLowerLimit();
			maxMotorTorque = jd.getMaxMotorTorque();
			motorSpeed = jd.getMotorSpeed();
			referenceAngle = jd.getReferenceAngle();
			upperAngle = jd.getUpperLimit();
			break;
		}
		
		case RopeJoint:{
			RopeJoint jd = (RopeJoint)j;
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			maxLength = jd.getMaxLength();
			break;
		}
		
		case WeldJoint:{
			WeldJoint jd = (WeldJoint)j;
			dampingRatio = jd.getDampingRatio();
			frequencyHz = jd.getFrequency();
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			//referenceAngle = jd.getReferenceAngle();
			logger.debug("Weld joint reference angle can not be set.");
			break;
		}
		
		case WheelJoint:{
			WheelJoint 	jd = (WheelJoint)j;
			dampingRatio = jd.getSpringDampingRatio();
			enableMotor = jd.isMotorEnabled();
			frequencyHz = jd.getSpringFrequencyHz();
			localAnchorA.set(jd.getLocalAnchorA());
			localAnchorB.set(jd.getLocalAnchorB());
			localAxisA.set(jd.getLocalAxisA());
			maxMotorTorque = jd.getMaxMotorTorque();
			motorSpeed = jd.getMotorSpeed();
			break;
		}
		
		default:
			System.out.println("JointModel: Unknown joint type ");
			break;
		}
	}
	
}