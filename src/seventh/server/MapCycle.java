/*
 * see license.txt 
 */
package seventh.server;

import java.util.List;

import seventh.shared.Command;
import seventh.shared.Console;
import seventh.shared.MapList.MapEntry;

/**
 * The play list for maps.
 * 
 * @author Tony
 *
 */
public class MapCycle {

    private List<MapEntry> maps;
    private int currentMap;
    
    /**
     * @param maps
     */
    public MapCycle(List<MapEntry> maps) {
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
                MapEntry currentMap = getCurrentMap();
                console.println("\n");
                console.println("Current map: " + currentMap);
                for(MapEntry map : maps) {
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
        this.maps.add(new MapEntry(map));
    }
    
    public void addMap(MapEntry map) {
        this.maps.add((map));
    }
    
    /**
     * Removes a map from the rotation
     * 
     * @param map
     */
    public void removeMap(String map) {
        int index = 0;
        for(MapEntry entry : this.maps) {
            if(entry.getFileName().equals(map)) {
                this.maps.remove(index);
                break;
            }
            index++;
        }
    }

    /**
     * If the supplied map file exists in this cycle
     * @param map
     * @return true if it exists
     */
    public boolean hasMap(String map) {
        for(MapEntry entry : this.maps) {
            if(entry.getFileName().equals(map)) {
                return true;
            }            
        }
        return false;
    }
    
    public boolean hasMap(MapEntry map) {
        return hasMap(map.getFileName());
    }
    
    /**
     * Sets the current map 
     * @param map
     */
    public void setCurrentMap(MapEntry map) {                
        if(!hasMap(map)) {
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
    public MapEntry getNextMap() {
        this.currentMap = (this.currentMap + 1) % maps.size();
        return this.maps.get(currentMap);
    }
    
    /**
     * @return the current map
     */
    public MapEntry getCurrentMap() {
        return this.maps.get(currentMap);
    }
}
