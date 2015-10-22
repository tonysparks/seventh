/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientHealthPack extends ClientEntity {

	/**
	 * @param game
	 * @param pos
	 */
	public ClientHealthPack(ClientGame game, Vector2f pos) {
		super(game, pos);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#killIfOutdated(long)
	 */
	@Override
	public boolean killIfOutdated(long gameClock) {
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Vector2f cameraPos = camera.getPosition();
		int x = (int)(pos.x - cameraPos.x);
		int y = (int)(pos.y - cameraPos.y);
		canvas.drawImage(Art.healthPack, x, y, null);
	}

}
