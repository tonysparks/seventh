/*
 * see license.txt 
 */
package seventh.network.messages;



/**
 * @author Tony
 *
 */
public class BombPlantedMessage extends AbstractNetMessage {
	/**
	 * 
	 */
	public BombPlantedMessage() {
		super(BufferIO.BOMB_PLANTED);
	}
}
