/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.Config;

/**
 * @author Tony
 *
 */
public class AIConfig {

	private Config config;
	
	/**
	 * 
	 */
	public AIConfig(Config config) {
		this.config = config;
	}
	
	public long getSightExpireTime() {
		return this.config.getInt(8_000, "ai", "sightExpireTime");
	}
	
	public long getSightPollTime() {
		return this.config.getInt(300, "ai", "sightPollTime");
	}
	
	public long getSoundPollTime() {
		return this.config.getInt(200, "ai", "soundPollTime");
	}
	public long getSoundExpireTime() {
		return this.config.getInt(1_000, "ai", "soundExpireTime");
	}

	public long getFeelPollTime() {
		return this.config.getInt(200, "ai", "soundPollTime");
	}
	public long getFeelExpireTime() {
		return this.config.getInt(1_000, "ai", "feelExpireTime");
	}
	
	public long getTriggeringSystemPollTime() {
		return this.config.getInt(200, "ai", "triggeringSystemPollTime");
	}
	
	public long getEvaluationPollTime() {
		return this.config.getInt(300, "ai", "evaluationPollTime");
	}
}
