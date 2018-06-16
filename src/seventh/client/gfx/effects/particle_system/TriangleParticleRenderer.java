/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import com.badlogic.gdx.graphics.Color;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleRenderer;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class TriangleParticleRenderer implements ParticleRenderer {

    private float base;
    private Vector2f a, b, c;

    public TriangleParticleRenderer() {
        this(1.0f);
    }
    
    /**
     * @param base
     */
    public TriangleParticleRenderer(float base) {
        this.base = base;
        this.a = new Vector2f();
        this.b = new Vector2f();
        this.c = new Vector2f();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle_system.Emitter.ParticleRenderer#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
     */
    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle_system.Emitter.ParticleRenderer#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float, seventh.client.gfx.particle_system.ParticleData)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha, ParticleData particles) {
        
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        for(int i = 0; i < particles.numberOfAliveParticles; i++) {
            
            Vector2f pos = particles.pos[i];
            float x = pos.x - cameraPos.x, y = pos.y - cameraPos.y;
            Color color = particles.color[i];
            
            float hypot = particles.scale[i];
            float base2 = (float)Math.sqrt((hypot*hypot) - (this.base*this.base));
            this.a.set(x, y);
            this.b.set(x + this.base, y);
            this.c.set(x, y + base2);
            
            float rot = (float)Math.toRadians(particles.rotation[i]);
            Vector2f delta = new Vector2f();       
            
            Vector2f.Vector2fSubtract(b, a, delta);            
            Vector2f.Vector2fRotate(delta, rot, delta);
            Vector2f.Vector2fAdd(delta, a, b);
            
            Vector2f.Vector2fSubtract(c, a, delta);            
            Vector2f.Vector2fRotate(delta, rot, delta);
            Vector2f.Vector2fAdd(delta, a, c);
                        
            canvas.fillTriangle(a.x, a.y, b.x, b.y, c.x, c.y, Color.argb8888(color));            
        }                
    }

}
