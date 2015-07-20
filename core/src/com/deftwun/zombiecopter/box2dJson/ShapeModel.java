package com.deftwun.zombiecopter.box2dJson;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;

public class ShapeModel{
	public Shape.Type shapeType = Shape.Type.Edge;
	public float radius;
	public boolean isLooped;
	public final Vector2 position = new Vector2();
	public final Array<Vector2> vertices = new Array<Vector2>();
	
	public ShapeModel(){}
	
	public ShapeModel(Shape s){
		shapeType = s.getType();
		radius = s.getRadius();
		
		switch (shapeType){
		case Circle:
			CircleShape circle = (CircleShape)s;
			position.set(circle.getPosition());
			break;
		
		case Polygon:
			PolygonShape poly = (PolygonShape)s;
			for (int i=0; i<poly.getVertexCount(); i++){
				Vector2 v = new Vector2();
				poly.getVertex(i, v);
				vertices.add(v);
			}
			break;
			
		case Edge:
			assert false : "Edge shapes not supported in PhysicsComponent.ShapeModel.";
			break;
			
		case Chain:
			ChainShape chain = (ChainShape)s;
			isLooped = chain.isLooped();
			for (int i=0; i<chain.getVertexCount(); i++){
				Vector2 v = new Vector2();
				chain.getVertex(i, v);
				vertices.add(v);
			}
			break;
		}
	}
	
	public Shape toShape(){
		Shape s = null;
		switch (shapeType){
		case Circle:
			CircleShape circle = new CircleShape();
			circle.setPosition(position);
			circle.setRadius(radius);
			s = circle;
			break;
		
		case Polygon:
			PolygonShape poly = new PolygonShape();
			Vector2[] verts = vertices.toArray(Vector2.class);
			poly.set(verts);
			s = poly;
			break;
			
		case Edge:
			EdgeShape edge = new EdgeShape();
			s = edge;
			assert false : "Edge shapes not supported in PhysicsComponent.ShapeModel.";
			break;
			
		case Chain:
			ChainShape chain = new ChainShape();
			if (chain.isLooped()) chain.createLoop(vertices.toArray());
			else chain.createChain(vertices.toArray());
			s = chain;
			break;
		}
		return s;
	}
}