/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import static seventh.math.Vector2f.Vector2fCopy;

import java.util.List;

import seventh.graph.GraphNode;
import seventh.math.Vector2f;


/**
 * Feeds the next graph node
 * 
 * @author Tony
 *
 */
public class PathFeeder<E> {
	private List<GraphNode<Tile, E>> path;
	private int currentNode;
	private Vector2f destination;
	
	
	
	/**
	 * @param path
	 */
	public PathFeeder(List<GraphNode<Tile, E>> path) {
		this.path = path;//path.size() > 2 ? path.subList(1, path.size()): path;
		this.currentNode = 0;
		this.destination = new Vector2f(); 
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
			
			if( Math.abs(tile.getX() - (int)currentPosition.x) < 6 
				&& Math.abs(tile.getY() - (int)currentPosition.y) < 6
				/*tile.getBounds().contains(currentPosition)*/) {
				currentNode++;				
			}
			
			destination.x = (tile.getX() - (int)currentPosition.x);
			destination.y = (tile.getY() - (int)currentPosition.y);
		}
		
		return destination;
	}

	/**
	 * @return true if the current position is about the end of the path
	 */
	public boolean atDestination() {
		if (currentNode == path.size())
			return true;
		return false;
	}
}

