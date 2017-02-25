/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.math.Vector2f;
import seventh.shared.Timer;

/**
 * Contains particle data
 * 
 * @author Tony
 *
 */
public class ParticleData {

    public final int maxParticles;
        
    public int numberOfAliveParticles;
    public Emitter emitter;
    public Vector2f[] pos;
    public Vector2f[] vel;
    public boolean[] isAlive;
    public Timer[] timeToLive;
    public float[] scale;
    public float[] rotation;
    public float[] speed;
    public Color[] color;
    public Sprite[] sprite;
    
    public ParticleData(int maxParticles) {
        this.maxParticles = maxParticles;
        this.numberOfAliveParticles = 0;
        
        this.pos = new Vector2f[maxParticles];        
        this.vel = new Vector2f[maxParticles];
        this.isAlive = new boolean[maxParticles];
        this.timeToLive = new Timer[maxParticles];
        this.scale = new float[maxParticles];
        this.rotation = new float[maxParticles];
        this.speed = new float[maxParticles];
        this.color = new Color[maxParticles];
        this.sprite = new Sprite[maxParticles];
        
        for(int i = 0; i < maxParticles; i++) {
            this.pos[i] = new Vector2f();
            this.vel[i] = new Vector2f();            
            this.timeToLive[i] = new Timer(false, 0);
            this.color[i] = new Color(1,1,1,1);
            this.sprite[i] = new Sprite();
            
            this.scale[i] = 1.0f;
        }
        
    }
    
    public int spawnParticle() {
        int index = -1;
        if(this.numberOfAliveParticles < this.maxParticles) {
            index = this.numberOfAliveParticles;
            this.numberOfAliveParticles++;
        }
        
        return index;
    }

    public void reset() {
        this.numberOfAliveParticles = 0;
        for(int i = 0; i < maxParticles; i++) {
            this.pos[i].zeroOut();
            this.vel[i].zeroOut();            
            this.timeToLive[i].reset();
            this.color[i].set(1, 1, 1, 1);
            this.isAlive[i] = false;
            this.scale[i] = 1.0f;
            this.rotation[i] = 0f;
        }
    }    
    
    public void kill(int index) {
        if(index > -1 && index < this.numberOfAliveParticles) {
            int endIndex = this.numberOfAliveParticles-1;
            this.pos[index].set(this.pos[endIndex]);
            this.vel[index].set(this.vel[endIndex]);
            Timer tmp = this.timeToLive[index]; 
            this.timeToLive[index] = this.timeToLive[endIndex];
            this.timeToLive[endIndex] = tmp;
            this.scale[index] = this.scale[endIndex];
            this.rotation[index] = this.rotation[endIndex];
            this.color[index].set(this.color[endIndex]);
            this.isAlive[endIndex] = false;
            this.numberOfAliveParticles--;
        }
    }
}
