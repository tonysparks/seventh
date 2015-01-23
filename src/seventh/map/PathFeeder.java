/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import static seventh.math.Vector2f.Vector2fCopy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.ai.basic.Zone;
import seventh.game.PlayerEntity;
import seventh.graph.AStarGraphSearch;
import seventh.graph.GraphNode;
import seventh.math.Rectangle;
import seventh.math.Vector2f;


/**
 * Feeds the next graph node.  This is the path planner for an agent.  This allows an agent to
 * know which tile to move to next
 * 
 * @author Tony
 *
 */
public class PathFeeder<E> {
	
	private MapGraph<E> graph;
	private List<GraphNode<Tile, E>> path;
	private int currentNode;
	private Vector2f destination;
	private Vector2f finalDestination;	
	private Random random;
	
	private class FuzzySearchPath extends AStarGraphSearch<Tile, E> {
		public int actualFuzzy;
		
		@Override
		protected int heuristicEstimateDistance(
				GraphNode<Tile, E> currentNode,
				GraphNode<Tile, E> goal) {			
			Tile currentTile = currentNode.getValue();
			Tile goalTile = goal.getValue();
			
			int distance = ((goalTile.getX() - currentTile.getX()) *
						    (goalTile.getX() - currentTile.getX())) +
						   ((goalTile.getY() - currentTile.getY()) *
						    (goalTile.getY() - currentTile.getY()));
			
			return distance + random.nextInt(actualFuzzy);
		}
	}
	
	private class AvoidSearchPath extends AStarGraphSearch<Tile,E> {
		public List<Zone> zonesToAvoid;
		
		@Override
		protected int heuristicEstimateDistance(
				GraphNode<Tile, E> currentNode,
				GraphNode<Tile, E> goal) {			
			Tile currentTile = currentNode.getValue();
			Tile goalTile = goal.getValue();
			
			int distance = ((goalTile.getX() - currentTile.getX()) *
						    (goalTile.getX() - currentTile.getX())) +
						   ((goalTile.getY() - currentTile.getY()) *
						    (goalTile.getY() - currentTile.getY()));
			
			return distance;
		}
		
