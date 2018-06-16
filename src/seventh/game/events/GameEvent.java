/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.math.Vector2f;
import seventh.math.Vector4f;
import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class GameEvent extends Event {

    public static enum EventType {
        CustomSound,        
        EnemySpawned,
        CustomTrigger,
        Message,
        LightAdjust,
        BrokenGlass,
        ;
        
        private static final EventType[] values = values();
        
        public static int numberOfBits() {
            return 5;
        }
        
        public static EventType fromNet(int bits) {
            if(bits>=0 && bits < values.length) {
                return values[bits];
            }
            return EventType.CustomSound;
        }
    }
    
    private Vector2f pos;
    private Vector2f pos2;
    private float rotation;
    private String path;
    private int playerId1;
    private int playerId2;
    private EventType eventType;
    
    private Vector4f light;
    
    /**
     * @param source
     */
    public GameEvent(Object source, 
                     EventType eventType, 
                     Vector2f pos,
                     Vector2f pos2,
                     float rotation,
                     String path, 
                     int playerId1, 
                     int playerId2, 
                     Vector4f light) {
        super(source);
        this.eventType = eventType;
        this.pos = pos;
        this.pos2 = pos2;
        this.rotation = rotation;
        this.path = path;
        this.playerId1 = playerId1;
        this.playerId2 = playerId2;
        this.light = light;
    }
    
    

    /**
     * @return the pos
     */
    public Vector2f getPos() {
        return pos;
    }
    
    /**
     * @return the pos2
     */
    public Vector2f getPos2() {
        return pos2;
    }
    
    /**
     * @return the rotation
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the playerId1
     */
    public int getPlayerId1() {
        return playerId1;
    }

    /**
     * @return the playerId2
     */
    public int getPlayerId2() {
        return playerId2;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }
    
    /**
     * @return the light
     */
    public Vector4f getLight() {
        return light;
    }
    
}
