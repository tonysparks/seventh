/*
 * see license.txt
 */
package seventh.game.events;

import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class TileAddedEvent extends Event {

    private int x, y;
    private int type;
    /**
     * 
     */
    public TileAddedEvent(Object source, int type, int x, int y) {
        super(source);
        this.type = type;
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return the type
     */
    public int getType() {
        return type;
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
