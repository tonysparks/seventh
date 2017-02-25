/*
 * see license.txt
 */
package seventh.graph;

import seventh.graph.Edges.Directions;

/**
 * A node in a graph linked by {@link Edge}s.
 * 
 * @author Tony
 *
 */
public class GraphNode<E, T> {

    private Edges<E, T> edges;
    private E value;
    
    /**
     * Constructs a {@link GraphNode}.
     * 
     * @param value
     */
    public GraphNode(E value) {
        this.edges = new Edges<E, T>();
        this.value = value;
    }
    
    
    /**
     * Constructs a {@link GraphNode}.
     */
    public GraphNode() {
        this(null);
    }
    
    
    /**
     * Adds an {@link Edge}.
     * 
     * @param edge
     */
    public void addEdge(Directions dir, Edge<E,T> edge) {
        this.edges.addEdge(dir, edge);
    }
    
    public void removeEdge(Directions dir) {
        this.edges.removeEdge(dir);
    }
    public void removeEdge(Edge<E,T> edge) {
        this.edges.removeEdge(edge);
    }
    
    /**
     * @return the value
     */
    public E getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(E value) {
        this.value = value;
    }

    /**
     * Get the graphs {@link Edge}s.
     * 
     * @return
     */
    public Edges<E, T> edges() {
        return this.edges;
    }
    
    /**
     * Finds the {@link Edge} that links this node with the
     * rightNode.
     * 
     * <p>
     * Note:  The Edge is found by testing pointer equality not Object equality (i.e., '==' and not .equals()).
     * 
     * @param rightNode - other node that is linked with this node
     * @return the edge that links the two nodes, null if no such nodes exists.
     */
    public Edge<E,T> getEdge(GraphNode<E,T> rightNode) {
        Edge<E,T> result = null;
        
        /* Search for the matching Edge (the one that links the right and left) */
        Edges<E, T> edges = edges();
        for(int i = 0; i < edges.size(); i++) {
            Edge<E,T> edge = edges.get(i);
            if(edge == null) {
                continue;
            }
            
            /* Notice we check if the REFERENCE equals the right node */
            if (edge.getRight() == rightNode) {
                result = edge;
                break;
            }
        }
        
        return (result);
    }
}
