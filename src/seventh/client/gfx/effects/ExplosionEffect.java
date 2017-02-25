/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Makes explosion (ripple effects) using post-processing shader.  This class is responsible for keeping track of the
 * data sent to the shader.  We limit the amount of explosions in a scene, we then pass this information in a float array
 * to the shader.
 * 
 * @author Tony
 *
 */
public class ExplosionEffect implements Updatable {

    private final float spreadRate;
    private final long timeToLive;
    
    
    private class ExplosionEffectData {
        long ttlCountDown;    
        Vector2f position;
        float time;
        
        public ExplosionEffectData() {
            this.position = new Vector2f();
        }
        
        /**
         * Activate this effect
         * @param pos
         */
        public void activate(Vector2f pos) {
            this.position.set(pos);
            this.time = 0f;
            this.ttlCountDown = timeToLive;
        }
        
        public void deactive() {
            this.ttlCountDown = 0;
            this.position.zeroOut();
        }
        
        /**
         * @return If this is active
         */
        public boolean isActive() {
            return this.ttlCountDown > 0;
        }
        
        public void update(TimeStep timeStep) {
            this.time += (Gdx.graphics.getDeltaTime() * spreadRate);
            this.ttlCountDown -= timeStep.getDeltaTime();
        }
        
        public void set(float[] data, int index) {
            data[index  ] = this.position.x;
            data[index+1] = this.position.y;
            data[index+2] = this.time;
        }
    }
    
    private ExplosionEffectData[] instances;
    private float[] shaderData;
    
    /**
     * @param maxInstances
     * @param ttl
     * @param spreadRate
     */
    public ExplosionEffect(int maxInstances, long ttl, float spreadRate) {
        this.timeToLive = ttl;
        this.spreadRate = spreadRate;
        
        this.instances = new ExplosionEffectData[maxInstances];
        for(int i = 0; i < maxInstances; i++) {
            this.instances[i] = new ExplosionEffectData();
        }
        
        this.shaderData = new float[3 * maxInstances];
    }


    /**
     * Activate this effect
     * @param pos
     */
    public void activate(int index, Vector2f pos) {
        if(index > -1 && index < this.instances.length) {
            if(!this.instances[index].isActive()) {
                this.instances[index].activate(pos);
            }
        }
    }
    
    /**
     * Deactivates all of the explosions
     */
    public void deactiveAll() {
        for(int i = 0; i < this.instances.length; i++) {
            this.instances[i].deactive();
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        
        /* Always clear out the data, this will get
         * overridden by the next loop if the explosion 
         * is active
         */
        for(int i = 0; i < this.instances.length; i++) {
            this.shaderData[i  ] = -0.1f;
            this.shaderData[i+1] = -0.1f;
            this.shaderData[i+2] = 0;
        }
        
        
        int activeCount = 0;
        for(int i = 0; i < this.instances.length; i++) {
            this.instances[i].update(timeStep);
            
            if(this.instances[i].isActive()) {
                this.instances[i].set(shaderData, activeCount++);
            }
        }
        
        ShaderProgram shader = ExplosionEffectShader.getInstance().getShader();
        shader.begin();
        shader.setUniform3fv("shockData", this.shaderData, 0, this.shaderData.length);
        shader.setUniformf("shockParams", 10.0f, 0.08f, 0.1f);        
        shader.end();

    }
}
