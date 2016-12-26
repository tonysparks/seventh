/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.shared.Config;

/**
 * @author Tony
 *
 */
public class VideoConfig {

	private Config config; 
	
	/**
	 * 
	 */
	public VideoConfig(Config config) {
		this.config = config;
	}
	
	/**
	 * @return true if there are settings in this section
	 */
	public boolean isPresent() {
		return (config.getBool("video", "width") && 
			    config.getBool("video", "height"));
	}
	
	public int getWidth() {
		return this.config.getInt(800, "video", "width");
	}
	
	public void setWidth(int width) {
		this.config.set(width, "video", "width");		
	}

	public int getHeight() {
		return this.config.getInt(800, "video", "height");
	}
	
	public void setHeight(int height) {
		this.config.set(height, "video", "height");		
	}
	
	public boolean isFullscreen() {
		return this.config.getBool("video", "fullscreen");
	}
	public void setFullscreen(boolean fullscreen) {
		this.config.set(fullscreen, "video", "fullscreen");
	}
	
	public boolean isVsync() {
		return this.config.getBool("video", "vsync");
	}
	public void setVsync(boolean vsync) {
		this.config.set(vsync, "video", "vsync");
	}
	
	public boolean useGL30() {
		return this.config.getBool("video", "useGL30");
	}
	
	public void setUseGL30(boolean useGL30) {
		this.config.set(useGL30, "video", "useGL30");		
	}

	
	
}
