/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * @author Tony
 *
 */
public class Tri<X,Y,Z> extends Pair<X, Y> {

    /**
     * Thrid Var
     */
    private Z third;
    
    /**
     * @param first
     * @param second
     */
    public Tri(X first, Y second, Z third) {
        super(first, second);
        this.third = third;
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
