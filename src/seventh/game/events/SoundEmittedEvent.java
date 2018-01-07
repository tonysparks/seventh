/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.math.Vector2f;
import seventh.shared.Event;
import seventh.shared.SoundType;

/**
 * @author Tony
 *
 */
public class SoundEmittedEvent extends Event {
    private int bufferIndex;
    private int id;
    private SoundType soundType;
    private Vector2f pos;
    private int entityId;
    /**
     * @param source
     * @param id
     * @param soundType
     * @param entityId
     */
    public SoundEmittedEvent(Object source, int id, SoundType soundType, int entityId) {
        super(source);
        this.id = id;
        this.soundType = soundType;
        this.entityId = entityId;
    }
    
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
        this.entityId = -1;
    }
    
    
    /**
     * @param bufferIndex the bufferIndex to set
     */
    public void setBufferIndex(int bufferIndex) {
        this.bufferIndex = bufferIndex;
    }
    
    /**
     * @return the bufferIndex
     */
    public int getBufferIndex() {
        return bufferIndex;
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
     * @return the entityId
     */
    public int getEntityId() {
        return entityId;
    }
    
    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
