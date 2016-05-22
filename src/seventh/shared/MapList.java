/*
 * see license.txt 
 */
package seventh.shared;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import seventh.game.type.GameType;
import seventh.game.type.GameType.Type;

/**
 * Utility class for finding all maps in the maps directory
 * 
 * @author Tony
 *
 */
public class MapList {

	private static File[] getMapFiles(String path) {
		File dir = new File(path);
		File[] maps = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {				
				return name.toLowerCase().endsWith(".json");
			}
		});
		
		return maps;
	}
	
	/**
	 * Look the the maps directory to see what maps are available for a 
	 * particular game type
	 * 
	 * @return the map listings
	 */
	public static List<String> getMapListing(GameType.Type gameType) {
		final String path = "./assets/maps/";
		File[] maps = getMapFiles(path);
		
		List<String> mapNames = new ArrayList<String>(maps.length);
		for(File f : maps) {
			File gameTypeFile = new File(path, f.getName() + "." + gameType.name().toLowerCase() + ".leola");
			if(gameType==GameType.Type.TDM || gameTypeFile.exists()) {
				mapNames.add(path + f.getName().replace(".json", ""));
			}
		}
		
		return mapNames;
	}
	
	/**
	 * Look the the maps directory to see what maps are available
	 * 
	 * @return the map listings
	 */
	public static List<String> getMapListing() {
		return getMapListing(Type.TDM);
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
