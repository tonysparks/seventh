/*
 * see license.txt
 */
package seventh.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import seventh.graph.Edges.Directions;
import seventh.shared.ArrayMap;

/**
 * Uses the A* (A-Star) optimal-path searching algorithm.
 * 
 * @author Tony
 *
 */
public class AStarGraphSearch<E, T> implements GraphSearchPath<E, T> {

    private Map<GraphNode<E,T>, Integer>  gScores   /* Distance from start to optimal path */
                                        , hScores   /* Heuristic scores */
                                        , fScores;  /* Sum of heuristic from node to goal */

    private Map<GraphNode<E,T>, GraphNode<E,T>> cameFrom;  /* Nodes visited to reach goal node */


    private Set<GraphNode<E,T>>  closedSet  /* List of nodes we do not care about anymore */
                               , openSet;   /* Working set of nodes to be tested */
    
    /**
     * 
     */
    public AStarGraphSearch() {
        boolean jdk = false;
        
        // TODO - Continue testing working on ArrayMap
        // removing GC from HashMap Entries
        if(jdk) {
            gScores = new HashMap<GraphNode<E,T>, Integer>();    
            hScores = new HashMap<GraphNode<E,T>, Integer>();            
            fScores = new HashMap<GraphNode<E,T>, Integer>();    
            
            
            cameFrom = new HashMap<GraphNode<E,T>, GraphNode<E,T>>();
        }
        else {
            gScores = new ArrayMap<>();
            hScores = new ArrayMap<GraphNode<E,T>, Integer>();
            fScores = new ArrayMap<>();
            cameFrom = new ArrayMap<GraphNode<E,T>, GraphNode<E,T>>();
        }
        
        closedSet = new HashSet<GraphNode<E,T>>();
        openSet   = new HashSet<GraphNode<E,T>>();
    }
    
    /*
     * (non-Javadoc)
     * @see leola.live.game.graph.GraphSearchPath#search(leola.live.game.graph.GraphNode, leola.live.game.graph.GraphNode)
     */
    public List<GraphNode<E,T>> search(GraphNode<E, T> start, GraphNode<E, T> goal) {
        if(start == null || goal == null) {
            return null;
        }
        
        return aStar(start, goal);
    }
    
    /**
     * Calculate the heuristic distance between the currentNode and the goal node.
     *  
     * @param currentNode
     * @param goal
     * @return
     */
    protected int heuristicEstimateDistance(GraphNode<E, T> startNode, GraphNode<E, T> currentNode, GraphNode<E, T> goal) {
        return 0; /* Make the H value 0, this essentially makes the A* algorithm Dijkstra's algorithm */
    }
    
    /**
     * If this node should be ignored
     * 
     * @param node
     * @return true if this node should be ignored
     */
    protected boolean shouldIgnore(GraphNode<E, T> node) {
        return false;
    }
    
    /**
     * Retrieves the lowest score (Heuristic 'H') in the supplied set from the map of total scores ('F').
     * 
     * <p>
     * To optimize performance, convert the openSet (set) into a PriorityQueue so this method call
     * turns into a O(1) versus a O(n) call.
     * 
     * @param set - set to retrieve the lowest scored node
     * @param fScores - map containing the heuristic scores
     * @return the lowest heuristic {@link GraphNode} in the supplied set.
     */
    private GraphNode<E,T> getLowestScore(Set<GraphNode<E,T>> set, Map<GraphNode<E,T>, Integer> fScores) {
        GraphNode<E,T> lowerScore = null;
        
        /*
         * Search for the lowest H score.
         */
        for(GraphNode<E,T> node : set) {
            
            /* Set the first node to the lowest */
            if ( lowerScore == null ) {
                lowerScore = node;                
                continue;
            }
            
            int score = fScores.get(node);
            
            /* Check to see if this node has a lower H score */
            if ( score < fScores.get(lowerScore)) {
                lowerScore = node;
            }            
        }
        
        return lowerScore;
    }
    
    
    /**
     * Reconstructs the path from the start to finish nodes.
     * 
     * @param <E>
     * @param <T>
     * @param cameFrom - path traversed
     * @param currentNode - current node
     * @param result - list of {@link GraphNode}s needed to reach the goal
     * @return the result list - for convience
     */
    private List<GraphNode<E,T>> reconstructPath(Map<GraphNode<E,T>, GraphNode<E,T>> cameFrom, GraphNode<E,T> currentNode, List<GraphNode<E,T>> result) {
        
        /* If we visited this node, add it to the result set */
        if ( cameFrom.containsKey(currentNode) ) {
            
            /* Find the rest of the path */
            reconstructPath(cameFrom, cameFrom.get(currentNode), result);
            
            result.add(currentNode);    /* Notice this is after the recursive call - we want the results to be in descending order */
            
            return result;
        }
        
        return result;
    }
    

