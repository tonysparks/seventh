/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.client.gfx.AnimationPool.AnimationFactory;
import seventh.shared.SeventhConstants;

/**
 * A pool of animations.  This reduces the GC load
 * 
 * @author Tony
 *
 */
public class AnimationPools {

	private AnimationPool alliedBackDeath;
	private AnimationPool alliedFrontDeath;
	
	private AnimationPool axisBackDeath;
	private AnimationPool axisFrontDeath;
	
	private AnimationPool explosion;
	private AnimationPool missle;
		
	/**
	 * 
	 */
	public AnimationPools() {
		this.alliedBackDeath = new AnimationPool("AlliedBackDeath", SeventhConstants.MAX_PLAYERS*2, new AnimationFactory() {
			
			@Override
			public AnimatedImage newAnimation() {
				return Art.newAlliedBackDeathAnim();
			}
		});
		this.alliedFrontDeath = new AnimationPool("AlliedFrontDeath", SeventhConstants.MAX_PLAYERS*2, new AnimationFactory() {
			
			@Override
			public AnimatedImage newAnimation() {
				return Art.newAlliedFrontDeathAnim();
			}
		});
		
		this.axisBackDeath = new AnimationPool("AxisBackDeath", SeventhConstants.MAX_PLAYERS*2, new AnimationFactory() {
			
			@Override
			public AnimatedImage newAnimation() {
				return Art.newAxisBackDeathAnim();
			}
		});
		this.axisFrontDeath = new AnimationPool("AxisFrontDeath", SeventhConstants.MAX_PLAYERS*2, new AnimationFactory() {
			
			@Override
			public AnimatedImage newAnimation() {
				return Art.newAxisFrontDeathAnim();
			}
		});
		
		this.explosion = new AnimationPool("Explosion", SeventhConstants.MAX_PLAYERS*32, new AnimationFactory() {
			
			@Override
			public AnimatedImage newAnimation() {
				return Art.newExplosionAnim();
			}
		});
		
		this.missle = new AnimationPool("Missle", SeventhConstants.MAX_PLAYERS*32, new AnimationFactory() {
			
			@Override
			public AnimatedImage newAnimation() {
				return Art.newMissileAnim();
			}
		});
	}

	/**
	 * @return the alliedBackDeath
	 */
	public AnimationPool getAlliedBackDeath() {
		return alliedBackDeath;
	}

	/**
	 * @return the alliedFrontDeath
	 */
	public AnimationPool getAlliedFrontDeath() {
		return alliedFrontDeath;
	}

	/**
	 * @return the axisBackDeath
	 */
	public AnimationPool getAxisBackDeath() {
		return axisBackDeath;
	}

	/**
	 * @return the axisFrontDeath
	 */
	public AnimationPool getAxisFrontDeath() {
		return axisFrontDeath;
	}

	/**
	 * @return the explosion
	 */
	public AnimationPool getExplosion() {
		return explosion;
	}

	/**
	 * @return the missle
	 */
	public AnimationPool getMissle() {
		return missle;
	}
	
	
}
