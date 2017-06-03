/*
 * see license.txt 
 */
package seventh.game.net;

import java.util.List;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.SoundEventPool;
import seventh.game.events.SoundEmittedEvent;
import seventh.math.Vector2f;
import seventh.shared.Bits;
import seventh.shared.SeventhConstants;
import seventh.shared.SoundType;

/**
 * Break sounds up into categories: 
 * 
 * 1) ones that are just spawned at a location
 * 2) others that are associated with an Entity (can be attached, so
 *    that the sound moves with the entity)
 * 3) global, ones that are heard always 
 * 
 * @author Tony
 *
 */
public class NetSound implements NetMessage {    
    public byte type;    
    
    public int posX, posY;
    private boolean hasPositionalInformation;
    
    private static final short TILE_WIDTH = 32;
    private static final short TILE_HEIGHT = 32;
    
    public NetSound() {
    }
    
    /**
     * @param pos
     */
    public NetSound(Vector2f pos) {
        this.setPos(pos);
        this.enablePosition();
    }
    
    public void setPos(Vector2f pos) {
        this.posX = (int)pos.x;
        this.posY = (int)pos.y;
    }
    
    public void setSoundType(SoundType soundType) {
        if(this.hasPositionalInformation) {
            this.type = Bits.setSignBit(soundType.netValue());
        }
        else {
            this.type = soundType.netValue();
        }
    }
    
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        /* type = buffer.get(); 
         * 
         * This is actually done by the readNetSound factory
         * method
         * */
        
        if(hasPositionalInformation()) {
            posX = (buffer.get() & 0xFF);
            posX *= TILE_WIDTH;
            
            posY = (buffer.get() & 0xFF);
            posY *= TILE_WIDTH;
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        if(this.hasPositionalInformation) {
            this.type = Bits.setSignBit(this.type);
        }
        
        buffer.put(type);
        
        if(this.hasPositionalInformation) {
            buffer.put( (byte)(posX/TILE_WIDTH) );
            buffer.put( (byte)(posY/TILE_HEIGHT) );
        }
    }
    
    /**
     * @return the {@link SoundType}
     */
    public SoundType getSoundType() {
        return SoundType.fromNet(Bits.getWithoutSignBit(type));
    }
    
    /**
     * Determines if this {@link NetSound} has positional (x,y)
     * information
     * 
     * @return the hasPositionalInformation
     */
    public boolean hasPositionalInformation() {
        return Bits.isSignBitSet(this.type);
    }
    
    /**
     * Enable the positional information of this NetSound
     */
    public void enablePosition() {
        this.hasPositionalInformation = true;
    }
    
    /**
     * Converts the {@link SoundEmittedEvent} into a {@link NetSound}
     * @param event
     */
    public static NetSound toNetSound(SoundEmittedEvent event) {
        NetSound sound = null;
        switch(event.getSoundType().getSourceType()) {
            case POSITIONAL: {        
                sound = new NetSound(event.getPos());
                break;
            }
            case REFERENCED: 
            case REFERENCED_ATTACHED: {
                sound = new NetSoundByEntity(event.getPos(), event.getEntityId());
                break;
            }
            default: sound = new NetSound();
        }
        
        sound.setSoundType(event.getSoundType());
        
        return sound;
    }
    
    public static NetSound readNetSound(IOBuffer buffer) {
        NetSound snd = null;
        byte type = buffer.get();
        switch(SoundType.fromNet(Bits.getWithoutSignBit(type)).getSourceType()) {
            case REFERENCED:
            case REFERENCED_ATTACHED:
                snd = new NetSoundByEntity();
                snd.type = type;
                snd.read(buffer);
                break;
            case POSITIONAL:
                snd = new NetSound();
                snd.type = type;
                snd.read(buffer);
                break;
            case GLOBAL:
                snd = new NetSound();
                snd.type = type;
                snd.read(buffer);
                break;
            default: throw new IllegalArgumentException("Invalid NetSound type: " + type);
        }
        
        
        
        return snd;
    }
    
    
    /**
     * Consolidates the {@link List} of {@link SoundEmittedEvent}'s, which means it will 
     * remove any duplicates
     * 
     * @param sounds
     * @return the list of {@link NetSound}s
     */
    public static NetSound[] consolidateToNetSounds(List<SoundEmittedEvent> sounds) {        
        
        int sum = 0;
        int size = sounds.size();
        SoundEmittedEvent[] buffer = new SoundEmittedEvent[SeventhConstants.MAX_SOUNDS];
        
        for(int i = 0; i < size; i++) {
            SoundEmittedEvent sndEvent = sounds.get(i);
            if(buffer[sndEvent.getBufferIndex()] == null) {
                buffer[sndEvent.getBufferIndex()] = sndEvent;        
                sum++;
            }
        }
        
        NetSound[] snds = new NetSound[sum];
        int index = 0;
        for(int i = 0; i < buffer.length; i++) {
            SoundEmittedEvent sndEvent = buffer[i];
            if(sndEvent != null) {
                snds[index++] = NetSound.toNetSound(sndEvent);
            }
        }
        
        return snds;
    }
    
    /**
     * @param sounds
     * @return converts the List of {@link SoundEmittedEvent} to the respective {@link NetSound} array
     */
    public static NetSound[] toNetSounds(List<SoundEmittedEvent> sounds) {        
        int size = sounds.size();
        
        NetSound[] snds = new NetSound[size];
        for(int i = 0; i < size; i++) {
            snds[i] = NetSound.toNetSound(sounds.get(i));
        }
        
        return snds;
    }
    
    /**
     * @param sounds
     * @return converts the List of {@link SoundEmittedEvent} to the respective {@link NetSound} array
     */
    public static NetSound[] toNetSounds(SoundEventPool sounds) {
        int size = sounds.numberOfSounds();
        
        NetSound[] snds = new NetSound[size];
        for(int i = 0; i < size; i++) {
            snds[i] = NetSound.toNetSound(sounds.getSound(i));
        }
        
        return snds;
    }
    
    
}
