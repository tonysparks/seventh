/*
 * see license.txt 
 */
package seventh.network.messages;


/**
 * @author Tony
 *
 */
public class ClientReadyMessage extends AbstractNetMessage {
    /**
     * 
     */
    public ClientReadyMessage() {
        super(BufferIO.CLIENT_READY);
    }
}
