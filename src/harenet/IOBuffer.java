/*
 * see license.txt 
 */
package harenet;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;

/**
 * IO Buffer
 * 
 * @author Tony
 *
 */
public interface IOBuffer {
	
	/**
	 * Simple factory methods
	 * 
	 * @author Tony
	 *
	 */
	public static class Factory {
		public static IOBuffer allocateDirect(int size) {
			return new ByteBufferIOBuffer(ByteBuffer.allocateDirect(size));
		}
		public static IOBuffer allocate(int size) {
			return new ByteBufferIOBuffer(ByteBuffer.allocate(size));
		}
		public static IOBuffer wrap(byte[] buff) {
			return new ByteBufferIOBuffer(ByteBuffer.wrap(buff));
		}
		public static IOBuffer wrap(byte[] buff, int offset, int len) {
			return new ByteBufferIOBuffer(ByteBuffer.wrap(buff, offset, len));
		}
	}

	/**
	 * @return the underlying {@link ByteBuffer}
	 */
	public ByteBuffer asByteBuffer();
		
	 /**
     * Creates a new byte buffer whose content is a shared subsequence of
     * this buffer's content.
     *
     * <p> The content of the new buffer will start at this buffer's current
     * position.  Changes to this buffer's content will be visible in the new
     * buffer, and vice versa; the two buffers' position, limit, and mark
     * values will be independent.
     *
     * <p> The new buffer's position will be zero, its capacity and its limit
     * will be the number of bytes remaining in this buffer, and its mark
     * will be undefined.  The new buffer will be direct if, and only if, this
     * buffer is direct, and it will be read-only if, and only if, this buffer
     * is read-only.  </p>
     *
     * @return  The new byte buffer
     */
    public abstract IOBuffer slice();

    /**
     * Creates a new byte buffer that shares this buffer's content.
     *
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer, and vice
     * versa; the two buffers' position, limit, and mark values will be
     * independent.
     *
     * <p> The new buffer's capacity, limit, position, and mark values will be
     * identical to those of this buffer.  The new buffer will be direct if,
     * and only if, this buffer is direct, and it will be read-only if, and
     * only if, this buffer is read-only.  </p>
     *
     * @return  The new byte buffer
     */
    public abstract IOBuffer duplicate();

    /**
     * Creates a new, read-only byte buffer that shares this buffer's
     * content.
     *
     * <p> The content of the new buffer will be that of this buffer.  Changes
     * to this buffer's content will be visible in the new buffer; the new
     * buffer itself, however, will be read-only and will not allow the shared
     * content to be modified.  The two buffers' position, limit, and mark
     * values will be independent.
     *
     * <p> The new buffer's capacity, limit, position, and mark values will be
     * identical to those of this buffer.
     *
     * <p> If this buffer is itself read-only then this method behaves in
     * exactly the same way as the {@link #duplicate duplicate} method.  </p>
     *
     * @return  The new, read-only byte buffer
     */
    public abstract IOBuffer asReadOnlyBuffer();


    // -- Singleton get/put methods --

    /**
     * Relative <i>get</i> method.  Reads the byte at this buffer's
     * current position, and then increments the position. </p>
     *
     * @return  The byte at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If the buffer's current position is not smaller than its limit
     */
    public abstract byte get();
    
    
    public abstract int getUnsignedByte();
    public abstract IOBuffer putUnsignedByte(int b);

    /**
     * Relative <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given byte into this buffer at the current
     * position, and then increments the position. </p>
     *
     * @param  b
     *         The byte to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If this buffer's current position is not smaller than its limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer put(byte b);

    /**
     * Absolute <i>get</i> method.  Reads the byte at the given
     * index. </p>
     *
     * @param  index
     *         The index from which the byte will be read
     *
     * @return  The byte at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit
     */
    public abstract byte get(int index);

