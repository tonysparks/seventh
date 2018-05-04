/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * @author Tony
 * 
 */
public class Tri<X,Y,Z> {

    /**
     * Get the first item
     */
    private X first;
    
    /**
     * Get the second item
     */
    private Y second;

    /**
     * Get the third item
     */
    private Z third;

    /**
     * @param first
     * @param second
     * @param third
     */
    public Tri(X first, Y second, Z third) {
        this.first  = first;
        this.second = second;
        this.third  = third;
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
   
    /**
     * @param third the third to set
     */
    public void setThird(Z third) {
        this.third = third;
    }

    /**
     * @return the third
     */
    public Z getThird() {
        return third;
    }

}
