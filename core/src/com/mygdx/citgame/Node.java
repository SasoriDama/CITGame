package com.mygdx.citgame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Node {

	public Vector2 coords, position;
	
	public int score;
	
	public boolean open = false;
	
	public Node(int x, int y, int size) {
		this.coords = new Vector2(x, y);
		this.position = new Vector2((x * size) + size/2, (y * size) + size/2);
	}
	
	public Array<Node> getSurroundingNodes() {
		Array<Node> nearby = new Array<Node>();
		
		int x = (int) coords.x;
		int y = (int) coords.y;
		
		
		nearby.add(Game.getNodes(x + 1, y));
		nearby.add(Game.getNodes(x, y + 1));
		nearby.add(Game.getNodes(x + 1, y + 1));
		
		nearby.add(Game.getNodes(x - 1, y));
		nearby.add(Game.getNodes(x, y - 1));
		nearby.add(Game.getNodes(x - 1, y - 1));
		
		nearby.add(Game.getNodes(x + 1,  y - 1));
		nearby.add(Game.getNodes(x - 1, y + 1));
		
		for (Node n: nearby) {
			if (n == null) nearby.removeValue(n, false);
		}
		
		return nearby;
	}
	
	public static int manhattanDistance(Node node, Node goalTile) {
		return (int) (Math.abs(goalTile.coords.x - node.coords.x) + Math.abs(goalTile.coords.y - node.coords.y));
	}
	
}
