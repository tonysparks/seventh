/*
 * see license.txt 
 */
package seventh.shared;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for finding all maps in the maps directory
 * 
 * @author Tony
 *
 */
public class MapList {

	/**
	 * Look the the maps directory to see what maps are available
	 * 
	 * @return the map listings
	 */
	public static List<String> getMapListing() {
		final String path = "./seventh/maps/";
		
		File dir = new File(path);
		File[] maps = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});
		
		List<String> mapNames = new ArrayList<String>(maps.length);
		for(File f : maps) {
			mapNames.add(path + f.getName().replace(".json", ""));
		}
		
		return mapNames;
	}
	
	/**
	 * Adds the map file extension is not present on the supplied name
	 * @param mapName the maps name
	 * @return the maps name with the file extension added if not present
	 * on the input
	 */
	public static String addFileExtension(String mapName) {
		if(!mapName.toLowerCase().endsWith(".json")) {
			mapName += ".json";
		}
		
		return mapName;
	}
}
