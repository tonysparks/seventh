/*
 * see license.txt 
 */
package seventh.network.messages;

/**
 * @author Tony
 *
 */
public class BombExplodedMessage extends AbstractNetMessage {    
    /**
     * 
     */
    public BombExplodedMessage() {
        super(BufferIO.BOMB_EXPLODED);
    }    
}
