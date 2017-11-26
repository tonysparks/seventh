/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.events.SurvivorEvent.EventType;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class SurvivorEventMessage extends AbstractNetMessage {
    public EventType eventType;
    public Vector2f pos;
    public String path;
    public int playerId1;
    public int playerId2;
    
    public SurvivorEventMessage() {
        super(BufferIO.SURVIVOR_EVENT);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.eventType = EventType.fromNet(buffer.getIntBits(EventType.numberOfBits()));
        switch(this.eventType) {
            case CustomSound:
                this.path = BufferIO.readString(buffer);
                break;
            case CustomTrigger:
                break;
            case EnemySpawned:
                this.playerId1 = BufferIO.readPlayerId(buffer);
                this.pos = new Vector2f();
                this.pos.x = BufferIO.readPos(buffer);
                this.pos.y = BufferIO.readPos(buffer);
                break;
            default:
                break;
        }
        
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);    
        buffer.putIntBits(this.eventType.ordinal(), EventType.numberOfBits());
        switch(this.eventType) {
            case CustomSound:
                BufferIO.writeString(buffer, this.path);
                break;
            case CustomTrigger:
                break;
            case EnemySpawned:                
                BufferIO.writePlayerId(buffer, this.playerId1);
                BufferIO.writePos(buffer, this.pos.x);
                BufferIO.writePos(buffer, this.pos.y);                
                break;
            default:
                break;
        }
    }
}
