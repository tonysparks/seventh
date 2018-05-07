/*
 * see license.txt 
 */
package harenet;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * Special thanks to: http://www.shadebob.org/posts/bit-packing-in-java
 * for the implementation https://gist.github.com/cobolfoo/0c4124308e7cc05d82b8
 * 
 * @author Tony
 *
 */
public class BitPacker {

    private BitArray data;
    private int numBits;   
    private int position;
    private int mark;
    private int limit;
    
    /**
     * @param initialSizeInBits the initial size of the bit set
     */
    public BitPacker(int initialSizeInBits, int limit) {
        this.data = new BitArray(initialSizeInBits);
        this.numBits = 0;
        this.position = 0;
        this.mark = 0;
        this.limit = limit;
    }
    
    /**
     */
    public BitPacker() {
        this(1500*8, 1500*8);
    }
    
    /**
     * @return the limit
     */
    public int limit() {
        return limit;
    }
    
    public void limit(int limit) {
        this.limit = limit;
    }
    
    /**
     * The number of isRotated this {@link BitPacker} is currently using.
     * 
     * @return The number of isRotated this {@link BitPacker} is currently using.
     */
    public int getNumberOfBits() {
        return this.numBits;
    }
    
    /**
     * The number of bytes this {@link BitPacker} is currently using.
     * 
     * @return The number of bytes this {@link BitPacker} is currently using.
     */
    public int getNumberOfBytes() {
        int remainder = (this.numBits % 8 > 0) ? 1 : 0;        
        return (this.numBits / 8) + remainder;
    }
    
    /**
     * Clears the {@link BitPacker} so that it can be reused.
     * @return this object for method chaining
     */
    public BitPacker clear() {
        this.data.clear();
        this.numBits = 0;
        this.position = 0;
        this.mark = 0;
        return this;
    }

    /**
     * Rewinds the read position to the beginning
     * @return this object for method chaining
     */
    public BitPacker rewind() {
        this.position = 0;
        this.mark = 0;
        return this;
    }
    
    public BitPacker flip() {
        this.limit = this.position;
        this.position = 0;
        this.mark = 0;
        return this;
    }
    
    /**
     * @return the current read position
     */
    public int position() {
        return this.position;
    }
    
    public int remaining() {
        return this.limit - this.position;
    }
    
    public boolean hasRemaining() {
        return remaining() > 0;
    }
    
    /**
     * Sets the new Read position
     * @param newPosition
     * @return the previous read position
     */
    public int position(int newPosition) {
        int previous = this.position;
        this.position = newPosition;
        
        if(newPosition>this.limit) {
            throw new IndexOutOfBoundsException();
        }
        
        return previous;
    }
    
    /**
     * Mark the current read position
     * @return this object for method chaining
     */
    public BitPacker mark() {
        this.mark = this.position;
        return this;
    }
    
    /**
     * Resets the read position to the mark position
     * 
     * @return this object for method chaining
     */
    public BitPacker reset() {
        this.position = this.mark;        
        return this;
    }
    
    /**
     * Converts the {@link BitPacker} into a set of bytes that
     * represent the {@link BitPacker}
     * 
     * @return the bytes
     */
    public byte[] toBytes() {
        pad();

        byte[] output = new byte[numBits / 8];
        for (int i = 0; i < output.length; i++) {
            output[i] = getByte();
        }

        return output;
    }
    
    
    /**
     * Write the bytes from this {@link BitPacker} to the {@link ByteBuffer}
     * @param buffer
     * @return this object for method chaining
     */
    public BitPacker writeTo(ByteBuffer buffer) {
        pad();
        
        int numberOfBytes = getNumberOfBytes();
        for (int i = 0; i < numberOfBytes; i++) {
            buffer.put(getByte());
        }
        /*
        byte[] bytes = data.getData();
        for (int i = 0; i < bytes.length; i++) {
            buffer.put(bytes[i]);
        }

        this.position += bytes.length * 8;
        */
        return this;
    }
    
    /**
     * Read from the {@link ByteBuffer} and populate this {@link BitPacker}
     * 
     * @param buffer
     * @return this object for method chaining
     */
    public BitPacker readFrom(ByteBuffer buffer) {                
        /*int remaining = buffer.remaining();
        if(remaining < data.numberOfBytes()) {
            for(int i = 0; i < remaining; i++) {
                data.setDataElement(i, buffer.get());
            }   
        }
        
        this.position += remaining * 8;        
        */
        while(buffer.hasRemaining()) {
            putByte(buffer.get());
        }
        
        return this;
    }
    