    /**
     * Absolute <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given byte into this buffer at the given
     * index. </p>
     *
     * @param  index
     *         The index at which the byte will be written
     *
     * @param  b
     *         The byte value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer put(int index, byte b);


    // -- Bulk get operations --

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p> This method transfers bytes from this buffer into the given
     * destination array.  If there are fewer bytes remaining in the
     * buffer than are required to satisfy the request, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>, then no
     * bytes are transferred and a {@link BufferUnderflowException} is
     * thrown.
     *
     * <p> Otherwise, this method copies <tt>length</tt> bytes from this
     * buffer into the given array, starting at the current position of this
     * buffer and at the given offset in the array.  The position of this
     * buffer is then incremented by <tt>length</tt>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>src.get(dst,&nbsp;off,&nbsp;len)</tt> has exactly the same effect as
     * the loop
     *
     * <pre>
     *     for (int i = off; i < off + len; i++)
     *         dst[i] = src.get(); </pre>
     *
     * except that it first checks that there are sufficient bytes in
     * this buffer and it is potentially much more efficient. </p>
     *
     * @param  dst
     *         The array into which bytes are to be written
     *
     * @param  offset
     *         The offset within the array of the first byte to be
     *         written; must be non-negative and no larger than
     *         <tt>dst.length</tt>
     *
     * @param  length
     *         The maximum number of bytes to be written to the given
     *         array; must be non-negative and no larger than
     *         <tt>dst.length - offset</tt>
     *
     * @return  This buffer
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than <tt>length</tt> bytes
     *          remaining in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>offset</tt> and <tt>length</tt>
     *          parameters do not hold
     */
    public IOBuffer get(byte[] dst, int offset, int length);

    /**
     * Relative bulk <i>get</i> method.
     *
     * <p> This method transfers bytes from this buffer into the given
     * destination array.  An invocation of this method of the form
     * <tt>src.get(a)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     src.get(a, 0, a.length) </pre>
     *
     * @return  This buffer
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than <tt>length</tt> bytes
     *          remaining in this buffer
     */
    public IOBuffer get(byte[] dst);


    // -- Bulk put operations --

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the bytes remaining in the given source
     * buffer into this buffer.  If there are more bytes remaining in the
     * source buffer than in this buffer, that is, if
     * <tt>src.remaining()</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>,
     * then no bytes are transferred and a {@link
     * BufferOverflowException} is thrown.
     *
     * <p> Otherwise, this method copies
     * <i>n</i>&nbsp;=&nbsp;<tt>src.remaining()</tt> bytes from the given
     * buffer into this buffer, starting at each buffer's current position.
     * The positions of both buffers are then incremented by <i>n</i>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>dst.put(src)</tt> has exactly the same effect as the loop
     *
     * <pre>
     *     while (src.hasRemaining())
     *         dst.put(src.get()); </pre>
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient. </p>
     *
     * @param  src
     *         The source buffer from which bytes are to be read;
     *         must not be this buffer
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *          for the remaining bytes in the source buffer
     *
     * @throws  IllegalArgumentException
     *          If the source buffer is this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public IOBuffer put(IOBuffer src);
    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers bytes into this buffer from the given
     * source array.  If there are more bytes to be copied from the array
     * than remain in this buffer, that is, if
     * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>, then no
     * bytes are transferred and a {@link BufferOverflowException} is
     * thrown.
     *
     * <p> Otherwise, this method copies <tt>length</tt> bytes from the
     * given array into this buffer, starting at the given offset in the array
     * and at the current position of this buffer.  The position of this buffer
     * is then incremented by <tt>length</tt>.
     *
     * <p> In other words, an invocation of this method of the form
     * <tt>dst.put(src,&nbsp;off,&nbsp;len)</tt> has exactly the same effect as
     * the loop
     *
     * <pre>
     *     for (int i = off; i < off + len; i++)
     *         dst.put(a[i]); </pre>
     *
     * except that it first checks that there is sufficient space in this
     * buffer and it is potentially much more efficient. </p>
     *
     * @param  src
     *         The array from which bytes are to be read
     *
     * @param  offset
     *         The offset within the array of the first byte to be read;
     *         must be non-negative and no larger than <tt>array.length</tt>
     *
     * @param  length
     *         The number of bytes to be read from the given array;
     *         must be non-negative and no larger than
     *         <tt>array.length - offset</tt>
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If the preconditions on the <tt>offset</tt> and <tt>length</tt>
     *          parameters do not hold
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public IOBuffer put(byte[] src, int offset, int length);

