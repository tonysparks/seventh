/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.network.messages.BufferIO;
import seventh.shared.BitArray;
import seventh.shared.SeventhConstants;

/**
 * @author Tony
 *
 */
public class NetGameState  implements NetMessage {
	
	public NetGameTypeInfo gameType;
	public NetEntity[] entities;	
	public NetMap map;
	public NetGameStats stats;
	
	protected byte bits;
	
	private BitArray bitArray;
	private int numberOfInts;
	/**
	 * 
	 */
	public NetGameState() {
		bitArray = new BitArray(SeventhConstants.MAX_ENTITIES - 1);
		entities = new NetEntity[SeventhConstants.MAX_ENTITIES];
		
		numberOfInts = bitArray.numberOfInts();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		bits = buffer.get();
		if( (bits & 1) != 0) {
			gameType = new NetGameTypeInfo();
			gameType.read(buffer);
		}
		
		if( (bits & 2) != 0) {
			
			for(int i = 0; i < numberOfInts; i++) {
				bitArray.setDataElement(i, buffer.getInt());
			}
			
			for(int i = 0; i < entities.length; i++) {
				if(bitArray.getBit(i)) {
					entities[i] = BufferIO.readEntity(buffer);	
					entities[i].id = i;
				}
			}

		}
		
		if ( (bits & 4) != 0) {
			map = new NetMap();
			map.read(buffer);
		}
		
		if( (bits & 8) != 0) {
			stats = new NetGameStats();
			stats.read(buffer);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		bits = 0;
		if(gameType != null) {
			bits |= 1;
		}
		if(entities != null && entities.length > 0) {
			bits |= 2;
		}
		if(map != null) {
			bits |= 4;
		}
		if(stats != null) {
			bits |= 8;
		}
		
		buffer.put(bits);
		
		if(gameType != null) {
			gameType.write(buffer);
		}
		if(entities != null && entities.length > 0) {
			bitArray.clear();
			
			for(int i = 0; i < entities.length; i++) {
				if(entities[i]!=null) {
					bitArray.setBit(i);
				}
			}
			
			int[] data = bitArray.getData();
			for(int i = 0; i < data.length; i++) {
				buffer.putInt(data[i]);
			}
			
			for(int i = 0; i < entities.length; i++) {
				if(entities[i]!=null) {
					entities[i].write(buffer);
				}
			}
		}
		
		if(map != null) {
			map.write(buffer);
		}
		if(stats != null) {
			stats.write(buffer);
		}
	}
}
