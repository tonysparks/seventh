/*
 * see license.txt
 */
package seventh.graph;

/**
 * An {@link Edge} denotes the 'link' between two {@link GraphNode}s.  An {@link Edge} can have an associated cost and data.
 * 
 * @author Tony
 *
 */
public class Edge<E, T> {

    private GraphNode<E, T> left;
    private GraphNode<E, T> right;
    private int weight;
    private T value;
    

    /**
     * Constructs a new {@link Edge} which links the left and right {@link GraphNode}s.
     * 
     * <p>
     * Defaults the weight to 0 if weight is not important.
     * 
     * @param left - the left {@link GraphNode}
     * @param right - the right {@link GraphNode}
     * @param value - the data stored in this edge.
     */
    public Edge(GraphNode<E, T> left, GraphNode<E, T> right, T value) {
        this(left, right, value, 0);
    }
    
    /**
     * Constructs a new {@link Edge} which links the left and right {@link GraphNode}s.
     * 
     * @param left - the left {@link GraphNode}
     * @param right - the right {@link GraphNode}
     * @param value - the data stored in this edge.
     * @param weight - the cost of this edge (used for searching).
     */
    public Edge(GraphNode<E, T> left, GraphNode<E, T> right, T value, int weight) {
        this.left = left;
        this.right = right;
        this.value = value;
        this.weight = weight;
    }
    
    /**
     * @return the left
     */
    public GraphNode<E, T> getLeft() {
        return this.left;
    }
    /**
     * @param left the left to set
     */
    public void setLeft(GraphNode<E, T> left) {
        this.left = left;
    }
    /**
     * @return the right
     */
    public GraphNode<E, T> getRight() {
        return this.right;
    }
    /**
     * @param right the right to set
     */
    public void setRight(GraphNode<E, T> right) {
        this.right = right;
    }
    /**
     * @return the weight
     */
    public int getWeight() {
        return this.weight;
    }
    /**
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        this.value = value;
    }       
}
