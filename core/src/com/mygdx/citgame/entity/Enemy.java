package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.citgame.Game;


public class Enemy extends HealthEntity {

	private Vector2 movementOrder;
	
	private Player player;
	
	private float thinkTime = 0;
	private final float THINK_TIME = .5f;
	
	private float speed = 500f;
	
	public Enemy(MapObjects collisions, float x, float y) {
		super(collisions, x, y);
		
		movementOrder = new Vector2(0, 0);
	}
	
	public void update(float delta, Array<Entity> entities) {
		super.update(delta, entities);
		
		if (player == null) {
			for (Entity e: entities) {
				if (e instanceof Player) player = (Player) e;
			}
		}
		
		if (thinkTime == 0) {
			this.decideAction();
			thinkTime = THINK_TIME;
		}
		
		if (thinkTime > 0) thinkTime -= delta;
		if (thinkTime < 0) thinkTime = 0;
		
		this.addForce(movementOrder);
		
	}
	
	public void decideAction() {
		Vector2 newMovementOrder;
		
		//newMovementOrder = moveRandomly();
		newMovementOrder = chasePlayer();
		
		this.movementOrder.x = newMovementOrder.x * speed;
		this.movementOrder.y = newMovementOrder.y * speed;
	}
	
	public Vector2 chasePlayer() {
		Vector2 movement;
		movement = Entity.getVectorBetween(position, player.position);
		movement.nor();
		
		return movement;
	}
	
	public Vector2 moveRandomly() {
		
		Vector2 movement;
		
		boolean dirXPositive = Game.RANDOM.nextBoolean();
		boolean dirYPositive = Game.RANDOM.nextBoolean();
		
		float dirX = -1;
		float dirY = -1;
		
		if (dirXPositive) dirX *= -1;
		if (dirYPositive) dirY *= -1;
		
		movement = new Vector2(dirX, dirY);
		return movement;
		
	}
	
	public void patrol() {
		//movementOrder.x = 1000 * (float) Math.sin(MyGdxGame.TIME);
		//movementOrder.nor().scl(600f);
	}
	
	@Override
	protected void reactToEntityCollision(Entity other) {
		
		if (other instanceof Player) {
			
			Vector2 pushForce = new Vector2();
			
			pushForce.set(Entity.getVectorBetween(position, other.position));
			pushForce.nor();
			pushForce.scl(10000f);
			
			other.addForce(pushForce);
			
			((HealthEntity) other).hurt(1);
			((HealthEntity) other).hurtTime = .4f;
			return;
			
		}
		
		super.reactToEntityCollision(other);
		
	}
	
	@Override
	public void remove() {
	
	}
	
}
