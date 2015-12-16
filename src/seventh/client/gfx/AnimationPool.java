/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.shared.Cons;

/**
 * A pool of animations.  This reduces the GC load
 * 
 * @author Tony
 *
 */
public class AnimationPool {
	
	public static interface AnimationFactory {
		AnimatedImage newAnimation();
	}
	
	private AnimatedImage[] pool;
	private AnimationFactory factory;
	private String name;
	
	/**
	 * @param name
	 * @param size
	 * @param factory
	 */
	public AnimationPool(String name, int size, AnimationFactory factory) {
		this.name = name;
		this.factory = factory;
		this.pool = newPool(size, factory);
	}
	
	
	private AnimatedImage[] newPool(int size, AnimationFactory factory) {
		AnimatedImage[] frames = new AnimatedImage[size];
		for(int i = 0; i < size; i++) {
			frames[i] = factory.newAnimation();
		}
		
		return frames;
	}
	
	public AnimatedImage create() {
		for(int i = 0; i < pool.length; i++) {
			if(pool[i] != null) {
				pool[i].reset();
				return pool[i];
			}
		}		
		
		Cons.println("*** WARNING: The animation pool '" + name + "' has been exhausted!");
		return factory.newAnimation();
	}
	
	
	public void free(AnimatedImage image) {
		for(int i = 0; i < pool.length; i++) {
			if(pool[i] == null) {
				pool[i] = image;
			}
		}
	}

}
