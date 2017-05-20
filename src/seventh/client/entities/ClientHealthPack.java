/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientHealthPack extends ClientEntity {

    /**
     * @param game
     * @param pos
     */
    public ClientHealthPack(ClientGame game, Vector2f pos) {
        super(game, pos);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.entities.ClientEntity#isBackgroundObject()
     */
    @Override
    public boolean isBackgroundObject() {
        return true;
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
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        float x = (pos.x - cameraPos.x);
        float y = (pos.y - cameraPos.y);
        canvas.drawImage(Art.healthPack, x, y, null);
    }

}