    /**
     * Relative bulk <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> This method transfers the entire content of the given source
     * byte array into this buffer.  An invocation of this method of the
     * form <tt>dst.put(a)</tt> behaves in exactly the same way as the
     * invocation
     *
     * <pre>
     *     dst.put(a, 0, a.length) </pre>
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there is insufficient space in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public IOBuffer put(byte[] src);


    // -- Other stuff --

    /**
     * Tells whether or not this buffer is backed by an accessible byte
     * array.
     *
     * <p> If this method returns <tt>true</tt> then the {@link #array() array}
     * and {@link #arrayOffset() arrayOffset} methods may safely be invoked.
     * </p>
     *
     * @return  <tt>true</tt> if, and only if, this buffer
     *          is backed by an array and is not read-only
     */
    public  boolean hasArray();

    /**
     * Returns the byte array that backs this
     * buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Modifications to this buffer's content will cause the returned
     * array's content to be modified, and vice versa.
     *
     * <p> Invoke the {@link #hasArray hasArray} method before invoking this
     * method in order to ensure that this buffer has an accessible backing
     * array.  </p>
     *
     * @return  The array that backs this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is backed by an array but is read-only
     *
     * @throws  UnsupportedOperationException
     *          If this buffer is not backed by an accessible array
     */
    public  byte[] array() ;

    /**
     * Returns the offset within this buffer's backing array of the first
     * element of the buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> If this buffer is backed by an array then buffer position <i>p</i>
     * corresponds to array index <i>p</i>&nbsp;+&nbsp;<tt>arrayOffset()</tt>.
     *
     * <p> Invoke the {@link #hasArray hasArray} method before invoking this
     * method in order to ensure that this buffer has an accessible backing
     * array.  </p>
     *
     * @return  The offset within this buffer's array
     *          of the first element of the buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is backed by an array but is read-only
     *
     * @throws  UnsupportedOperationException
     *          If this buffer is not backed by an accessible array
     */
    public int arrayOffset();

    /**
     * Compacts this buffer&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> The bytes between the buffer's current position and its limit,
     * if any, are copied to the beginning of the buffer.  That is, the
     * byte at index <i>p</i>&nbsp;=&nbsp;<tt>position()</tt> is copied
     * to index zero, the byte at index <i>p</i>&nbsp;+&nbsp;1 is copied
     * to index one, and so forth until the byte at index
     * <tt>limit()</tt>&nbsp;-&nbsp;1 is copied to index
     * <i>n</i>&nbsp;=&nbsp;<tt>limit()</tt>&nbsp;-&nbsp;<tt>1</tt>&nbsp;-&nbsp;<i>p</i>.
     * The buffer's position is then set to <i>n+1</i> and its limit is set to
     * its capacity.  The mark, if defined, is discarded.
     *
     * <p> The buffer's position is set to the number of bytes copied,
     * rather than to zero, so that an invocation of this method can be
     * followed immediately by an invocation of another relative <i>put</i>
     * method. </p>
     *

     *
     * <p> Invoke this method after writing data from a buffer in case the
     * write was incomplete.  The following loop, for example, copies bytes
     * from one channel to another via the buffer <tt>buf</tt>:
     *
     * <blockquote><pre>
     * buf.clear();          // Prepare buffer for use
     * while (in.read(buf) >= 0 || buf.position != 0) {
     *     buf.flip();
     *     out.write(buf);
     *     buf.compact();    // In case of partial write
     * }</pre></blockquote>
     *

     *
     * @return  This buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer compact();

    /**
     * Tells whether or not this byte buffer is direct. </p>
     *
     * @return  <tt>true</tt> if, and only if, this buffer is direct
     */
    public abstract boolean isDirect();

    /**
     * Returns this buffer's capacity. </p>
     *
     * @return  The capacity of this buffer
     */
    public  int capacity();

    /**
     * Returns this buffer's position. </p>
     *
     * @return  The position of this buffer
     */
    public  int position();

    /**
     * Sets this buffer's position.  If the mark is defined and larger than the
     * new position then it is discarded. </p>
     *
     * @param  newPosition
     *         The new position value; must be non-negative
     *         and no larger than the current limit
     *
     * @return  This buffer
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on <tt>newPosition</tt> do not hold
     */
    public  IOBuffer position(int newPosition);

