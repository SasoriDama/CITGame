package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends HealthEntity {
	
	public int score = 0;
	
	public float attackTime = 0;
	
	//FIGURE OUT HOW THIS WORKS WITH SCALE
	public final int ATTACK_RANGE = 80;
	
	public int keys = 0;
	
	public boolean hasTask = false;
	
	public Player(MapObjects collisions, int x, int y) {
		super(collisions, x, y);
	}

	public void update(float delta, Array<Entity> entities) {
		super.update(delta, entities);
		
		if (attackTime > 0) attackTime -= delta;
		if (attackTime < 0) attackTime = 0;
		
	}

	@Override
	protected void reactToEntityCollision(Entity other) {
		
		if (other instanceof Enemy) {
			return;
		}
		
		if (other instanceof Liftable) {
			Liftable l = ((Liftable) other);
			l.owner = this;
			return;
		}
		
		if(other instanceof Door) {
			Door d = ((Door) other);
			if (d.closed) {
				if (keys > 0) {
					keys --;
					d.closed = false;
				}
			}
		}
		
		if (other instanceof Item) {
		
			if (((Item) other).type == Item.Type.KEY) keys ++;
			
			if (((Item) other).type == Item.Type.HEART) health ++;
			
		
			other.removed = true;
		}
		
		super.reactToEntityCollision(other);
		
	}
	
	public void meleetAttack(Array<Entity> entities) {
		//NOT REAL
		//Game.addEntity(new Projectile(collisions, position.x, position.y, 1));
		
		if (attackTime > 0) return;
		
		attackTime = .5f;
		
		this.velocity.scl(0);
		
		float distance;
		Vector2 pushForce = new Vector2();
		
		for (Entity e: entities) {
			
			if (!(e instanceof HealthEntity)) continue;
			if (e == this) continue;
			
			HealthEntity he = (HealthEntity) e;
			
			if (he.hurtTime > 0) continue;
			
			distance = position.dst(he.position);
			if (distance > ATTACK_RANGE) continue;
			
			pushForce.set(Entity.getVectorBetween(position, he.position));
			pushForce.scl(500f);
				
			he.addForce(pushForce);
			he.hurt(1);
			he.hurtTime = .25f;
		}
	}
	
}
