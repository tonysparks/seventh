/*
 * see license.txt
 */
package seventh.game.events;

import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class TileRemovedEvent extends Event {

    private int x, y;
    
    /**
     * 
     */
    public TileRemovedEvent(Object source, int x, int y) {
        super(source);
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return the tileX
     */
    public int getTileX() {
        return x;
    }
    
    /**
     * @return the tileY
     */
    public int getTileY() {
        return y;
    }

}
