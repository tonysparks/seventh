/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * @author Tony
 *
 */
public class Pair<X, Y> {

    /**
     * Get the first item
     */
    private X first;
    
    /**
     * Get the second item
     */
    private Y second;

    /**
     * 
     * @param first
     * @param second
     */
    public Pair(X first, Y second) {
        this.first=first;
        this.second=second;
    }
    
    /**
     */
    public Pair() {        
    }
    
    /**
     * @param first the first to set
     */
    public void setFirst(X first) {
        this.first = first;
    }

    /**
     * @return the first
     */
    public X getFirst() {
        return first;
    }

    /**
     * @param second the second to set
     */
    public void setSecond(Y second) {
        this.second = second;
    }

    /**
     * @return the second
     */
    public Y getSecond() {
        return second;
    }
}
