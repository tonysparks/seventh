/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientTank extends ClientEntity {

	/**
	 * @param pos
	 */
	public ClientTank(ClientGame game, Vector2f pos) {
		super(game, pos);
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		
		// TODO
		
//		BufferedImage image = Art.tankImage;
//		int hw = image.getWidth()/2;
//		int hh = image.getHeight()/2;
//		
//		
//		Vector2f cameraPos = camera.getPosition();
//		int x = (int)(pos.x - cameraPos.x) + bounds.width/2;
//		int y = (int)(pos.y - cameraPos.y) + bounds.height/2;				
//		
//		double d = Math.toDegrees(orientation) + 90;
//		canvas.rotate(d, x, y);		
//		canvas.drawImage(image, x-hw, y-hh, null);
//		canvas.rotate(-d, x, y);
	}

}
