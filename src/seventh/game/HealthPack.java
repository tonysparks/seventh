/*
 * see license.txt
 */
package seventh.game;

import seventh.game.net.NetEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Health pack which adds health to a Player's entity.
 * 
 * @author Tony
 *
 */
public class HealthPack extends Entity {

    /**
     * @param position
     * @param game
     */
    public HealthPack(Vector2f position, Game game) {
        super(position, 0, game, Type.HEALTH_PACK);
        init(game);
    }

    /**
     * @param id
     * @param position
     * @param game
     */
    public HealthPack(int id, Vector2f position, final Game game) {
        super(id, position, 0, game, Type.HEALTH_PACK);
        init(game);
    }

    private void init(final Game game) {
        this.onTouch = new OnTouchListener() {
            
            @Override
            public void onTouch(Entity me, Entity other) {
                if(other.isAlive() && other.getType().equals(Type.PLAYER)) {
                    other.setHealth(other.getMaxHealth());
                    game.emitSound(getId(), SoundType.HEALTH_PACK_PICKUP, getCenterPos());
                    softKill();
                }
            }
        };
    }
    /* (non-Javadoc)
     * @see seventh.game.Entity#getNetEntity()
     */
    @Override
    public NetEntity getNetEntity() {
        return null;
    }

    /* (non-Javadoc)
     * @see seventh.game.Entity#canTakeDamage()
     */
    @Override
    public boolean canTakeDamage() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#update(seventh.shared.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        return true;
    }
}
