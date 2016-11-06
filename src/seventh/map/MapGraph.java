/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import java.util.List;
import java.util.Random;

import seventh.ai.basic.Zone;
import seventh.game.Game;
import seventh.graph.AStarGraphSearch;
import seventh.graph.GraphNode;
import seventh.graph.GraphSearchPath;
import seventh.math.Vector2f;

/**
 * A {@link GraphNode} of the {@link Map}
 * 
 * @author Tony
 *
 */
@SuppressWarnings("all")
public class MapGraph<T> {

	public GraphNode[][] graph;
	private seventh.map.Map map;
	private Random random;
	private int width, height;
	private GraphSearchPath<Tile, T> defaultSearchPath;
	/**
	 * 
	 */
	public MapGraph(Map map, GraphNode[][] graph) {
		this.map = map;
		this.graph = graph;
		
		this.height = graph.length;
		this.width = graph[0].length;
		
		this.random = new Random();
		
		this.defaultSearchPath = new AStarGraphSearch<>();
	}
	
	public void removeNode(int x, int y) {
		GraphNode<Tile, T> node = graph[y][x];
		if(node != null) {
			node.edges().removeEdges();
			graph[y][x] = null;
		}
	}
	
	public void addNode(int x, int y) {
		Tile tile = this.map.getTile(0, x, y);
		if(tile != null) {
			GraphNode<Tile, T> node = new GraphNode<Tile, T>(tile);
			graph[y][x] = node;
			this.map.addNode(null, graph, node, x, y);
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @return get the {@link GraphNode} by the x and y index (not world coordinates)
	 */
	public GraphNode<Tile, T> getNodeByIndex(int x, int y) {
		return (GraphNode<Tile, T>)graph[y][x];
	}
	
	/**
	 * @param wx
	 * @param wy
	 * @return the graph node at a world coordinate
	 */
	public GraphNode<Tile, T> getNodeByWorld(int wx, int wy) {				
		int tileOffset_x = 0;// (wx % map.getTileWidth());
		int x = (tileOffset_x + wx) / map.getTileWidth();

		int tileOffset_y = 0; //(wy % map.getTileHeight());
		int y = (tileOffset_y + wy) / map.getTileHeight();
		
		if(map.checkTileBounds(x, y)) {
			return null;
		}
		
		return x<width && y<height ? (GraphNode<Tile, T>)graph[y][x] : null;
	}
	
	public GraphNode<Tile, T> getNearestNodeByWorld(Vector2f pos) {
		return getNearestNodeByWorld((int)pos.x, (int)pos.y);
	}
	
	public GraphNode<Tile, T> getNearestNodeByWorld(int wx, int wy) {
		GraphNode<Tile, T> node = getNodeByWorld(wx, wy);
		
		if(node != null) return node;
		
		node = getNodeByWorld(wx+map.getTileHeight(), wy);
				
		if(node != null) return node;
		
		node = getNodeByWorld(wx-map.getTileHeight(), wy);
		
		if(node != null) return node;
		
		node = getNodeByWorld(wx, wy + map.getTileWidth());
		
		if(node != null) return node;
		
		node = getNodeByWorld(wx, wy - map.getTileWidth());
		
		if(node != null) return node;
		
		node = getNodeByWorld(wx + map.getTileHeight(), wy + map.getTileWidth());
		
		if(node != null) return node;
		
		node = getNodeByWorld(wx - map.getTileHeight(), wy - map.getTileWidth());
		
		if(node != null) return node;
		
		node = getNodeByWorld(wx - map.getTileHeight(), wy + map.getTileWidth());
	
		if(node != null) return node;
		
		node = getNodeByWorld(wx + map.getTileHeight(), wy - map.getTileWidth());
		
		return node;
	}

	
	/**
	 * Calculate the estimated cost of the path from the start to destination
	 * 
	 * @param start
	 * @param destination
	 * @return the estimated cost of moving from start to destination
	 */
	public int pathCost(Vector2f start, Vector2f destination) {
		List<GraphNode<Tile, T>> newPath = this.findPath(this.defaultSearchPath, start, destination);
		int cost = newPath.size() * 32;
		return cost;
	}
	
	/**
	 * Finds a path, avoiding the supplied {@link Zone}s
	 * 
	 * 
	 * @param start
	 * @param destination
	 * @param zonesToAvoid
	 * @return the list of node to travel to reach the destination
	 */
	public List<GraphNode<Tile, T>> findPathAvoidZones(GraphSearchPath<Tile, T> searchPath, Vector2f start, Vector2f destination, final List<Zone> zonesToAvoid) {						
		GraphNode<Tile, T> startNode = getNearestNodeByWorld(start);
		GraphNode<Tile, T> destNode = getNearestNodeByWorld(destination);
		List<GraphNode<Tile, T>> resultPath = searchPath.search(startNode, destNode);
		return resultPath; 
	}
	
	/**
	 * Finds a fuzzy (meaning not necessarily the most optimal but different) path between the start and end point
	 * 
	 * @param start
	 * @param destination
	 * @return the list of node to travel to reach the destination
	 */
	public List<GraphNode<Tile, T>> findPath(GraphSearchPath<Tile, T> searchPath, Vector2f start, Vector2f destination) {							
		GraphNode<Tile, T> startNode = getNearestNodeByWorld(start);		
		GraphNode<Tile, T> destNode = getNearestNodeByWorld(destination);
		List<GraphNode<Tile, T>> resultPath = searchPath.search(startNode, destNode);
		return resultPath; 
	}

}
