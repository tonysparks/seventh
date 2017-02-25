/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.math.Vector2f;

/**
 * Just stands there, so I can shoot them for testing purposes.
 * 
 * @author Tony
 *
 */
public class DummyBotPlayerEntity extends PlayerEntity {

    /**
     * @param id
     * @param position
     * @param game
     */
    public DummyBotPlayerEntity(int id, Vector2f position, Game game) {
        super(id, position, game);        
    }
}
