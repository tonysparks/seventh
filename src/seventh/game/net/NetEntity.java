/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

import java.util.List;

import seventh.game.Entity;

/**
 * @author Tony
 *
 */
public class NetEntity implements NetMessage {		
	public byte type;
	public int id;
	
//	public byte dir;
	public short orientation;
	
	public short posX;
	public short posY;
	
//	public byte width;
//	public byte height;
	
//	public byte events;
	
		
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		type = buffer.get();
		
				
//		id = buffer.getInt();		
//		orientation = buffer.getShort();
		
		posX = buffer.getShort();
		posY = buffer.getShort();
		
//		width = buffer.get();
//		height = buffer.get();
		
//		events = buffer.get();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		buffer.put(type);
						
//		buffer.putInt(id);
		
//		buffer.putShort(orientation);
		
		buffer.putShort(posX);
		buffer.putShort(posY);
		
//		buffer.put(width);
//		buffer.put(height);
		
//		buffer.put(events);
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
