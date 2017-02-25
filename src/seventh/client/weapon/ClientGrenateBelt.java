/*
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.entities.ClientPlayerEntity;

/**
 * @author Tony
 *
 */
public class ClientGrenateBelt extends ClientWeapon {

    /**
     * @param ownerId
     */
    public ClientGrenateBelt(ClientPlayerEntity owner) {
        super(owner);
    
        this.weaponIcon = null;        
        this.weaponImage = null;
    }

}
