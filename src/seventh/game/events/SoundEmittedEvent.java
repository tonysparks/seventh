/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.SoundType;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class SoundEmittedEvent extends Event {
	private int id;
	private SoundType soundType;
	private Vector2f pos;
		
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
	}
	
	
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}



	/**
	 * @param soundType the soundType to set
	 */
	public void setSoundType(SoundType soundType) {
		this.soundType = soundType;
	}



	/**
	 * @param pos the pos to set
	 */
	public void setPos(Vector2f pos) {
		this.pos.set(pos);
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
//	public NetSound getNetSound() {
//		this.netSound.type = soundType.netValue();
//		this.netSound.posX = (short)pos.x;
//		this.netSound.posY = (short)pos.y;
//		
//		return netSound;
//	}
	
}
