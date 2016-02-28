/*
 * see license.txt 
 */
package seventh.client;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.game.net.NetEntity;
import seventh.game.net.NetFlag;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientFlag extends ClientEntity {
		
	private Sprite flagImg;
	private ClientPlayer carrier;
	
	/**
	 * 
	 */
	public ClientFlag(ClientGame game, ClientTeam team, Vector2f pos) {
		super(game, pos);
		
		
		bounds.width = SeventhConstants.FLAG_WIDTH;
		bounds.height = SeventhConstants.FLAG_HEIGHT;
		bounds.centerAround(pos);
		
		switch(team) {
			case ALLIES: flagImg = new Sprite(Art.alliedFlagImg); break;
			case AXIS: flagImg = new Sprite(Art.axisFlagImg); break;
			default: throw new IllegalArgumentException();
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.client.ClientEntity#updateState(palisma.game.net.NetEntity, long)
	 */
	@Override
	public void updateState(NetEntity state, long time) {	
		super.updateState(state, time);		
		
		NetFlag flag = (NetFlag) state;
		carrier = game.getPlayers().getPlayer(flag.carriedBy);
	}
	

	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#killIfOutdated(long)
	 */
	@Override
	public boolean killIfOutdated(long gameClock) {
		return false;
	}
		
	/* (non-Javadoc)
	 * @see palisma.client.ClientEntity#update(leola.live.TimeStep)
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
		if(flagImg != null) {
			Vector2f cameraPos = camera.getRenderPosition(alpha);
			Vector2f flagPos = pos;
			if (carrier != null) {
				flagPos = carrier.getEntity().getRenderPos(alpha);
			}
			
			float x = (flagPos.x - cameraPos.x);
			float y = (flagPos.y - cameraPos.y);
			flagImg.setPosition(x, y);
			canvas.drawSprite(flagImg);			
		}
		
	}

}

