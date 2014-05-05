/*
 * see license.txt 
 */
package seventh.network.messages;


/**
 * @author Tony
 *
 */
public class ClientDisconnectMessage extends AbstractNetMessage {
	/**
	 * 
	 */
	public ClientDisconnectMessage() {
		super(BufferIO.CLIENT_DISCONNECTED);
	}
}
