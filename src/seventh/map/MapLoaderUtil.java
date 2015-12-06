/*
 * see license.txt 
 */
package seventh.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
		String contents = loadFileContents(file);
		
		LeoMap mapData = JSON.parseJson(runtime, contents).as();
		MapLoader mapLoader = new TiledMapLoader();
		Map map = mapLoader.loadMap(mapData, loadAssets);
		return map;
	}
	
	private static String loadFileContents(File file) throws IOException {		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			StringBuilder sb = new StringBuilder((int)raf.length());
			String line = null;
			do {
				line = raf.readLine();
				if ( line != null) {
					sb.append(line).append("\n");
				}
				
			} while(line != null);
			
			return sb.toString();
		}
		finally {
			raf.close();
		}
		
	}
	
}
