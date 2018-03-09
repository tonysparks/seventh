/*
 * see license.txt 
 */
package seventh.map;

import java.io.File;
import java.nio.file.Files;

import leola.vm.Leola;
import leola.vm.types.LeoMap;
import seventh.shared.JSON;

/**
 * Map loading routine
 * 
 * @author Tony
 *
 */
public class MapLoaderUtil {

    /**
     * Loads a {@link Map}
     * 
     * @param runtime
     * @param mapFile the map file
     * @param loadAssets whether or not to load the Assets along with the map
     * @return the {@link Map}
     * @throws Exception
     */
    public static Map loadMap(Leola runtime, String mapFile, boolean loadAssets) throws Exception {
        File file = new File(mapFile);        
        String contents = new String(Files.readAllBytes(file.toPath()));
        
        MapObjectFactory factory = new DefaultMapObjectFactory(runtime, mapFile, loadAssets);
        
        LeoMap mapData = JSON.parseJson(runtime, contents).as();
        MapLoader mapLoader = new TiledMapLoader();
        Map map = mapLoader.loadMap(mapData, factory, loadAssets);
        return map;
    }
}
