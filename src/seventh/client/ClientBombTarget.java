/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientBombTarget extends ClientEntity {

	private Sprite sprite;
	private boolean isBombPlanted;
	
	/**
	 * @param pos
	 */
	public ClientBombTarget(ClientGame game, Vector2f pos) {
		super(game, pos);
		
		this.isBombPlanted = false;
		this.sprite = new Sprite(Art.computerImage);
		this.sprite.setSize(32, 32);
		getBounds().setSize(32, 32);
	}

	/**
	 * @param isBombPlanted the isBombPlanted to set
	 */
	public void setBombPlanted(boolean isBombPlanted) {
		this.isBombPlanted = isBombPlanted;
	}
	
	/**
	 * @return the isBombPlanted
	 */
	public boolean isBombPlanted() {
		return isBombPlanted;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#killIfOutdated(long)
	 */
	@Override
	public boolean killIfOutdated(long gameClock) {	
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#isBackgroundObject()
	 */
	@Override
	public boolean isBackgroundObject() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		float x = (pos.x - cameraPos.x);
		float y = (pos.y - cameraPos.y);
		sprite.setPosition(x, y);
		
//		canvas.drawImage(Art.computerImage, x, y, null);
		canvas.drawSprite(sprite);
		
	}

}