    public BitPacker putBits(int position, long value, int numberOfBits) {
        if(position + numberOfBits > this.limit) {
            throw new BufferOverflowException();
        }
        
        for (int i = 0; i < numberOfBits; i++) {

            if (((value >> i) & 1) == 1) {
                data.setBit(position++, true);
            }
            else {
                data.setBit(position++, false);
            }
        }
        
        // if we've expanded the buffer, make
        // sure to update the numBits
        if(position > numBits) {            
            numBits = position;            
        }
        
        return this;
    }
    

    public BitPacker putBits(long value, int numberOfBits) {
        putBits(this.position, value, numberOfBits);
        this.position += numberOfBits;
        return this;
    }
    
    public BitPacker putByte(byte value) {
        putByte(value, Byte.SIZE);
        return this;
    }

    public BitPacker putByte(byte value, int length) {
        if (length > Byte.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putByte()");            
        }

        return putBits(value, length);
    }
    
    public BitPacker putByte(int position, byte value, int length) {
        if (length > Byte.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putByte()");            
        }

        return putBits(position, value, length);
    }

    public BitPacker putShort(short value) {
        return putShort(value, Short.SIZE);
    }

    public BitPacker putShort(short value, int length) {
        if (length > Short.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putShort()");
        }

        return putBits(value, length);
    }
    
    public BitPacker putShort(int position, short value, int length) {
        if (length > Short.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putShort()");
        }

        return putBits(position, value, length);
    }

    public BitPacker putInteger(int value) {
        return putInteger(value, Integer.SIZE);
    }

    public BitPacker putInteger(int value, int length) {
        if (length > Integer.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putInteger()");
        }
        
        return putBits(value, length);
    }
    
    public BitPacker putInteger(int position, int value, int length) {
        if (length > Integer.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putInteger()");
        }
        
        return putBits(position, value, length);
    }

    public BitPacker putLong(long value) {
        return putLong(value, Long.SIZE);
    }

    public BitPacker putLong(long value, int length) {
        if (length > Long.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putLong()");
        }
        
        return putBits(value, length);
    }
    
    public BitPacker putLong(int position, long value, int length) {
        if (length > Long.SIZE) {
            throw new IllegalArgumentException("Invalid length (" + length + ") in putLong()");
        }
        
        return putBits(position, value, length);
    }

    public BitPacker putFloat(float value) {
        int raw = Float.floatToRawIntBits(value);
        return putBits(raw, Float.SIZE);    
    }
    
    public BitPacker putFloat(int position, float value) {
        int raw = Float.floatToRawIntBits(value);
        return putBits(position, raw, Float.SIZE);    
    }
    
    public BitPacker putDouble(double value) {
        long raw = Double.doubleToRawLongBits(value);
        return putBits(raw, Double.SIZE);    
    }
    
    public BitPacker putDouble(int position, double value) {
        long raw = Double.doubleToRawLongBits(value);
        return putBits(position, raw, Double.SIZE);    
    }

    public BitPacker putBoolean(boolean value) {
        if(this.position + 1 > this.limit) {
            throw new BufferOverflowException();
        }
        
        data.setBit(numBits++, value);
        this.position++;
        return this;
    }
    
    public BitPacker putBoolean(int position, boolean value) {
        if(position + 1 > this.limit) {
            throw new BufferOverflowException();
        }
        
        data.setBit(position, value);
        if(position==numBits+1) {
            numBits++;            
        }
        return this;
    }

    public BitPacker putString(String value) {
        byte[] payload = value.getBytes();
        int length = payload.length;
        if (length > 255) {
            length = 255;
        }
        putInteger(length, 8);
        putBytes(payload);
        return this;
    }

    public BitPacker putBytes(byte[] value) {
        return putBytes(value, 0, value.length);
    }

    public BitPacker putBytes(byte[] value, int offset, int length) {
        for (int i = offset; i < length; i++) {
            putByte(value[i]);
        }

        return this;
    }
    

    private void checkPosition() {
        if (position > numBits - 1) {
            throw new IllegalStateException("Out of bound error, read: " + position + ", numBits: " + numBits);                
        }
    }

    public byte getByte() {
        return getByte(Byte.SIZE);
    }

