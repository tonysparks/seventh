/*
 * see license.txt 
 */
package harenet;

import harenet.messages.ConnectionAcceptedMessage;
import harenet.messages.ConnectionRequestMessage;
import harenet.messages.DisconnectMessage;
import harenet.messages.HeartbeatMessage;
import harenet.messages.Message;
import harenet.messages.NetMessageFactory;
import harenet.messages.PingMessage;
import harenet.messages.PongMessage;
import harenet.messages.ReliableNetMessage;
import harenet.messages.UnReliableNetMessage;

/**
 * The Harenet protocol message headers.  Serves as a means for 
 * creating Harenet messages.
 * 
 * @author Tony
 *
 */
public class MessageHeader {
        
    public static final byte CONNECTION_REQUEST_MESSAGE = 1;
    public static final byte CONNECTION_ACCEPTED_MESSAGE = 2;
    public static final byte DISCONNECT_MESSAGE = 3;
    
    public static final byte HEARTBEAT_MESSAGE = 4;
    public static final byte PING_MESSAGE = 5;
    public static final byte PONG_MESSAGE = 6;
    
    // messages past this must have a NetMessage Attached
    public static final byte RELIABLE_NETMESSAGE = 7;
    public static final byte UNRELIABLE_NETMESSAGE = 8;
    
    
    
    /**
     * Reads the buffer to determine what Message to allocate.
     * 
     * @param buf
     * @param messageFactory
     * @return the Message;
     */
    public static Message readMessageHeader(IOBuffer buf, NetMessageFactory messageFactory) {
        
        Message message = null;
        byte messageType = buf.get();
        switch(messageType) {
            case CONNECTION_REQUEST_MESSAGE: {
                message = new ConnectionRequestMessage();
                break;
            }
            case CONNECTION_ACCEPTED_MESSAGE: {
                message = new ConnectionAcceptedMessage();
                break;
            }
            case DISCONNECT_MESSAGE: {
                message = new DisconnectMessage();
                break;
            }
            case RELIABLE_NETMESSAGE: {
                message = new ReliableNetMessage();
                break;
            }
            case UNRELIABLE_NETMESSAGE: {
                message = new UnReliableNetMessage();
                break;
            }
            case HEARTBEAT_MESSAGE: {
                message = HeartbeatMessage.INSTANCE;
                break;
            }
            case PING_MESSAGE: {
                message = PingMessage.INSTANCE;
                break;
            }
            case PONG_MESSAGE: {
                message = PongMessage.INSTANCE;
                break;
            }
        }
        
        message.readFrom(buf, messageFactory);
        
        return message;
    }
}