    /**
     * Returns this buffer's limit. </p>
     *
     * @return  The limit of this buffer
     */
    public  int limit();

    /**
     * Sets this buffer's limit.  If the position is larger than the new limit
     * then it is set to the new limit.  If the mark is defined and larger than
     * the new limit then it is discarded. </p>
     *
     * @param  newLimit
     *         The new limit value; must be non-negative
     *         and no larger than this buffer's capacity
     *
     * @return  This buffer
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on <tt>newLimit</tt> do not hold
     */
    public  IOBuffer limit(int newLimit) ;

    /**
     * Sets this buffer's mark at its position. </p>
     *
     * @return  This buffer
     */
    public  IOBuffer mark();

    /**
     * Resets this buffer's position to the previously-marked position.
     *
     * <p> Invoking this method neither changes nor discards the mark's
     * value. </p>
     *
     * @return  This buffer
     *
     * @throws  InvalidMarkException
     *          If the mark has not been set
     */
    public  IOBuffer reset();

    /**
     * Clears this buffer.  The position is set to zero, the limit is set to
     * the capacity, and the mark is discarded.
     *
     * <p> Invoke this method before using a sequence of channel-read or
     * <i>put</i> operations to fill this buffer.  For example:
     *
     * <blockquote><pre>
     * buf.clear();     // Prepare buffer for reading
     * in.read(buf);    // Read data</pre></blockquote>
     *
     * <p> This method does not actually erase the data in the buffer, but it
     * is named as if it did because it will most often be used in situations
     * in which that might as well be the case. </p>
     *
     * @return  This buffer
     */
    public  IOBuffer clear();

    /**
     * Flips this buffer.  The limit is set to the current position and then
     * the position is set to zero.  If the mark is defined then it is
     * discarded.
     *
     * <p> After a sequence of channel-read or <i>put</i> operations, invoke
     * this method to prepare for a sequence of channel-write or relative
     * <i>get</i> operations.  For example:
     *
     * <blockquote><pre>
     * buf.put(magic);    // Prepend header
     * in.read(buf);      // Read data into rest of buffer
     * buf.flip();        // Flip buffer
     * out.write(buf);    // Write header + data to channel</pre></blockquote>
     *
     * <p> This method is often used in conjunction with the {@link
     * java.nio.IOBuffer#compact compact} method when transferring data from
     * one place to another.  </p>
     *
     * @return  This buffer
     */
    public  IOBuffer flip();

    /**
     * Rewinds this buffer.  The position is set to zero and the mark is
     * discarded.
     *
     * <p> Invoke this method before a sequence of channel-write or <i>get</i>
     * operations, assuming that the limit has already been set
     * appropriately.  For example:
     *
     * <blockquote><pre>
     * out.write(buf);    // Write remaining data
     * buf.rewind();      // Rewind buffer
     * buf.get(array);    // Copy data into array</pre></blockquote>
     *
     * @return  This buffer
     */
    public  IOBuffer rewind();

    /**
     * Returns the number of elements between the current position and the
     * limit. </p>
     *
     * @return  The number of elements remaining in this buffer
     */
    public  int remaining();

    /**
     * Tells whether there are any elements between the current position and
     * the limit. </p>
     *
     * @return  <tt>true</tt> if, and only if, there is at least one element
     *          remaining in this buffer
     */
    public boolean hasRemaining();

    /**
     * Tells whether or not this buffer is read-only. </p>
     *
     * @return  <tt>true</tt> if, and only if, this buffer is read-only
     */
    public abstract boolean isReadOnly();
    
    
    
    /**
     * Relative <i>get</i> method for reading a char value.
     *
     * <p> Reads the next two bytes at this buffer's current position,
     * composing them into a char value according to the current byte order,
     * and then increments the position by two.  </p>
     *
     * @return  The char value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     */
    public abstract char getChar();

    /**
     * Relative <i>put</i> method for writing a char
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given char value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by two.  </p>
     *
     * @param  value
     *         The char value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putChar(char value);

    /**
     * Absolute <i>get</i> method for reading a char value.
     *
     * <p> Reads two bytes at the given index, composing them into a
     * char value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The char value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     */
    public abstract char getChar(int index);

