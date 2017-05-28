/*
 * see license.txt 
 */
package seventh.server;

import seventh.network.messages.RconMessage;
import seventh.shared.Logger;

/**
 * @author Tony
 *
 */
public class RconLogger implements Logger {

    private ServerNetworkProtocol protocol;
    private int clientId;
    
    /**
     * @param clientId
     * @param protocol
     */
    public RconLogger(int clientId, ServerNetworkProtocol protocol) {
        this.clientId = clientId;
        this.protocol = protocol;
    }

    @Override
    public void print(Object msg) {
        this.protocol.sendRconMessage(clientId, new RconMessage(msg.toString()));
    }

    @Override
    public void println(Object msg) {
        print(msg.toString() + "\n");
    }

    @Override
    public void printf(Object msg, Object... args) {
        print(String.format(msg.toString(), args));
    }

}
