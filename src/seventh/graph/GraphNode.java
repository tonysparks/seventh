/*
 * see license.txt
 */
package seventh.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A node in a graph linked by {@link Edge}s.
 * 
 * @author Tony
 *
 */
public class GraphNode<E, T> {

    private List<Edge<E,T>> edges;
    private E value;
    
    /**
     * Constructs a {@link GraphNode}.
     * 
     * @param value
     */
    public GraphNode(E value) {
        this.edges = new ArrayList<Edge<E,T>>();
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
    public void addEdge(Edge<E,T> edge) {
        this.edges.add(edge);
    }
    
    /**
     * Removes an {@link Edge}
     * @param edge
     */
    public void removeEdge(Edge<E,T> edge) {
        this.edges.remove(edge);
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
    public Iterator<Edge<E,T>> edges() {
        return this.edges.iterator();
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
        Iterator<Edge<E,T>> it = edges();
        while (it.hasNext()) {
            Edge<E,T> edge = it.next();

            /* Notice we check if the REFERENCE equals the right node */
            if (edge.getRight() == rightNode) {
                result = edge;
                break;
            }
        }
        
        return (result);
    }
}
