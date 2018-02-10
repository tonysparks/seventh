/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.sfx;

import java.util.Random;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

/**
 * @author Tony
 *
 */
public class Sound {
    private static final Random rand = new Random();
    
    private String sourceName;    
    private String soundFile;
    private SoundSystem soundSystem;
    /**
     * @param soundFile
     * @param soundSystem
     */
    public Sound(SoundSystem soundSystem, String soundFile, String soundName) throws Exception {        
        this.soundSystem = soundSystem;
        this.sourceName = soundName;
        this.soundFile = soundFile;
        
        this.soundSystem.newSource(true, this.sourceName, soundFile, false, 0, 0, 0, 
                SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());                
    }

    private float getFuzzyPitch() {
        return (95 + rand.nextInt(5)) / 100.0f;
    }
    
    /**
     * @return the sourceName
     */
    public String getSourceName() {
        return sourceName;
    }
    
    /**
     * @return the soundFile
     */
    public String getSoundFile() {
        return soundFile;
    }
    
    public void setPosition(float x, float y) {
        this.soundSystem.setPosition(sourceName, x, y, 0);
    }
    
    public void play(float x, float y) {
        play(x, y, false);
    }
    
    public void play(float x, float y, boolean loop) {
        this.soundSystem.setDistOrRoll(sourceName, 0.009f);
        this.soundSystem.setPosition(sourceName, x, y, 0);
        this.soundSystem.setLooping(sourceName, loop);
        this.soundSystem.setPitch(sourceName, getFuzzyPitch());
        this.soundSystem.play(sourceName);                        
    }
    
    public void stop() {
        this.soundSystem.stop(sourceName);
    }
    
    public void pause() {
        this.soundSystem.pause(sourceName);
    }
    
    public boolean isPlaying() {
        return this.soundSystem.playing(sourceName);
    }
    
    public void reset() {
        this.soundSystem.rewind(sourceName);
    }
    
    public void setVolume(float v) {
        this.soundSystem.setVolume(sourceName, v);
    }
    
    public float getVolume() {
        return this.soundSystem.getVolume(sourceName);
    }
    
    public void destroy() {
        this.soundSystem.removeSource(sourceName);
    }
    
}
