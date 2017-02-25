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

    public static class MapEntry {
        private String displayName;
        private String fileName;
        
        /**
         * @param fileName
         */
        public MapEntry(String fileName) {
            this(MapList.stripFileExtension(fileName), fileName);
        }
        
        /**
         * @param displayName
         * @param fileName
         */
        public MapEntry(String displayName, String fileName) {
            super();
            this.displayName = displayName;
            this.fileName = fileName;
        }
        
        /**
         * @return the displayName
         */
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * @return the fileName
         */
        public String getFileName() {
            return fileName;
        }
        
        @Override
        public String toString() {        
            return this.displayName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
            result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MapEntry other = (MapEntry) obj;
            if (displayName == null) {
                if (other.displayName != null)
                    return false;
            } else if (!displayName.equals(other.displayName))
                return false;
            if (fileName == null) {
                if (other.fileName != null)
                    return false;
            } else if (!fileName.equals(other.fileName))
                return false;
            return true;
        }
        
        
    }
    
    public static final String MapFilePath = "./assets/maps/";
    
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
    public static List<MapEntry> getMapListing(GameType.Type gameType) {        
        File[] maps = getMapFiles(MapFilePath);
        
        List<MapEntry> mapNames = new ArrayList<MapEntry>(maps.length);
        for(File f : maps) {
            File gameTypeFile = new File(MapFilePath, f.getName() + "." + gameType.name().toLowerCase() + ".leola");
            if(gameType==GameType.Type.TDM || gameTypeFile.exists()) {
                String displayName = stripFileExtension(f.getName());
                MapEntry entry = new MapEntry(displayName, MapFilePath + f.getName());
                mapNames.add(entry);
            }
        }
        
        return mapNames;
    }
    
    /**
     * Look the the maps directory to see what maps are available
     * 
     * @return the map listings
     */
    public static List<MapEntry> getMapListing() {
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
    
    public static String stripFileExtension(String fileName) {
        String displayName = fileName.replace(".json", "");
        return displayName;
    }
}
