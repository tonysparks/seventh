/*
 * see license.txt 
 */
package seventh.ai.basic.memory;

import java.util.List;

import seventh.game.SoundEventPool;
import seventh.game.entities.PlayerEntity;
import seventh.game.events.SoundEmittedEvent;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Sound memory -- what this bot has heard, we store in a small cache so that he doesn't immediately forget the
 * world around him
 * 
 * 
 * @author Tony
 *
 */
public class SoundMemory implements Updatable {

    
    /**
     * Record of a sound
     * 
     * @author Tony
     *
     */
    public static class SoundMemoryRecord {
        private final long expireTime;    
        private long timeHeard;
        private long timeHeardAgo;
        
        private SoundEmittedEvent sound;
        private boolean isValid;
        
        /**
         * @param expireTime
         */
        public SoundMemoryRecord(long expireTime) {
            this.expireTime = expireTime;                        
            this.isValid = false;
            this.sound = new SoundEmittedEvent(this, 0, SoundType.MUTE, new Vector2f());
        }
        
        
        /**
         * See a {@link PlayerEntity}
         * 
         * @param timeStep
         * @param entity
         */
        public void hearSound(TimeStep timeStep, SoundEmittedEvent sound) {
            this.timeHeard = timeStep.getGameClock();
            this.sound.setId(sound.getId());
            this.sound.setPos(sound.getPos());
            this.sound.setSoundType(sound.getSoundType());
            this.sound.setSource(sound.getSource());
            this.isValid = true;            
        }
        
        /**
         * @return the isValid
         */
        public boolean isValid() {
            return isValid;
        }
        
        /**
         * Checks if this memory record has expired
         * 
         * @param timeStep
         */
        public void checkExpired(TimeStep timeStep) {
            this.timeHeardAgo = timeStep.getGameClock() - this.timeHeard;
            if(isExpired(timeStep)) {
                expire();
            }
        }
        
        /**
         * Expire this record
         */
        public void expire() {
            this.sound.setId(-1);
            this.sound.setSoundType(SoundType.MUTE);
            this.isValid = false;
        }
        
        /**
         * If this sound is expired
         * 
         * @param timeStep
         * @return
         */
        public boolean isExpired(TimeStep timeStep) {
            return (timeStep.getGameClock() - this.timeHeard > this.expireTime);
        }
        
        /**
         * @return the sound
         */
        public SoundEmittedEvent getSound() {
            return sound;
        }
        
        /**
         * @return the timeHeard
         */
        public long getTimeHeard() {
            return timeHeard;
        }
        
        /**
         * @return the timeHeardAgo
         */
        public long getTimeHeardAgo() {
            return timeHeardAgo;
        }        
        
    }
    
    private SoundMemoryRecord[] soundRecords;
        
    
    /**
     * 
     */
    public SoundMemory(long expireTime) {    
        this.soundRecords = new SoundMemoryRecord[SeventhConstants.MAX_SOUNDS*3];
        for(int i = 0; i < this.soundRecords.length; i++) {
            this.soundRecords[i] = new SoundMemoryRecord(expireTime);
        }        
    }
    
    

    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        updateSoundMemory(timeStep);                
    }
    
    /**
     * Expire this memory
     */
    public void clear() {
        for(int i = 0; i < this.soundRecords.length; i++) {
            this.soundRecords[i].expire();            
        }
    }
    
    /**
     * @return the soundRecords
     */
    public SoundMemoryRecord[] getSoundRecords() {
        return soundRecords;
    }
    
    /**
     * Expire the sounds
     * 
     * @param timeStep
     */
    private void updateSoundMemory(TimeStep timeStep) {
        for(int i = 0; i < this.soundRecords.length; i++) {
            this.soundRecords[i].checkExpired(timeStep);            
        }
    }

    /**
     * Hear sounds
     * 
     * @param timeStep
     * @param sounds
     */
    public void hear(TimeStep timeStep, List<SoundEmittedEvent> sounds) {
        for(int i = 0; i < sounds.size(); i++) {
            SoundEmittedEvent sound = sounds.get(i);
            hear(timeStep, sound);
        }
    }
        
    /**
     * Hear sounds
     * 
     * @param timeStep
     * @param sounds
     */
    public void hear(TimeStep timeStep, SoundEventPool pool) {
        for(int i = 0; i < pool.numberOfSounds(); i++) {
            SoundEmittedEvent sound = pool.getSound(i);
            hear(timeStep, sound);
        }
    }

    /**
     * Hear a sound
     * 
     * @param timeStep
     * @param event
     */
    public void hear(TimeStep timeStep, SoundEmittedEvent event) {
        long oldest = 0;
        int oldestIndex = 0;
        
        for(int i = 0; i < this.soundRecords.length; i++) {
            if(!this.soundRecords[i].isValid()) {
                oldestIndex = i;
                break;
            }
            else {
                /* replace with oldest sound */                    
                if( oldest < this.soundRecords[i].getTimeHeardAgo()) {
                    oldest = this.soundRecords[i].getTimeHeardAgo();
                    oldestIndex = i;
                }
            }
        }
        
        this.soundRecords[oldestIndex].hearSound(timeStep, event);
    }
    
}
