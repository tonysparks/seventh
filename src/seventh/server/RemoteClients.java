/*
 * see license.txt 
 */
package seventh.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tony
 *
 */
public class RemoteClients {
	
	/**
	 * Iterator for remote clients
	 * 
	 * @author Tony
	 *
	 */
	public static interface RemoteClientIterator {
		public void onRemoteClient(RemoteClient client);
	}

	private Map<Integer, RemoteClient> clients;
	private int maxClients;
	
	/**
	 * @param maxClients the max number of clients allowed
	 */
	public RemoteClients(int maxClients) {
		this.maxClients = maxClients;
		this.clients = new ConcurrentHashMap<>();
	}
	
	/**
	 * @return the maxClients
	 */
	public int getMaxClients() {
		return maxClients;
	}
	
	/**
	 * Iterates through each registered client
	 * 
	 * @param it
	 */
	public void foreach(RemoteClientIterator it) {
		for(RemoteClient client : this.clients.values()) {
			it.onRemoteClient(client);
		}
	}
	
	/**
	 * Adds a {@link RemoteClient}
	 * 
	 * @param clientId
	 * @param client
	 */
	public void addRemoteClient(int clientId, RemoteClient client) {
		if(this.clients.size() > this.maxClients) {
			throw new IllegalArgumentException("Max clients has been reached: " + this.clients.size());
		}
		
		this.clients.put(clientId, client);
		
	}
	
	/**
	 * Get the client by ID
	 * 
	 * @param clientId
	 * @return the client, or null if not found
	 */
	public RemoteClient getClient(int clientId) {
		return this.clients.get(clientId);
	}
	
	/**
	 * Removes the {@link RemoteClient} bound by the client ID.
	 * 
	 * @param clientId
	 */
	public void removeClient(int clientId) {
		this.clients.remove(clientId);
	}

}
