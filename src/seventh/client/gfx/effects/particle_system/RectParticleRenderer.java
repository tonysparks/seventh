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
public class RectParticleRenderer implements ParticleRenderer {

    private final int width, height;
    
    /**
     * a default width/height of 1
     */
    public RectParticleRenderer() {
        this(1,1);
    }
    
    public RectParticleRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
    }

    @Override
    public void render(Canvas canvas, Camera camera, float alpha, ParticleData particles) {
        
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        for(int i = 0; i < particles.numberOfAliveParticles; i++) {
            
            Vector2f pos = particles.pos[i];
            float x = pos.x - cameraPos.x, y = pos.y - cameraPos.y;
            Color color = particles.color[i];
            
            canvas.fillRect(x, y, width, height, Color.argb8888(color));            
        }                
    }

}