		@Override
		protected boolean shouldIgnore(GraphNode<Tile, E> node) {
			Tile tile = node.getValue();
			for(int i = 0; i < zonesToAvoid.size(); i++) {
				Zone zone = zonesToAvoid.get(i);
				if(zone.getBounds().intersects(tile.getBounds())) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	private FuzzySearchPath fuzzySearchPath; 	
	private AvoidSearchPath avoidSearchPath;
	
	/**
	 * @param path
	 */
	public PathFeeder(MapGraph<E> graph) {
		this.graph = graph;
		this.finalDestination = new Vector2f();
		this.destination = new Vector2f();
		this.random = new Random();
		
		this.path = new ArrayList<GraphNode<Tile, E>>();
		this.currentNode = 0;
		
		this.fuzzySearchPath = new FuzzySearchPath();
		this.avoidSearchPath = new AvoidSearchPath();		
	} 
	
	private void setPath(List<GraphNode<Tile, E>> newPath) {
		clearPath();
		if(newPath != null) {
			for(int i = 0; i < newPath.size(); i++) {
				this.path.add(newPath.get(i));
			}
		}
	}
	
	/**
	 * Clears out the path
	 */
	public void clearPath() {
		this.currentNode = 0;
		this.finalDestination.zeroOut();
		this.path.clear();
	}
	
	/**
	 * Finds the optimal path between the start and end point
	 * 
	 * @param start
	 * @param destination
	 */
	public void findPath(Vector2f start, Vector2f destination) {				
		this.fuzzySearchPath.actualFuzzy = 1;
		this.finalDestination.set(destination);
		List<GraphNode<Tile, E>> newPath = this.graph.findPath(this.fuzzySearchPath, start, destination);
		setPath(newPath);
	}
	
	
	/**
	 * Finds a fuzzy (meaning not necessarily the most optimal but different) path between the start and end point
	 * @param start
	 * @param destination
	 * @param the amount of fuzzy the add to the path (the greater the number the less efficient the 
	 * path is to the destination)
	 */
	public void findFuzzyPath(Vector2f start, Vector2f destination, final int fuzzyness) {
		this.fuzzySearchPath.actualFuzzy = Math.max(1, fuzzyness);
		this.finalDestination.set(destination);
		List<GraphNode<Tile, E>> newPath = this.graph.findFuzzyPath(this.fuzzySearchPath, start, destination, fuzzyness);
		setPath(newPath);
	}
	
	/**
	 * Finds a path, avoiding the supplied {@link Zone}s
	 * 
	 * 
	 * @param start
	 * @param destination
	 * @param zonesToAvoid
	 */
	public void findAvoidancePath(Vector2f start, Vector2f destination, List<Zone> zonesToAvoid) {
		this.avoidSearchPath.zonesToAvoid = zonesToAvoid;
		this.finalDestination.set(destination);
		List<GraphNode<Tile, E>> newPath = this.graph.findPathAvoidZones(this.avoidSearchPath, start, destination, zonesToAvoid);
		setPath(newPath);
	}
	
	/**
	 * @return if there is currently a path
	 */
	public boolean hasPath() {
		return !this.path.isEmpty();
	}
	
	/**
	 * @return the final destination
	 */
	public Vector2f getDestination() {
		return this.finalDestination;
	}

	/**
	 * @return the path
	 */
	public List<GraphNode<Tile, E>> getPath() {
		return path;
	}

	/**
	 * @return the current node that the entity is trying to reach
	 */
	public GraphNode<Tile, E> getCurrentNode() {
		if (!path.isEmpty() && currentNode < path.size()) {
			return path.get(currentNode);
		}
		return null;
	}
	
	/**
	 * @return true if this path is on the first node (just started)
	 */
	public boolean onFirstNode() {
		return currentNode == 0 && !path.isEmpty();
	}

	public Vector2f nextDestination(PlayerEntity ent) {
//		destination.x = bounds.x;
//		destination.y = bounds.y;
		Rectangle bounds = ent.getBounds();
		
		destination.zeroOut();
		
		if(! path.isEmpty() && currentNode < path.size() ) {
			GraphNode<Tile, E> node = path.get(currentNode);
			Tile tile = node.getValue();
			
//			if( Math.abs(tile.getX() - (int)currentPosition.x) < 6 
//				&& Math.abs(tile.getY() - (int)currentPosition.y) < 6
//				/*tile.getBounds().contains(currentPosition)*/) {
//				currentNode++;				
//			}
//			destination.x = (tile.getX() - (int)currentPosition.x);
//			destination.y = (tile.getY() - (int)currentPosition.y);
			
//			int centerX = tile.getX() + tile.getWidth()/2;
//			int centerY = tile.getY() + tile.getHeight()/2;
//						
			if(tile.getBounds().intersects(bounds)) {
				currentNode++;			
				
				if(ent.isSprinting()) {
					if(currentNode < path.size()) {
						tile = path.get(currentNode).getValue();
					}
				}
			}
			
			destination.x = (tile.getX() - bounds.x);
			destination.y = (tile.getY() - bounds.y);
		}
		
		return destination;
	}
	
	/**
	 * Gets the next destination vector
	 * @param currentPosition
	 * @return the next destination
	 */
	public Vector2f nextDestination(Vector2f currentPosition) {
		
		Vector2fCopy(currentPosition, destination);
		if(! path.isEmpty() && currentNode < path.size() ) {
			GraphNode<Tile, E> node = path.get(currentNode);
			Tile tile = node.getValue();
			
//			if( Math.abs(tile.getX() - (int)currentPosition.x) < 6 
//				&& Math.abs(tile.getY() - (int)currentPosition.y) < 6
//				/*tile.getBounds().contains(currentPosition)*/) {
//				currentNode++;				
//			}
//			destination.x = (tile.getX() - (int)currentPosition.x);
//			destination.y = (tile.getY() - (int)currentPosition.y);
			
			int centerX = tile.getX();// + tile.getWidth()/2;
			int centerY = tile.getY();// + tile.getHeight()/2;
			
			if(Math.abs(centerX - (int)currentPosition.x) < 6 
			&& Math.abs(centerY - (int)currentPosition.y) < 6) {
				currentNode++;				
			}
			
			destination.x = (int)((tile.getX() - (int)currentPosition.x));
			destination.y = (int)((tile.getY() - (int)currentPosition.y));
//			System.out.println(destination);
		}
		
		return destination;
	}

	/**
	 * @return true if the current position is about the end of the path
	 */
	public boolean atDestination() {
		return (currentNode >= path.size());
	}
}

