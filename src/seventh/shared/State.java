/*
 * see license.txt 
 */
package seventh.shared;


/**
 * @author Tony
 *
 */
public interface State {
	public void enter();
	public void update(TimeStep timeStep);
	public void exit();
}
