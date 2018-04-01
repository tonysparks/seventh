/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.game.net.NetBase;
import seventh.game.net.NetEntity;
import seventh.game.weapons.Bullet;
import seventh.map.Map;
import seventh.math.Vector2f;
import seventh.shared.SoundType;

/**
 * Base in which the team must protect.
 * 
 * @author Tony
 *
 */
public class Base extends Entity {

    private NetBase netBase;
    
    /**
     * @param position
     * @param game
     * @param type
     */
    public Base(Game game, Vector2f position, Type type) {
        super(position, 0, game, type);
        
        this.netBase = new NetBase(getType());
        
        Map map = game.getMap();
        this.bounds.setSize(map.getTileWidth() * 2, map.getTileHeight() * 2);
        this.bounds.setLocation(position);
        
        int health = 100;
        
        this.setHealth(health);
        this.setMaxHealth(health);
    }
    
    @Override
    public void damage(Entity damager, int amount) {
        if(damager instanceof Bullet) {
            Bullet bullet = (Bullet) damager;
            bullet.kill(this);        
            game.emitSound(bullet.getId(), SoundType.IMPACT_METAL, bullet.getCenterPos());
        }
        
        super.damage(damager, amount);
    }

    @Override
    public NetEntity getNetEntity() {
        setNetEntity(netBase);
        return this.netBase;
    }

}
