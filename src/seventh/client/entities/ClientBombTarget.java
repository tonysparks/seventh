/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.sfx.Sound;
import seventh.game.net.NetBombTarget;
import seventh.game.net.NetEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class ClientBombTarget extends ClientEntity {

    private Sprite sprite;
    private boolean isBombPlanted;
    private boolean isFlipped;
    
    private Rectangle radioBroadcastArea;
    private Timer emitSound;
    private Sound activeSound;
    
    /**
     * @param pos
     */
    public ClientBombTarget(ClientGame game, Vector2f pos) {
        super(game, pos);
        
        this.isBombPlanted = false;
        this.sprite = new Sprite(Art.radioImage);
        getBounds().setSize(64, 32);
        
        radioBroadcastArea = new Rectangle(150, 150);
        radioBroadcastArea.centerAround(pos);
        
        emitSound = new Timer(true, 3_000);
        emitSound.start();
        
        setOnRemove(new OnRemove() {
            
            @Override
            public void onRemove(ClientEntity me, ClientGame game) {
                if(activeSound!=null) {
                    activeSound.stop();
                }
            }
        });
    }
        
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#updateState(seventh.game.net.NetEntity, long)
     */
    @Override
    public void updateState(NetEntity state, long time) {    
        super.updateState(state, time);
        
        NetBombTarget target = (NetBombTarget) state;
        if(target.rotated90() && !isFlipped) {
            isFlipped = true;
            sprite.rotate(90f);
            int width = getBounds().width;
            getBounds().width = getBounds().height;
            getBounds().height = width;
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {    
        super.update(timeStep);
        
        final ClientPlayer player = game.getLocalPlayer();
        if(player.isAlive()) {
            final ClientEntity entity = player.getEntity();
            if(isInEarShot(entity)) {
                emitSound.update(timeStep);
                
                if(emitSound.isOnFirstTime()) {
                    emitSound.setEndTime(3_100 + game.getRandom().nextInt(1_000));
                    
                    Vector2f pos = getCenterPos();
                    activeSound = game.playSound(SoundType.RADIO_STATIC, pos.x, pos.y);
                }
            }
        }
    }
    
    /**
     * @param isBombPlanted the isBombPlanted to set
     */
    public void setBombPlanted(boolean isBombPlanted) {
        this.isBombPlanted = isBombPlanted;
    }
    
    /**
     * @return the isBombPlanted
     */
    public boolean isBombPlanted() {
        return isBombPlanted;
    }
    
    /**
     * If the supplied entity can hear this target
     * 
     * @param entity
     * @return true if the entity should be able to hear it
     */
    public boolean isInEarShot(ClientEntity entity) {
        return this.radioBroadcastArea.intersects(entity.getBounds());
    }
    
    
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#killIfOutdated(long)
     */
    @Override
    public boolean killIfOutdated(long gameClock) {    
        return false;
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#isBackgroundObject()
     */
    @Override
    public boolean isBackgroundObject() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        float x = (pos.x - cameraPos.x);
        float y = (pos.y - cameraPos.y);
        sprite.setPosition(x, y);
        
//        canvas.drawImage(Art.computerImage, x, y, null);
        canvas.drawSprite(sprite);
        
    }

}
