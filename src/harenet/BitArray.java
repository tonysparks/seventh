/*
 * see license.txt 
 */
package harenet;

import seventh.shared.SeventhConstants;

/**
 * Stores bits
 * 
 * @author Tony
 *
 */
public class BitArray {

    private static final int WORD_SIZE = 8;
    
    private byte[] data;
    
    /**
     * @param maxNumberOfBits
     */
    public BitArray(int maxNumberOfBits) {
        int leftOver = maxNumberOfBits % WORD_SIZE;
        this.data = new byte[(maxNumberOfBits / WORD_SIZE) + ((leftOver>0) ? 1 : 0)];
    }
    
    private int bitIndex(int b) {
        return b / WORD_SIZE; 
    }
    
    private int bitOffset(int b) {
        return b % WORD_SIZE;
    }
    
    /**
     * Sets the bit (is zero based)
     * 
     * @param b
     * @param isSet
     */
    public void setBit(int b, boolean isSet) {
        if(isSet) {
            data[bitIndex(b)] |= 1 << (bitOffset(b));
        }
        else {
            data[bitIndex(b)] &= ~(1 << (bitOffset(b)));
        }
    }
    
    /**
     * Sets the bit (is zero based)
     * 
     * @param b
     */
    public void setBit(int b) {
        data[bitIndex(b)] |= 1 << (bitOffset(b));
    }
    
    /**
     * Zero based index
     * 
     * @param b
     * @return true if the bit is set
     */
    public boolean getBit(int b) {
        return (data[bitIndex(b)] & (1 << (bitOffset(b)))) != 0;
    }

    /**
     * Set the data directly
     * 
     * @param i
     * @param data
     */
    public void setDataElement(int i, byte data) {
        this.data[i] = data;
    }
    
    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
    
    public void clear() {
        for(int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }
        
    public void setAll() {
        for(int i = 0; i < data.length; i++) {
            data[i] = 0xFFFFFFFF;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < data.length; i++) {
            sb.append(Integer.toBinaryString(data[i]));
            sb.append(" ");
        }
        
        return sb.toString();
    }
        
    /**
     * The number of bytes to represent the number of bits
     * 
     * @return The number of bytes to represent the number of bits
     */
    public int numberOfBytes() {
        return this.data.length;
    }
    
    /**
     * The number of bits used in this bit array
     * 
     * @return the number of bits
     */
    public int size() {
        return this.data.length * WORD_SIZE;
    }
    
    public static void main(String[] args) {
        BitArray a = new BitArray(255);
        
//        int[] ents = new int[256];
        for(int i = 0; i < 255; i++) {
            a.setBit(i);
        }
        
        System.out.println("NumberOfBytes:" + a.numberOfBytes() + " : " + a.size() + " : " + a);
        
        BitArray b = new BitArray(SeventhConstants.MAX_PERSISTANT_ENTITIES - 1);
        
//        int[] ents = new int[256];
        for(int i = 2; i < b.size(); i++) {
            b.setBit(i);
        }
        
        System.out.println("NumberOfBytes:" + b.numberOfBytes() + " : " + b.size() + " : " + b);
    }
}
