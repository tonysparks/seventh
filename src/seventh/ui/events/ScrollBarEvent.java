/*
 * see license.txt 
 */
package seventh.ui.events;

import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class ScrollBarEvent extends Event {

    private int movementDelta;
    
    /**
     * @param source
     */
    public ScrollBarEvent(Object source, int movementDelta) {
        super(source);
        this.movementDelta = movementDelta;
    }
    
    /**
     * @return the movementDelta
     */
    public int getMovementDelta() {
        return movementDelta;
    }
}
