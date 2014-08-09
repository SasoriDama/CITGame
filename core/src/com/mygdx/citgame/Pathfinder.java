package com.mygdx.citgame;

import com.badlogic.gdx.utils.Array;

public class Pathfinder {

	private Array<Node> closedNodes, openNodes, path;
	
	private Node startTile, goalTile;
	
	private int cost = 0;
	
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
			
			for (Node n: openNodes) {
				n.setCosts(curTile, goalTile);
				n.parent = curTile;
			}
			
			curTile = findClosestNode(openNodes);
			
			if (curTile == goalTile) break;
			
			openNodes.removeValue(curTile, false);
			closedNodes.add(curTile);
			
			for (Node n: curTile.getSurroundingNodes()) {
				this.addNodeToOpen(n);
			}
			
		}
		
		constructPath(curTile, startTile);
		
	}
	
	public void constructPath(Node end, Node start) {
		path.add(end);
		
		int i = 0;
		
		while (path.get(i).parent != null && !path.contains(start, false)) {
			path.add(path.get(i).parent);
			path.get(i).index = i;
			i++;
		}
		
		System.out.println(path.size);
		path.reverse();
	}
	
	
	private Node findClosestNode(Array<Node> openNodes) {
		
		Node closestNode = openNodes.get(0);
		int smallestF = closestNode.getFCost();
		
		int f;
		
		for (Node n: openNodes) {
			f = Node.manhattanDistance(n, goalTile);
			if (f < smallestF) smallestF = f;
		}
		
		for (Node n: openNodes) {
			f = Node.manhattanDistance(n, goalTile);
			if (f == smallestF) closestNode = n;
		}
		
		return closestNode;
	}
	
	/*
	private Node findClosestNode(Node curTile, Array<Node> openNodes) {
		
		Node closestNode = openNodes.get(0);
		int smallestDistance = Node.manhattanDistance(closestNode, goalTile) + Node.manhattanDistance(curTile, closestNode);
		
		int distance;
		
		for (Node n: openNodes) {
			distance = Node.manhattanDistance(n, goalTile) + Node.manhattanDistance(curTile, n);
			if (distance < smallestDistance) smallestDistance = distance;
		}
		
		for (Node n: openNodes) {
			distance = Node.manhattanDistance(n, goalTile) + Node.manhattanDistance(curTile, n);
			if (distance == smallestDistance) n = closestNode;
		}
		
		return closestNode;
	}
	*/
	
	public Array<Node> getPath() {
		
		for (Node n: closedNodes) {
			n.open = false;
			n.g = 0;
			n.h = 0;
			n.parent = null;
		}
		
		for (Node n: openNodes) {
			n.open = false;
			n.g = 0;
			n.h = 0;
			n.parent = null;
		}
		
		
		for (Node n: path) {
			n.open = true;
		}
		
		return path;
	}
	
	private void addNodeToOpen(Node node) {
		if (node == null) return;
		if (closedNodes.contains(node, false) || openNodes.contains(node, false)) return;
		if (!node.passable) return;
		
		openNodes.add(node);
		
		
	}
	
}
