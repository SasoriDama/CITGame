package com.mygdx.citgame.entity;

import com.badlogic.gdx.math.Rectangle;

public class EntityBounds extends Rectangle {

	public Entity owner;
	
	public EntityBounds(Entity owner) {
		super();
		this.owner = owner;
	}
	
}
