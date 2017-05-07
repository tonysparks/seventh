/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.particle_system.Emitters;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientSmoke extends ClientEntity {
        
    /**
     * 
     */
    public ClientSmoke(ClientGame game, Vector2f pos) {
        super(game, pos);
        
        bounds.width = 16;
        bounds.height = 16;
        game.addForegroundEffect(Emitters.newSmokeEmitter(pos, 18_000, 0x238261ff, 0x838B83ff, 40, .9f, 1.6f, true)
                                         .attachTo(this)
                                         .setDieInstantly(false));        
    }
       
    @Override
    public void update(TimeStep timeStep) {    
        super.update(timeStep);        
    }

    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {        
    }

}

