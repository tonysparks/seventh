/*
 *	leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.TextureRegion;



/**
 * @author Tony
 *
 */
public class AnimatedImage {

	/**
	 * images
	 */
	private TextureRegion [] images;

	/**
	 * The animation
	 */
	private Animation animation;

	/**
	 * Constructs a new {@link AnimatedImage}.
	 *
	 * @param image
	 * @param animation
	 */
	public AnimatedImage(TextureRegion [] images, Animation animation) {
		if ( images == null ) {
			throw new NullPointerException("Images can not be null!");
		}

		this.images = images;
		this.animation = animation;
	}

	/**
	 * @param loop
	 * @return this instance for method chaining
	 */
	public AnimatedImage loop(boolean loop) {
		this.animation.loop(loop);
		return this;
	}
	
	public boolean isDone() {
		return this.animation.isDone();
	}
	
	/**
	 * Update the animation.
	 *
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		this.animation.update(timeStep);
	}

	/**
	 * Return the current frame.
	 *
	 * @return
	 */
	public TextureRegion getCurrentImage() {
		return this.images[ this.animation.getCurrentFrame() ];
	}

	/**
	 * Get the Images that make up this animation.
	 * @return
	 */
	public TextureRegion[] getImages() {
		return this.images;
	}
	
	/**
	 * @param i
	 * @return the frame
	 */
	public TextureRegion getFrame(int i) {
		return this.images[i];
	}

	/**
	 * Get the animation.
	 *
	 * @return
	 */
	public Animation getAnimation() {
		return this.animation;
	}
	
	/**
	 * @return this instance for method chaining
	 */
	public AnimatedImage reset() {
		this.animation.reset();
		return this;
	}
	
	/**
	 * Set to the last frame
	 * @return this instance for method chaining
	 */
	public AnimatedImage moveToLastFrame() {
		this.animation.setCurrentFrame(this.animation.getNumberOfFrames()-1);
		return this;
	}
}
