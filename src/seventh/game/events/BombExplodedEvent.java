/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.entities.Bomb;

/**
 * @author Tony
 *
 */
public class BombExplodedEvent extends Event {

    private Bomb bomb;
    
    /**
     * @param source
     */
    public BombExplodedEvent(Object source, Bomb bomb) {
        super(source);
        this.bomb = bomb;
    }

    /**
     * @return the bomb
     */
    public Bomb getBomb() {
        return bomb;
    }
}
