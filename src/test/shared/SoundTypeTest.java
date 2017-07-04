package test.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.SoundType;
import seventh.shared.SoundType.SoundSourceType;

public class SoundTypeTest {

    /*
     * Purpose: check M1_GARAND_LAST_FIRE SoundType value number(valid)
     * Input: SoundType.fromNet (byte)9
     * Expected: 
     *             M1_GARAND_LAST_FIRE is 10th value (starting number 0)
     */
    @Test
    public void testValidSoundType() {
        final SoundType expectedSoundType = SoundType.M1_GARAND_LAST_FIRE;
        assertEquals(expectedSoundType,SoundType.fromNet((byte) 9));
    }
    
    /*
     * Purpose: check M1_GARAND_LAST_FIRE SoundType value number(invalid)
     * Input: SoundType.fromNet (byte)14
     * Expected: 
     *             M1_GARAND_LAST_FIRE SoundType is (byte)9
     *             (byte)14 is SPRINGFIELD_RECHAMBER
     */
    @Test
    public void testInvalidSoundType() {
        final SoundType expectedSoundType = SoundType.M1_GARAND_LAST_FIRE;
        assertNotEquals(expectedSoundType,SoundType.fromNet((byte) 14));
    }
    
    /*
     * Purpose: MUTE Sound Source Type value when number over length of value
     * Input: SoundType.fromNet (byte)127 > SoundType value count
     * Expected: 
     *             Sound Source Type is MUTE
     */
    @Test
    public void testMuteOverValueLength() {
        final SoundType expected = SoundType.MUTE;
        byte typeSize = (byte) 127;
        assertTrue(typeSize > SoundType.values().length);
        assertEquals(expected,SoundType.fromNet(typeSize));
    }
    
    /*
     * Purpose: MUTE Sound Source Type value when number under 0
     * Input: SoundType.fromNet (byte)240 < 0
     * Expected: 
     *             Sound Source Type is MUTE
     */
    @Test
    public void testMuteUnderZero() {
        final SoundType expected = SoundType.MUTE;
        byte typeSize = (byte) 240;
        assertTrue(typeSize < 0);
        assertEquals(expected,SoundType.fromNet(typeSize));
    }
}
