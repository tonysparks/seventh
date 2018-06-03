/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.Effect;
import seventh.client.gfx.effects.particle_system.Emitters;
import seventh.client.sfx.Sound;
import seventh.client.sfx.Sounds;
import seventh.game.net.NetEntity;
import seventh.game.net.NetFire;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientFire extends ClientEntity {
    
    private boolean soundPlayed;    
    private long ownerId;    
    
    private Sound sound;
    /**
     * 
     */
    public ClientFire(ClientGame game, Vector2f pos) {
        super(game, pos);
        
        this.soundPlayed = false;
        
        bounds.width = 16;
        bounds.height = 16;
      //  game.addForegroundEffect(Emitters.newSmokeEmitter(getCenterPos(), 25_000, false).attachTo(this));
        game.addForegroundEffect(Emitters.newFireEmitter(getCenterPos()).attachTo(this));        
        setOnRemove(new OnRemove() {
            
            @Override
            public void onRemove(ClientEntity me, ClientGame game) {
                                
                // fade the fire sound off
                if(sound != null) {
                    game.addForegroundEffect(new Effect() {   
                        float volume = sound.getVolume();
                        
                        @Override
                        public void destroy() {
                            sound.stop();  
                        }
                        
                        @Override
                        public boolean isDone() {                    
                            return volume <= 0.01f;
                        }
                        @Override
                        public void update(TimeStep timeStep) {
                            volume *= 0.875f;
                            sound.setVolume(volume);
                        }
                        @Override
                        public void render(Canvas canvas, Camera camera, float alpha) {                        
                        }
                    });
                }
            }
        });
                
    }
        
    @Override
    public void updateState(NetEntity state, long time) {    
        super.updateState(state, time);
        
        NetFire fire = (NetFire)state;
        ownerId = fire.ownerId;        
    }
        
    @Override
    public void update(TimeStep timeStep) {    
        super.update(timeStep);
                
        if(!soundPlayed) {
            sound = Sounds.playSound(Sounds.fire, ownerId, pos, true);
            this.soundPlayed = true;
        }
    }

    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {                
    }

}

