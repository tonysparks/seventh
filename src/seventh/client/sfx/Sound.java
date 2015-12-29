/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.sfx;

import java.io.File;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

/**
 * @author Tony
 *
 */
public class Sound {	
	private String soundFile;	
	private SoundSystem soundSystem;
	/**
	 * @param soundFile
	 * @param soundSystem
	 */
	public Sound(String soundFile, SoundSystem soundSystem) throws Exception {
		super();
		this.soundFile = soundFile;
		this.soundSystem = soundSystem;
		this.soundSystem.loadSound(new File(soundFile).toURI().toURL(), soundFile);
//		this.soundSystem.newSource(false, soundFile, soundFile, false, 0, 0, 0, SoundSystemConfig.ATTENUATION_ROLLOFF,
//                SoundSystemConfig.getDefaultRolloff()); 
	}

	public void play(float x, float y) {
		play(x, y, false);
	}
	
	public void play(float x, float y, boolean loop) {
		SoundSystemConfig.setDefaultFadeDistance(10000f);
		this.soundSystem.quickPlay(true, soundFile, loop, x, y, 0, 
				SoundSystemConfig.ATTENUATION_NONE, 10.0f);				
	}
	
	public void stop() {
		this.soundSystem.stop(soundFile);
	}
	
	public void pause() {
		this.soundSystem.pause(soundFile);
	}
	
	public boolean isPlaying() {
		return this.soundSystem.playing(soundFile);
	}
	
	public void reset() {
		this.soundSystem.rewind(soundFile);
	}
	
	public void setVolume(float v) {
		this.soundSystem.setVolume(soundFile, v);
	}
	
	public float getVolume() {
		return this.soundSystem.getVolume(soundFile);
	}
	
	public void destroy() {
		this.soundSystem.unloadSound(soundFile);
	}
	
}
