package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.mygdx.citgame.Game;

public class DestructableContainer extends HealthEntity {

	public static enum Type {
		RANDOM,
		KEY;
	}
	
	public Type type;
	
	public DestructableContainer(MapObjects collisions, float x, float y) {
		super(collisions, x, y);
		moves = false;
		health = 1;
		type = Type.RANDOM;
	}

	@Override
	public void remove() {
		
		if (type == Type.RANDOM) {
			
			if (Game.RANDOM.nextInt(10) < 3) {
				Game.addEntity(new Item(collisions, position.x, position.y, Item.Type.HEART));
			}
			return;
		}
	
		if (type == Type.KEY) {
			Game.addEntity(new Item(collisions, position.x, position.y, Item.Type.KEY));
			return;
		}
		
	
	}
	
}
