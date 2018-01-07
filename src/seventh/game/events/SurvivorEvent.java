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
public class SurvivorEvent extends Event {

    public static enum EventType {
        CustomSound,        
        EnemySpawned,
        CustomTrigger,
        Message,
        LightAdjust,
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
    private String path;
    private int playerId1;
    private int playerId2;
    private EventType eventType;
    
    private Vector4f light;
    
    /**
     * @param source
     */
    public SurvivorEvent(Object source, EventType eventType, Vector2f pos, String path, int playerId1, int playerId2, Vector4f light) {
        super(source);
        this.eventType = eventType;
        this.pos = pos;
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
