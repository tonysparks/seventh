/*
 * see license.txt 
 */
package seventh.shared;

import java.net.DatagramPacket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import seventh.server.ServerContext;
import seventh.shared.BroadcastListener.OnMessageReceivedListener;

/**
 * Registers with the LAN
 * 
 * @author Tony
 *
 */
public class LANServerRegistration implements Runnable {

	private BroadcastListener listener;
	private ExecutorService service;
	
	/**
	 * 
	 */
	public LANServerRegistration(final ServerContext serverContext) {
		
		final LANServerConfig config = serverContext.getConfig().getLANServerConfig();
		this.listener = new BroadcastListener(config.getMtu(), config.getBroadcastAddress(), config.getPort());
		this.listener.addOnMessageReceivedListener(new OnMessageReceivedListener() {
				
			@Override
			public void onMessage(DatagramPacket packet) {
				String msg = new String(packet.getData(), packet.getOffset(), packet.getLength()).trim();
				if(msg.equalsIgnoreCase("ping")) {
					try(Broadcaster caster = new Broadcaster(config.getMtu(), config.getBroadcastAddress(), config.getPort())) {
						ServerInfo info = new ServerInfo(serverContext);
						caster.broadcastMessage(info.toString());
					}
					catch(Exception e) {
						Cons.println("*** ERROR: Unable to broadcast response: " + e);
					}
				}
			}
		});
		
	}
	
	/**
	 * Start the background process in which this listens for any LAN broadcast pings
	 * 
	 */
	public void start() {
		if(this.service == null) {
		
			this.service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r, "lan-server-listener");
					thread.setDaemon(true);
					
					return thread;
				}
			});
			
			this.service.execute(this);
		}
	}
	
	/**
	 * Shuts down the broadcaster
	 */
	public void shutdown() {
		try {
			this.listener.close();
		}
		catch(Exception e) {
			Cons.println("*** ERROR: Closing out lan-server-listener: " + e);
		}
		finally {
			if(this.service != null) {
				this.service.shutdownNow();
				this.service = null;
			}
		}
	}
	
	@Override
	public void run() {
		try {														
			listener.start();			
		}		
		catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		catch(Exception e) {
			Cons.println("*** ERROR: An exception occurred during LAN BroadcastListner: " + e);
		}
	}

}
