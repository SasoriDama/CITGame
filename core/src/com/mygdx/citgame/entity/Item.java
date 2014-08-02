package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;

public class Item extends Entity {

	public static enum Type {
		KEY, HEART;
	}
	
	public Type type;
	
	public Item(MapObjects collisions, float x, float y, Type type) {
		super(collisions, x, y);
		moves = false;
		this.type = type;
	}
	
	@Override
	public boolean isObstacle(Entity collider) {
		return false;
	}
	
}
