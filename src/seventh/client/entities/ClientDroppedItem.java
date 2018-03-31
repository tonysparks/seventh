/*
 * see license.txt 
 */
package seventh.client.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.ClientGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.game.entities.Entity.Type;
import seventh.game.net.NetDroppedItem;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientDroppedItem extends ClientEntity {

    private Sprite weapon;
    private Type droppedItem;
        
    private float spin;
    /**
     * @param pos
     */
    public ClientDroppedItem(ClientGame game, Vector2f pos) {
        super(game, pos);        
        
        this.bounds.width = 24;
        this.bounds.height = 24;
        
        spin = (float)Math.toRadians((pos.x + pos.y) % 360);
        Vector2f.Vector2fRound(pos, pos);
    }

    
    @Override
    public void updateState(NetEntity state, long time) {    
        super.updateState(state, time);
        
        super.orientation = spin;
        
        NetDroppedItem item = (NetDroppedItem)state;        
        if(this.droppedItem == null) {
            setDroppedItem(item.droppedItem);            
        }        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#isBackgroundObject()
     */
    @Override
    public boolean isBackgroundObject() {
        return true;
    }
    
    /**
     * @param droppedItem the droppedItem to set
     */
    public void setDroppedItem(Type item) {
        this.droppedItem = item;
        switch(item) {
            case KAR98:
                weapon = new Sprite(Art.kar98Icon);
                break;
            case M1_GARAND:
                weapon = new Sprite(Art.m1GarandIcon);
                break;
            case MP40:
                weapon = new Sprite(Art.mp40Icon);
                break;
            case MP44:
                weapon = new Sprite(Art.mp44Icon);
                break;
            case ROCKET_LAUNCHER:
                weapon = new Sprite(Art.rocketIcon);
                break;
            case SHOTGUN:
                weapon = new Sprite(Art.shotgunIcon);
                break;
            case SPRINGFIELD:
                weapon = new Sprite(Art.springfieldIcon);
                break;
            case THOMPSON:
                weapon = new Sprite(Art.thompsonIcon);
                break;            
            case PISTOL:
                weapon = new Sprite(Art.pistolIcon);
                break;
            case RISKER:
                weapon = new Sprite(Art.riskerIcon);
                break;
            case FLAME_THROWER:
                weapon = new Sprite(Art.flameThrowerIcon);
                break;
            case HAMMER:
                weapon = new Sprite(Art.hammerIcon);
                break;
            default: {            
            }
        }
        if(weapon!=null) {            
            weapon.scale(-0.65f);
            weapon.rotate( (float)Math.toDegrees(this.orientation));
        }
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {

        if(weapon!=null) {                        
            Vector2f cameraPos = camera.getRenderPosition(alpha);
            float x = (pos.x - cameraPos.x);
            float y = (pos.y - cameraPos.y);
            weapon.setPosition(x-54f, y-24f);                        
    
            canvas.fillCircle(10f, (int)x, (int)y, 0xafafafff);//0x3f4a4f8f);
            canvas.drawSprite(weapon);
        }
    }

}
