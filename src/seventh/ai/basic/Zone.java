/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.game.entities.BombTarget;
import seventh.game.entities.Entity;
import seventh.math.Rectangle;
import seventh.shared.Debugable;

/**
 * A {@link Zone} represents a section of the game world.  We break the game world
 * into {@link Zone}s so that we can gather statistics about "hot" areas and makes
 * it easier to defend or attack areas of the map.
 * 
 * @author Tony
 *
 */
public class Zone implements Debugable {

	private Rectangle bounds;
	private int id;
	
	private List<BombTarget> targets;
	
	private ZoneStats stats;
	
	private boolean isHabitable;
	
	/**
	 * @param id
	 * @param bounds
	 * @param isHabitable
	 */
	public Zone(int id, Rectangle bounds, boolean isHabitable) {
		this.id = id;
		this.bounds = bounds;
		this.targets = new ArrayList<BombTarget>();
		this.stats = new ZoneStats(this);
		this.isHabitable = isHabitable;
	}
	
	/**
	 * @return the isHabitable
	 */
	public boolean isHabitable() {
		return isHabitable;
	}
	
	/**
	 * @return the stats
	 */
	public ZoneStats getStats() {
		return stats;
	}
	
	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return true if this Zone has {@link BombTarget}s
	 */
	public boolean hasTargets() {
		return !this.targets.isEmpty();
	}
	
	/**
	 * @return the targets
	 */
	public List<BombTarget> getTargets() {
		return targets;
	}
	
	
	/**
	 * Clears any registered targets
	 */
	public void clearTargets() {
		this.targets.clear();
	}
	
	/**
	 * Adds a bomb target to this {@link Zone}
	 * @param target
	 */
	public void addTarget(BombTarget target) {
		this.targets.add(target);
	}
	
	/**
	 * @return true if this zone still contains undestroyed {@link BombTarget}s
	 */
	public boolean isTargetsStillActive() {
		boolean stillActive = false;
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(target.isAlive()) {
				stillActive = true;
				break;
			}
		}
		
		return stillActive;
	}
	
	/**
	 * @return true if there is an active bomb
	 */
	public boolean hasActiveBomb() {
		
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(target.isAlive()) {
				if(target.bombActive() || target.bombPlanting()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @param entity
	 * @return true if the supplied Entity is in this {@link Zone}
	 */
	public boolean contains(Entity entity) {
		return this.bounds.intersects(entity.getBounds());
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("id", this.id)
		  .add("bounds", this.bounds);
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}
}
