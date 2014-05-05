/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import java.util.ArrayList;
import java.util.List;

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

	private GraphNode[][] graph;
	private seventh.map.Map map;
		
	private int width, height;
	/**
	 * 
	 */
	public MapGraph(Map map, GraphNode[][] graph) {
		this.map = map;
		this.graph = graph;
		
		this.height = graph.length;
		this.width = graph[0].length;		
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
		int tileOffset_x = 0;//(wx % map.getTileWidth());
		int x = (tileOffset_x + wx) / map.getTileWidth();

		int tileOffset_y = 0;//(wy % map.getTileHeight());
		int y = (tileOffset_y + wy) / map.getTileHeight();
		return x<width && y<height ? (GraphNode<Tile, T>)graph[y][x] : null;
	}

	/**
	 * Finds the optimal path between the start and end point
	 * @param start
	 * @param destination
	 * @return the list of node to travel to reach the destination
	 */
	public PathFeeder<T> findPath(Vector2f start, Vector2f destination) {				
		GraphSearchPath<Tile, T> searchPath = new AStarGraphSearch<Tile,T>(){
			/* (non-Javadoc)
			 * @see graph.AStarGraphSearch#heuristicEstimateDistance(graph.GraphNode, graph.GraphNode)
			 */
			@Override
			protected int heuristicEstimateDistance(
					GraphNode<Tile, T> currentNode,
					GraphNode<Tile, T> goal) {			
				Tile currentTile = currentNode.getValue();
				Tile goalTile = goal.getValue();
				
				int distance = ((currentTile.getX() - goalTile.getX()) *
							    (currentTile.getX() - goalTile.getX())) +
							   ((currentTile.getY() - goalTile.getY()) *
							    (currentTile.getY() - goalTile.getY()));
				
				return distance;
			}
		};
		
				
		GraphNode<Tile, T> startNode = getNodeByWorld((int)start.x, (int)start.y);
		GraphNode<Tile, T> destNode = getNodeByWorld((int)destination.x, (int)destination.y);
		List<GraphNode<Tile, T>> resultPath = searchPath.search(startNode, destNode);
		return new PathFeeder(resultPath == null ? new ArrayList<GraphNode<Tile,T>>() : resultPath); 
	}	
}
