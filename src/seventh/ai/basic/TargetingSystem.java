/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.ai.basic.memory.SightMemory.SightMemoryRecord;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.game.entities.Entity.Type;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.Updatable;

/**
 * Responsible for fixating on the most appropriate target (enemy).
 * 
 * @author Tony
 *
 */
public class TargetingSystem implements Updatable {

    private Brain brain;
    private PlayerEntity currentTarget;
    
    private Timer checkTimer;
    private Timer reactionTimeTimer;
    /**
     * 
     */
    public TargetingSystem(Brain brain) {
        this.brain = brain;
        AIConfig config = brain.getConfig();
        this.checkTimer = new Timer(true, config.getTriggeringSystemPollTime());
        this.checkTimer.start();
        
        this.reactionTimeTimer = new Timer(false, config.getReactionTime());
    }

    public void reset(Brain brain) {
        this.clearTarget();        
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        checkTimer.update(timeStep);
        reactionTimeTimer.update(timeStep);
        
        if(checkTimer.isTime()) {
        
            Sensors sensors = brain.getSensors();
            
            Entity recentAttacker = sensors.getFeelSensor().getMostRecentAttacker(); 
            PlayerEntity closestEnemyInSight = sensors.getSightSensor().getClosestEnemy();
    //        SoundEmittedEvent closestSound = sensors.getSoundSensor().getClosestSound();
            
            PlayerEntity newTarget = this.currentTarget;
            this.currentTarget = null;
            
            /* if we are being attacked, this is fairly high priority */
            if(recentAttacker != null && recentAttacker.getType().equals(Type.PLAYER)) {
                
                if(closestEnemyInSight != null) {
                    
                    /* if the closest in sight and attacker are the same, then
                     * the choice is obvious
                     */
                    if(closestEnemyInSight == recentAttacker) {
                        this.currentTarget = closestEnemyInSight;
                    }
                    else {
                        
                        Vector2f botPos = this.brain.getEntityOwner().getCenterPos();
                        float attackerDis = Vector2f.Vector2fDistanceSq(botPos, recentAttacker.getCenterPos());
                        float sightDis = Vector2f.Vector2fDistanceSq(botPos, closestEnemyInSight.getCenterPos());
                        
                        if(sightDis < attackerDis) {
                            this.currentTarget = closestEnemyInSight;
                        }
                        else {
                            this.currentTarget = (PlayerEntity)recentAttacker;        
                        }
                    }
                }
                else {                
                    this.currentTarget = (PlayerEntity)recentAttacker;            
                }
            }
            else {
                
                if(closestEnemyInSight != null) {
                    this.currentTarget = closestEnemyInSight;
                }
                
            }
            
            if(newTarget != this.currentTarget) {
                this.reactionTimeTimer.reset();
            }
        }
        
        /*
         * If we have a target, look at them
         */
        stareAtTarget();
        
    }
    
    /**
     * Stare at the current target, if there is one
     */
    public void stareAtTarget() {
        if(hasTarget()) {
            brain.getMotion().stareAtEntity(getCurrentTarget());
        }
        else {
            brain.getMotion().scanArea();
        }
    }
    
    /**
     * If there is a target
     * @return
     */
    public boolean hasTarget() {
        return this.currentTarget != null && this.currentTarget.isAlive() && this.reactionTimeTimer.isTime();
    }
    
    
    /**
     * Clears the target
     */
    public void clearTarget() {
        this.currentTarget = null;
    }
    
    /**
     * @return the currentTarget
     */
    public PlayerEntity getCurrentTarget() {
        return currentTarget;
    }
    
    
    /**
     * The last remembered position
     * 
     * @return
     */
    public Vector2f getLastRemeberedPosition() {
        if(hasTarget()) {
            SightMemoryRecord record = this.brain.getSensors().getSightSensor().getMemoryRecordFor(currentTarget);
            if(record.isValid()) {
                return record.getLastSeenAt();
            }
        }
        
        return null;
    }
    
    /**
     * @param target
     * @return true if the current target position is in the line of fire
     */
    public boolean targetInLineOfFire(Vector2f target) {
        PlayerEntity bot = brain.getEntityOwner();
        float distanceSq = bot.distanceFromSq(target);
        if(distanceSq <= bot.getCurrentWeaponDistanceSq()) {            
            return brain.getWorld().inLineOfFire(bot, target);
        }
        return false;
    }
    
    /**
     * @return true if the current target is in the line of fire
     */
    public boolean currentTargetInLineOfFire() {
        if(hasTarget()) {
            PlayerEntity bot = brain.getEntityOwner();
            float distanceSq = bot.distanceFromSq(currentTarget);
            if(distanceSq <= bot.getCurrentWeaponDistanceSq()) {            
                return brain.getWorld().inLineOfFire(bot, currentTarget);
            }
        }
        return false;
    }
}
