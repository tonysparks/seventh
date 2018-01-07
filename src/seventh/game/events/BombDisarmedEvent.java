/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.entities.Bomb;
import seventh.game.entities.BombTarget;
import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class BombDisarmedEvent extends Event {

    private Bomb bomb;
    private BombTarget bombTarget;
    /**
     * @param source
     */
    public BombDisarmedEvent(Object source, Bomb bomb) {
        super(source);
        this.bomb = bomb;
        this.bombTarget = bomb.getBombTarget();
    }

    /**
     * @return the bomb
     */
    public Bomb getBomb() {
        return bomb;
    }
    
    /**
     * @return the bombTarget
     */
    public BombTarget getBombTarget() {
        return bombTarget;
    }
}
