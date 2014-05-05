/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.SoundType;
import seventh.game.net.NetSound;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class SoundEmittedEvent extends Event {
	private int id;
	private SoundType soundType;
	private Vector2f pos;
	
	private NetSound netSound;
	
	/**
	 * @param source
	 * @param id
	 * @param soundType
	 * @param pos
	 */
	public SoundEmittedEvent(Object source, int id, SoundType soundType, Vector2f pos) {
		super(source);
		this.id = id;
		this.soundType = soundType;
		this.pos = pos;
		
		this.netSound = new NetSound();
		this.netSound.type = soundType.netValue();
		this.netSound.posX = (short)pos.x;
		this.netSound.posY = (short)pos.y;
		
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	/**
	 * @return the soundType
	 */
	public SoundType getSoundType() {
		return soundType;
	}
	
	/**
	 * @return the netSound
	 */
	public NetSound getNetSound() {
		return netSound;
	}
	
}
