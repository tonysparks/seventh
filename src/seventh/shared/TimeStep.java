/*
 *	leola-live 
 *  see license.txt
 */
package seventh.shared;

/**
 * @author Tony
 *
 */
public class TimeStep {

	private long gameClock;
	private long deltaTime;
	/**
	 * @return the gameClock
	 */
	public long getGameClock() {
		return gameClock;
	}
	/**
	 * @param gameClock the gameClock to set
	 */
	public void setGameClock(long gameClock) {
		this.gameClock = gameClock;
	}
	/**
	 * @return the deltaTime
	 */
	public long getDeltaTime() {
		return deltaTime;
	}
	
	/**
	 * @return returns the time step as a fraction
	 */
	public double asFraction() {
		return deltaTime / 1000.0d;
	}
	/**
	 * @param deltaTime the deltaTime to set
	 */
	public void setDeltaTime(long deltaTime) {
		this.deltaTime = deltaTime;
	}		
}

