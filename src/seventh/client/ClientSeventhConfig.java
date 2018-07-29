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

    /**
     * Cached variadic params (avoids creating
     * the array's)
     */
    private static final String[][] KEYS = {
            {"controls"},                       // 0
            {"name"},                           // 1
            {"mouse_sensitivity"},              // 2
            {"sound","volume"},                 // 3
            {"sound","number_of_channels"},     // 4
            {"console","foreground_color"},     // 5
            {"console","background_color"},     // 6
            {"game", "weapon_recoil"},          // 7
            {"game", "blood"},                  // 8
            {"game", "follow_reticle"},         // 9
            {"game", "follow_reticle_offset"},  // 10
            {"show_debug_info"},                // 11
            {"show_fps"},                       // 12
            {"sound","sound_config_path"},      // 13
    };
     
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
        LeoObject controls = this.config.get(KEYS[0]);
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
        return this.config.getStr("Noob", KEYS[1]);
    }
    
    /**
     * @param playerName
     */
    public void setPlayerName(String playerName) {
        this.config.set(playerName, KEYS[1]);
    }

    /**
     * @return the mouse sensitivity
     */
    public float getMouseSensitivity() {
        return this.config.getFloat(KEYS[2]);
    }
    
    /**
     * Sets the sensitivity
     * 
     * @param sensivity
     */
    public void setMouseSensitivity(float sensivity) {
        this.config.set(sensivity, KEYS[2]);
    }
    
    /**
     * @return the sound volume
     */
    public float getVolume() {
        return this.config.getFloat(KEYS[3]);
    }
    
    /**
     * @param volume
     */
    public void setVolume(float volume) {
        this.config.set(volume, KEYS[3]);
    }
    
    public int getNumberOfSoundChannels() {
        return this.config.getInt(256, KEYS[4]);
    }
    
    public void setNumberOfSoundChannels(int numberOfChannels) {
        this.config.set(numberOfChannels, KEYS[4]);
    }

    /**
     * The in-game debug console foreground color
     * @return The in-game debug console foreground color
     */
    public int getConsoleForegroundColor() {
        return this.config.getInt(0xffFFFF00, KEYS[5]);
    }
    
    
    /**
     * The in-game debug console background color
     * @return The in-game debug console background color
     */
    public int getConsoleBackgroundColor() {
        return this.config.getInt(0x8f0000FF, KEYS[6]);
    }
    
    /**
     * @return true if the weapon shows weapon recoiling (camera shaking)
     */
    public boolean getWeaponRecoilEnabled() {
        return this.config.getBool(true, KEYS[7]);
    }
    
    /**
     * Enables/Disables weapon recoil (camera shaking)
     * @param recoilEnabled
     */
    public void setWeaponRecoilEnabled(boolean recoilEnabled) {
        this.config.set(recoilEnabled, KEYS[7]);
    }
    
    /**
     * @return true if blood is shown
     */
    public boolean getBloodEnabled() {
        return this.config.getBool(true, KEYS[8]);
    }
    
    /**
     * Enables/Disables blood/guts
     * @param bloodEnabled
     */
    public void setBloodEnabled(boolean bloodEnabled) {
        this.config.set(bloodEnabled, KEYS[8]);
    }
    
    public boolean getFollowReticleEnabled() {
        return this.config.getBool(false, KEYS[9]);
    }
    
    public void setFollowReticleEnabled(boolean enabled) {
        this.config.set(enabled, KEYS[9]);
    }
    
    
    /**
     * @return The camera offset for following the reticle
     */
    public float getFollowReticleOffset() {
        return this.config.getFloat(80f, KEYS[10]);
    }

    /**
     * Sets the camera offset for following the reticle
     * 
     * @param offset
     */
    public void setFollowReticleOffset(float offset) {
        this.config.set(offset, KEYS[10]);
    }
    
    public boolean showDebugInfo() {
        return this.config.getBool(false, KEYS[11]);
    }
    
    public void showDebugInfo(boolean show) {
        this.config.set(show, KEYS[11]);
    }
    
    public boolean showFps() {
        return this.config.getBool(true, KEYS[12]);
    }
    
    public void showFps(boolean show) {
        this.config.set(show, KEYS[12]);
    }
    
    public String getSoundConfigPath() {
        return this.config.getStr("./assets/sfx/sounds.leola", KEYS[13]);
    }
    
    public void setSoundConfigPath(String path) {
        this.config.set(LeoObject.valueOf(path), KEYS[13]);
    }
}
