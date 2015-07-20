package com.deftwun.zombiecopter.components;
 
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
 
public class LookComponent extends Component implements Poolable{
	public float sweepRate = 10;
	public float sweepAngle = 30;
	public float sweepAcc = 0;
	
	public boolean controlSpriteFlip = false,
				   isSweeping = false,
				   reverseSweep = false;
				   
	public Vector2 position = new Vector2(0,0),
	               direction = new Vector2(1,0);
	
	public void sweep(float startAngle, float endAngle, float rate){
		isSweeping = true;
		if (endAngle < startAngle) reverseSweep = true;
		sweepAngle = Math.abs(endAngle - startAngle);
		sweepRate = rate;
		sweepAcc = 0;
	}
	
	public void lookAtPoint(Vector2 focalPoint){
		direction.set(focalPoint).sub(position).nor();
	}
	
	public void lookAtPoint(float x, float y){
		direction.set(x,y).sub(position).nor();
	}
	
	public void fuzzyLookAtPoint(Vector2 point, float tolerance){
		float px = point.x + MathUtils.randomTriangular(tolerance),
			  py = point.y + MathUtils.randomTriangular(tolerance);
		lookAtPoint(px,py);			
	}
	
    @Override
    public void reset() {
    	sweepRate = 10;
    	sweepAngle = 30;
    	sweepAcc = 0;
		controlSpriteFlip = false;
    	isSweeping = false;
		reverseSweep = false;
		position.set(0,0);
		direction.set(1,0);
    }
}