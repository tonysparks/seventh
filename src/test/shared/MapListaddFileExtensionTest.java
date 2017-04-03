package test.shared;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.MapList;

public class MapListaddFileExtensionTest {
	private MapList maplist;

	
	/*
	 * Purpose: test addFileExtension(mapName). 
	 *          if it is not end with ".json" 
	 *          this function needs to add ".json" at the end of filename
	 * Input: fileName = "abc.json"   
	 * Expected: "abc.json"
	 */
	
	@Test
	public void addFileTest() {
		String mapName = "abc.json";
		String actual = maplist.addFileExtension(mapName);
		String expected = "abc.json";
		assertEquals(actual, expected);
	}

	
	
	/*
	 * Purpose: test other  file name
	 * Input: fileName = "abc.exe"   
	 * Expected: "abc.exe.json"
	 */
	
	@Test
	public void differentTypeTest() {
		String mapName = "abc.exe";
		String actual = maplist.addFileExtension(mapName);
		String expected = "abc.exe.json";
		assertEquals(actual, expected);
	}
	
	
	
	/*
	 * Purpose: test integer file name
	 * Input: fileName = "123"   
	 * Expected: "123.json"
	 */
	
	@Test
	public void integerFileNameTest() {
		String mapName = "123";
		String actual = maplist.addFileExtension(mapName);
		String expected = "123.json";
		assertEquals(actual, expected);
	}

	

	/*
	 * Purpose: test other file name
	 * Input: fileName = "123abc.exe"   
	 * Expected: "123abc.exe.json"
	 */
	
	@Test
	public void mixedFileNameTest() {
		String mapName = "123abc";
		String actual = maplist.addFileExtension(mapName);
		String expected = "123abc.json";
		assertEquals(actual, expected);
	}
	

	/*
	 * Purpose: test other type's file name
	 * Input: fileName = "abc.exe"   
	 * Expected: "abc.exe.json"
	 */
	
	@Test
	public void otherFileNameTest() {
		String mapName = "#43.";
		String actual = maplist.addFileExtension(mapName);
		String expected = "#43..json";
		assertEquals(actual, expected);
	}
	
	/*
	 * Purpose: test long type's file name
	 * Input: fileName = "aaabbbcccdddeeeeffffttttttttqewrqerqe"   
	 * Expected: "aaabbbcccdddeeeeffffttttttttqewrqerqe.json"
	 */
	
	@Test
	public void longFileNameTest() {
		String mapName = "aaabbbcccdddeeeeffffttttttttqewrqerqe";
		String actual = maplist.addFileExtension(mapName);
		String expected = "aaabbbcccdddeeeeffffttttttttqewrqerqe.json";
		assertEquals(actual, expected);
	}
	
	/*
	 * Purpose: test single file name
	 * Input: fileName = "aaabbbcccdddeeeeffffttttttttqewrqerqe"   
	 * Expected: "aaabbbcccdddeeeeffffttttttttqewrqerqe.json"
	 */
	
	@Test
	public void singleFileNameTest() {
		String mapName = "a";
		String actual = maplist.addFileExtension(mapName);
		String expected = "a.json";
		assertEquals(actual, expected);
	}
	

}
