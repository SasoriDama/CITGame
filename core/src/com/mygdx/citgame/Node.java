package com.mygdx.citgame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Node {

	public Vector2 coords, position;
	
	public int index = 0;
	
	public int h, g;
	
	public boolean open = false;
	
	public boolean passable = true;
	
	public Node parent = null;
	
	private Rectangle bounds;
	
	public Node(int x, int y, int size) {
		this.coords = new Vector2(x, y);
		this.position = new Vector2(x * size, y * size);
		this.bounds = new Rectangle((int) position.x, (int) position.y, size, size);
		//reset position to center
		position.set((x * size) + size/2, (y * size) + size/2);
	}
	
	public Array<Node> getSurroundingNodes() {
		Array<Node> nearby = new Array<Node>();
		
		int x = (int) coords.x;
		int y = (int) coords.y;
		
		
		nearby.add(Game.getNodes(x + 1, y));
		nearby.add(Game.getNodes(x, y + 1));
		nearby.add(Game.getNodes(x - 1, y));
		nearby.add(Game.getNodes(x, y - 1));
		
		
		nearby.add(Game.getNodes(x - 1, y - 1));
		nearby.add(Game.getNodes(x + 1, y + 1));
		nearby.add(Game.getNodes(x + 1,  y - 1));
		nearby.add(Game.getNodes(x - 1, y + 1));

		return nearby;
	}
	
	public void setPassable(Rectangle collisionObject) {
		if (collisionObject.overlaps(bounds)) passable = false;
	}
	
	public void setCosts(Node parent, Node goal) {
		g = Node.manhattanDistance(this, parent);
		h = Node.manhattanDistance(this, goal);
	}
	
	public int getFCost() {
		return g + h;
	}
	
	public static int manhattanDistance(Node node, Node target) {
		return (int) (Math.abs(target.coords.x - node.coords.x) + Math.abs(target.coords.y - node.coords.y));
	}
	
}
