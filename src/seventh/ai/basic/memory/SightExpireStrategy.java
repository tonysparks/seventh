package seventh.ai.basic.memory;

import seventh.game.entities.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;

public class SightExpireStrategy implements ExpireStrategy {

    protected PlayerEntity entity;
    protected Weapon lastSeenWithWeapon;
    protected Vector2f lastSeenAt;
    protected boolean isValid;
    @Override
    public void expire() {
        this.entity = null;
        this.lastSeenWithWeapon = null; 
        this.lastSeenAt.zeroOut();                        
        this.isValid = false;		
        }
}