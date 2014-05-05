/*
 * see license.txt 
 */
package seventh.ai.basic.strategy;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.ThoughtProcess;
import seventh.game.PlayerEntity;
import seventh.game.Team;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * A very simple strategy for a bot
 * 
 * @author Tony
 *
 */
public class DefendAreaStrategy implements ThoughtProcess {

	private Vector2f position;		
	
	/**
	 * @param position
	 */
	public DefendAreaStrategy(Vector2f position) {
		this.position = position;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onKilled(seventh.ai.basic.Brain)
	 */
	@Override
	public void onKilled(Brain brain) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onSpawn(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawn(Brain brain) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see seventh.ai.Strategy#think(seventh.shared.TimeStep, seventh.ai.Brain)
	 */
	@Override
	public void think(TimeStep timeStep, Brain brain) {
		Locomotion motion = brain.getMotion();
		PlayerEntity bot = brain.getEntityOwner();
		
		if(bot.getBounds().contains(position)) {
			motion.stopMoving();
			motion.scanArea();
			
			checkForEnemies(brain);
		}
		else {
			
			if(!motion.isMoving()) {
				motion.scanArea();
				motion.moveTo(position);
			}
		}
	}
	
	private void checkForEnemies(Brain brain) {
		Locomotion motion = brain.getMotion();
		PlayerEntity bot = brain.getEntityOwner();
		Team myTeam = bot.getTeam();
				
		boolean engagingEnemy = false;
		
		// if we are already engaged in action, lets skip this
		if(motion.handsInUse() && motion.isMoving() && motion.isStaringAtEntity()) {
			engagingEnemy = true;
		}
		
		// #2
		// Now lets check to see if any enemies
		// are in our sights.  If so, attack! 
		
		if(!engagingEnemy) {
			List<PlayerEntity> entitiesInView = brain.getSensors().getSightSensor().getEntitiesInView();
			int size = entitiesInView.size();
			for(int i = 0; i < size; i++) {
				PlayerEntity playerEnt = entitiesInView.get(i);
										
				Team otherTeam = playerEnt.getTeam();
				if(otherTeam==null || myTeam==null || otherTeam.getId() != myTeam.getId()) {
					
					float dist = Vector2f.Vector2fDistanceSq(bot.getPos(), playerEnt.getPos());
					if(dist < 1050 ) {						
						motion.attack(playerEnt);							
						engagingEnemy = true;
						break;
					}												
				}
				
			}
		}
	}
}
