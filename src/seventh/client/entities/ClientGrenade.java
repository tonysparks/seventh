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
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientGrenade extends ClientEntity {

    private float timeAlive;
    private float orientation;
    private int spinDirection = 0;
    private Sprite sprite;
    private Type grenadeType;
    /**
     * 
     */
    public ClientGrenade(ClientGame game, Vector2f pos, Type grenadeType) {
        super(game, pos);    
        this.grenadeType = grenadeType;
        
        sprite = new Sprite(grenadeType==Type.SMOKE_GRENADE ? Art.smokeGrenadeImage : Art.fragGrenadeImage);
        sprite.setSize(16, 16);
        sprite.setOrigin(8, 8);
        sprite.flip(false, true);            
    }
    
    /**
     * @return the grenadeType
     */
    public Type getGrenadeType() {
        return grenadeType;
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
        
        timeAlive += 0.05f; 
        
        if(spinDirection==0) {
            if(timeStep.getGameClock() % 2 == 0) {
                spinDirection = -1;
            }
            else {
                spinDirection = 1;
            }
        }
    }

    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        float x = (pos.x - cameraPos.x);
        float y = (pos.y - cameraPos.y);
        
        float scale = 1.6f - timeAlive;
        
        int rate = 8;
        if(scale < 1.4f) {
            rate = 5;
        }
        
        if(scale < 1.1f) {
            rate = 2;
        }
        
        if(scale < 1.0f) {
            scale = 1.0f;
            rate = 2;
        }
        
        
        if(prevState == null || scale > 1) {
            this.orientation += rate * spinDirection;    
        }
                
        sprite.setPosition(x, y);
        sprite.setScale(scale);
        sprite.setRotation( this.orientation );
        
        canvas.drawSprite(sprite);
    }

}
