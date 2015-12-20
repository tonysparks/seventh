/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class FadeValue {

	private int currentValue, startValue, endValue, delta;
	/**
	 * 
	 */
	public FadeValue(int start, int end, int time) {		
		this.currentValue = start;
		this.startValue = start;
		this.endValue = end;
		
		int numberOfTicks = time / 33;
		if(numberOfTicks==0) numberOfTicks=1;
		if(time > 0) {
			if(end > start) {
				delta = ((end - start) / numberOfTicks) + 1;
			}
			else {
				delta = - (((start - end) / numberOfTicks) + 1);
			}
		}
	}
	
	public void reset() {
		this.currentValue = this.startValue;
	}
	
	public void update(TimeStep timeStep) {		
		this.currentValue += delta;//(delta * timeStep.asFraction());
		
		if(delta> 0) {
			if(this.currentValue>this.endValue) {
				this.currentValue = this.endValue;
			}
		}
		else {
			if(this.currentValue<this.endValue) {
				this.currentValue = this.endValue;
			}
		}
	}
	
	public boolean isDone() {
		return this.currentValue == this.endValue;
	}
	
	/**
	 * @return the endValue
	 */
	public int getEndValue() {
		return endValue;
	}
	
	/**
	 * @return the currentValue
	 */
	public int getCurrentValue() {
		return currentValue;
	}

}
