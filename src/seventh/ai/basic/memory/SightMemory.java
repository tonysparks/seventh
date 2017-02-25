/*
 * see license.txt 
 */
package seventh.ai.basic.memory;

import java.util.List;

import seventh.game.entities.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Visual sight memory -- what this bot has seen, we store in a small cache so that he doesn't immediately forget the
 * world around him
 * 
 * 
 * @author Tony
 *
 */
public class SightMemory implements Updatable {

    
    /**
     * Visual record of a sighting of a {@link PlayerEntity}
     * 
     * @author Tony
     *
     */
    public static class SightMemoryRecord {
        private final long expireTime;
        
        private PlayerEntity entity;
        private Weapon lastSeenWithWeapon;
        private Vector2f lastSeenAt;
        private long timeSeen;
        private long seenDelta;
        
        private boolean isValid;
        
        /**
         * @param expireTime
         */
        public SightMemoryRecord(long expireTime) {
            this.expireTime = expireTime;            
            this.lastSeenAt = new Vector2f();
            this.isValid = false;
        }
        
        
        /**
         * See a {@link PlayerEntity}
         * 
         * @param timeStep
         * @param entity
         */
        public void seeEntity(TimeStep timeStep, PlayerEntity ent) {
            if(ent.isAlive()) {
                this.entity = ent;
                this.timeSeen = timeStep.getGameClock();
                this.lastSeenAt = ent.getCenterPos();
                
                this.lastSeenWithWeapon = ent.getInventory().currentItem();
                this.isValid = true;
            }
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
            this.seenDelta = timeStep.getGameClock() - this.timeSeen;
            if(isExpired(timeStep)) {
                expire();
            }
        }
        
        /**
         * Expire this record
         */
        public void expire() {
            this.entity = null;
            this.lastSeenWithWeapon = null;
            
            this.lastSeenAt.zeroOut();                        
            this.isValid = false;
        }
        
        /**
         * If this entity is expired
         * 
         * @param timeStep
         * @return
         */
        public boolean isExpired(TimeStep timeStep) {
            return this.entity == null ||
                  !this.entity.isAlive() ||
                  (timeStep.getGameClock() - this.timeSeen > this.expireTime);
        }
        
        /**
         * @return the entity
         */
        public PlayerEntity getEntity() {
            return entity;
        }
        
        /**
         * @return the lastSeenAt
         */
        public Vector2f getLastSeenAt() {
            return lastSeenAt;
        }
        
        /**
         * @return the lastSeenWithWeapon
         */
        public Weapon getLastSeenWithWeapon() {
            return lastSeenWithWeapon;
        }
        
        /**
         * @return the timeSeen
         */
        public long getTimeSeen() {
            return timeSeen;
        }
        
        /**
         * @return the amount of msec's ago this entity was seen
         */
        public long getTimeSeenAgo() {
            return seenDelta;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {        
            return isValid + "";
        }
        
    }
    
    private SightMemoryRecord[] entityRecords;
    
    
    
    /**
     * 
     */
    public SightMemory(long expireTime) {    
        this.entityRecords = new SightMemoryRecord[SeventhConstants.MAX_PLAYERS];
        for(int i = 0; i < this.entityRecords.length; i++) {
            this.entityRecords[i] = new SightMemoryRecord(expireTime);
        }        
    }
    
    

    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        updateSightMemory(timeStep);                
    }
    
    /**
     * Expire this memory
     */
    public void clear() {
        for(int i = 0; i < this.entityRecords.length; i++) {
            this.entityRecords[i].expire();            
        }
    }
    
    private void updateSightMemory(TimeStep timeStep) {
        for(int i = 0; i < this.entityRecords.length; i++) {
            this.entityRecords[i].checkExpired(timeStep);            
        }
    }
    
    /**
     * @return the entityRecords
     */
    public SightMemoryRecord[] getEntityRecords() {
        return entityRecords;
    }
    
    
    /**
     * See a set of {@link PlayerEntity}
     * 
     * @param timeStep
     * @param entitiesInView
     */
    public void see(TimeStep timeStep,List<PlayerEntity> entitiesInView) {
        for(int i = 0; i < entitiesInView.size(); i++) {
            PlayerEntity ent = entitiesInView.get(i);
            see(timeStep, ent);
        }
    }
    
    
    /**
     * See a {@link PlayerEntity}
     * 
     * @param timeStep
     * @param ent
     */
    public void see(TimeStep timeStep, PlayerEntity ent) {
        if(ent != null) {
            int id = ent.getId();
            if(id>-1 && id < this.entityRecords.length) {
                this.entityRecords[id].seeEntity(timeStep, ent);
            }
        }
    }
}
