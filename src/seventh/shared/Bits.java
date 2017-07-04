/*
 * see license.txt 
 */
package seventh.shared;

/**
 * Simple bit operations
 * 
 * @author Tony
 *
 */
public class Bits {
    
    /**
     * Set the sign bit of a byte
     * @param b
     * @return the byte with its sign bit set
     */
    public static byte setSignBit(byte b) {
        return (byte) (b | (byte) (1 << 7));
                            //10000000
    }

    /**
     * Checks to see if the sign bit is set on the byte
     * @param b
     * @return true if the sign bit is set
     */
    public static boolean isSignBitSet(byte b) {
        return ((b & (1 << 7)) > 0);
    }

    /**
     * Get the byte value ignoring the sign bit
     * @param b
     * @return the byte value ignoring the sign bit
     */
    public static byte getWithoutSignBit(byte b) {
        return (byte) (b & ~(1 << 7));
    }
    
    public static short setSignedShort(short value, int numberOfBits) {
        if(value < 0) {
            return (short) (value | (short) (1<<numberOfBits-1));
        }
        return value;
    }
    
    public static short getSignedShort(short value, int numberOfBits) {
        if((value & (1<<numberOfBits-1)) > 0) {
            
            int mask = 0xffFFffFF;
            mask >>>= Integer.SIZE - numberOfBits;
           
            return (short) -(mask - value);           
        }
        return value;
    }

}
