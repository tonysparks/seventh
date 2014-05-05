/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.client.gfx.Art;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class GibEmitter extends Emitter {

	/**
	 * @param pos
	 */
	public GibEmitter(Vector2f pos) {
		super(pos, 4000, 100);
		this.maxParticles = 2;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {
		Random r = getRandom();
		Vector2f pos = getPos().createClone();
		if(r.nextBoolean()) {
			pos.x += r.nextInt(15);
		}
		else {
			pos.x -= r.nextInt(15);
		}
		
		if(r.nextBoolean()) {
			pos.y += r.nextInt(15);
		}
		else {
			pos.y -= r.nextInt(15);
		}
		Vector2f vel = new Vector2f(1,0);
		Vector2f.Vector2fRotate(vel, Math.toRadians(r.nextInt(360)), vel);
		BloodParticle p = new BloodParticle(pos, vel, r.nextInt(360), 5000);
		p.setSpeed(40);
		p.setImage(Art.randomGib());
		
		return p;
	}

}
