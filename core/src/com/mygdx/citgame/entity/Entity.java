package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Entity {

	public Vector2 position, newPosition, velocity, acceleration;
	public Vector2 totalForces;
	
	protected Array<Rectangle> obstacles;
	
	public MapObjects collisions;
	public EntityBounds bounds;
	
	public int size = 32;
	
	public boolean removed = false;
	
	protected boolean moves = true;
	
	//higher values = more drag
	protected float drag = 5;
	
	public Entity(MapObjects collisions, float x, float y) {
		this.collisions = collisions;
		position = new Vector2(x, y);
		velocity = new Vector2(0, 0);
		acceleration = new Vector2(0, 0);
		
		totalForces = new Vector2(0, 0);
		
		bounds = new EntityBounds(this);
		bounds.set(position.x, position.y, size, size);
		
		newPosition = new Vector2(0, 0);
		
	}
	
	public void update(float delta, Array<Entity> entities) {
		if (moves) this.updatePos(delta, entities);
	}
	
	protected void updatePos(float delta, Array<Entity> entities) {
		
		bounds.set(position.x, position.y, size, size);
		
		Vector2 oldVelocity = velocity;
		newPosition.set(position);
		
    	velocity.x += acceleration.x * delta;
    	velocity.y += acceleration.y * delta;
    	
        newPosition.x += (oldVelocity.x + velocity.x) * .5f * delta;
    	newPosition.y += (oldVelocity.y + velocity.y) * .5f * delta;
    	
    	obstacles = new Array<Rectangle>();
    	for (MapObject o: collisions) {
    		obstacles.add(((RectangleMapObject) o).getRectangle());
    	}
    	
    	for (Entity e: entities) {
    		if (e == this) continue;
    		obstacles.add((e.bounds));
    	}
    	
    	boolean stoppedX = !canMove(newPosition.x, position.y, obstacles);
    	boolean stoppedY = !canMove(position.x, newPosition.y, obstacles);
    	
    	if (stoppedX) {
    		velocity.x = 0;
    		//addForce(-totalForces.x * 15, 0);
    	}
    	
    	else position.x = newPosition.x;
    	
    	if (stoppedY) {
    		velocity.y = 0;
    		//addForce(0, -totalForces.y * 15);
    	}
    	
    	else position.y = newPosition.y;
    	
    	//drag
    	addForce(- drag * velocity.x, - drag * velocity.y);
    	
    	
    	applyForces();
    	clearForces();	
	}

	public static Vector2 getVectorBetween(Vector2 p1, Vector2 p2) {
		return new Vector2(p2.x - p1.x, p2.y - p1.y);
	}
	
	public static Rectangle checkRectangleVsRectangleList(Rectangle bounds, Array<Rectangle> rectangles) {
	
		for (Rectangle r: rectangles) {
			if (bounds.overlaps(r)) return r;
		}
		
		return null;
	}
	
	public boolean canMove(float x, float y, Array<Rectangle> obstacles) {
		
		Rectangle r = new Rectangle(x, y, size, size);
		
		Rectangle collidedWith = Entity.checkRectangleVsRectangleList(r, obstacles);
		
		if (collidedWith == null) return true;
	
		if (collidedWith instanceof EntityBounds) {
					
			Entity other = ((EntityBounds) collidedWith).owner;
			
			this.reactToEntityCollision(other);
			
			if (!other.isObstacle(this))  return true;
					
			
		}
		
		return false;
	}
	
	protected boolean isObstacle(Entity collider) {
		return true;
	}
	
	//called by Game loop when entity.removed == true
	public void remove() {
		
	}
	
	protected void reactToEntityCollision(Entity other) {
		
		//More Aggressive Pushy
		//((EntityBounds) o).owner.addForce(totalForces.nor().scl(6000f));
		//this.addForce(totalForces.nor().scl(-6000f));
				
		//More Friendly Push
		
		Vector2 avgForce = new Vector2((other.totalForces.x + this.totalForces.x) * .5f, 

		(other.totalForces.y + this.totalForces.y) * .5f);
		
		Vector2 avgForceHalf = avgForce.scl(.66f);
				
		other.addForce(avgForceHalf);
		this.addForce(avgForceHalf);
		//other.addForce(totalForces.nor().scl(6000f));
		//this.addForce(totalForces.nor().scl(-6000f));
				
	}
	
	public void addForce(float x, float y) {
		totalForces.add(x, y);
	}	
	
	public void addForce(Vector2 force) {
		if (!moves) return;
		totalForces.add(force);
	}	
	
	private void applyForces() {
		acceleration.set(totalForces);
	}
	
	private void clearForces() {
		totalForces.scl(0);
	}
}
