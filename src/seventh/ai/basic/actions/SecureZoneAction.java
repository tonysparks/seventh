/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Zone;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class SecureZoneAction extends AdapterAction {

	private Zone zone;
	
	private List<PlayerEntity> playersInZone;
	/**
	 * 
	 */
	public SecureZoneAction(Zone zone) {		
		this.zone = zone;
		this.playersInZone = new ArrayList<>();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		Vector2f pos = brain.getWorld().getRandomSpot(brain.getEntityOwner(), zone.getBounds());
		if(pos != null) {	
			brain.getMotion().moveTo(pos);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		start(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		this.playersInZone.clear();
		brain.getWorld().playersIn(this.playersInZone, zone.getBounds());
		
		boolean isClear = true;
		
		for(int i = 0; i < playersInZone.size(); i++) {
			PlayerEntity ent = playersInZone.get(i);
			if(ent.isAlive()) {
				if(!brain.getEntityOwner().isOnTeamWith(ent)) {
					isClear = false;
					break;
				}
			}
		}
				
		return isClear;
		
	}

	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("zone", this.zone);
	}
}
