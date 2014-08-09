package com.mygdx.citgame;

import com.badlogic.gdx.utils.Array;

public class Pathfinder {

	private Array<Node> closedNodes, openNodes, path;
	
	private Node startTile, goalTile;
	
	public Pathfinder() {
		
		
	}
	
	public void findPath(Node startTile, Node goalTile) {
		
		openNodes = new Array<Node>();
		closedNodes = new Array<Node>();
		path = new Array<Node>();
		
		this.startTile = startTile;
		this.goalTile = goalTile;
		
		addNodeToOpen(startTile);
		
		Node curTile = startTile;
		
		while (openNodes.size > 0) {
			this.addNodeToOpen(curTile);
			for (Node t: curTile.getSurroundingNodes()) {
				this.addNodeToOpen(t);
			}
			
			curTile = findClosestNode(openNodes);
			
			openNodes.removeValue(curTile, false);
			closedNodes.add(curTile);
			path.add(curTile);
			
			if (curTile == goalTile) break;
		
		}
		
		
	}
	
	private Node findClosestNode(Array<Node> openNodes) {
		
		Node closestNode = openNodes.get(0);
		int smallestDistance = Node.manhattanDistance(closestNode, goalTile);
		
		int distance;
		
		for (Node n: openNodes) {
			distance = Node.manhattanDistance(n, goalTile);
			if (distance < smallestDistance) smallestDistance = distance;
		}
		
		for (Node n: openNodes) {
			distance = Node.manhattanDistance(n, goalTile);
			if (distance == smallestDistance) n = closestNode;
		}
		
		return closestNode;
	}
	
	public Array<Node> getPath() {
		for (Node n: closedNodes) {
			n.open = true;
		}
		return path;
	}
	
	private void addNodeToOpen(Node node) {
		if (closedNodes.contains(node, false) || openNodes.contains(node, false)) return;
		
		openNodes.add(node);
		
		
	}
	
}
