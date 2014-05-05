/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public interface Sensor {

	public void reset(Brain brain);
	public void update(TimeStep timeStep);
}
