/*
 * see license.txt 
 */
package seventh.client;

/**
 * @author Tony
 *
 */
public class ClientEntities {

	private ClientEntity[] entities;
	
	/**
	 * @param maxEntities
	 */
	public ClientEntities(int maxEntities) {
		this.entities = new ClientEntity[maxEntities];
	}
	
	/**
	 * @return maximum number of entities
	 */
	public int getMaxNumberOfEntities() {
	    return this.entities.length;
	}
	
	/**
	 * @return the entities
	 */
	public ClientEntity[] getEntities() {
		return entities;
	}
	
	/**
	 * @param id
	 * @return the {@link ClientEntity} at the supplied position or null if not available
	 */
	public ClientEntity getEntity(int id) {
		if(id > -1 && id < this.entities.length) {
			return this.entities[id];
		}
		return null;
	}
	
	/**
	 * @param id
	 * @return true if the entity exists in this list
	 */
	public boolean containsEntity(int id) {
		if(id > -1 && id < this.entities.length) {
			return this.entities[id] != null;
		}
		return false; 
	}
	
	/**
	 * Adds the entity
	 * @param e
	 */
	public void addEntity(int id, ClientEntity e) {
		this.entities[id] = e;
		e.id = id;
	}
	
	/**
	 * Removes {@link ClientEntity}
	 * @param id
	 */
	public ClientEntity removeEntity(int id) {	
		ClientEntity result = null;
		if(id > -1 && id < this.entities.length) {
			result = this.entities[id];
			this.entities[id] = null;
		}
		return result;
	}
	
	/**
	 * Removes a {@link ClientEntity}
	 * @param e
	 */
	public ClientEntity removeEntity(ClientEntity e) {
		return removeEntity(e.getId());
	}
	
	/**
	 * Clears out the entities
	 */
	public void clear() {
		for(int i = 0; i < this.entities.length; i++) {
			if(this.entities[i] != null) {
				this.entities[i].destroy();
			}
			
			this.entities[i] = null;
		}
	}
	
}
