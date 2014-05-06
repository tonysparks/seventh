/*
 * see license.txt 
 */
package seventh.ai.basic;

/**
 * Some useful statistics about a zone.  This helps the AI
 * make some decisions on strategy.
 * 
 * @author Tony
 *
 */
public class ZoneStats {

	private int numberOfAxisDeaths;
	private int numberOfAxisKills;
	
	private int numberOfAlliedDeaths;
	private int numberOfAlliedKills;
	
	private Zone zone;
	
	/**
	 */
	public ZoneStats(Zone zone) {
		this.zone = zone;
	}	
	
	/**
	 * @return the {@link Zone}
	 */
	public Zone getZone() {
		return zone;
	}
	
	/**
	 * @return the total number of deaths in this Zone
	 */
	public int getTotalKilled() {
		return this.numberOfAlliedDeaths + this.numberOfAxisDeaths;
	}
	
	public void addAxisDeath() {
		numberOfAxisDeaths++;
	}
	
	public void addAxisKill() {
		numberOfAxisKills++;
	}
	
	/**
	 * @return the numberOfDeaths
	 */
	public int getNumberOfAxisDeaths() {
		return numberOfAxisDeaths;
	}
	
	/**
	 * @return the numberOfKills
	 */
	public int getNumberOfAxisKills() {
		return numberOfAxisKills;
	}
	
	
	public void addAlliedDeath() {
		numberOfAlliedDeaths++;
	}
	
	public void addAlliedKill() {
		numberOfAlliedKills++;
	}
	
	/**
	 * @return the numberOfDeaths
	 */
	public int getNumberOfAlliedDeaths() {
		return numberOfAlliedDeaths;
	}
	
	/**
	 * @return the numberOfKills
	 */
	public int getNumberOfAlliedKills() {
		return numberOfAlliedKills;
	}

}
