/*
 * see license.txt 
 */
package seventh.ai.basic;

/**
 * @author Tony
 *
 */
public class ZoneStats {

	private int numberOfAxisDeaths;
	private int numberOfAxisKills;
	
	private int numberOfAlliedDeaths;
	private int numberOfAlliedKills;
	
	/**
	 * 
	 */
	public ZoneStats() {
		// TODO Auto-generated constructor stub
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
