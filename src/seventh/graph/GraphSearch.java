/*
 * see license.txt
 */
package seventh.graph;

/**
 * This interface allows for different search algorithms for graphs to implement.
 * 
 * @author Tony
 *
 */
public interface GraphSearch<E,T> {

    /**
     * A {@link SearchCondition} allows an implementer to listen to each node while
     * it is being traversed by the {@link GraphSearch}.  Upon a condition specified by the
     * implementer, a node can be found.
     * 
     * @author Tonys
     *
     * @param <E>
     * @param <T>
     */
    public interface SearchCondition<E,T> {
        
        /**
         * Determine if the item has been found in the node.
         * 
         * @param node - current node being searched.
         * @return if this node contains the search criteria.
         */
        public boolean foundItem(GraphNode<E,T> node);
    }
    
    
    /**
     * Performs a search on the given graph.
     * 
     * @param graph - graph to search
     * @param condition - condition
     * @return node that was searched for, null if not found.
     */
    public GraphNode<E, T> search(GraphNode<E, T> graph, SearchCondition<E, T> condition);
}
