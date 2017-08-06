/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.BitArray;
import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.type.GameType.Type;
import seventh.network.messages.BufferIO;
import seventh.shared.SeventhConstants;

/**
 * Full game state
 * 
 * @author Tony
 *
 */
public class NetGameState  implements NetMessage {
    private static final int FL_GAMETYPE = (1<<0);
    private static final int FL_ENTITIES = (1<<1);
    private static final int FL_GAMEMAP  = (1<<2);
    private static final int FL_GAMESTATS= (1<<3);
    private static final int FL_DESTRUCTABLES = (1<<4);
    
    public NetGameTypeInfo gameType;
    public NetEntity[] entities;    
    public NetMap map;
    public NetGameStats stats;
    public NetMapDestructables mapDestructables;
    
    protected byte bits;
    
    private BitArray bitArray;
    private int numberOfBytes;
    
    
    public NetGameState() {
        bitArray = new BitArray(SeventhConstants.MAX_ENTITIES);
        entities = new NetEntity[SeventhConstants.MAX_ENTITIES];
        
        numberOfBytes = bitArray.numberOfBytes();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        bits = buffer.getByte();
        if( (bits & FL_GAMETYPE) != 0) {
            gameType = NetGameTypeInfo.newNetGameTypeInfo(Type.fromNet(buffer.getByteBits(Type.numOfBits())));
            gameType.read(buffer);
        }
        
        if( (bits & FL_ENTITIES) != 0) {
            
            for(int i = 0; i < numberOfBytes; i++) {
                bitArray.setDataElement(i, buffer.getByte());
            }
            
            for(int i = 0; i < entities.length; i++) {
                if(bitArray.getBit(i)) {
                    entities[i] = BufferIO.readEntity(buffer);    
                    entities[i].id = i;
                }
            }

        }
        
        if ( (bits & FL_GAMEMAP) != 0) {
            map = new NetMap();
            map.read(buffer);
        }
        
        if( (bits & FL_GAMESTATS) != 0) {
            stats = new NetGameStats();
            stats.read(buffer);
        }
        
        if( (bits & FL_DESTRUCTABLES) != 0) {
            mapDestructables = new NetMapDestructables();
            mapDestructables.read(buffer);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        bits = 0;
        if(gameType != null) {
            bits |= FL_GAMETYPE;
        }
        if(entities != null && entities.length > 0) {
            bits |= FL_ENTITIES;
        }
        if(map != null) {
            bits |= FL_GAMEMAP;
        }
        if(stats != null) {
            bits |= FL_GAMESTATS;
        }
        if(mapDestructables != null && mapDestructables.length > 0) {
            bits |= FL_DESTRUCTABLES;
        }
        
        buffer.putByte(bits);
        
        if(gameType != null) {
            buffer.putByteBits(gameType.type, Type.numOfBits());
            gameType.write(buffer);
        }
        if(entities != null && entities.length > 0) {
            bitArray.clear();
            
            for(int i = 0; i < entities.length; i++) {
                if(entities[i]!=null) {
                    bitArray.setBit(i);
                }
            }
            
            byte[] data = bitArray.getData();
            for(int i = 0; i < data.length; i++) {
                buffer.putByte(data[i]);
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
        if(mapDestructables != null) {
            mapDestructables.write(buffer);
        }
    }
}
