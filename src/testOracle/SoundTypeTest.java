package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.SoundType;
import seventh.shared.SoundType.SoundSourceType;

public class SoundTypeTest {

	/*
	 * Purpose: check M1_GARAND_LAST_FIRE SoundType value number(valid)
	 * Input: SoundType.fromNet (byte)9
	 * Expected: 
	 * 			M1_GARAND_LAST_FIRE is 10th value (starting number 0)
	 */
	@Test
	public void testValidSoundType() {
		final SoundType expectedSoundType = SoundType.M1_GARAND_LAST_FIRE;
		assertEquals(expectedSoundType,SoundType.fromNet((byte) 9));
	}
	
}
