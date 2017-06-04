/*
 * see license.txt 
 */
package seventh.client;

import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.client.gfx.VideoConfig;
import seventh.client.inputs.KeyMap;
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
    
    /**
     * @return the players name
     */
    public String getPlayerName() {
        return this.config.getStr("Noob", "name");
    }
    
    /**
     * @param playerName
     */
    public void setPlayerName(String playerName) {
        this.config.set(playerName, "name");
    }

    /**
     * @return the mouse sensitivity
     */
    public float getMouseSensitivity() {
        return this.config.getFloat("mouse_sensitivity");
    }
    
    /**
     * Sets the sensitivity
     * 
     * @param sensivity
     */
    public void setMouseSensitivity(float sensivity) {
        this.config.set(sensivity, "mouse_sensitivity");
    }
    
    /**
     * @return the sound volume
     */
    public float getVolume() {
        return this.config.getFloat("sound", "volume");
    }
    
    /**
     * @param volume
     */
    public void setVolume(float volume) {
        this.config.set(volume, "sound", "volume");
    }

    /**
     * The in-game debug console foreground color
     * @return The in-game debug console foreground color
     */
    public int getConsoleForegroundColor() {
        return this.config.getInt(0xffFFFF00, "console", "foreground_color");
    }
    
    
    /**
     * The in-game debug console background color
     * @return The in-game debug console background color
     */
    public int getConsoleBackgroundColor() {
        return this.config.getInt(0x8f0000FF, "console", "background_color");
    }
    
    /**
     * @return true if the weapon shows weapon recoiling (camera shaking)
     */
    public boolean getWeaponRecoilEnabled() {
        return this.config.getBool(true, "game", "weapon_recoil");
    }
    
    /**
     * Enables/Disables weapon recoil (camera shaking)
     * @param recoilEnabled
     */
    public void setWeaponRecoilEnabled(boolean recoilEnabled) {
        this.config.set(recoilEnabled, "game", "weapon_recoil");
    }
    
    /**
     * @return true if blood is shown
     */
    public boolean getBloodEnabled() {
        return this.config.getBool(true, "game", "blood");
    }
    
    /**
     * Enables/Disables blood/guts
     * @param bloodEnabled
     */
    public void setBloodEnabled(boolean bloodEnabled) {
        this.config.set(bloodEnabled, "game", "blood");
    }
    
    public boolean getFollowReticleEnabled() {
        return this.config.getBool(true, "game", "follow_reticle");
    }
    
    public void setFollowReticleEnabled(boolean enabled) {
        this.config.set(enabled, "game", "follow_reticle");
    }
    
    public boolean showDebugInfo() {
        return this.config.getBool(false, "show_debug_info");
    }
    
    public void showDebugInfo(boolean show) {
        this.config.set(show, "show_debug_info");
    }
    
    public boolean showFps() {
        return this.config.getBool(true, "show_fps");
    }
    
    public void showFps(boolean show) {
        this.config.set(show, "show_fps");
    }
}
