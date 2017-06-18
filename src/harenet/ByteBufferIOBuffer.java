/*
 * see license.txt 
 */
package harenet;

import java.nio.ByteBuffer;

/**
 * An IOBuffer backed by a {@link ByteBuffer} 
 * 
 * @author Tony
 *
 */
public class ByteBufferIOBuffer implements IOBuffer {

    private ByteBuffer buffer;
    private BitPacker packer;
        
    /**
     */
    public ByteBufferIOBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
        this.packer = new BitPacker(this.buffer.capacity() * 8, buffer.limit() * 8);
        
        // syncs what's in the ByteBuffer to the BitPacker
        syncBuffer();
    }
    
    /**
     * @param size
     */
    public ByteBufferIOBuffer(int size) {
        this.buffer = ByteBuffer.allocate(size);
        this.packer = new BitPacker(this.buffer.capacity() * 8, size * 8);
    }

    private void syncBuffer() {                      
        buffer.clear();
        
        int oldPosition = packer.position(0);
        packer.writeTo(buffer);
        packer.position(oldPosition);
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#receiveSync()
     */
    @Override
    public IOBuffer receiveSync() {

        buffer.mark();
        {
            packer.clear();
            packer.readFrom(buffer);
            packer.flip();
        }
        buffer.reset();
        
        return this;

    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#sync()
     */
    @Override
    public IOBuffer sendSync() {        
        syncBuffer();        
        return this;
    }
    
    /*
     * (non-Javadoc)
     * @see harenet.IOBuffer#asByteBuffer()
     */
    @Override
    public ByteBuffer asByteBuffer() {        
        return buffer;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#slice()
     */
    @Override
    public IOBuffer slice() {
        syncBuffer();
        return new ByteBufferIOBuffer(buffer.slice());
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#duplicate()
     */
    @Override
    public IOBuffer duplicate() {
        syncBuffer();
        return new ByteBufferIOBuffer(buffer.duplicate());
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#asReadOnlyBuffer()
     */
    @Override
    public IOBuffer asReadOnlyBuffer() {
        syncBuffer();
        return new ByteBufferIOBuffer(buffer.asReadOnlyBuffer());
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getUnsignedByte()
     */
    @Override
    public int getUnsignedByte() {    
        return packer.getByte() & 0xFF;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#get()
     */
    @Override
    public byte getByte() {
        return packer.getByte();
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#putUnsignedByte(int)
     */
    @Override
    public IOBuffer putUnsignedByte(int b) {            
        packer.putByte( (byte) b);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte)
     */
    @Override
    public IOBuffer putByte(byte b) {        
        packer.putByte(b);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(int)
     */
    @Override
    public byte getByte(int index) {
        packer.mark();
        packer.position(index * 8);
        byte result = packer.getByte();
        packer.reset();
        return result;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(int, byte)
     */
    @Override
    public IOBuffer putByte(int index, byte b) {
        packer.putBits(index * 8, b, Byte.SIZE);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(byte[], int, int)
     */
    @Override
    public IOBuffer getBytes(byte[] dst, int offset, int length) {
        packer.getBytes(dst, offset, length);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(byte[])
     */
    @Override
    public IOBuffer getBytes(byte[] dst) {
        packer.getBytes(dst, 0, dst.length);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(netspark.IOBuffer)
     */
    @Override
    public IOBuffer put(IOBuffer src) {
        //buffer.put(src.array(), src.arrayOffset(), src.capacity());
        //buffer.put(src.asByteBuffer());
        //ByteBuffer buffer = src.asByteBuffer();
        // TODO: Think about this more
        packer.putBytes(buffer.array(), buffer.arrayOffset(), buffer.capacity());
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte[], int, int)
     */
    @Override
    public IOBuffer putBytes(byte[] src, int offset, int length) {
        //buffer.put(src, offset, length);
        packer.putBytes(src, offset, length);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte[])
     */
    @Override
    public IOBuffer putBytes(byte[] src) {
        packer.putBytes(src);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#hasArray()
     */
    @Override
    public boolean hasArray() {
        return buffer.hasArray();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#array()
     */
    @Override
    public byte[] array() {
        syncBuffer();
        return buffer.array();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#arrayOffset()
     */
    @Override
    public int arrayOffset() {
        return buffer.arrayOffset();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#compact()
     */
    @Override
    public IOBuffer compact() {
        syncBuffer();
        buffer.compact();        
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#isDirect()
     */
    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#capacity()
     */
    @Override
    public int capacity() {
        return buffer.capacity();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#position()
     */
    @Override
    public int position() {
        int leftOver = packer.position() % 8;
        if(leftOver>0) {
            return (packer.position() / 8) + 1;
        }
        
        return (packer.position() / 8);
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#position(int)
     */
    @Override
    public IOBuffer position(int newPosition) {
        packer.position(newPosition * 8);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#limit()
     */
    @Override
    public int limit() {
        int leftOver = packer.limit() % 8;
        if(leftOver>0) {
            return (packer.limit() / 8) + 1;
        }
        
        return (packer.limit() / 8);
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#limit(int)
     */
    @Override
    public IOBuffer limit(int newLimit) {
        packer.limit(newLimit * 8);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#mark()
     */
    @Override
    public IOBuffer mark() {
        packer.mark();
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#reset()
     */
    @Override
    public IOBuffer reset() {
        packer.reset();
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#clear()
     */
    @Override
    public IOBuffer clear() {
        packer.clear();
        packer.limit(buffer.capacity() * 8);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#flip()
     */
    @Override
    public IOBuffer flip() {
        packer.flip();
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#rewind()
     */
    @Override
    public IOBuffer rewind() {
        packer.rewind();
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#remaining()
     */
    @Override
    public int remaining() {
        int leftOver = packer.remaining() % 8;
        if(leftOver>0) {
            return (packer.remaining() / 8) + 1;
        }
        
        return (packer.remaining() / 8);
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#hasRemaining()
     */
    @Override
    public boolean hasRemaining() {
        return packer.hasRemaining();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return buffer.isReadOnly();
    }


    /* (non-Javadoc)
     * @see netspark.IOBuffer#getShort()
     */
    @Override
    public short getShort() {    
        return packer.getShort();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putShort(short)
     */
    @Override
    public IOBuffer putShort(short value) {
        packer.putShort(value);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getShort(int)
     */
    @Override
    public short getShort(int index) {
        packer.mark();
        packer.position(index * 8);
        short result = packer.getShort();
        packer.reset();
        return result;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putShort(int, short)
     */
    @Override
    public IOBuffer putShort(int index, short value) {
        packer.putShort(index, value, Short.SIZE);
        return this;
    }

    

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getInt()
     */
    @Override
    public int getInt() {
        return packer.getInteger();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putInt(int)
     */
    @Override
    public IOBuffer putInt(int value) {
        packer.putInteger(value);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getInt(int)
     */
    @Override
    public int getInt(int index) {
        packer.mark();
        packer.position(index * 8);
        int result = packer.getInteger();
        packer.reset();
        return result;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putInt(int, int)
     */
    @Override
    public IOBuffer putInt(int index, int value) {
        packer.putInteger(index, value, Integer.SIZE);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getLong()
     */
    @Override
    public long getLong() {
        return packer.getLong();
        
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putLong(long)
     */
    @Override
    public IOBuffer putLong(long value) {
        packer.putLong(value);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getLong(int)
     */
    @Override
    public long getLong(int index) {
        packer.mark();
        packer.position(index * 8);
        long result = packer.getLong();
        packer.reset();
        return result;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putLong(int, long)
     */
    @Override
    public IOBuffer putLong(int index, long value) {
        packer.putLong(index, value, Long.SIZE);
        return this;
    }


    /* (non-Javadoc)
     * @see netspark.IOBuffer#getFloat()
     */
    @Override
    public float getFloat() {        
        return packer.getFloat();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putFloat(float)
     */
    @Override
    public IOBuffer putFloat(float value) {
        packer.putFloat(value);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getFloat(int)
     */
    @Override
    public float getFloat(int index) {
        packer.mark();
        packer.position(index * 8);
        float result = packer.getFloat();
        packer.reset();
        return result;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putFloat(int, float)
     */
    @Override
    public IOBuffer putFloat(int index, float value) {
        packer.putFloat(index, value);
        return this;
    }


    /* (non-Javadoc)
     * @see netspark.IOBuffer#getDouble()
     */
    @Override
    public double getDouble() {
        return packer.getDouble();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putDouble(double)
     */
    @Override
    public IOBuffer putDouble(double value) {
        packer.putDouble(value);
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getDouble(int)
     */
    @Override
    public double getDouble(int index) {
        packer.mark();
        packer.position(index * 8);
        double result = packer.getDouble();
        packer.reset();
        return result;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putDouble(int, double)
     */
    @Override
    public IOBuffer putDouble(int index, double value) {
        packer.putDouble(index, value);
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putBooleanBit(boolean)
     */
    @Override
    public IOBuffer putBooleanBit(boolean value) {
        packer.putBoolean(value);
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putLongBits(long, int)
     */
    @Override
    public IOBuffer putLongBits(long value, int numberOfBits) {
        packer.putLong(value, numberOfBits);
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putByteBits(byte, int)
     */
    @Override
    public IOBuffer putByteBits(byte value, int numberOfBits) {
        packer.putByte(value, numberOfBits);
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putShortBits(short, int)
     */
    @Override
    public IOBuffer putShortBits(short value, int numberOfBits) {
        packer.putShort(value, numberOfBits);
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putIntBits(int, int)
     */
    @Override
    public IOBuffer putIntBits(int value, int numberOfBits) {
        packer.putInteger(value, numberOfBits);
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getBooleanBit()
     */
    @Override
    public boolean getBooleanBit() {
        return packer.getBoolean();
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getByteBits()
     */
    @Override
    public byte getByteBits() {
        return packer.getByte();
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getByteBits(int)
     */
    @Override
    public byte getByteBits(int numberOfBits) {
        return packer.getByte(numberOfBits);
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getShortBits()
     */
    @Override
    public short getShortBits() {
        return packer.getShort();
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getShortBits(int)
     */
    @Override
    public short getShortBits(int numberOfBits) {
        return packer.getShort(numberOfBits);
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getIntBits()
     */
    @Override
    public int getIntBits() {
        return packer.getInteger();
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getIntBits(int)
     */
    @Override
    public int getIntBits(int numberOfBits) {
        return packer.getInteger(numberOfBits);
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#getLongBits()
     */
    @Override
    public long getLongBits() {     
        return packer.getLong();
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#getLongBits(int)
     */
    @Override
    public long getLongBits(int numberOfBits) {     
        return packer.getLong(numberOfBits);
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#bitPosition(int)
     */
    @Override
    public int bitPosition(int position) {
        return packer.position(position);        
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#bitPosition()
     */
    @Override
    public int bitPosition() {
        return packer.position();
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#bitCapacity()
     */
    @Override
    public int bitCapacity() {
        return packer.getNumberOfBits();
    }

    
}
