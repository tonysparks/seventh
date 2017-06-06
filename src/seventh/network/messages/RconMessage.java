/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;



/**
 * Remote Control message
 * 
 * @author Tony
 *
 */
public class RconMessage extends AbstractNetMessage {
    
    private String command;
    
    /**
     */
    public RconMessage() {
        this("");
    }
    
    /**
     * @param command
     */
    public RconMessage(String command) {
        super(BufferIO.RCON_MESSAGE);
        this.command = command;
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(netspark.IOBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.command = BufferIO.readBigString(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(netspark.IOBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writeBigString(buffer, command);
    }
    
    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }
    
    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }
}
