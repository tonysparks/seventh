/*
 * see license.txt 
 */
package seventh.shared;

/**
 * LAN Server configuration settings
 * 
 * @author Tony
 *
 */
public class LANServerConfig {

	private Config config;
	
	/**
	 * @param config
	 */
	public LANServerConfig(Config config) {
		this.config = config;
	}
	
	/**
	 * @return the broadcast address
	 */
	public String getBroadcastAddress() {
		return this.config.getStr("224.0.0.44", "lan", "broadcast_address");
	}
	
	/**
	 * @param url
	 */
	public void setBroadcastAddress(String address) {
		this.config.set(address, "lan", "broadcast_address");
	}
	
	
	/**
	 * @return the LAN broadcast port
	 */
	public int getPort() {
		return this.config.getInt(4888, "lan", "port");
	}
	
	/**
	 * Sets the LAN broadcast port
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.config.set(port, "lan", "port");
	}
		
	/**
	 * @return the LAN broadcast mtu
	 */
	public int getMtu() {
		return this.config.getInt(1500, "lan", "mtu");
	}
	
	/**
	 * Sets the LAN broadcast mtu
	 * 
	 * @param mtu
	 */
	public void setMtu(int mtu) {
		this.config.set(mtu, "lan", "mtu");
	}
}
