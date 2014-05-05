/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link Brain}s memory, store and retrieve information.
 * 
 * @author Tony
 *
 */
public class Memory {

	private Map<String, Object> memory;
	
	
	public Memory() {
		this.memory = new HashMap<String, Object>();
	}
	
	/**
	 * Clear the memory
	 */
	public void clear() {
		this.memory.clear();
	}
	
	/**
	 * Stores the memory object
	 * 
	 * @param key
	 * @param data
	 */
	public void store(String key, Object data) {
		this.memory.put(key, data);
	}
	
	/**
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	public <T> boolean has(String key, Class<T> type) {
		if(this.memory.containsKey(key)) {
			Object value = this.memory.get(key);
			return value != null && type.isAssignableFrom(value.getClass());
		}
		return false;
	}
	
	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getType(String key) {
		Object obj = this.memory.get(key);
		return (T)obj;
	}
	
	/**
	 * Gets the data from the memory bank.
	 * 
	 * @param type
	 * @param key
	 * @return the object, or null if not found 
	 */	
	public Object get(String key) {
		Object obj = this.memory.get(key);
		return obj;
	}
}
