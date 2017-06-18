/*
 * see license.txt 
 */
package harenet;

import java.nio.ByteBuffer;

/**
 * Just counts the bytes
 * 
 * @author Tony
 *
 */
public class ByteCounterIOBuffer implements IOBuffer {

    private int numberOfBits;
    
    /**
     * 
     */
    public ByteCounterIOBuffer() {
        this.numberOfBits = 0;
    }
    
    public ByteCounterIOBuffer(int size) {
        this.numberOfBits = size;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#receiveSync()
     */
    @Override
    public IOBuffer receiveSync() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#sync()
     */
    @Override
    public IOBuffer sendSync() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#asByteBuffer()
     */
    @Override
    public ByteBuffer asByteBuffer() {    
        return null;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#slice()
     */
    @Override
    public IOBuffer slice() {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#duplicate()
     */
    @Override
    public IOBuffer duplicate() {
        return new ByteCounterIOBuffer(this.numberOfBits);
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#asReadOnlyBuffer()
     */
    @Override
    public IOBuffer asReadOnlyBuffer() {
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getUnsignedByte()
     */
    @Override
    public int getUnsignedByte() {    
        return 0;
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#putUnsignedByte(int)
     */
    @Override
    public IOBuffer putUnsignedByte(int b) {
        this.numberOfBits+=Byte.SIZE;
        return this;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#get()
     */
    @Override
    public byte getByte() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte)
     */
    @Override
    public IOBuffer putByte(byte b) {
        this.numberOfBits+=Byte.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(int)
     */
    @Override
    public byte getByte(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(int, byte)
     */
    @Override
    public IOBuffer putByte(int index, byte b) {
        this.numberOfBits += Byte.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(byte[], int, int)
     */
    @Override
    public IOBuffer getBytes(byte[] dst, int offset, int length) {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(byte[])
     */
    @Override
    public IOBuffer getBytes(byte[] dst) {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(netspark.IOBuffer)
     */
    @Override
    public IOBuffer put(IOBuffer src) {
        this.numberOfBits += src.remaining() * 8;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte[], int, int)
     */
    @Override
    public IOBuffer putBytes(byte[] src, int offset, int length) {
        this.numberOfBits += length * 8;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte[])
     */
    @Override
    public IOBuffer putBytes(byte[] src) {
        this.numberOfBits += src.length * 8;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#hasArray()
     */
    @Override
    public boolean hasArray() {
        return false;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#array()
     */
    @Override
    public byte[] array() {
        return null;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#arrayOffset()
     */
    @Override
    public int arrayOffset() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#compact()
     */
    @Override
    public IOBuffer compact() {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#isDirect()
     */
    @Override
    public boolean isDirect() {
        return false;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#capacity()
     */
    @Override
    public int capacity() {
        int leftOver = this.numberOfBits % 8;
        if(leftOver > 0) {
            return (this.numberOfBits / 8) + 1;
        }
        return this.numberOfBits / 8;        
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#position()
     */
    @Override
    public int position() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#position(int)
     */
    @Override
    public IOBuffer position(int newPosition) {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#limit()
     */
    @Override
    public int limit() {
        return capacity();
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#limit(int)
     */
    @Override
    public IOBuffer limit(int newLimit) {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#mark()
     */
    @Override
    public IOBuffer mark() {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#reset()
     */
    @Override
    public IOBuffer reset() {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#clear()
     */
    @Override
    public IOBuffer clear() {
        this.numberOfBits = 0;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#flip()
     */
    @Override
    public IOBuffer flip() {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#rewind()
     */
    @Override
    public IOBuffer rewind() {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#remaining()
     */
    @Override
    public int remaining() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#hasRemaining()
     */
    @Override
    public boolean hasRemaining() {
        return false;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#getShort()
     */
    @Override
    public short getShort() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putShort(short)
     */
    @Override
    public IOBuffer putShort(short value) {
        this.numberOfBits += Short.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getShort(int)
     */
    @Override
    public short getShort(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putShort(int, short)
     */
    @Override
    public IOBuffer putShort(int index, short value) {
        this.numberOfBits += Short.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getInt()
     */
    @Override
    public int getInt() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putInt(int)
     */
    @Override
    public IOBuffer putInt(int value) {
        this.numberOfBits += Integer.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getInt(int)
     */
    @Override
    public int getInt(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putInt(int, int)
     */
    @Override
    public IOBuffer putInt(int index, int value) {
        this.numberOfBits += Integer.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getLong()
     */
    @Override
    public long getLong() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putLong(long)
     */
    @Override
    public IOBuffer putLong(long value) {
        this.numberOfBits += Long.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getLong(int)
     */
    @Override
    public long getLong(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putLong(int, long)
     */
    @Override
    public IOBuffer putLong(int index, long value) {
        this.numberOfBits += Long.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getFloat()
     */
    @Override
    public float getFloat() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putFloat(float)
     */
    @Override
    public IOBuffer putFloat(float value) {
        this.numberOfBits += Float.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getFloat(int)
     */
    @Override
    public float getFloat(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putFloat(int, float)
     */
    @Override
    public IOBuffer putFloat(int index, float value) {
        this.numberOfBits += Float.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getDouble()
     */
    @Override
    public double getDouble() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putDouble(double)
     */
    @Override
    public IOBuffer putDouble(double value) {
        this.numberOfBits += Double.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getDouble(int)
     */
    @Override
    public double getDouble(int index) {        
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putDouble(int, double)
     */
    @Override
    public IOBuffer putDouble(int index, double value) {
        this.numberOfBits += Double.SIZE;
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putBooleanBit(boolean)
     */
    @Override
    public IOBuffer putBooleanBit(boolean value) {
        this.numberOfBits ++;
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putLongBits(long, int)
     */
    @Override
    public IOBuffer putLongBits(long value, int numberOfBits) {
        this.numberOfBits += numberOfBits;
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putByteBits(byte, int)
     */
    @Override
    public IOBuffer putByteBits(byte value, int numberOfBits) {
        this.numberOfBits += numberOfBits;
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putShortBits(short, int)
     */
    @Override
    public IOBuffer putShortBits(short value, int numberOfBits) {
        this.numberOfBits += numberOfBits;
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#putIntBits(int, int)
     */
    @Override
    public IOBuffer putIntBits(int value, int numberOfBits) {
        this.numberOfBits += numberOfBits;
        return this;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getBooleanBit()
     */
    @Override
    public boolean getBooleanBit() {
        return false;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getByteBits()
     */
    @Override
    public byte getByteBits() {
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getByteBits(int)
     */
    @Override
    public byte getByteBits(int numberOfBits) {
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getShortBits()
     */
    @Override
    public short getShortBits() {
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getShortBits(int)
     */
    @Override
    public short getShortBits(int numberOfBits) {
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getIntBits()
     */
    @Override
    public int getIntBits() {
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#getIntBits(int)
     */
    @Override
    public int getIntBits(int numberOfBits) {
        return 0;
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#getLongBits()
     */
    @Override
    public long getLongBits() {     
        return 0;
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#getLongBits(int)
     */
    @Override
    public long getLongBits(int numberOfBits) {     
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#bitPosition()
     */
    @Override
    public int bitPosition() {
        return 0;
    }
    
    /* (non-Javadoc)
     * @see harenet.IOBuffer#bitPosition(int)
     */
    @Override
    public int bitPosition(int position) {     
        return 0;
    }

    /* (non-Javadoc)
     * @see harenet.IOBuffer#bitCapacity()
     */
    @Override
    public int bitCapacity() {
        return capacity() * 8;
    }

}
