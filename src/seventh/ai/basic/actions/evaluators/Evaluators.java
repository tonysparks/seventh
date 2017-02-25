/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.game.entities.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Evaluators {

    /**
     * Evaluate the best possible action for the supplied brain given a set of evaluators.
     * 
     * @param brain
     * @param evaluators
     * @return the best {@link ActionEvaluator} of the bunch
     */
    public static ActionEvaluator evaluate(Brain brain, ActionEvaluator ... evaluators) {
        double highestDesire = 0;
        ActionEvaluator bestEval = null;
        
        int size = evaluators.length;
        for(int i = 0; i < size; i++) {
            ActionEvaluator eval = evaluators[i]; 
            double desire = eval.calculateDesirability(brain);
            if(bestEval == null || desire > highestDesire) {
                bestEval = eval;
                highestDesire = desire;
            }
        }
        
        return bestEval;
    }
    
    /**
     * Evaluate the best possible action for the supplied brain given a set of evaluators.
     * 
     * @param brain
     * @param evaluators
     * @return the best {@link ActionEvaluator} of the bunch
     */
    public static ActionEvaluator evaluate(Brain brain, List<ActionEvaluator> evaluators) {
        double highestDesire = 0;
        ActionEvaluator bestEval = null;
        
        int size = evaluators.size();
        for(int i = 0; i < size; i++) {
            ActionEvaluator eval = evaluators.get(i); 
            double desire = eval.calculateDesirability(brain);
            if(bestEval == null || desire > highestDesire) {
                bestEval = eval;
                highestDesire = desire;
            }
        }
        
        return bestEval;
    }
    
    /**
     * Health score between 0 and 1
     * @param ent
     * @return
     */
    public static double healthScore(PlayerEntity ent) {
        return (double) ent.getHealth() / (double) ent.getMaxHealth();
    }
    
    public static double weaponStrengthScore(Weapon weapon) {
        double score = 0;
        if ( weapon != null ) {
            switch(weapon.getType()) {
                case PISTOL:
                    score = 0.2;
                    break;    
                case ROCKET_LAUNCHER:
                    score = 0.5;
                    break;    
                case MP40:
                    score = 0.7;
                    break;
                case THOMPSON:
                    score = 0.7;
                    break;                    
                case KAR98:
                    score = 0.8;
                    break;
                case M1_GARAND:
                    score = 0.8;
                    break;                
                case MP44:
                    score = 0.8;
                    break;                
                case MG42:
                    score = 0.8;
                    break;
                case RISKER:
                    score = 0.8;
                    break;                
                case SHOTGUN:
                    score = 0.8;
                    break;
                case SPRINGFIELD:
                    score = 0.8;
                    break;                        
                default:
                    break;        
            }
        }
        return score;
    }
    
    /**
     * Current weapon score (only accounts for bullets in clip)
     * @param ent
     * @return
     */
    public static double currentWeaponAmmoScore(PlayerEntity ent) {
        
        double score = 0;
        Weapon weapon = ent.getInventory().currentItem();
        score = weaponAmmoScore(weapon);
        
        return score;
    }
    
    /**
     * Current weapon score (only accounts for bullets in clip)
     * @param ent
     * @return
     */
    public static double weaponAmmoScore(Weapon weapon) {
        
        double score = 0;
        if(weapon != null) {            
            double clipSize = (double)weapon.getClipSize();
            if(clipSize != 0) {
                return (double)weapon.getBulletsInClip() / clipSize;
            }
        }
        
        return score;
    }
    
    
    public static double weaponDistanceScore(PlayerEntity ent, PlayerEntity enemy) {
        return weaponDistanceScore(ent, enemy.getCenterPos());
    }
    
    public static double weaponDistanceScore(PlayerEntity ent, Vector2f target) {
        double score = 0;
        Weapon weapon = ent.getInventory().currentItem();
        if(weapon != null) {
            float distanceAwaySq = Vector2f.Vector2fDistanceSq(ent.getCenterPos(), target);
            double bulletRangeSq = ent.getCurrentWeaponDistanceSq();
            if(bulletRangeSq > 0 && distanceAwaySq < bulletRangeSq) {
                score = 0.75;
            
                double distanceScore = distanceAwaySq / bulletRangeSq;
    
                
                /* lower the score if they are too close */
                if(distanceScore < 0.15) {
                    distanceScore = 0;
                }
                
                /* lower the score if they are too far away */
                else if(distanceScore > 0.85) {
                    distanceScore *= 0.80;
                }
                
                
                score *= distanceScore;
            }
            
            
        }
        
        return score;
    }
}
