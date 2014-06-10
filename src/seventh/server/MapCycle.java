/*
 * see license.txt 
 */
package seventh.server;

import java.util.List;

import seventh.shared.Command;
import seventh.shared.Console;
import seventh.shared.MapList;

/**
 * The play list for maps.
 * 
 * @author Tony
 *
 */
public class MapCycle {

	private List<String> maps;
	private int currentMap;
	
	/**
	 * @param maps
	 */
	public MapCycle(List<String> maps) {
		this.maps = maps;
		this.currentMap = 0;
		
		if(this.maps.isEmpty()) {
			throw new IllegalArgumentException("There are no maps defined the the map list.");
		}
	}

	/**
	 * @return the Command that lists out the map cycle list
	 */
	public Command getMapListCommand() {
		return new Command("map_list") {
			
			@Override
			public void execute(Console console, String... args) {
				String currentMap = getCurrentMap();
				console.println("\n");
				console.println("Current map: " + currentMap);
				for(String map : maps) {
					if(currentMap.equals(map)) {
						console.println(map + "*");
					}
					else {
						console.println(map);
					}
				}
				console.println("\n");
			}
		};
	}
	
	/**
	 * @return the {@link Command} that adds to the map list
	 */
	public Command getMapAddCommand() {
		return new Command("map_add") {
			@Override
			public void execute(Console console, String... args) {
				addMap(this.mergeArgsDelim(" ", args));
			}
		};
	}
	
	/**
	 * @return the {@link Command} that removes from the map list
	 */
	public Command getMapRemoveCommand() {
		return new Command("map_remove") {			
			@Override
			public void execute(Console console, String... args) {
				removeMap(this.mergeArgsDelim(" ", args));
			}
		};
	}
	
	/**
	 * Adds a map to the rotation 
	 * @param map
	 */
	public void addMap(String map) {
		this.maps.add(map);
	}
	
	/**
	 * Removes a map from the rotation
	 * 
	 * @param map
	 */
	public void removeMap(String map) {
		this.maps.remove(map);
	}
	
	/**
	 * Sets the current map 
	 * @param map
	 */
	public void setCurrentMap(String map) {
		map = MapList.addFileExtension(map);
		
		if(!maps.contains(map)) {
			addMap(map);
		}
		
		for(int i = 0; i < maps.size(); i++) {
			if(maps.get(i).equals(map)) {
				this.currentMap = i;
				break;
			}
		}
	}
	
	/**
	 * @return the next map
	 */
	public String getNextMap() {
		this.currentMap = (this.currentMap + 1) % maps.size();
		return this.maps.get(currentMap);
	}
	
	/**
	 * @return the current map
	 */
	public String getCurrentMap() {
		return this.maps.get(currentMap);
	}
}
