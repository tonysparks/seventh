/*
 * see license.txt 
 */
package seventh.ai.basic;

import static seventh.shared.SeventhConstants.PLAYER_HEIGHT;
import static seventh.shared.SeventhConstants.PLAYER_WIDTH;

import java.util.ArrayList;
import java.util.List;

import seventh.game.GameInfo;
import seventh.game.entities.BombTarget;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * A container for all the {@link Zone}s in a map
 * 
 * @author Tony
 *
 */
public class Zones {

    private Zone[][] zones;
    
    private final int zoneWidth, zoneHeight;
    private final int mapWidth, mapHeight;
    private final int numberOfCols, numberOfRows;
    private final int numberOfZones;
    private List<Zone> bombTargetZones;
    private GameInfo game;
        
    /**
     * @param game
     */
    public Zones(GameInfo game) {
        this.game = game;
        bombTargetZones = new ArrayList<>();
        
        final int ZONE_SIZE_IN_TILES = 12;
        
        Map map = game.getMap();

        mapWidth = map.getMapWidth();
        mapHeight = map.getMapHeight();
        
        zoneWidth = mapWidth / ZONE_SIZE_IN_TILES;
        zoneHeight = mapHeight / ZONE_SIZE_IN_TILES;
        
        
        numberOfCols = mapWidth / zoneWidth;
        numberOfRows = mapHeight / zoneHeight;
        
        numberOfZones = numberOfCols * numberOfRows;
        
        zones = new Zone[numberOfRows][numberOfCols];
        
        Rectangle entityBounds = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT);
        
        int id = 0;
        for(int y = 0; y < numberOfRows; y++) {
            for(int x = 0; x < numberOfCols; x++) {                
                Rectangle bounds = new Rectangle(x * zoneWidth, y * zoneHeight, zoneWidth, zoneHeight);
                boolean isHabitable = hasHabitableLocation(entityBounds, bounds, map);
                                                                
                zones[y][x] = new Zone(id++, bounds, isHabitable);
            }
        }            
    }

    /**
     * Determines if a player could fit somewhere inside this zone
     * 
     * @param entityBounds
     * @param zoneBounds
     * @param map
     * @return true if a player could fit in this zone
     */
    private boolean hasHabitableLocation(Rectangle entityBounds, Rectangle zoneBounds, Map map) {
        int x = zoneBounds.x;
        int y = zoneBounds.y;
        
        entityBounds.setLocation(x, y);
        
        int maxX = zoneBounds.x+zoneBounds.width;
        int maxY = zoneBounds.y+zoneBounds.height;
        
        boolean hitMaxX = false;
        boolean hitMaxY = false;
        
        while (map.rectCollides(entityBounds)) {
            if(x+entityBounds.width <= maxX) {
                x += entityBounds.width; 
            }
            else {
                hitMaxX = true;
            }
            
            if(y+entityBounds.height <= maxY) {
                y += entityBounds.height;            
            }
            else {
                hitMaxY = true;
            }
            
            entityBounds.setLocation(x, y);
            
            if(hitMaxX && hitMaxY) {
                return false;
            }
        }
                
        return true;
    }
    
    /**
     * Determines which Zone's the {@link BombTarget}s
     * fall into
     */
    public void calculateBombTargets() {
        for(int y = 0; y < numberOfRows; y++) {
            for(int x = 0; x < numberOfCols; x++) {
                zones[y][x].clearTargets();
            }
        }
        
        List<BombTarget> targets = game.getBombTargets();
        for(BombTarget target : targets) {
            Zone zone = getZone(target.getCenterPos());
            if(zone != null && zone.isHabitable()) {
                zone.addTarget(target);
                bombTargetZones.add(zone);
            }                    
        }
    }
    
    /**
     * @return the zones
     */
    public Zone[][] getZones() {
        return zones;
    }
    
    /**
     * @return the number of zones
     */
    public int getNumberOfZones() {
        return this.numberOfZones;
    }
        
    
    /**
     * @return the bombTargetZones
     */
    public List<Zone> getBombTargetZones() {
        return bombTargetZones;
    }
    
    /**
     * @param pos
     * @return a {@link Zone} at a specified location
     */
    public Zone getZone(Vector2f pos) {
        return getZone( (int)pos.x, (int)pos.y);
    }
    
    
    /**
     * @param x
     * @param y
     * @return a {@link Zone} at a specified location
     */
    public Zone getZone(int x, int y) {
        if(x<0 || y<0 || x>mapWidth || y>mapHeight) {
            return null;
        }
        
        int offsetX = 0;//(x % zoneWidth);
        int offsetY = 0;//(y % zoneHeight);
        
        int xIndex = (x + offsetX) / zoneWidth;
        int yIndex = (y + offsetY) / zoneHeight;
        
        if (xIndex > zones[0].length-1 || yIndex > zones.length-1) {
            return null;
        }
        
        
        return zones[yIndex][xIndex];
    }
    
    
    /**
     * Finds the adjacent zones
     * 
     * @param zone
     * @return the adjacent zones
     */
    public Zone[] getAdjacentZones(Zone zone) {
        return getAdjacentZones(zone.getBounds().x, zone.getBounds().y);
    }
    
    /**
     * Finds the adjacent nodes
     * 
     * @param x
     * @param y
     * @return the adjacent nodes
     */
    public Zone[] getAdjacentZones(int x, int y) {
        
        if(x<0 || y<0 || x>mapWidth || y>mapHeight) {
            return null;
        }
        
        int xIndex = x / zoneWidth;
        int yIndex = y / zoneHeight;
        
        if (xIndex > zones[0].length-1 || yIndex > zones.length-1) {
            return null;
        }
                                
        Zone[] adjacentZones = new Zone[8];
        adjacentZones[0] = getZoneByIndex(xIndex,   yIndex+1);
        adjacentZones[1] = getZoneByIndex(xIndex+1, yIndex+1);
        adjacentZones[2] = getZoneByIndex(xIndex+1, yIndex);
        adjacentZones[3] = getZoneByIndex(xIndex+1, yIndex-1);
        adjacentZones[4] = getZoneByIndex(xIndex,   yIndex-1);
        adjacentZones[5] = getZoneByIndex(xIndex-1, yIndex-1);
        adjacentZones[6] = getZoneByIndex(xIndex-1, yIndex);
        adjacentZones[7] = getZoneByIndex(xIndex-1, yIndex+1);
        
        return adjacentZones;
        
    }
    
    private Zone getZoneByIndex(int x, int y) {
        if(y >= zones.length || x >= zones[0].length) {
            return null;
        }
        
        if(x < 0 || y < 0) {
            return null;
        }
        
        return zones[y][x];
    }
    /**
     * @param id
     * @return the {@link Zone}
     */
//    public Zone getZoneById(int id) {
//        // TODO
////        int y = (id / this.numberOfRows) - 1;
////        int x = (id-y) % this.numberOfCols;
////        return this.zones[y][x];
//        return null;
//    }
}
