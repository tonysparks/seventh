/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.net.NetMap;
import seventh.map.Map;

/**
 * @author Tony
 *
 */
public class GameMap {

	private NetMap netMap;
	private Map map;
	
	/**
	 * @param path
	 * @param name
	 * @param map
	 */
	public GameMap(String path, String name, Map map) {
		this.map = map;
		
		this.netMap = new NetMap();
		this.netMap.id = 0;
		this.netMap.path = path;
		this.netMap.name = name;
	}
	
	/**
	 * @return the file system name of the map
	 */
	public String getMapFileName() {
		return this.netMap.path;
	}

	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}
	
	/**
	 * @return the netMap
	 */
	public NetMap getNetMap() {
		return netMap;
	}
}
