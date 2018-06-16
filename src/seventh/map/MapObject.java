/*
 * see license.txt 
 */
package seventh.map;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.map.Tile.SurfaceType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class MapObject implements Renderable {

    private String type;
    protected Vector2f pos;
    protected Rectangle bounds;
    
    public MapObject(String type) {
        this.type = type;
        this.pos = new Vector2f();
        this.bounds = new Rectangle();
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @return the pos
     */
    public Vector2f getPos() {
        return pos;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public boolean isCollidable() {
        return true;
    }
    
    public SurfaceType geSurfaceType() {
        return SurfaceType.UNKNOWN;
    }
    
    public boolean isTouching(Rectangle bounds) {
        return false;
    }
    
    /**
     * If this {@link MapObject} touches the supplied {@link Entity}
     * 
     * @param ent
     * @return true if touching
     */
    public boolean isTouching(Entity ent) {
        return false;
    }
    
    /**
     * This {@link MapObject} is touched by an {@link Entity}
     * 
     * @param game
     * @param ent
     */
    public void onTouch(Game game, Entity ent) {       
    }
    
    public void destroy() {        
    }
    

    @Override
    public void update(TimeStep timeStep) {
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {       
    }
}
