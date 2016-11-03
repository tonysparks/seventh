/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.client.gfx.Art;
import seventh.math.Vector2f;
import seventh.shared.Randomizer;

/**
 * @author Tony
 *
 */
public class GibEmitter extends Emitter {

	/**
	 * @param pos
	 */
	public GibEmitter(Vector2f pos, int numberOfGibs) {
		super(pos, 10_000, 0);
		this.maxParticles = numberOfGibs;
	}
	
	public GibEmitter(Vector2f pos) {
		this(pos, 15);
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {
		Random r = getRandom();
		Vector2f pos = getPos().createClone();
		final int maxSpread = 35;
		if(r.nextBoolean()) {
			pos.x += r.nextInt(maxSpread);
		}
		else {
			pos.x -= r.nextInt(maxSpread);
		}
		
		if(r.nextBoolean()) {
			pos.y += r.nextInt(maxSpread);
		}
		else {
			pos.y -= r.nextInt(maxSpread);
		}
		Vector2f vel = new Vector2f(1,0);
		Vector2f.Vector2fRotate(vel, Math.toRadians(r.nextInt(360)), vel);
		
		float scale = (float)Randomizer.getRandomRange(r, 0.15f, 1.2f);
		BloodParticle p = new BloodParticle(pos, vel, r.nextInt(360), scale, 10_000);
		p.setSpeed(0);
		p.setImage(Art.randomGib());
		
		return p;
	}

}
