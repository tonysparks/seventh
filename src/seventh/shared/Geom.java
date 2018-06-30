/*
 * see license.txt 
 */
package seventh.shared;

import static seventh.map.Tile.TILE_EAST_INVISIBLE;
import static seventh.map.Tile.TILE_INVISIBLE;
import static seventh.map.Tile.TILE_NORTH_INVISIBLE;
import static seventh.map.Tile.TILE_SOUTH_INVISIBLE;
import static seventh.map.Tile.TILE_VISIBLE;
import static seventh.map.Tile.TILE_WEST_INVISIBLE;

import java.util.List;

import seventh.map.Map;
import seventh.map.Tile;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Geom {
            
    /**
     * The right vector, used for retrieving the angle
     * or other vectors
     */
    public static final Vector2f RIGHT_VECTOR = new Vector2f(1, 0);
    
    /**
     * Clears the visibility masks
     * 
     * @param tiles
     * @param map
     */
    public static void clearMask(List<Tile> tiles, Map map) {
        map.setMask(tiles, 0);
    }
    
    /**
     * Utility function for calculating entity line of sight
     * 
     * @param tiles
     * @param pos
     * @param facing
     * @param radius
     * @param map
     */
    public static List<Tile> calculateLineOfSight(List<Tile> tiles, Vector2f pos, Vector2f facing, int radius, Map map, int heightMask, Vector2f cache) {
        map.setMask(tiles, 0);
        
        float fx = facing.x * radius + (facing.x * -64);
        float fy = facing.y * radius + (facing.y * -64);
        
        tiles = map.getTilesInCircle((int)(pos.x + fx), (int)(pos.y + fy), radius, tiles);
        Vector2f tilePos = cache; // avoid allocation
        
        int size = tiles.size();
        for(int i = 0; i < size; i++) {
            Tile tile = tiles.get(i);            
            tilePos.set(tile.getX() + (tile.getWidth()/2), tile.getY() + (tile.getHeight()/2));
            //tilePos.set(tile.getX(), tile.getY());
            if(map.lineCollides(tilePos, pos, heightMask) /*|| map.lineCollides(pos, tilePos)*/) {
                tile.setMask(TILE_INVISIBLE);
            }
            else {
                tile.setMask(TILE_VISIBLE);
            }
        
        }
        return tiles;
    }
    
    public static List<Tile> addFadeEffect(Map map, List<Tile> tiles) {
        int size = tiles.size();
                
        for(int i = 0; i < size; i++) {
            Tile tile = tiles.get(i);
            if(tile != null) {
                                
                int width = tile.getWidth();
                int height = tile.getHeight();
                
                int x = tile.getX() + width/2;
                int y = tile.getY() + height/2;
                
                int mask = tile.getMask();
                
                if(mask > TILE_INVISIBLE) 
                {
                    mask = tile.getMask();
                    
                    Tile north = getTile(map, x, y - height, width, height);
                    if(north==null || north.getMask() == TILE_INVISIBLE) {
                        tile.setMask( mask | TILE_NORTH_INVISIBLE);
                    }
                    
                    mask = tile.getMask();
                    
//                    Tile ne = map.getTile(0, x + width, y + height);
//                    if(ne == null || ne.getMask() == TILE_INVISIBLE) {
//                        tile.setMask(mask | Tile.TILE_NE_CORNER_INVISIBLE);
//                    }
//                    
//                    mask = tile.getMask();
                    
                    Tile east = getTile(map, x + width, y, width, height);
                    if(east==null || east.getMask() == TILE_INVISIBLE) {
                        tile.setMask( mask | TILE_EAST_INVISIBLE);
                    }
                    
                    mask = tile.getMask();
                    
//                    Tile se = map.getTile(0, x + width, y - height);
                    Tile south = getTile(map, x, y + height, width, height);
                    if(south==null || south.getMask() == TILE_INVISIBLE) {
                        tile.setMask( mask | TILE_SOUTH_INVISIBLE);
                    }
                    
                    mask = tile.getMask();
                    
//                    Tile sw = map.getTile(0, x - width, y - height);
                    Tile west = getTile(map, x - width, y, width, height);
                    if(west==null || west.getMask() == TILE_INVISIBLE) {
                        tile.setMask( mask | TILE_WEST_INVISIBLE);
                    }
//                    Tile nw = map.getTile(0, x - width, y + height);
                }
            }
        }
        
        return tiles;
    }
    
    private static Tile getTile(Map map, int worldX, int worldY, int tileWidth, int tileHeight) {
        
        int x = (worldX) / tileWidth;
        int y = (worldY) / tileHeight;
        
        Tile tile = null;
        if(!map.checkTileBounds(x, y)) {
            tile = map.getTile(0, x, y);
        }
        return tile;
    }
}
