/*
 * see license.txt
 */
package seventh.graph;

import java.util.List;

/**
 * Searches for the path from a given start node to an end node.
 * 
 * @author Tony
 *
 */
public interface GraphSearchPath<E,T> {

    /**
     * Finds the path from the start node to the end goal node.
     * 
     * @param start - starting point
     * @param goal - ending point
     * @return a list of nodes that link the start and end node, null if no path exists.
     */
    public List<GraphNode<E,T>> search(GraphNode<E,T> start, GraphNode<E,T> goal);
}
