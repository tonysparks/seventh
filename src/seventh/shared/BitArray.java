/*
 * see license.txt 
 */
package seventh.shared;


/**
 * @author Tony
 *
 */
public class BitArray {

    private static final int WORD_SIZE = 4 * 8;
    
    private int[] data;
    /**
     * 
     */
    public BitArray(int size) {
        this.data = new int[size / 32 + 1];
    }
    
    private int bitIndex(int b) {
        return b / WORD_SIZE; 
    }
    
    private int bitOffset(int b) {
        return b % WORD_SIZE;
    }
    
    public void setBit(int b) {
        data[bitIndex(b)] |= 1 << (bitOffset(b));
    }
    
    public boolean getBit(int b) {
        return (data[bitIndex(b)] & (1 << (bitOffset(b)))) != 0;
    }

    public void setDataElement(int i, int data) {
        this.data[i] = data;
    }
    
    /**
     * @return the data
     */
    public int[] getData() {
        return data;
    }
    
    public void clear() {
        for(int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }
        
    public void setAll() {
        for(int i = 0; i < data.length; i++) {
            data[i] = 0xFFffFFff;
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
    
    public int numberOfInts() {
        return this.data.length;
    }
    
    public int numberOfBytes() {
        return this.data.length * 4;
    }
    
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
