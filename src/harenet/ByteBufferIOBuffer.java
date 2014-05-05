/*
 * see license.txt 
 */
package harenet;

import java.nio.ByteBuffer;

/**
 * @author Tony
 *
 */
public class ByteBufferIOBuffer implements IOBuffer {

	private ByteBuffer buffer;
	/**
	 */
	public ByteBufferIOBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * @param size
	 */
	public ByteBufferIOBuffer(int size) {
		this.buffer = ByteBuffer.allocate(size);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#asByteBuffer()
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
		return new ByteBufferIOBuffer(buffer.slice());
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#duplicate()
	 */
	@Override
	public IOBuffer duplicate() {
		return new ByteBufferIOBuffer(buffer.duplicate());
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#asReadOnlyBuffer()
	 */
	@Override
	public IOBuffer asReadOnlyBuffer() {
		return new ByteBufferIOBuffer(buffer.asReadOnlyBuffer());
	}

	/* (non-Javadoc)
	 * @see harenet.IOBuffer#getUnsignedByte()
	 */
	@Override
	public int getUnsignedByte() {	
		return buffer.get() & 0xFF;
	}
	
	/* (non-Javadoc)
	 * @see netspark.IOBuffer#get()
	 */
	@Override
	public byte get() {
		return buffer.get();
	}
	
	/* (non-Javadoc)
	 * @see harenet.IOBuffer#putUnsignedByte(int)
	 */
	@Override
	public IOBuffer putUnsignedByte(int b) {	
		buffer.put( (byte) b);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#put(byte)
	 */
	@Override
	public IOBuffer put(byte b) {
		buffer.put(b);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#get(int)
	 */
	@Override
	public byte get(int index) {
		return buffer.get(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#put(int, byte)
	 */
	@Override
	public IOBuffer put(int index, byte b) {
		buffer.put(index, b);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#get(byte[], int, int)
	 */
	@Override
	public IOBuffer get(byte[] dst, int offset, int length) {
		buffer.get(dst, offset, length);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#get(byte[])
	 */
	@Override
	public IOBuffer get(byte[] dst) {
		buffer.get(dst);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#put(netspark.IOBuffer)
	 */
	@Override
	public IOBuffer put(IOBuffer src) {
		buffer.put(src.array(), src.arrayOffset(), src.capacity());
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#put(byte[], int, int)
	 */
	@Override
	public IOBuffer put(byte[] src, int offset, int length) {
		buffer.put(src, offset, length);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#put(byte[])
	 */
	@Override
	public IOBuffer put(byte[] src) {
		buffer.put(src);
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
		return buffer.position();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#position(int)
	 */
	@Override
	public IOBuffer position(int newPosition) {
		buffer.position(newPosition);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#limit()
	 */
	@Override
	public int limit() {
		return buffer.limit();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#limit(int)
	 */
	@Override
	public IOBuffer limit(int newLimit) {
		buffer.limit(newLimit);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#mark()
	 */
	@Override
	public IOBuffer mark() {
		buffer.mark();
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#reset()
	 */
	@Override
	public IOBuffer reset() {
		buffer.reset();
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#clear()
	 */
	@Override
	public IOBuffer clear() {
		buffer.clear();
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#flip()
	 */
	@Override
	public IOBuffer flip() {
		buffer.flip();
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#rewind()
	 */
	@Override
	public IOBuffer rewind() {
		buffer.rewind();
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#remaining()
	 */
	@Override
	public int remaining() {
		return buffer.remaining();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#hasRemaining()
	 */
	@Override
	public boolean hasRemaining() {
		return buffer.hasRemaining();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return buffer.isReadOnly();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getChar()
	 */
	@Override
	public char getChar() {
		return buffer.getChar();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putChar(char)
	 */
	@Override
	public IOBuffer putChar(char value) {
		buffer.putChar(value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getChar(int)
	 */
	@Override
	public char getChar(int index) {
		return buffer.getChar(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putChar(int, char)
	 */
	@Override
	public IOBuffer putChar(int index, char value) {
		buffer.putChar(index, value);
		return this;
	}

	

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getShort()
	 */
	@Override
	public short getShort() {	
		return buffer.getShort();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putShort(short)
	 */
	@Override
	public IOBuffer putShort(short value) {
		buffer.putShort(value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getShort(int)
	 */
	@Override
	public short getShort(int index) {
		return buffer.getShort(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putShort(int, short)
	 */
	@Override
	public IOBuffer putShort(int index, short value) {
		buffer.putShort(index, value);
		return this;
	}

	

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getInt()
	 */
	@Override
	public int getInt() {
		return buffer.getInt();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putInt(int)
	 */
	@Override
	public IOBuffer putInt(int value) {
		buffer.putInt(value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getInt(int)
	 */
	@Override
	public int getInt(int index) {
		return buffer.getInt(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putInt(int, int)
	 */
	@Override
	public IOBuffer putInt(int index, int value) {
		buffer.putInt(index, value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getLong()
	 */
	@Override
	public long getLong() {
		return buffer.getLong();
		
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putLong(long)
	 */
	@Override
	public IOBuffer putLong(long value) {
		buffer.putLong(value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getLong(int)
	 */
	@Override
	public long getLong(int index) {
		return buffer.getLong(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putLong(int, long)
	 */
	@Override
	public IOBuffer putLong(int index, long value) {
		buffer.putLong(index, value);
		return this;
	}


	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getFloat()
	 */
	@Override
	public float getFloat() {		
		return buffer.getFloat();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putFloat(float)
	 */
	@Override
	public IOBuffer putFloat(float value) {
		buffer.putFloat(value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getFloat(int)
	 */
	@Override
	public float getFloat(int index) {
		return buffer.getFloat(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putFloat(int, float)
	 */
	@Override
	public IOBuffer putFloat(int index, float value) {
		buffer.putFloat(index, value);
		return this;
	}


	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getDouble()
	 */
	@Override
	public double getDouble() {
		return buffer.getDouble();
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putDouble(double)
	 */
	@Override
	public IOBuffer putDouble(double value) {
		buffer.putDouble(value);
		return this;
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#getDouble(int)
	 */
	@Override
	public double getDouble(int index) {
		return buffer.getDouble(index);
	}

	/* (non-Javadoc)
	 * @see netspark.IOBuffer#putDouble(int, double)
	 */
	@Override
	public IOBuffer putDouble(int index, double value) {
		buffer.putDouble(index, value);
		return this;
	}

}
