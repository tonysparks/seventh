/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.entities.Bomb;
import seventh.game.entities.BombTarget;

/**
 * @author Tony
 *
 */
public class BombPlantedEvent extends Event {

    private Bomb bomb;
    private BombTarget bombTarget;
    /**
     * @param source
     */
    public BombPlantedEvent(Object source, Bomb bomb) {
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
     * @return the {@link BombTarget} the {@link Bomb} is planted on
     */
    public BombTarget getBombTarget() {
        return bombTarget;
    }
}
