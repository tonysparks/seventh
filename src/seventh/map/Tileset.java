/*
 *    leola-live 
 *  see license.txt
 */
package seventh.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Art;
import seventh.client.gfx.TextureUtil;
import seventh.map.Tile.SurfaceType;

/**
 * @author Tony
 *
 */
public class Tileset {

    private TextureRegion[] image;
    private int startId;
    private LeoMap props;
    
    public Tileset(int startId, TextureRegion[] image, LeoMap props) {
        this.startId = startId;
        this.image = image;
        this.props = props;
    }
    
    /**
     * @return the startId
     */
    public int getStartId() {
        return startId;
    }
    
    /**
     * @param tileid
     * @return the {@link SurfaceType}
     */
    public SurfaceType getSurfaceType(int tileid) {
        if(props != null) {
            String id = Integer.toString(toIndex(tileid));
            LeoObject p = props.getByString(id);
            if(LeoObject.isTrue(p)) {
                LeoObject s = p.getObject("surface");
                if(LeoObject.isTrue(s)) {
                    return SurfaceType.fromString(s.toString());
                }
            }
        }
        return SurfaceType.UNKNOWN;
    }
    
    /**
     * @param tileid
     * @return true if this tile id is an animation
     */
    public boolean isAnimatedImage(int tileid) {
        if(props != null) {
            String id = Integer.toString(toIndex(tileid));
            LeoObject p = props.getByString(id);
            if(LeoObject.isTrue(p)) {
                LeoObject animation = p.getObject("animation");
                if(LeoObject.isTrue(animation)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @param tileid
     * @return the {@link AnimatedImage} for this tile id
     */
    public AnimatedImage getAnimatedImage(int tileid) {
        if(props != null) {
            String id = Integer.toString(toIndex(tileid));
            LeoObject p = props.getByString(id);
            if(LeoObject.isTrue(p)) {
                LeoObject animation = p.getObject("animation");
                if(LeoObject.isTrue(animation)) {
                    TextureRegion tex = Art.loadImage(animation.toString());
                    int rowNum = tex.getRegionHeight() / 32;
                    int colNum = tex.getRegionWidth() / 32;
                    
                    LeoObject rows = p.getObject("rows");
                    LeoObject cols = p.getObject("cols");
                    
                    if(LeoObject.isTrue(rows)) {
                        rowNum = Integer.valueOf(rows.toString());
                    }
                    
                    if(LeoObject.isTrue(cols)) {
                        colNum = Integer.valueOf(cols.toString());
                    }
                    
                    int frameTime = 800;
                    LeoObject fps = p.getObject("fps");
                    if(LeoObject.isTrue(fps)) {
                        frameTime = Integer.valueOf(fps.toString());
                    }
                    
                    int numberOfFrames = rowNum * colNum;
                    int[] frames = new int[numberOfFrames];
                    for(int i = 0; i < numberOfFrames; i++) {
                        frames[i] = frameTime;
                    }
                    
                    return new AnimatedImage(TextureUtil.splitImage(tex, rowNum, colNum), Art.newAnimation(frames));
                }
            }
        }
        
        return null;
    }
    
    private int toIndex(int id) {
        return id - startId;
    }
    
    /**
     * @param id
     * @return
     */
    public TextureRegion getTile(int id) {
        int index = toIndex(id);
        if ( index < 0 || index >= image.length ) {
            return null;
        }
        
        return image[index];
    }
    
    public Integer getTileId(int id) {
        int index = toIndex(id);        
        return index + 1;
    }
}
