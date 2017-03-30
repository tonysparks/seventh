package testOracle;

import static org.junit.Assert.*;
import org.junit.Test;

import seventh.shared.SeventhConfig;

public class SeventhConfigTest {
	final String validPath = "./assets/client_config.leola";
	final String inValidPath = "invalid";
	final String validRootNode = "client_config";
	final String invalidRootNode = "invalid";
	/*
	 * Purpose: invalid Path
	 * Input: SeventhConfig ("invalid", "client_config")
	 * Expected: 
	 * 			throw Exception
	 */
	@Test(expected=Exception.class)
	public void testInvalidPath() throws Exception {
		SeventhConfig seventhconfig = new SeventhConfig(inValidPath,validRootNode);
	}	
}
