/*
 *    leola-live 
 *  see license.txt
 */
package seventh.map;

import java.util.ArrayList;
import java.util.List;

import seventh.client.gfx.AnimatedImage;
import seventh.map.Tile.SurfaceType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class TilesetAtlas {

    private List<Tileset> tilesets;
    
    /**
     * 
     */
    public TilesetAtlas() {
        this.tilesets = new ArrayList<Tileset>();
    }
    
    public void addTileset(Tileset t) {
        this.tilesets.add(t);
    }
    
    public TextureRegion getTile(int id) {
        for(Tileset t : tilesets) {
            TextureRegion img = t.getTile(id);
            if(img != null) {
                return img;
            }
        }
        
        return null;
    }
    
    /**
     * @param id
     * @return true if the tile id is an animated image
     */
    public boolean isAnimatedTile(int id) {
        for(Tileset t : tilesets) {
            TextureRegion img = t.getTile(id);
            if(img != null) {
                return t.isAnimatedImage(id);
            }
        }
        
        return false;
    }
    
    public AnimatedImage getAnimatedTile(int id) {
        for(Tileset t : tilesets) {
            TextureRegion img = t.getTile(id);
            if(img != null) {
                return t.getAnimatedImage(id);
            }
        }
        
        return null;
    }
    
    public SurfaceType getTileSurfaceType(int id) {
        for(Tileset t : tilesets) {
            SurfaceType type = t.getSurfaceType(id);
            if(type != SurfaceType.UNKNOWN) {
                return type;
            }
        }
        
        return SurfaceType.UNKNOWN;
    }
    
    public Integer getTileId(int id) {
        Tileset bestmatch = null;
        for(Tileset t : tilesets) {
            if(id >= t.getStartId()) {
                if(bestmatch == null || bestmatch.getStartId() < t.getStartId()) {
                    bestmatch = t;
                }
            }
            
        }
        
        if(bestmatch != null) {
            return bestmatch.getTileId(id);
        }
        
        return -1;
    }
    
    /**
     * Frees the allocated textures
     */
    public void destroy() {
        for(Tileset t : tilesets) {
            t.destroy();
        }
    }
}
