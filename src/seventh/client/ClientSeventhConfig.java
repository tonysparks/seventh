/*
 * see license.txt 
 */
package seventh.client;

import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.shared.Config;
import seventh.shared.SeventhConfig;

/**
 * Client configuration file
 * 
 * @author Tony
 *
 */
public class ClientSeventhConfig extends SeventhConfig {

	
	private KeyMap keyMap;
	private VideoConfig video;
	
	/**
	 * @param configurationPath
	 * @param configurationRootNode
	 * @throws Exception
	 */
	public ClientSeventhConfig(String configurationPath, String configurationRootNode) throws Exception {
		super(configurationPath, configurationRootNode);

		init();
	}

	/**
	 * @param config
	 */
	public ClientSeventhConfig(Config config) {
		super(config);
		
		init();
	}
	
	
	/**
	 * Initializes the configuration
	 */
	private void init() {
		LeoObject controls = this.config.get("controls");
		this.keyMap = new KeyMap( (controls.isMap()) ? (LeoMap)controls : new LeoMap() );
		
		this.video = new VideoConfig(config);
	}
	
	
	/**
	 * @return the keyMap
	 */
	public KeyMap getKeyMap() {
		return keyMap;
	}

	/**
	 * @return the video
	 */
	public VideoConfig getVideo() {
		return video;
	}
	
	
	public String getPlayerName() {
		return this.config.getStr("Noob", "name");
	}
	public void setPlayerName(String playerName) {
		this.config.set(playerName, "name");
	}
	
	public float getMouseSensitivity() {
		return this.config.getFloat("mouse_sensitivity");
	}
	public void setMouseSensitivity(float sensivity) {
		this.config.set(sensivity, "mouse_sensitivity");
	}
	
	public float getVolume() {
		return this.config.getFloat("sound", "volume");
	}
	public void setVolume(float volume) {
		this.config.set(volume, "sound", "volume");
	}
}