    /**
     * Find the most optimal path to the goal node. The best (or most optimal) path is calculated by the A* (A-Star) algorithm. 
     * 
     * @param start - starting node
     * @param goal - ending node
     * @return the optimal node traversal from start to goal nodes.  null if no path found.
     */
    private List<GraphNode<E,T>> aStar(GraphNode<E,T> start, GraphNode<E,T> goal) {
        gScores.clear();    
        hScores.clear();            
        fScores.clear();                    
        cameFrom.clear();        
        closedSet.clear();
        openSet.clear();
        
        
        /* Push the start node so we have a starting point */
        openSet.add(start);
        
        gScores.put(start, 0);                                          /* No other possibility, thus 0 to denote optimal path */
        hScores.put(start, heuristicEstimateDistance(start, start, goal));     /* Guess the cost from start to goal nodes */
        fScores.put(start, hScores.get(start));                         /* Store the sum of the cost 0 + X = X */
        
        /*
         * Until we run out of nodes of interest, lets compile our path.  If there
         * are no more nodes of interest, and we have not found our goal node, this means
         * there is no path.
         */
        while( ! openSet.isEmpty() ) {
            
            /* Get the most optimal node to work from */
            GraphNode<E, T> x = getLowestScore(openSet, fScores);
            
            /* If this node is the goal, we are done */
            if ( x == goal ) {
                
                /* optimal path from start to finish */               
                return reconstructPath(cameFrom, goal, new ArrayList<GraphNode<E, T>>());
            }
            
            /* Remove this node so we don't visit it again */
            openSet.remove(x);
            closedSet.add(x);
            
            
            /*
             * For each neighbor (the nodes edges contain the neighbors)
             */
            Edges<E, T> edges = x.edges();            
            int skipMask = 0;
            
            for(int i = 0; i < edges.size(); i++) {
                Edge<E,T> edge = edges.get(i);     
                if(edge == null) {
                    continue;
                }
                
                GraphNode<E, T> y = edge.getRight();
                
                /* If this node has been visited before, ignore it and move on */
                if ( y == null || closedSet.contains(y)) {
                    continue;
                }
                
                Directions dir = Directions.fromIndex(i);
                if(shouldIgnore(y)) {
                    if(Directions.isCardinal(i)) {
                        switch(dir) {
                            case N:
                                skipMask |= Directions.NE.getMask();
                                skipMask |= Directions.NW.getMask();
                                break;
                            case E:
                                skipMask |= Directions.NE.getMask();
                                skipMask |= Directions.SE.getMask();
                                break;
                            case S:
                                skipMask |= Directions.SE.getMask();
                                skipMask |= Directions.SW.getMask();
                                break;
                            case W:                                
                                skipMask |= Directions.NW.getMask();
                                skipMask |= Directions.SW.getMask();
                                break;
                            default:
                        }
                    }
                    continue;
                }
                
                if((dir.getMask() & skipMask) != 0) {
                    continue;
                }
                
                /* Compile the shortest distance traveled between x and y plus the sum scores*/
                int tentativeGscore = gScores.get(x) + edge.getWeight();
                boolean tentativeIsBetter = false;
                
                /* If this neighbor has not been tested, lets go ahead and add it */
                if ( ! openSet.contains(y)) {
                    openSet.add(y);
                    
                    /* Calculate the heuristic to determine if this direction is the most optimal */
                    hScores.put(y, heuristicEstimateDistance(start, y, goal));
                    tentativeIsBetter = true;
                }                                   
                /* The neighbor is waiting to be tested (in the openSet) so test to see if the distance
                 * from x to y is better than y to goal.  If this neighbor is being visited from another
                 * parent, it might be a more optimal path so test for that.
                 */
                else if ( gScores.containsKey(y) && tentativeGscore < gScores.get(y)) {
                    tentativeIsBetter = true;
                }
                
                /*
                 * If traveling to this neighbor is cheaper than from another node, override
                 * the path to this one.  If this is the first time this neighbor is to be
                 * visited, place it in our path.
                 */
                if ( tentativeIsBetter ) {
                    cameFrom.put(y, x);                                 /* remember our path */
                    gScores.put(y, tentativeGscore);                    /* Remember our score */
                    fScores.put(y, tentativeGscore + hScores.get(y));   /* remember the total score */
                }
            }
            
        }
        
        return null;    /* No path found */
        
    }
}
