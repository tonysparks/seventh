/*
 * see license.txt 
 */
package seventh.client.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.ClientGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.game.SmoothOrientation;
import seventh.game.entities.Door.DoorHinge;
import seventh.game.net.NetDoor;
import seventh.game.net.NetEntity;
import seventh.math.Line;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientDoor extends ClientEntity {

    private SmoothOrientation rotation;
    private Vector2f frontDoorHandle,
                     rearDoorHandle, 
                     rearHingePos;
    
    private DoorHinge hinge;
    
    private Sprite sprite;
    
    /**
     * @param game
     * @param pos
     */
    public ClientDoor(ClientGame game, Vector2f pos) {
        super(game, pos);
        
        this.rotation = new SmoothOrientation(0.05);
        this.rotation.setOrientation(0);
        this.frontDoorHandle = new Vector2f();
        this.rearDoorHandle = new Vector2f();
        this.rearHingePos = new Vector2f();
        
        this.sprite = new Sprite(Art.doorImg);
        this.sprite.setOrigin(0, Art.doorImg.getRegionHeight()/2);
    }
    
    @Override
    public void updateState(NetEntity state, long time) {    
        super.updateState(state, time);
        
        NetDoor door = (NetDoor)state;
        this.hinge = DoorHinge.fromNetValue(door.hinge);
        this.rotation.setOrientation(this.getOrientation());

        Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.frontDoorHandle);
        this.rearHingePos = this.hinge.getRearHingePosition(getPos(), this.rotation.getFacing(), this.rearHingePos);                
        Vector2f.Vector2fMA(this.rearHingePos, this.rotation.getFacing(), 64, this.rearDoorHandle);
        
//        this.rearHingePos = this.hinge.getRearHandlePosition(getPos(), this.rearHingePos);
//        
//        Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.frontDoorHandle);
//        Vector2f.Vector2fMA(this.rearHingePos, this.rotation.getFacing(), 64, this.rearDoorHandle);
        
    }
    
    /**
     * @return the frontDoorHandle
     */
    public Vector2f getHandle() {
        return frontDoorHandle;
    }
    
    @Override
    public boolean killIfOutdated(long gameClock) {    
        return false;
    }
    
    @Override
    public boolean touches(ClientEntity other) {
        return isTouching(other.getBounds());
    }
    
    public boolean isTouching(Rectangle bounds) {
//        return Line.lineIntersectsRectangle(getPos(), this.frontDoorHandle, bounds) ||
//               Line.lineIntersectsRectangle(this.rearHingePos, this.rearDoorHandle, bounds);
        boolean isTouching = Line.lineIntersectsRectangle(getPos(), this.frontDoorHandle, bounds) ||
                Line.lineIntersectsRectangle(this.rearHingePos, this.rearDoorHandle, bounds);
//         if(isTouching) {
//             DebugDraw.fillRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xff00ff00);
//             DebugDraw.drawLineRelative(getPos(), this.frontDoorHandle, 0xffffff00);
//             DebugDraw.drawLineRelative(this.rearHingePos, this.rearDoorHandle, 0xffffff00);
//         }
         return isTouching;
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {        
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        /*canvas.drawLine(getPos().x - cameraPos.x, getPos().y - cameraPos.y, 
                        this.doorHandle.x - cameraPos.x, this.doorHandle.y - cameraPos.y, 0xffff00ff);*/
        
        this.sprite.setRotation((float)Math.toDegrees(getOrientation()));
        this.sprite.setPosition(getPos().x - cameraPos.x, getPos().y - cameraPos.y);
        canvas.drawRawSprite(this.sprite);
    }

}
