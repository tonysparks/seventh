/*
 * see license.txt 
 */
package seventh.game.entities.vehicles;

import seventh.game.Game;
import seventh.math.Vector2f;

/**
 * A German Panzer Tank
 * 
 * @author Tony
 *
 */
public class PanzerTank extends Tank {

    /**
     * @param position
     * @param game
     */
    public PanzerTank(Vector2f position, Game game, long timeToKill) {
        super(Type.PANZER_TANK, position, game, timeToKill);
    }

}
