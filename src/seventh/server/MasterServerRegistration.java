/*
 * see license.txt 
 */
package seventh.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import seventh.shared.Cons;
import seventh.shared.MasterServerClient;
import seventh.shared.MasterServerConfig;


/**
 * Registers with the Master Server
 * 
 * @author Tony
 *
 */
public class MasterServerRegistration implements Runnable {

    private ServerContext serverContext;
    private MasterServerClient serverClient;
    
    private int pingRate;
    
    private ScheduledExecutorService service;
    
    /**
     * @param serverContext
     */
    public MasterServerRegistration(ServerContext serverContext) {
        this.serverContext = serverContext;
                
        MasterServerConfig config = serverContext.getConfig().getMasterServerConfig();
        this.serverClient = new MasterServerClient(config);        
        this.pingRate = config.getPingRateMinutes();
    }
    
    
    /**
     * Start the background process in which pings the master server at a fixed rate
     * for as long as this game server is operational.
     * 
     */
    public void start() {
        if(this.service == null) {
        
            this.service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "master-server-ping");
                    thread.setDaemon(true);
                    
                    return thread;
                }
            });
            this.service.scheduleWithFixedDelay(this, pingRate, pingRate, TimeUnit.MINUTES);
        }
    }
    
    /**
     * Shuts down the heartbeat
     */
    public void shutdown() {
        if(this.service != null) {
            this.service.shutdownNow();
            this.service = null;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            sendHeartbeat();
        }
        catch(Exception e) {
            Cons.println("*** Error, unable to send heartbeat to master server: " + e);
        }
    }

    
    /**
     * Sends a heart beat to the master server
     * @throws Exception
     */
    public void sendHeartbeat() throws Exception {
        this.serverClient.sendHeartbeat(this.serverContext);
    }    
}
