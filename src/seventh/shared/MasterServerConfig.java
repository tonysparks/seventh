/*
 * see license.txt 
 */
package seventh.shared;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import leola.vm.types.LeoObject;

/**
 * Master Server configuration settings
 * 
 * @author Tony
 *
 */
public class MasterServerConfig {

	private Config config;
	
	/**
	 * @param config
	 */
	public MasterServerConfig(Config config) {
		this.config = config;
	}
	
	/**
	 * @return the master server URL
	 */
	public String getUrl() {
		return this.config.getString("master_server", "url");
	}
	
	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.config.set(url, "master_server", "url");
	}
	
	
	/**
	 * @return the frequency in minutes in which the server will ping the master server
	 */
	public int getPingRateMinutes() {
		return this.config.getInt(1, "master_server", "ping_rate_minutes");
	}
	
	/**
	 * Sets the frequency in minutes in which the server will ping the master server
	 * 
	 * @param minutes
	 */
	public void setPingRateMinutes(int minutes) {
		this.config.set(minutes, "master_server", "ping_rate_minutes");
	}
	
	
	/**
	 * @return the proxy settings
	 */
	public Proxy getProxy() {		
		LeoObject proxy = this.config.get("master_server", "proxy_settings");
		if(LeoObject.isTrue(proxy)) {
			Proxy p = new Proxy(Type.HTTP, new InetSocketAddress(this.config.getString("master_server", "proxy_settings", "address"), 
																 this.config.getInt(88, "master_server", "proxy_settings", "port")));
			
			return (p);
		}
		
		return Proxy.NO_PROXY;
	}

	/**
	 * @return the proxy user name
	 */
	public String getProxyUser() {
		return this.config.getString("master_server", "proxy_settings", "user");
	}
	
	/**
	 * Sets the proxy user name
	 * 
	 * @param user
	 */
	public void setProxyUser(String user) {
		this.config.set(user, "master_server", "proxy_settings", "user");
	}
	
	
	/**
	 * @return the proxy password
	 */
	public String getProxyPassword() {
		return this.config.getString("master_server", "proxy_settings", "password");
	}
	
	/**
	 * Sets the proxy password
	 * 
	 * @param password
	 */
	public void setProxyPassword(String password) {
		this.config.set(password, "master_server", "proxy_settings", "password");
	}
	
}
