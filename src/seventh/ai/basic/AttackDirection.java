/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.math.Vector2f;

/**
 * A direction that an enemy can attack from
 * 
 * @author Tony
 *
 */
public class AttackDirection {

    private Vector2f direction;
    
    /**
     * 
     */
    public AttackDirection(Vector2f direction) {
        this.direction = direction;
    }
    
    /**
     * @return the direction
     */
    public Vector2f getDirection() {
        return direction;
    }

}
