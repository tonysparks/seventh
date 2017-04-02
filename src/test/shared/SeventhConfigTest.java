package test.shared;

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
	
	/*
	 * Purpose: invalid Root node
	 * Input: SeventhConfig ("./assets/client_config.leola","invalid")
	 * Expected: 
	 * 			throw Exception
	 */
	@Test(expected=Exception.class)
	public void testInvalidRootNode() throws Exception {
		SeventhConfig seventhconfig = new SeventhConfig(validPath,invalidRootNode);
	}
	
	/*
	 * Purpose: valid seventhConfig construct
	 * Input: SeventhConfig ("./assets/client_config.leola","client_config")
	 * Expected: 
	 * 			new SeventhConfig
	 */
	@Test
	public void testSeventhConfigConstructor() throws Exception{
		SeventhConfig seventhconfig = new SeventhConfig(validPath,validRootNode);
		assertTrue(seventhconfig instanceof SeventhConfig);
	}
}
