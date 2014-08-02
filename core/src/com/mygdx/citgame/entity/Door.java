package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;


public class Door extends Entity {

	public boolean closed = true;
	
	public Door(MapObjects collisions, float x, float y) {
		super(collisions, x, y);
		moves = false;
	}
	
	@Override
	protected boolean isObstacle(Entity collider) {
		
		//disables doors for liftable entities that are being lifted
		if (collider instanceof Liftable) {
			if (((Liftable) collider).owner != null) return false;
		}
		
		return closed;
	}
	
}
