/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.ClientGame;
import seventh.client.entities.ClientEntity;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientBomb extends ClientEntity {

	/**
	 * @param pos
	 */
	public ClientBomb(ClientGame game, Vector2f pos) {
		super(game, pos);
	}

	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#isBackgroundObject()
	 */
	@Override
	public boolean isBackgroundObject() {
		return true;
	}
		
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);		
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		float x = (pos.x - cameraPos.x);
		float y = (pos.y - cameraPos.y);
		canvas.drawImage(Art.bombImage, x, y, null);
	}

}
