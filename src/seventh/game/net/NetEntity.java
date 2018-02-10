/*
 * see license.txt 
 */
package seventh.game.net;

import java.util.List;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.Type;
import seventh.network.messages.BufferIO;

/**
 * @author Tony
 *
 */
public class NetEntity implements NetMessage {        
    public Type type; 
    public int id;
    
//    public byte dir;
    public short orientation;
    
    public int posX;
    public int posY;
    
//    public byte width;
//    public byte height;
    
//    public byte events;
    
        
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        type = BufferIO.readType(buffer);  // NOTE: Must match BufferIO.readEntity
        //type = buffer.get();
                
//        id = buffer.getInt();        
//        orientation = buffer.getShort();
        
//        posX = buffer.getShort();
//        posY = buffer.getShort();
        posX = buffer.getIntBits(13); // max X & Y of 256x256 tiles (32x32 tiles) ~8191 
        posY = buffer.getIntBits(13);
        
//        width = buffer.get();
//        height = buffer.get();
        
//        events = buffer.get();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        BufferIO.writeType(buffer, type);
        //buffer.put(type);  
        
//        buffer.putInt(id);
        
//        buffer.putShort(orientation);
        
//        buffer.putShort(posX);
//        buffer.putShort(posY);
        buffer.putIntBits(posX, 13);
        buffer.putIntBits(posY, 13);
        
//        buffer.put(width);
//        buffer.put(height);
        
//        buffer.put(events);
    }
    
    /**
     * Copies the Entity array into the NetEntity array
     * @param entities
     * @param results
     * @return the passed in NetEntity array
     */
    public static NetEntity[] toNetEntities(Entity[] entities, NetEntity[] results) {                
        for(int i = 0; i < entities.length; i++) {
            if(entities[i]!=null) {
                results[i] = entities[i].getNetEntity();
            }
        }
        
        return results;
    }
    
    /**
     * @param entities
     * @return converts the List of {@link Entity} to the respective {@link NetEntity} array
     */
    public static NetEntity[] toNetEntities(List<Entity> entities, NetEntity[] results) {        
        int size = entities.size();
        for(int i = 0; i < size; i++) {
            Entity ent = entities.get(i);
            if(ent != null) {
                results[ent.getId()] = ent.getNetEntity();
            }
        }
        
        return results;
    }
}
