/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.math.Rectangle;

/**
 * A {@link Zone} represents a section of the game world.  We break the game world
 * into {@link Zone}s so that we can gather statistics about "hot" areas and makes
 * it easier to defend or attack areas of the map.
 * 
 * @author Tony
 *
 */
public class Zone {

	private Rectangle bounds;
	private int id;
	
	private List<BombTarget> targets;
	
	private ZoneStats stats;
	
	/**
	 * @param id
	 * @param bounds
	 */
	public Zone(int id, Rectangle bounds) {
		this.id = id;
		this.bounds = bounds;
		this.targets = new ArrayList<BombTarget>();
		this.stats = new ZoneStats(this);
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
	 * @param entity
	 * @return true if the supplied Entity is in this {@link Zone}
	 */
	public boolean contains(Entity entity) {
		return this.bounds.intersects(entity.getBounds());
	}

}
