/*
 *	leola-live 
 *  see license.txt
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.game.Entity;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.game.PlayerInfo;
import seventh.game.vehicles.Vehicle;
import seventh.graph.AStarGraphSearch;
import seventh.graph.GraphNode;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.OBB;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;


/**
 * Feeds the next graph node.  This is the path planner for an agent.  This allows an agent to
 * know which tile to move to next
 * 
 * @author Tony
 *
 */
public class PathPlanner<E> {
	
	private MapGraph<E> graph;
	private List<GraphNode<Tile, E>> path;
	private int currentNode;
	private Vector2f nextWaypoint;
	private Vector2f finalDestination;	
	
	private World world;
	private Brain brain;
	
	private List<Tile> tilesToAvoid, dbg;
	
	private Entity isEntityOnTile(Tile tile) {
		Entity ent = isVehicleOnTile(tile);
		if(ent==null) {
			ent = isTeammateOnTile(tile);
		}
		return ent;
	}
	
	private Entity isTeammateOnTile(Tile tile) {
	    List<Player> teammates = world.getTeammates(brain);
	    PlayerInfo bot = brain.getPlayer();
	    int size = teammates.size();
	    for(int i = 0; i < size; i++) {
	        if(bot.getId() != i) {
    	        Player p = teammates.get(i);
    	        PlayerEntity ent = p.getEntity();
    	        if(p.isAlive()) {
    	            if(tile.getBounds().contains(ent.getCenterPos())) {
    	                return ent;
    	            }
    	        }
	        }
	    }
	    
	    return null;
	}
	
	private Entity isVehicleOnTile(Tile tile) {
	    List<Vehicle> vehicles = world.getVehicles();
	    int size = vehicles.size();
	    for(int i = 0; i < size; i++) {
	        
	        Vehicle v = vehicles.get(i);	        
            if(tile.getBounds().intersects(v.getBounds())) {
            	if(v.getOBB().intersects(tile.getBounds())) {
            		return v;
            	}
            }	        	        
	    }
	    
	    return null;
	}
	
	public static class SearchPath<E> extends AStarGraphSearch<Tile, E> {
		public List<Tile> tilesToAvoid = new ArrayList<>();
		
		@Override
		protected int heuristicEstimateDistance(
				GraphNode<Tile, E> currentNode,
				GraphNode<Tile, E> goal) {			
			Tile currentTile = currentNode.getValue();
			Tile goalTile = goal.getValue();
						
			int dx = Math.abs(currentTile.getX() - goalTile.getX());
			int dy = Math.abs(currentTile.getY() - goalTile.getY());						
			
			final int D = 1;
			//final int D2 = 2;
			
			//distance = D * (dx+dy) + (D2 - 2 * D) * Math.min(dx, dy);
			int distance = D * (dx+dy);
			
			return distance;//
		}
		
		@Override
		protected boolean shouldIgnore(GraphNode<Tile, E> node) {
			boolean ignore = this.tilesToAvoid.contains(node.getValue());
			if(ignore) {
				//System.out.println("Ignoring: " + node.getValue().getXIndex() + "," + node.getValue().getYIndex());
				return true;
			}
			return false;
		}
	}
	
	public static class AvoidSearchPath<E> extends AStarGraphSearch<Tile,E> {
		public List<Tile> tilesToAvoid = new ArrayList<>();
		public List<Zone> zonesToAvoid;
		
		@Override
		protected int heuristicEstimateDistance(
				GraphNode<Tile, E> currentNode,
				GraphNode<Tile, E> goal) {			
			Tile currentTile = currentNode.getValue();
			Tile goalTile = goal.getValue();
			
			int dx = Math.abs(currentTile.getX() - goalTile.getX());
			int dy = Math.abs(currentTile.getY() - goalTile.getY());						
			
			final int D = 1;
			//final int D2 = 2;
			
			//distance = D * (dx+dy) + (D2 - 2 * D) * Math.min(dx, dy);
			int distance = D * (dx+dy);
			
			if(shouldBeAvoided(currentTile)||shouldBeAvoided(goalTile)) {
				distance = Integer.MAX_VALUE;
			}
			
			return distance;
		}
		
