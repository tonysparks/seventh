/*
 * see license.txt 
 */
package harenet;

import harenet.messages.NetMessageFactory;

/**
 * The Harenet configuration.  This allows for tweaking
 * a number of parameters to better suit your needs.
 * 
 * @author Tony
 *
 */
public class NetConfig {

    private long timeout;
    private int mtu;
    private int maxConnections;
    private long reliableMessageTimeout;
    private long heartbeatTime;
    private int pollRate;
    
    private long pingRate;
    
    private int compressionThreshold;
    private boolean useDirectBuffers;
    
    private Log log;
    private NetMessageFactory messageFactory;
    
    /**
     * @param messageFactory
     */
    public NetConfig(NetMessageFactory messageFactory) {
        this(15_000        // timeout
            , 5_000           // reliable timeout
            , 150          // heartbeat
            , 10_000       // ping rate
            , 33           // poll the network 
            , 1500         // mtu
            , 16           // max connections
            , 500          // compressionThreshold
            , false        // useDirectBuffers
            , new SysoutLog()
            , messageFactory);
    }
    
    /**
     * @param timeout
     * @param reliableMessageTimeout
     * @param heartbeat
     * @param pingRate
     * @param pollRate
     * @param mtu
     * @param maxConnections
     * @param compressionThreshold
     * @param useDirectBuffers
     * @param log
     * @param messageFactory
     */
    public NetConfig(long timeout
                , long reliableMessageTimeout
                , long heartbeat
                , long pingRate
                , int pollRate
                , int mtu, int maxConnections
                , int compressionThreshold
                , boolean useDirectBuffers
                , Log log
                , NetMessageFactory messageFactory) {
        super();
        this.timeout = timeout;
        this.reliableMessageTimeout = reliableMessageTimeout;
        this.heartbeatTime = heartbeat;
        this.pingRate = pingRate;
        this.pollRate = pollRate;
        this.mtu = mtu;
        this.maxConnections = maxConnections;
        this.compressionThreshold = compressionThreshold;
        this.useDirectBuffers = useDirectBuffers;
        this.log = log;
        this.messageFactory = messageFactory;
    }
    
    /**
     * Turn logging on/off
     * @param b
     */
    public void enableLog(boolean b) {
        log.setEnabled(b);
    }
    
    /**
     * @return the messageFactory
     */
    public NetMessageFactory getMessageFactory() {
        return messageFactory;
    }
    
    /**
     * @param messageFactory the messageFactory to set
     */
    public void setMessageFactory(NetMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }
    
    /**
     * @return the pollRate
     */
    public int getPollRate() {
        return pollRate;
    }

    /**
     * @return the pingRate
     */
    public long getPingRate() {
        return pingRate;
    }

    /**
     * @return the heartbeatTime
     */
    public long getHeartbeatTime() {
        return heartbeatTime;
    }
    
    /**
     * @param heartbeatTime the heartbeatTime to set
     */
    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }
    
    /**
     * @param pingRate the pingRate to set
     */
    public void setPingRate(long pingRate) {
        this.pingRate = pingRate;
    }

    /**
     * @param reliableMessageTimeout the reliableMessageTimeout to set
     */
    public void setReliableMessageTimeout(long reliableMessageTimeout) {
        this.reliableMessageTimeout = reliableMessageTimeout;
    }
    
    /**
     * @return the reliableMessageTimeout
     */
    public long getReliableMessageTimeout() {
        return reliableMessageTimeout;
    }

    /**
     * @param pollRate the pollRate to set
     */
    public void setPollRate(int pollRate) {
        this.pollRate = pollRate;
    }
    
    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * @param mtu the mtu to set
     */
    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    /**
     * @param maxConnections the maxConnections to set
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return the log
     */
    public Log getLog() {
        return log;
    }
    
    /**
     * @return the maxConnections
     */
    public int getMaxConnections() {
        return maxConnections;
    }
    
    /**
     * @return the mtu
     */
    public int getMtu() {
        return mtu;
    }
    
    /**
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }
    
    /**
     * @return the compressionThreshold
     */
    public int getCompressionThreshold() {
        return compressionThreshold;
    }
    
    /**
     * @param compressionThreshold the compressionThreshold to set
     */
    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
    
    /**
     * @return if native buffers are to be used for reading/writing from
     * the network sockets
     */
    public boolean useDirectBuffers() {
        return this.useDirectBuffers;
    }
    
    /**
     * @param useDirectBuffers the useDirectBuffers to set
     */
    public void setUseDirectBuffers(boolean useDirectBuffers) {
        this.useDirectBuffers = useDirectBuffers;
    }
}
