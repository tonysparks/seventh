/*
 * see license.txt 
 */
package seventh.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import seventh.shared.Config;
import seventh.shared.Cons;
import seventh.shared.MasterServerApi;


/**
 * Registers with the Master Server
 * 
 * @author Tony
 *
 */
public class MasterServerRegistration implements Runnable {

	private GameServer gameServer;
	private MasterServerApi serverApi;
	
	private int pingRate;
	
	private ScheduledExecutorService service;
	
	/**
	 * @param gameServer
	 */
	public MasterServerRegistration(GameServer gameServer) {
		this.gameServer = gameServer;
		
		Config config = gameServer.getConfig();
		this.serverApi = new MasterServerApi(config);		
		this.pingRate = config.getInt("master_server", "ping_rate_minutes");		
		this.service = Executors.newSingleThreadScheduledExecutor();
	}
	
	public void start() {
		this.service.scheduleWithFixedDelay(this, pingRate, pingRate, TimeUnit.MINUTES);
	}
	
	/**
	 * Shuts down the heartbeat
	 */
	public void shutdown() {
		this.service.shutdownNow();
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
		this.serverApi.sendHeartbeat(gameServer);
	}	
}