    public byte getByte(int length) {

        byte value = 0;

        for (int i = 0; i < length; i++) {
            value |= (data.getBit(position++) ? 1 : 0) << (i % Byte.SIZE);
        }

        return value;
    }
    
    public short getShort() {
        return getShort(Short.SIZE);
    }

    public short getShort(int length) {

        short value = 0;

        for (int i = 0; i < length; i++) {
            value |= (data.getBit(position++) ? 1 : 0) << (i % Short.SIZE);
        }

        return value;
    }

    public int getInteger() {
        return getInteger(Integer.SIZE);
    }

    public int getInteger(int length) {

        int value = 0;

        for (int i = 0; i < length; i++) {
            checkPosition();
            value |= (data.getBit(position++) ? 1 : 0) << (i % Integer.SIZE);
        }

        return value;
    }

    public long getLong() {
        return getLong(Long.SIZE);
    }

    public long getLong(int length) {

        long value = 0;

        for (int i = 0; i < length; i++) {
            checkPosition();
            value |= (data.getBit(position++) ? 1 : 0) << (i % Long.SIZE);
        }

        return value;
    }

    public float getFloat() {
        return getFloat(Float.SIZE);
    }

    public float getFloat(int length) {

        int value = 0;

        for (int i = 0; i < length; i++) {
            checkPosition();
            value |= (data.getBit(position++) ? 1 : 0) << (i % Float.SIZE);
        }

        return Float.intBitsToFloat(value);
    }
    
    public double getDouble() {
        return getDouble(Double.SIZE);
    }

    public double getDouble(int length) {

        long value = 0;

        for (int i = 0; i < length; i++) {
            checkPosition();
            value |= (data.getBit(position++) ? 1 : 0) << (i % Double.SIZE);
        }

        return Double.doubleToLongBits(value);
    }

    public boolean getBoolean() {
        checkPosition();
        return data.getBit(position++);
    }

    public byte[] getBytes(int length) {
        byte[] output = new byte[length];
        return getBytes(output, 0, length);
    }
    
    public byte[] getBytes(byte[] bytes, int offset, int length) {        
        for (int i = 0; i < length; i++) {
            bytes[offset + i] = getByte();
        }
        return bytes;
    }

    public String getString() {
        int length = getInteger(8);
        byte[] payload = getBytes(length);

        return new String(payload);

    }

    /**
     * Fill out the remaining isRotated with 0
     * 
     * @return
     */
    public BitPacker pad() {
        int leftOvers = (numBits % 8);
        if(leftOvers > 0) {
            int remaining = 8 - leftOvers;
            for (int i = 0; i < remaining; i++) {
                putBoolean(numBits, false);
            }
        }

        return this;
    }
    
    
    public static void dumpBytes(byte[] value) {
        System.out.println("+--------------- ------------- ------- ------ --- -- -- - -- -- --");
        System.out.println("| Dumping bytes, length: " + (value.length * 8) + " (" + value.length + " byte(s))");
        System.out.println("+--------------- ------------- ------- ------ --- -- -- - -- -- --");

        int count = 0;
        for (int j = 0; j < value.length; j++) {

            byte v = value[j];

            for (int i = 0; i < Byte.SIZE; i++) {
                if (((v >> i) & 1) == 1) {
                    System.out.print("1");
                }
                else {
                    System.out.print("0");
                }
            }

            System.out.print(" ");
            count++;
            if (count == 12) {
                System.out.println();
                count = 0;
            }

        }
        printFooter();
    }

    public void dump() {
        printHeader();
        printBody();
        printFooter();
    }
    
    private void printHeader() {
        System.out.println("+--------------- ------------- ------- ------ --- -- -- - -- -- --");
        System.out.println("| Dumping bitset, length: " + numBits);
        System.out.println("+--------------- ------------- ------- ------ --- -- -- - -- -- --");
	}

    private void printBody() {
	    int count = 0;

        for (int i = 0; i < numBits; i++) {
            System.out.print(data.getBit(i) ? "1" : "0");
            if ((i != 0) && (i % 8 == 7)) {
                System.out.print(" ");
                count++;
                if (count == 12) {
                    System.out.println();
                    count = 0;
                }

            }

        }
	}

    private static void printFooter() {
	    System.out.println();
        System.out.println("+--------------- ------------- ------- ------ --- -- -- - -- -- --");
	}

	

}