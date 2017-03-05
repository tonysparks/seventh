/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientDoor extends ClientEntity {

	/**
	 * @param game
	 * @param pos
	 */
	public ClientDoor(ClientGame game, Vector2f pos) {
		super(game, pos);
	}
	
	@Override
	public void updateState(NetEntity state, long time) {	
		super.updateState(state, time);
	}
	
    @Override
    public boolean killIfOutdated(long gameClock) {    
        return false;
    }
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		// TODO Auto-generated method stub

	}

}
