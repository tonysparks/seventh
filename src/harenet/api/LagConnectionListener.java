/*
 * see license.txt 
 */
package harenet.api;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Used for debugging purposes.  This will introduce a variable amount of lag.
 * 
 * @author Tony
 *
 */
public class LagConnectionListener implements ConnectionListener {

	private Random random;
	
	private ConnectionListener decorator;
	private Queue<Msg> inbox;
	
	private int minLag, maxLag;
	
	private ScheduledExecutorService service;
	
	class Msg {
		Connection conn;
		Object msg;
		
		Msg(Connection conn, Object msg) {
			this.conn = conn;
			this.msg = msg;
		}
		
		
	}

	/**
	 * @param minLag
	 * @param maxLag
	 * @param decorator
	 */
	public LagConnectionListener(final int minLag, final int maxLag, final ConnectionListener decorator) {
		super();
		this.minLag = minLag;
		this.maxLag = maxLag;
		
		this.decorator = decorator;
		this.random = new Random();
		this.inbox = new ConcurrentLinkedQueue<>();
		
		this.service = Executors.newScheduledThreadPool(1);		
//		Runnable runner = new Runnable() {
//			
//			@Override
//			public void run() {
//				for(;;) {
//					Msg msg = inbox.poll();
//					if(msg != null) {
//					
//						long sleep = minLag + random.nextInt(maxLag-minLag);
//						if(sleep > maxLag) {
//							sleep = maxLag;
//						}
//						
//						try {
//							Thread.sleep(sleep);
//						} catch (InterruptedException e) {
//						}
//						decorator.onReceived(msg.conn, msg.msg);
//					}
//					else {
//						Thread.yield();
//					}
//				}
//			}
//		};
//		
//		Thread thread = new Thread(runner, "LagIntroducer");
//		thread.setDaemon(true);
//		thread.start();		
	}
	

	/* (non-Javadoc)
	 * @see harenet.api.ConnectionListener#onConnected(harenet.api.Connection)
	 */
	@Override
	public void onConnected(Connection conn) {
		decorator.onConnected(conn);
	}

	/* (non-Javadoc)
	 * @see harenet.api.ConnectionListener#onDisconnected(harenet.api.Connection)
	 */
	@Override
	public void onDisconnected(Connection conn) {
		decorator.onDisconnected(conn);
	}

	/* (non-Javadoc)
	 * @see harenet.api.ConnectionListener#onReceived(harenet.api.Connection, java.lang.Object)
	 */
	@Override
	public void onReceived(Connection conn, Object msg) {
		this.inbox.add(new Msg(conn,msg));
		
		minLag = 50;
		maxLag = 60;
		
		long sleep = minLag + random.nextInt(maxLag-minLag);
		if(sleep > maxLag) {
			sleep = maxLag;
		}
		
		this.service.schedule(new Runnable() {
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				Msg msg = inbox.poll();
				if(msg != null) {				
					decorator.onReceived(msg.conn, msg.msg);
				}
							
			}
		}, sleep, TimeUnit.MILLISECONDS);
	}

}
