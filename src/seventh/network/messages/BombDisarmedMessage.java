/*
 * see license.txt 
 */
package seventh.network.messages;



/**
 * @author Tony
 *
 */
public class BombDisarmedMessage extends AbstractNetMessage {
	
	public BombDisarmedMessage() {
		super(BufferIO.BOMB_DIARMED);
	}	
}
