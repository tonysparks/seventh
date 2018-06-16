/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.events.GameEvent.EventType;
import seventh.math.Vector2f;
import seventh.math.Vector4f;

/**
 * @author Tony
 *
 */
public class GameEventMessage extends AbstractNetMessage {
    public EventType eventType;
    public Vector2f pos;
    public Vector2f pos2;
    public float rotation;
    public String path;
    public int playerId1;
    public int playerId2;
    
    public Vector4f light;
    
    public GameEventMessage() {
        super(BufferIO.GAME_EVENT);
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
            case Message:
                this.path = BufferIO.readString(buffer);
                break;
            case LightAdjust:
                this.light = new Vector4f();
                int r = buffer.getUnsignedByte();
                int g = buffer.getUnsignedByte();
                int b = buffer.getUnsignedByte();
                int intensity = buffer.getUnsignedByte();
                
                this.light.x = (float)r / 255f;
                this.light.y = (float)g / 255f;
                this.light.z = (float)b / 255f;
                this.light.w = (float)intensity / 255f;
                break;
            case BrokenGlass:
                this.pos = new Vector2f();
                this.pos.x = BufferIO.readPos(buffer);
                this.pos.y = BufferIO.readPos(buffer);
                
                this.rotation = BufferIO.readAngle(buffer);
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
            case Message:
                BufferIO.writeString(buffer, this.path);
                break;
            case LightAdjust:
                buffer.putUnsignedByte( (int)(255f * this.light.x) );
                buffer.putUnsignedByte( (int)(255f * this.light.y) );
                buffer.putUnsignedByte( (int)(255f * this.light.z) );
                buffer.putUnsignedByte( (int)(255f * this.light.w) );
                break;
            case BrokenGlass:
                BufferIO.writePos(buffer, this.pos.x);
                BufferIO.writePos(buffer, this.pos.y);
                BufferIO.writeAngle(buffer, (int)this.rotation);
                break;
            default:
                break;
        }
    }
}
