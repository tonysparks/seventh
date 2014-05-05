/*
 * 
 * see license.txt
 */
package seventh.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Performs a Depth-First search on the given graph.
 * 
 * @author Tony
 *
 */
public class DepthFirstGraphSearch<E,T> implements GraphSearch<E,T> {

    /*
     * (non-Javadoc)
     * @see leola.live.game.graph.GraphSearch#search(leola.live.game.graph.GraphNode, leola.live.game.graph.GraphSearch.SearchCondition)
     */
    public GraphNode<E,T> search(GraphNode<E,T> graph, SearchCondition<E,T> condition) {
        return search(graph, condition, new HashSet<GraphNode<E,T>>());
    }

    /**
     * Does a depth first search for the root node.
     * 
     * @param node
     * @param visited
     * @return
     */
    private GraphNode<E,T> search(GraphNode<E,T> node, SearchCondition<E,T> condition, Set<GraphNode<E,T>> visited ) {
        if ( !visited.contains(node)) {
            visited.add(node);
            
            /* If we reached the node of interest, return it */
            if ( condition.foundItem(node) ) {
                return node;
            }
            
            /* Search each neighbor for the root */
            Iterator<Edge<E,T>> it = node.edges();
            while(it.hasNext()) {
                Edge<E,T> edge = it.next();

                /* Search the neighbor node */
                GraphNode<E,T> rightNode = edge.getRight();
                if ( rightNode !=null && !visited.contains(rightNode) ) {
                    return search(rightNode, condition, visited);
                }
            }    
        }        
        
        return null; /* Not found */
    }
}
