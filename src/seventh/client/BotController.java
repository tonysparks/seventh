/**
 * 
 */
package seventh.client;

import seventh.client.gfx.Cursor;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * @author Tony
 *
 */
public class BotController implements Updatable {
	private ClientGame game;
	
	/**	
	 */
	public BotController(ClientGame game) {
		this.game = game;
	}

	public void followMe(Cursor cursor) {
		Vector2f pos = cursor.getCursorPos();
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		
	}
}
