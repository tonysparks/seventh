/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.LinkedList;
import java.util.Queue;

import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class EmitterPool {

    public static interface EmitterFactory {
        public Emitter newEmitter();
    }
    
    private Queue<Emitter> pool;
    private EmitterFactory factory;
    /**
     * 
     */
    public EmitterPool(EmitterFactory factory) {
        this.factory = factory;
        this.pool = new LinkedList<>();
    }
    
    public Emitter allocate(Vector2f pos) {
        Emitter emitter = this.pool.poll();
        
        if(emitter == null) {
            emitter = this.factory.newEmitter();
        }
        
        emitter.reset();
        emitter.setPos(pos);
        
        return emitter;
    }
    
    public void free(Emitter emitter) {
        this.pool.add(emitter);
    }

}
