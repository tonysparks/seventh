/*
 * see license.txt 
 */
package seventh.client.sfx;

import java.io.File;
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
    /**
     * 
     */
    public SoundBuffer(SoundSystem soundSystem, String soundFile, Random random) throws Exception {
        this.soundSystem = soundSystem;
        this.soundFile = soundFile;
        this.random = random;
        this.soundSystem.loadSound(new File(soundFile).toURI().toURL(), soundFile);
    }
    
    /**
     * Constructs a new {@link Sound}
     * @return the {@link Sound}
     * @throws Exception
     */
    public Sound newSound() throws Exception {
        return new Sound(this.soundSystem, this.soundFile, this.soundFile+this.random.nextLong());
    }
    
    /**
     * Destroys this resource, cleaning up any memory allocated
     */
    public void destroy() {
        this.soundSystem.unloadSound(this.soundFile);
    }

}
