package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.utils.Array;

public class Liftable extends Entity {

	public Entity owner;
	
	private static final int DROP_DIST = 60;
	
	public Liftable(MapObjects collisions, float x, float y) {
		super(collisions, x, y);
		
		drag = 4f;
		size *= 1.25f;
	
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
				owner.drag = 5;
				owner = null;
			}
			
		}
				
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
