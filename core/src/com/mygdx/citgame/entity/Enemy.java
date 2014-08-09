package com.mygdx.citgame.entity;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.citgame.Game;
import com.mygdx.citgame.Node;
import com.mygdx.citgame.Pathfinder;


public class Enemy extends HealthEntity {

	private Vector2 movementOrder;
	
	private Player player;
	
	private float thinkTime = 0;
	private final float THINK_TIME = 0f;//.5f;
	
	private float speed = 500f;
	
	private Pathfinder pathFinder;
	
	private Array<Node> path;
	
	private boolean calculatePath = true;
	
	int nextNode = 0;
	
	public Enemy(MapObjects collisions, float x, float y) {
		super(collisions, x, y);
		
		movementOrder = new Vector2(0, 0);
		
		pathFinder = new Pathfinder();
		
		drag = 10;
		speed *= 2;
		
		noCollideWithWall = true;
		
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
	
	private void followPath(Array<Node> path) {
		
		Vector2 v = new Vector2(0, 0);
		
			if (Game.getNodeFromPosition(center) != path.get(nextNode)) {
				System.out.println(nextNode);
				v = Entity.getVectorBetween(center, path.get(nextNode).position);
			}
			else {
				nextNode ++;
				if (nextNode > path.size - 1) {
					calculatePath = true;
					return;
				}
			}
		
		v.nor();
		
		this.movementOrder.x = v.x * speed;
		this.movementOrder.y = v.y * speed;
		
	}
	
	public void decideAction() {
		
		if (calculatePath) {
			nextNode = 0;
			Node n = Game.getNodeFromPosition(position);
			pathFinder.findPath(n, Game.getNodeFromPosition(player.position));
			
			path = pathFinder.getPath();
			
			System.out.println("calculated path");
			calculatePath = false;
		}
		
		followPath(path);
		
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
