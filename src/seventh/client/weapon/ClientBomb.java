/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.ClientEntity;
import seventh.client.ClientGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.sfx.Sounds;
import seventh.game.net.NetBomb;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientBomb extends ClientEntity {

	private long timeRemaining;
	private int tickMarker;
	/**
	 * @param pos
	 */
	public ClientBomb(ClientGame game, Vector2f pos) {
		super(game, pos);
		this.tickMarker = 10;				
	}

	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#isBackgroundObject()
	 */
	@Override
	public boolean isBackgroundObject() {
		return true;
	}
		
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#updateState(seventh.game.net.NetEntity, long)
	 */
	@Override
	public void updateState(NetEntity state, long time) {	
		super.updateState(state, time);
		
		NetBomb bombState = (NetBomb)state;
		this.timeRemaining = bombState.timeRemaining;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		
		// if less than 10 seconds, begin
		// to beep
		if(this.timeRemaining < 10_000) {
			long trSec = timeRemaining/1_000;
			if(trSec <= tickMarker) {
				Sounds.startPlaySound(Sounds.bombTick, Sounds.uiChannel, getPos());
				tickMarker--;
			}			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Vector2f cameraPos = camera.getPosition();
		int x = (int)(pos.x - cameraPos.x);
		int y = (int)(pos.y - cameraPos.y);
		canvas.drawImage(Art.bombImage, x, y, null);
	}

}
