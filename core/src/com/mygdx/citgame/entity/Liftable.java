package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Liftable extends Entity {

	public Player owner;
	
	private static final int DROP_DIST = 60;
	
	private Rectangle goal;
	
	public Liftable(MapObjects collisions, float x, float y, Rectangle goal) {
		super(collisions, x, y);
		
		drag = 4f;
		size *= 1.25f;
	
		this.goal = goal;
	}

	@Override
	protected void reactToEntityCollision(Entity other) {
		
		if (other instanceof Player) {
			if (owner != null) return;
		}
		
		super.reactToEntityCollision(other);
	}
	
	@Override
	public void update(float delta, Array<Entity> entities) {
		super.update(delta, entities);
		
		if (owner != null) {
			//has the liftable entity follow the entity carrying it
			this.addForce(Entity.getVectorBetween(position, owner.position).scl(20f));
			//slows the entity carrying it
			owner.drag = 13.5f;
			//detach from player if the player goes too far
			if (owner.position.dst(position) > DROP_DIST) {
				clearOwner();
			}
			
			if (goal.contains(bounds)) {
				owner.score ++;
				owner.hasTask = false;
				removed = true;
			}
			
		}
				
	}
	
	private void clearOwner() {
		owner.drag = 5f;
		owner = null;
	}
	
	@Override 
	public void remove() {
		this.clearOwner();
	}
	
	@Override
	public boolean isObstacle(Entity collider) {
		if (collider instanceof Player) {
			if (owner == null) return true;
			else return false;
		}
		return true;
	}
	
}
