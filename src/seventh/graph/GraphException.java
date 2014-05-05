/*
 * see license.txt
 */
package seventh.graph;

/**
 * Base Exception.
 * 
 * @author Tony
 *
 */
public class GraphException extends Exception {

    /**
     * UID
     */
    private static final long serialVersionUID = 1560018059471863025L;

    /**
     * 
     */
    public GraphException() {
    }

    /**
     * @param message
     */
    public GraphException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public GraphException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }

}