		private boolean shouldBeAvoided(Tile tile) {
			for(int i = 0; i < zonesToAvoid.size(); i++) {
				Zone zone = zonesToAvoid.get(i);
				if(zone.getBounds().intersects(tile.getBounds())) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		protected boolean shouldIgnore(GraphNode<Tile, E> node) {
			/*Tile tile = node.getValue();
			for(int i = 0; i < zonesToAvoid.size(); i++) {
				Zone zone = zonesToAvoid.get(i);
				if(zone.getBounds().intersects(tile.getBounds())) {
					return true;
				}
			}*/
			
			return this.tilesToAvoid.contains(node.getValue());
		}
	}
	
	private SearchPath<E> fuzzySearchPath; 	
	private AvoidSearchPath<E> avoidSearchPath;
	
	/**
	 * @param path
	 */
	public PathPlanner(Brain brain, MapGraph<E> graph) {
	    this.brain = brain;
	    this.world = brain.getWorld();
		this.graph = graph;
		this.finalDestination = new Vector2f();
		this.nextWaypoint = new Vector2f();
		
		this.path = new ArrayList<GraphNode<Tile, E>>();
		this.tilesToAvoid = new ArrayList<Tile>();
		this.dbg = new ArrayList<>();
		this.currentNode = 0;
		
		this.fuzzySearchPath = new SearchPath<E>();
		this.avoidSearchPath = new AvoidSearchPath<E>();		
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
		this.tilesToAvoid.clear();
	}
	
	/**
	 * Calculate the estimated cost of the path from the start to destination
	 * 
	 * @param start
	 * @param destination
	 * @return the estimated cost of moving from start to destination
	 */
	public int pathCost(Vector2f start, Vector2f destination) {
		List<GraphNode<Tile, E>> newPath = this.graph.findPath(this.fuzzySearchPath, start, destination);
		int cost = newPath.size() * 32;
		return cost;
	}
	
	/**
	 * Finds the optimal path between the start and end point
	 * 
	 * @param start
	 * @param destination
	 */
	public void findPath(Vector2f start, Vector2f destination) {				
		List<GraphNode<Tile, E>> newPath = this.graph.findPath(this.fuzzySearchPath, start, destination);
		setPath(newPath);
		
		this.finalDestination.set(destination);
	}
	
	public void findPath(Vector2f start, Vector2f destination, List<Tile> tilesToAvoid) {
		this.fuzzySearchPath.tilesToAvoid.clear();
		this.fuzzySearchPath.tilesToAvoid.addAll(tilesToAvoid);
		
		List<GraphNode<Tile, E>> newPath = this.graph.findPath(this.fuzzySearchPath, start, destination);
		setPath(newPath);
		
		this.finalDestination.set(destination);
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

		List<GraphNode<Tile, E>> newPath = this.graph.findPathAvoidZones(this.avoidSearchPath, start, destination, zonesToAvoid);
		setPath(newPath);
		
		this.finalDestination.set(destination);		
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

	
	/**
	 * Retrieves the next way-point on the path.
	 * 
	 * @param ent
	 * @return the next way-point on the path
	 */
	public Vector2f nextWaypoint(PlayerEntity ent) {
		Vector2f cPos = ent.getCenterPos();
		int x = (int)cPos.x;
		int y = (int)cPos.y;
		
		//for(Tile t : dbg) {
			//DebugDraw.fillRectRelative(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 0xff00ffff);
			//DebugDraw.drawRectRelative(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 0xffff00ff);
			//DebugDraw.drawStringRelative("" + t.getXIndex() + "," + t.getYIndex(),t.getX()+16, t.getY()+16, 0xffff00ff);
		//}
		
		nextWaypoint.zeroOut();
		
		if(! path.isEmpty() && currentNode < path.size() ) {
			GraphNode<Tile, E> node = path.get(currentNode);
			Tile tile = node.getValue();
		
			int centerX = tile.getX() + tile.getWidth()/2;
			int centerY = tile.getY() + tile.getHeight()/2;
			if( Math.abs(centerX - x) < 6 
				&& Math.abs(centerY - y) < 6
				//tile.getBounds().contains(currentPosition)
				) {
				currentNode++;
			
//				if(ent.isSprinting()) {
//					if(currentNode < path.size()) {
//						tile = path.get(currentNode).getValue();
//					}
//				}			

				if(currentNode < path.size()) {
					tile = path.get(currentNode).getValue();
				
					Entity entOnTile = isEntityOnTile(tile);
					if(entOnTile != null) {						
						world.getMap().getTilesInRect(entOnTile.getBounds(), tilesToAvoid);
						System.out.println("Length: " + tilesToAvoid.size());
						if(entOnTile.getType().isVehicle()) {
							Vehicle vehicle = (Vehicle) entOnTile;
							OBB oob = vehicle.getOBB();
							for(int i = 0; i < tilesToAvoid.size(); ) {
								Tile t = tilesToAvoid.get(i);
								if(!oob.intersects(t.getBounds())) {
									tilesToAvoid.remove(i);
								}
								else {
									i++;
								}
							}
						}
						
						// TODO: Problem, we are eliminating Orthonogol tiles, which
						// makes diagnol movement legal, since it is removed, we 
						// have to figure out how to make the diagnol movement illegal
						
						System.out.println("New Length: " + tilesToAvoid.size());
						
						dbg.clear();
						dbg.addAll(tilesToAvoid);
						
						
						//tilesToAvoid.add(tile);
						findPath(cPos, this.finalDestination, tilesToAvoid);
						tilesToAvoid.clear();
						return nextWaypoint(ent);
					}
				}
			}
						
//			if(tile.getBounds().intersects(bounds)) {
//				currentNode++;			
//				
//				if(ent.isSprinting()) {
//					if(currentNode < path.size()) {
//						tile = path.get(currentNode).getValue();
//					}
//				}
//			}
			
			nextWaypoint.x = (centerX - x);
			nextWaypoint.y = (centerY - y);
		}
		
		return nextWaypoint;
	}
	
	/**
	 * @return true if the current position is about the end of the path
	 */
	public boolean atDestination() {
		return (currentNode >= path.size());
	}
}

