/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.math.Vector2f;

/**
 * Cache of {@link ClientBullet}s.
 * 
 * @author Tony
 *
 */
public class ClientBulletPool {

	private ClientBullet[] bullets;
	
	/**
	 * @param maxEntities
	 */
	public ClientBulletPool(ClientGame game, int maxEntities) {
		this.bullets = new ClientBullet[maxEntities];
		for(int i = 0; i < maxEntities; i++) {
			this.bullets[i] = new ClientBullet(game, new Vector2f());
			this.bullets[i].setId(i);
		}
	}
		
	/**
	 * @param id
	 * @return the {@link ClientBullet} at the supplied position or null if not available
	 */
	public ClientBullet alloc(int id, Vector2f pos) {
		if(id > -1 && id < this.bullets.length) {
			ClientBullet bullet = this.bullets[id];
			bullet.reset();
			bullet.setOrigin(pos);
			
			return bullet;
		}
		return null;
	}
	
	/**
	 * @param id
	 * @return true if the entity exists in this list
	 */
	public boolean isFree(int id) {
		if(id > -1 && id < this.bullets.length) {			
			return this.bullets[id] != null && this.bullets[id].isDestroyed();
		}
		return false; 
	}
	
	/**
	 * Adds the entity
	 * @param e
	 */
	public void free(ClientBullet e) {
		this.bullets[e.getId()] = e;
		e.destroy();
	}
	
	
	/**
	 * Clears out the entities
	 */
	public void clear() {
		for(int i = 0; i < this.bullets.length; i++) {
			if(this.bullets[i] != null) {
				this.bullets[i].destroy();
			}						
		}
	}
	
}
