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

    private int numberOfBytes;
    /**
     * 
     */
    public ByteCounterIOBuffer() {
        this.numberOfBytes = 0;
    }
    
    public ByteCounterIOBuffer(int size) {
        this.numberOfBytes = size;
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
        return new ByteCounterIOBuffer(this.numberOfBytes);
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
        numberOfBytes++;
        return this;
    }
    
    /* (non-Javadoc)
     * @see netspark.IOBuffer#get()
     */
    @Override
    public byte get() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte)
     */
    @Override
    public IOBuffer put(byte b) {
        numberOfBytes++;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(int)
     */
    @Override
    public byte get(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(int, byte)
     */
    @Override
    public IOBuffer put(int index, byte b) {
        numberOfBytes += 1;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(byte[], int, int)
     */
    @Override
    public IOBuffer get(byte[] dst, int offset, int length) {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#get(byte[])
     */
    @Override
    public IOBuffer get(byte[] dst) {
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(netspark.IOBuffer)
     */
    @Override
    public IOBuffer put(IOBuffer src) {
        numberOfBytes += src.remaining();
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte[], int, int)
     */
    @Override
    public IOBuffer put(byte[] src, int offset, int length) {
        numberOfBytes += length;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#put(byte[])
     */
    @Override
    public IOBuffer put(byte[] src) {
        numberOfBytes += src.length;
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
        return numberOfBytes;
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
        return numberOfBytes;
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
        numberOfBytes = 0;
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
     * @see netspark.IOBuffer#getChar()
     */
    @Override
    public char getChar() {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putChar(char)
     */
    @Override
    public IOBuffer putChar(char value) {
        numberOfBytes += 2;
        return this;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#getChar(int)
     */
    @Override
    public char getChar(int index) {
        return 0;
    }

    /* (non-Javadoc)
     * @see netspark.IOBuffer#putChar(int, char)
     */
    @Override
    public IOBuffer putChar(int index, char value) {
        numberOfBytes += 2;
        return this;
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
        numberOfBytes += 2;
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
        numberOfBytes += 2;
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
        numberOfBytes += 4;
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
        numberOfBytes += 4;
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
        numberOfBytes += 8;
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
        numberOfBytes += 8;
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
        numberOfBytes += 4;
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
        numberOfBytes += 4;
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
        numberOfBytes += 8;
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
        numberOfBytes += 8;
        return this;
    }

}
