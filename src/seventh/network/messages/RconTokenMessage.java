/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.server.ServerContext;



/**
 * Remote Control message token sent from the server to the client
 * 
 * @author Tony
 *
 */
public class RconTokenMessage extends AbstractNetMessage {
    
    private long token;
    
    /**
     */
    public RconTokenMessage() {
        this(ServerContext.INVALID_RCON_TOKEN);
    }
    
    /**
     * @param command
     */
    public RconTokenMessage(long token) {
        super(BufferIO.RCON_TOKEN_MESSAGE);
        this.token = token;
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(netspark.IOBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        token = buffer.getLong();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(netspark.IOBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putLong(token);
    }
    
    /**
     * @return the token
     */
    public long getToken() {
        return token;
    }
    
    /**
     * @param token the token to set
     */
    public void setToken(long token) {
        this.token = token;
    }
}
