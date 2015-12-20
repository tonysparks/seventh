/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.AnimationPool;
import seventh.client.gfx.Art;
import seventh.client.gfx.AnimationPool.AnimationFactory;
import seventh.shared.SeventhConstants;

/**
 * A collection of pools
 * @author Tony
 *
 */
public class Pools {

	private AnimationPool alliedBackDeath;
	private AnimationPool alliedFrontDeath;
	
	private AnimationPool axisBackDeath;
	private AnimationPool axisFrontDeath;
	
	private AnimationPool explosion;
	private AnimationPool missle;
		
	private ClientBulletPool bulletPool;
	/**
	 * 
	 */
	public Pools(ClientGame game) {
		this.bulletPool = new ClientBulletPool(game, SeventhConstants.MAX_ENTITIES);
		
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
	 * Cleanup used resources
	 */
	public void destroy() {
		bulletPool.clear();
	}
	
	/**
	 * @return the bulletPool
	 */
	public ClientBulletPool getBulletPool() {
		return bulletPool;
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
