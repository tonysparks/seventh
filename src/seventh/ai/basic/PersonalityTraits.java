/*
 * see license.txt 
 */
package seventh.ai.basic;

/**
 * Personality traits of a bot player
 * 
 * @author Tony
 *
 */
public class PersonalityTraits {

    /**
     * Max value to offset aiming
     */
    private static final double MAX_SLOP = (Math.PI/8);
    
    public double aggressiveness;
    public double obedience;
    public double accuracy;
    public double curiosity;
    
    public float calculateAccuracy(Brain brain) {
        double slop = (1.0-brain.getRandomRangeMax(accuracy)) * (MAX_SLOP);
        slop *= brain.getWorld().getRandom().nextBoolean() ? -1 : 1; 
        return (float)slop;
    }
}
