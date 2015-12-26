/*
 *	leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import java.util.Arrays;
import java.util.Comparator;

import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class FramedAnimation extends Animation {

	/**
	 * Animation frames
	 */
	private AnimationFrame [] animationFrames;

	/**
	 * Current frame
	 */
	private AnimationFrame currentAnimationFrame;

	/**
	 * Current frame index
	 */
	private int currentFrame;

	/**
	 * Time elapsed
	 */
	private long elapsedTime;

	/**
	 * Number of frames
	 */
	private int numberOfFrames;

	/**
	 * Constructs a new {@link FramedAnimation}.
	 *
	 * @param frames
	 */
	public FramedAnimation(final AnimationFrame[] frames) {
		super();
		
		this.animationFrames = frames;
		this.numberOfFrames = frames.length;
		
		Arrays.sort(frames, new Comparator<AnimationFrame>()
		{
			public int compare(AnimationFrame o1, AnimationFrame o2) {
				return o1.getFrameNumber() - o2.getFrameNumber();
			}
		});
		
		setCurrentFrame(0);
		this.elapsedTime = 0L;
	}

	/**
	 * Update the animation.
	 *
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {

		/* Do not increment the animation if we are paused */
		if ( this.isPaused() ) {
			return;
		}

		/* Increment the elapsed time */
		this.elapsedTime += timeStep.getDeltaTime();

		/* If the frame time has expired, increment to the next frame */
		if ( this.elapsedTime > this.currentAnimationFrame.getFrameTime() ) {

			/* Reset the elapsed timer */
			this.elapsedTime -= this.currentAnimationFrame.getFrameTime();

			int desiredFrame = this.currentFrame + 1;

			if ( !this.isLooping() && desiredFrame >= this.numberOfFrames ) {
				desiredFrame = this.numberOfFrames - 1;
			}

			/* Advance the frame */
			setCurrentFrame(desiredFrame);

		}
	}
	
	/* (non-Javadoc)
	 * @see leola.live.animation.Animation#isDone()
	 */
	@Override
	public boolean isDone() {	
		return !isLooping() && this.currentFrame >= (this.numberOfFrames-1);
	}

	/**
	 * The current frame number.
	 *
	 * @return
	 */
	public int getCurrentFrame() {
		return this.currentFrame;
	}

	/**
	 * Set the current frame.
	 *
	 * @param frameNumber
	 */
	public void setCurrentFrame(int frameNumber) {
		this.currentFrame = frameNumber % this.numberOfFrames;
		this.currentAnimationFrame = this.animationFrames[ this.currentFrame ];
		
		if(! this.isLooping()) {
			this.isDone = this.currentFrame >= (this.numberOfFrames-1);
		}
	}

	/* (non-Javadoc)
	 * @see org.myriad.render.animation.Animation#getNumberOfFrames()
	 */
	@Override
	public int getNumberOfFrames() {
		return this.numberOfFrames;
	}

	/* (non-Javadoc)
	 * @see org.myriad.render.animation.Animation#reset()
	 */
	@Override
	public void reset() {
		setCurrentFrame(0);
		this.elapsedTime = 0L;
		this.isDone = false;
	}

}

