/*
 *	leola-live 
 *  see license.txt
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class AnimationFrame {

	/**
	 * Time to spend on this frame
	 */
	private long frameTime;

	/**
	 * Frame Number
	 */
	private int frameNumber;

	/**
	 * Constructs a new {@link AnimationFrame}.
	 *
	 * @param frameTime
	 * @param frameNumber
	 */
	public AnimationFrame(long frameTime, int frameNumber) {
		this.frameTime = frameTime;
		this.frameNumber = frameNumber;
	}

	/**
	 * @return the frameTime
	 */
	public long getFrameTime() {
		return frameTime;
	}

	/**
	 * @return the frameNumber
	 */
	public int getFrameNumber() {
		return frameNumber;
	}


}

