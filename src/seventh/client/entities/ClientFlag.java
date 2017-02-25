/*
 * see license.txt 
 */
package seventh.client.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientTeam;
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
    private int fadeAlphaColor;
    
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
     * @see seventh.client.ClientEntity#isBackgroundObject()
     */
    @Override
    public boolean isBackgroundObject() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see palisma.client.ClientEntity#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {    
        super.update(timeStep);
        
        long clockTime = timeStep.getGameClock();
        
        if (carrier != null && carrier.isAlive()) {            
            long lastUpdate = carrier.getEntity().getLastUpdate(); 
            
            if ((lastUpdate+150) < clockTime) {
                fadeAlphaColor = 255 - ((int)(clockTime-lastUpdate)/3);
                if (fadeAlphaColor < 0) fadeAlphaColor = 0;                        
            }
            else {
                fadeAlphaColor = 255;
            }
        }
        else {
            fadeAlphaColor = 255;
        }
    }

    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {        
        if(flagImg != null) {
            Vector2f cameraPos = camera.getRenderPosition(alpha);
            Vector2f flagPos = pos;
            if (fadeAlphaColor>0 && carrier != null && carrier.isAlive()) {
                flagPos = carrier.getEntity().getRenderPos(alpha);
                //flagPos.x = flagPos.x + carrier.getEntity().bounds.width/2;
                //flagPos.y = flagPos.y + carrier.getEntity().bounds.height/2;
            }
            
            float x = (flagPos.x - cameraPos.x);
            float y = (flagPos.y - cameraPos.y);
            flagImg.setPosition(x, y);
            flagImg.setAlpha(fadeAlphaColor/255.0f);            
            canvas.drawRawSprite(flagImg);            
        }
        
    }

}

