/*
 * see license.txt 
 */
package seventh.client.sfx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import paulscode.sound.SoundSystem;

/**
 * Manages the loaded sound data into a buffer.  Spawn {@link Sound}s that use the
 * {@link SoundBuffer} data.
 * 
 * @author Tony
 *
 */
public class SoundBuffer {

    private SoundSystem soundSystem;
    private String soundFile;
    private Random random;
    
    private SoundConfig soundCfg;
    
    private List<Sound> createdSounds;
    
    /**
     * 
     */
    public SoundBuffer(SoundSystem soundSystem, String soundFile, Random random, SoundConfig soundCfg) throws Exception {
        this.soundSystem = soundSystem;
        this.soundFile = soundFile;
        this.random = random;
        this.soundCfg = soundCfg;
        
        this.soundSystem.loadSound(new File(soundFile).toURI().toURL(), soundFile);
        
        this.createdSounds = new ArrayList<>();
    }
    
    /**
     * Constructs a new {@link Sound}
     * @return the {@link Sound}
     * @throws Exception
     */
    public Sound newSound() throws Exception {
        Sound sound = new Sound(this.soundSystem, 
                                this.soundFile, 
                                this.soundFile+this.random.nextLong(), 
                                this.soundCfg);
        
        this.createdSounds.add(sound);
        
        return sound;
    }
    
    /**
     * @return the createdSounds
     */
    public List<Sound> getCreatedSounds() {
        return createdSounds;
    }
    
    /**
     * Destroys this resource, cleaning up any memory allocated
     */
    public void destroy() {
        for(Sound snd : this.createdSounds) {
            if(snd.isPlaying()) {
                snd.stop();
            }
            
            snd.destroy();
        }
        
        this.soundSystem.unloadSound(this.soundFile);
    }

}
