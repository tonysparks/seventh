/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.memory.SightMemory;
import seventh.ai.basic.memory.SightMemory.SightMemoryRecord;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.game.Team;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * The bots vision
 * 
 * @author Tony
 *
 */
public class SightSensor implements Sensor {

	private SightMemory memory;
	private PlayerEntity entity;	
	private World world;
	
	private Timer updateSight;
		
	private List<PlayerEntity> entitiesInView;
	
	/**
	 * @param width
	 * @param height
	 */
	public SightSensor(Brain brain) {
		this.memory = brain.getMemory().getSightMemory();
		this.entity = brain.getEntityOwner();
		this.world = brain.getWorld();
				
		this.updateSight = new Timer(true, brain.getConfig().getSightPollTime());
		this.updateSight.start();
		
		this.entitiesInView = new ArrayList<PlayerEntity>();	
	}		
	
	/**
	 * The sight records
	 * 
	 * @return
	 */
	public SightMemoryRecord[] getSightMemoryRecords() {
		return this.memory.getEntityRecords();
	}
	
	
	/**
	 * Get the closest entity to this bot
	 * @return the closest entity to this bot
	 */
	public PlayerEntity getClosestEntity() {
		PlayerEntity result = null;
		
		float closestDistance = Float.MAX_VALUE;		
		Vector2f botPos = this.entity.getCenterPos();
		
		SightMemoryRecord[] records = getSightMemoryRecords();
		for(int i = 0; i < records.length; i++) {
			if( records[i].isValid()) {
				if(result==null) {
					result = records[i].getEntity();
				}
				else {
					Vector2f pos = records[i].getEntity().getCenterPos();
					
					
					float distance = Vector2f.Vector2fDistanceSq(pos, botPos);
					if(distance < closestDistance) {
						result = records[i].getEntity();
						closestDistance = distance;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Get the closest enemy to this bot
	 * @return the closest enemy to this bot
	 */
	public PlayerEntity getClosestEnemy() {
		PlayerEntity result = null;
		
		float closestDistance = Float.MAX_VALUE;		
		Vector2f botPos = this.entity.getCenterPos();
		Team myTeam = this.entity.getTeam();
		
		SightMemoryRecord[] records = getSightMemoryRecords();
		for(int i = 0; i < records.length; i++) {
			if( records[i].isValid() ) {
				PlayerEntity other = records[i].getEntity(); 
				if(other.getTeam().getId() == myTeam.getId()) {
					continue;
				}
				
				if(result==null) {
					result = other;
				}
				else {
					Vector2f pos = other.getCenterPos();
					
					
					float distance = Vector2f.Vector2fDistanceSq(pos, botPos);
					if(distance < closestDistance) {
						result = other;
						closestDistance = distance;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Get the list of enemies in view
	 * @param results the resulting list of enemies in the view
	 * @return the same results object, just convenience
	 */
	public List<PlayerEntity> getEnemies(List<PlayerEntity> results) {
		Team myTeam = entity.getTeam();
		SightMemoryRecord[] records = getSightMemoryRecords();
		for(int i = 0; i < records.length; i++) {
			if( records[i].isValid()) {
				PlayerEntity otherPlayer = records[i].getEntity();
				
				Team otherTeam = otherPlayer.getTeam();
				if(otherTeam==null || myTeam==null || otherTeam.getId() != myTeam.getId()) {
					results.add(otherPlayer);
					break;
				}
			}		
		}
		
		return results;
	}
	
	
	/**
	 * The memory record for the entity
	 * 
	 * @param ent
	 * @return
	 */
	public SightMemoryRecord getMemoryRecordFor(Entity ent) {
		if(ent != null) {
			return getSightMemoryRecords()[ent.getId()];
		}
		return null;
	}
	
	/**
	 * If the supplied entity was in view (or recently in view)
	 * 
	 * @param entity 
	 * @return If the supplied entity was in view (or recently in view)
	 */
	public boolean inView(Entity entity) {
		SightMemoryRecord[] records = getSightMemoryRecords();
		if(entity != null) {
			return records[entity.getId()].isValid();
		}
		return false;
	}
	
	/**
	 * Last time this {@link Entity} was seen by the bot
	 * 
	 * @param entity
	 * @return Last time this {@link Entity} was seen by the bot
	 */
	public long lastSeen(Entity entity) {
		SightMemoryRecord[] records = getSightMemoryRecords();
		if(entity != null) {
			return records[entity.getId()].getTimeSeen();
		}
		return -1;
	}
	
	/**
	 * The amount of msec's this {@link Entity} was last seen.
	 * 
	 * @param entity
	 * @return The amount of msec's this {@link Entity} was last seen.
	 */
	public long timeSeenAgo(Entity entity) {
		SightMemoryRecord[] records = getSightMemoryRecords();
		if(entity != null) {
			return records[entity.getId()].getTimeSeenAgo();
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.Sensor#reset()
	 */
	@Override
	public void reset(Brain brain) {
		this.entitiesInView.clear();
		this.entity = brain.getEntityOwner();
		this.updateSight.reset();
		this.memory.clear();
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Sensor#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.updateSight.update(timeStep);
				
		if(this.updateSight.isTime()) {
			see(timeStep);
		}
		
	}

	/**
	 * Looks for anything interesting in the 
	 * current view port
	 */
	private void see(TimeStep timeStep) {
		
		
		/*
		 * If we should pool the world for visuals, go 
		 * ahead and do so now
		 */			
		if(this.entity != null && this.entity.isAlive()) {
			this.entitiesInView.clear();
			this.world.getPlayersInLineOfSight(this.entitiesInView, this.entity);
			
			this.memory.see(timeStep, entitiesInView);
		}
						
	}
}
