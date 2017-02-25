/*
 * see license.txt 
 */
package harenet;

import harenet.messages.Message;

import java.net.InetSocketAddress;

/**
 * A {@link DummyPeer} simulates an connected {@link Peer}.  This does not 
 * actually send or receive any data from a {@link Host}
 * 
 * @author Tony
 *
 */
public class DummyPeer extends Peer {

    private static final InetSocketAddress DUMMY_ADDRESS = new InetSocketAddress(0);
    /**
     * @param host
     * @param address
     * @param id
     */
    public DummyPeer(Host host, byte id) {
        super(host, DUMMY_ADDRESS, id);
    }
    
    /* (non-Javadoc)
     * @see harenet.Peer#isConnected()
     */
    @Override
    public boolean isConnected() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see harenet.Peer#isConnecting()
     */
    @Override
    public boolean isConnecting() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see harenet.Peer#checkIfTimedOut(long, long)
     */
    @Override
    public boolean checkIfTimedOut(long currentTime, long timeout) {
        return false;
    }
        
    /* (non-Javadoc)
     * @see harenet.Peer#send(harenet.messages.Message)
     */
    @Override
    public void send(Message message) {
    }
    
    /* (non-Javadoc)
     * @see harenet.Peer#receive(harenet.messages.Message)
     */
    @Override
    public void receive(Message message) {
    }
        
}
