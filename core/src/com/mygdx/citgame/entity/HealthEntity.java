package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.utils.Array;

public class HealthEntity extends Entity {

	public int health = 3;
	
	public float hurtTime;
	
	public HealthEntity(MapObjects collisions, float x, float y) {
		super(collisions, x, y);
	}

	@Override
	public void update(float delta, Array<Entity> entities) {
		super.update(delta, entities);
		
		if (hurtTime > 0) hurtTime -= delta;
		if (hurtTime < 0) hurtTime = 0;
		
	}
	
	public void hurt(int amt) {
		if (hurtTime > 0) return;
		this.health -= amt;
		if (health <= 0) removed = true;
	}
	
}
