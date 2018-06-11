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
    private byte type;
    /**
     * 
     */
    public TileAddedEvent(Object source, byte type, int x, int y) {
        super(source);
        this.type = type;
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return the type
     */
    public byte getType() {
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