    /**
     * Absolute <i>put</i> method for writing a char
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given char value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The char value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putChar(int index, char value);

    


    /**
     * Relative <i>get</i> method for reading a short value.
     *
     * <p> Reads the next two bytes at this buffer's current position,
     * composing them into a short value according to the current byte order,
     * and then increments the position by two.  </p>
     *
     * @return  The short value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     */
    public abstract short getShort();

    /**
     * Relative <i>put</i> method for writing a short
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given short value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by two.  </p>
     *
     * @param  value
     *         The short value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putShort(short value);

    /**
     * Absolute <i>get</i> method for reading a short value.
     *
     * <p> Reads two bytes at the given index, composing them into a
     * short value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The short value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     */
    public abstract short getShort(int index);

    /**
     * Absolute <i>put</i> method for writing a short
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given short value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The short value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putShort(int index, short value);


    /**
     * Relative <i>get</i> method for reading an int value.
     *
     * <p> Reads the next four bytes at this buffer's current position,
     * composing them into an int value according to the current byte order,
     * and then increments the position by four.  </p>
     *
     * @return  The int value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     */
    public abstract int getInt();

    /**
     * Relative <i>put</i> method for writing an int
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given int value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by four.  </p>
     *
     * @param  value
     *         The int value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putInt(int value);

    /**
     * Absolute <i>get</i> method for reading an int value.
     *
     * <p> Reads four bytes at the given index, composing them into a
     * int value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The int value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus three
     */
    public abstract int getInt(int index);

    /**
     * Absolute <i>put</i> method for writing an int
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given int value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The int value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus three
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putInt(int index, int value);


    /**
     * Relative <i>get</i> method for reading a long value.
     *
     * <p> Reads the next eight bytes at this buffer's current position,
     * composing them into a long value according to the current byte order,
     * and then increments the position by eight.  </p>
     *
     * @return  The long value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than eight bytes
     *          remaining in this buffer
     */
    public abstract long getLong();

    /**
     * Relative <i>put</i> method for writing a long
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given long value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by eight.  </p>
     *
     * @param  value
     *         The long value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than eight bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putLong(long value);

    /**
     * Absolute <i>get</i> method for reading a long value.
     *
     * <p> Reads eight bytes at the given index, composing them into a
     * long value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The long value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus seven
     */
    public abstract long getLong(int index);

    /**
     * Absolute <i>put</i> method for writing a long
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given long value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The long value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus seven
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putLong(int index, long value);



    /**
     * Relative <i>get</i> method for reading a float value.
     *
     * <p> Reads the next four bytes at this buffer's current position,
     * composing them into a float value according to the current byte order,
     * and then increments the position by four.  </p>
     *
     * @return  The float value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     */
    public abstract float getFloat();

    /**
     * Relative <i>put</i> method for writing a float
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given float value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by four.  </p>
     *
     * @param  value
     *         The float value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putFloat(float value);

    /**
     * Absolute <i>get</i> method for reading a float value.
     *
     * <p> Reads four bytes at the given index, composing them into a
     * float value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The float value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus three
     */
    public abstract float getFloat(int index);

    /**
     * Absolute <i>put</i> method for writing a float
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given float value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The float value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus three
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putFloat(int index, float value);

  
    /**
     * Relative <i>get</i> method for reading a double value.
     *
     * <p> Reads the next eight bytes at this buffer's current position,
     * composing them into a double value according to the current byte order,
     * and then increments the position by eight.  </p>
     *
     * @return  The double value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than eight bytes
     *          remaining in this buffer
     */
    public abstract double getDouble();

    /**
     * Relative <i>put</i> method for writing a double
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given double value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by eight.  </p>
     *
     * @param  value
     *         The double value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than eight bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putDouble(double value);

    /**
     * Absolute <i>get</i> method for reading a double value.
     *
     * <p> Reads eight bytes at the given index, composing them into a
     * double value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The double value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus seven
     */
    public abstract double getDouble(int index);

    /**
     * Absolute <i>put</i> method for writing a double
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given double value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The double value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>index</tt> is negative
     *          or not smaller than the buffer's limit,
     *          minus seven
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public abstract IOBuffer putDouble(int index, double value);

  

}
