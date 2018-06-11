/*
 * see license.txt 
 */
package seventh.client.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.ClientGame;
import seventh.client.ClientTeam;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Colors;
import seventh.game.net.NetEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientBase extends ClientEntity {   
    private Sprite sprite;
    private int color;
    private ClientTeam team;
    
    /**
     * @param game
     * @param pos
     */
    public ClientBase(ClientGame game, ClientTeam team, Vector2f pos) {
        super(game, pos);

        this.team = team;
        
        this.bounds.setSize(64, 64);
        this.bounds.setLocation(pos);        
        
        this.sprite = new Sprite(Art.doorImg);
        this.sprite.setOrigin(0, Art.doorImg.getRegionHeight()/2);
        
        this.color = team.getColor();
        this.color = Colors.setAlpha(color, 95);
    }
    
    /**
     * @return the team
     */
    public ClientTeam getTeam() {
        return team;
    }
    
    @Override
    public void updateState(NetEntity state, long time) {    
        super.updateState(state, time);                
    }
    
    
    @Override
    public boolean killIfOutdated(long gameClock) {    
        return false;
    }
    
    @Override
    public boolean touches(ClientEntity other) {
        return isTouching(other.getBounds());
    }
    
    public boolean isTouching(Rectangle bounds) {
        return this.bounds.intersects(bounds);
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {        
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        
        Vector2f pos = getPos();
        
        canvas.fillRect(pos.x - cameraPos.x, pos.y - cameraPos.y, this.bounds.width, this.bounds.height, color);
        
        //this.sprite.setRotation((float)Math.toDegrees(getOrientation()));
        //this.sprite.setPosition(pos.x - cameraPos.x, pos.y - cameraPos.y - 8f);
        //canvas.drawRawSprite(this.sprite);
    }

}
