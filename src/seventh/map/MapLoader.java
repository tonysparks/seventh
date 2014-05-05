/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import leola.vm.types.LeoMap;

/**
 * @author Tony
 *
 */
public interface MapLoader {

	public Map loadMap(LeoMap map, boolean loadAssets) throws Exception;
}
