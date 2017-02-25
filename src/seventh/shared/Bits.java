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

}
