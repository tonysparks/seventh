/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.MoveToAction;
import seventh.game.events.SoundEmittedEvent;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class InvestigateActionEvaluator extends ActionEvaluator {

	private MoveToAction moveToAction;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public InvestigateActionEvaluator(Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.moveToAction = new MoveToAction(new Vector2f());
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desirability = 0.0;
		
		SoundEmittedEvent sound = brain.getSensors().getSoundSensor().getClosestSound();
		if(sound != null) {
			desirability = brain.getPersonality().curiosity;
			
			switch(sound.getSoundType()) {																		
				case EXPLOSION:	
				case GRENADE_THROW:
				case KAR98_FIRE:			
				case M1_GARAND_FIRE:			
				case MELEE_HIT:				
				case MELEE_SWING:				
				case MP40_FIRE:				
				case MP44_FIRE:			
				case PISTOL_FIRE:
				case RISKER_FIRE:
				case RPG_FIRE:	
				case SHOTGUN_FIRE:			
				case SPRINGFIELD_FIRE:
				case THOMPSON_FIRE:
					desirability += brain.getRandomRange(0.2, 0.4);
					break;
				
				
				case WEAPON_PICKUP:
				case AMMO_PICKUP:
					desirability += brain.getRandomRange(0.2, 0.43);
					break;
				
				/* enemy is busy doing something, good time
				 * to attack
				 */
				case WEAPON_DROPPED:
				case WEAPON_SWITCH:
				case THOMPSON_RELOAD:
				case SPRINGFIELD_RECHAMBER:
				case SPRINGFIELD_RELOAD:
				case SHOTGUN_RELOAD:
				case SHOTGUN_PUMP:
				case RISKER_RELOAD:
				case RISKER_RECHAMBER:
				case PISTOL_RELOAD:
				case MP44_RELOAD:	
				case MP40_RELOAD:
				case M1_GARAND_RELOAD:
				case KAR98_RELOAD:
				case KAR98_RECHAMBER:
					desirability += brain.getRandomRange(0.4, 0.45);
					break;
					
					
				case EMPTY_FIRE:
				case GRENADE_PINPULLED:				
				case M1_GARAND_LAST_FIRE:
					desirability += brain.getRandomRange(0.64, 0.85);
					break;
										
				case BOMB_DISARM:
				case BOMB_PLANT:
					desirability += brain.getRandomRange(0.2, 0.5);
					break;
					
				case TANK_IDLE:
				case TANK_OFF:
				case TANK_ON:
				case TANK_REV_DOWN:
				case TANK_REV_UP:
				case TANK_SHIFT:
				case TANK_TURRET_MOVE:
					desirability = 0f; // get the hell out of dodge
					break;
				default:
					desirability = 0.1f;
			}
		}
		
		desirability = Math.min(desirability, 1f);
		desirability *= getCharacterBias();
		
		return desirability;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {		
		SoundEmittedEvent sound = brain.getSensors().getSoundSensor().getClosestSound();				
		if(sound != null) {
			this.moveToAction.reset(brain, sound.getPos());
			brain.getMotion().lookAt(this.moveToAction.getDestination());
		}	
		
		return this.moveToAction;
	}

}
