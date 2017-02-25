/*
 * see license.txt 
 */
package seventh.game;

import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.Updatable;

/**
 * Limits the frequency of a sound.  Use this if you want to play a sound only within an 
 * alloted amount of time (i.e., it won't play if the alloted amount of time hasn't elapsed).
 * 
 * @author Tony
 *
 */
public class SoundEmitter implements Updatable {

    private Timer timer;
    private boolean hasStarted;;
    
    public SoundEmitter(long start) {
        this(start, false);
    }
    
    public SoundEmitter(long start, boolean loop) {
        this.timer = new Timer(loop, start);
        this.hasStarted = false;
    }
    
    public void reset() {
        this.timer.reset();
        this.hasStarted = false;
    }
    
    public void stop() {
        this.timer.stop();
        this.hasStarted = false;
    }
    
    public void play(Game game, int id, SoundType sound, Vector2f pos) {
        if(this.timer.isTime()|| !this.hasStarted) {
            game.emitSound(id, sound, pos);
            this.hasStarted = true;
        }
    }
    
    @Override
    public void update(TimeStep timeStep) {
        this.timer.update(timeStep);
    }

}
