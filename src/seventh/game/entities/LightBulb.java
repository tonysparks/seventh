/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.game.net.NetEntity;
import seventh.game.net.NetLight;
import seventh.math.Vector2f;
import seventh.math.Vector3f;

/**
 * @author Tony
 *
 */
public class LightBulb extends Entity {

    private NetLight netEntity;
    private Vector3f color;
    private float luminacity;
    private int size;
    
    /**
     * @param position
     * @param speed
     * @param game
     * @param type
     */
    public LightBulb(Vector2f position, Game game) {
        super(game.getNextPersistantId(), position, 0, game, Type.LIGHT_BULB);
        
        this.bounds.width = 5;
        this.bounds.height = 5;
        this.bounds.centerAround(position);
        
        this.color = new Vector3f(0.3f, 0.3f, 0.7f);        
        this.luminacity = 0.25f;
        this.size = 512;
//        this.size = 5;
        
        this.netEntity = new NetLight();
        setNetEntity(netEntity);
                        
    }
    
    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
    
    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }
    
    /**
     * @param luminacity the luminacity to set
     */
    public void setLuminacity(float luminacity) {
        this.luminacity = luminacity;
    }
    
    /**
     * @return the luminacity
     */
    public float getLuminacity() {
        return luminacity;
    }
    
    /**
     * @param color the color to set
     */
    public void setColor(Vector3f color) {
        this.color.set(color);
    }
    
    public void setColor(float r, float g, float b) {
        this.color.set(r,g,b);;
    }
    
    /**
     * @return the color
     */
    public Vector3f getColor() {
        return color;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#getNetEntity()
     */
    @Override
    public NetEntity getNetEntity() {
        this.netEntity.r = (short) (255 * color.x);
        this.netEntity.g = (short) (255 * color.y);
        this.netEntity.b = (short) (255 * color.z);
        
        this.netEntity.luminacity = (short) (255 * luminacity);
        this.netEntity.size = (short)size;
        return this.netEntity;
    }

}
