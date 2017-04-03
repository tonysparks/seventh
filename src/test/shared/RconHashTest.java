package test.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.RconHash;

public class RconHashTest {

	/*
	 * Purpose: Rconhash hashing
	 * Input: RconHash token 10, hash hello world
	 * Expected: 
	 * 			hashing string = 52uLz5GE4rvH/QG44Mjaseol6L3l7eMpWLcIT/58zds=
	 */
	@Test
	public void testMinMaxScope(){
		final String expected = "52uLz5GE4rvH/QG44Mjaseol6L3l7eMpWLcIT/58zds=";
		RconHash rconhash = new RconHash(10);
		assertEquals(expected,rconhash.hash("hello world"));
	}
}
