/*
 *    leola-live 
 *  see license.txt
 */
package seventh.map;

import seventh.graph.Edge;
import seventh.graph.GraphNode;

/**
 * Creates {@link GraphNode} and {@link Edge} data elements.
 * 
 * @author Tony
 *
 */
public interface GraphNodeFactory<T> {
    
    /**
     * @param map
     * @param left
     * @param right
     * @return a new edge data
     */
    public T createEdgeData(Map map, GraphNode<Tile, T> left, GraphNode<Tile, T> right);
}
