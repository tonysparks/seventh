/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.body.SwitchWeaponAction;
import seventh.game.Inventory;
import seventh.game.entities.Entity.Type;
import seventh.game.weapons.Weapon;

/**
 * @author Tony
 *
 */
public class SwitchWeaponEvaluator extends ActionEvaluator {

    private Type weaponType;
    private SwitchWeaponAction switchWeaponAction;
    
    /**
     * @param goals
     * @param characterBias
     */
    public SwitchWeaponEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        
        this.switchWeaponAction = new SwitchWeaponAction(weaponType);
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double desire = 0.0;
        Inventory inventory = brain.getEntityOwner().getInventory();
        if(inventory.numberOfItems() > 1 ) {
            Weapon weapon = inventory.currentItem();
            if (weapon != null) {            
                                                                        
                // do we have enough ammo?
//                double weaponScore = Evaluators.currentWeaponAmmoScore(brain.getEntityOwner());
                
                /* ensure we are not currently firing/reloading/switching
                 * the weapon
                 */
                if(weapon.isReady()) {
                    if( weapon.getTotalAmmo() <= 0 ) {
                        desire = 1;
                    }
                    
                    
                    Weapon bestWeapon = getBestScoreWeapon(inventory);
                    if(bestWeapon != weapon) {
                        desire += brain.getRandomRange(0.1, 0.4);
                    }                                        
                }
                
            }
            else {
                desire = 1.0;
            }
            

            /* if we are currently targeting someone, 
             * we should lessen the desire to switch weapons
             */
            TargetingSystem system = brain.getTargetingSystem();
            if(desire < 1.0 && system.hasTarget()) {
                desire *= brain.getRandomRange(0.4, 0.7);
            }
            
            desire *= getCharacterBias();
        }
        
        return desire;
    }

    private Weapon getBestScoreWeapon(Inventory inventory) {
        int size = inventory.numberOfItems();
        List<Weapon> weapons = inventory.getItems(); 
        double score = -1;
        Weapon bestWeapon = null;
        for(int i = 0; i < size; i++) {
            Weapon weapon = weapons.get(i);
            double weaponScore  = Evaluators.weaponStrengthScore(weapon);
                   weaponScore *= Evaluators.weaponAmmoScore(weapon);
                   
            if(bestWeapon == null || score < weaponScore) {
                score = weaponScore;
                bestWeapon = weapon;
            }
        }
        
        return bestWeapon;
    }

    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        Weapon bestWeapon = getBestScoreWeapon(brain.getEntityOwner().getInventory());
        this.switchWeaponAction.reset(bestWeapon.getType());
        return this.switchWeaponAction;
    }

}
