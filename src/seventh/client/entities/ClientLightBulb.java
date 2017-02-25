/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Light;
import seventh.game.net.NetEntity;
import seventh.game.net.NetLight;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientLightBulb extends ClientEntity {

    private Light light;
    
    /**
     * @param game
     * @param pos
     */
    public ClientLightBulb(ClientGame game, Vector2f pos) {
        super(game, pos);
                
        this.light = game.getLightSystem().newPointLight();
        this.light.setPos(pos);
        
        this.bounds.width = 5;
        this.bounds.height = 5;
        
        setOnRemove(new OnRemove() {
            
            @Override
            public void onRemove(ClientEntity me, ClientGame game) {
                game.getLightSystem().removeLight(light);
            }
        });
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#updateState(seventh.game.net.NetEntity, long)
     */
    @Override
    public void updateState(NetEntity state, long time) {    
        super.updateState(state, time);
        
        NetLight netLight = (NetLight)state;
        light.setColor( (float)(netLight.r/255.0f), (float)(netLight.g/255.0f), (float)(netLight.b/255.0f));        
        light.setLuminacity((float)(netLight.luminacity/255.0f));
        light.setLightSize(netLight.size);        
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
    public void render(Canvas canvas, Camera camera, float alpha) {
    }

}
