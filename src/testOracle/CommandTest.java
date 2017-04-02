package testOracle;


import static org.junit.Assert.*;

import org.junit.Test;
import seventh.shared.*;



public class CommandTest extends Command {

	public CommandTest() {
		super("expected");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute(Console console, String... args) {
		// TODO Auto-generated method stub		
	}
	/*
	 * Purpose: get the Command's name
	 * Input: 
	 * Expected: 
	 * 			the Command's name
	 */
	@Test
	public void getNameTest() {
		CommandTest test = new CommandTest();
		assertEquals("expected",test.getName());
	}
	
	/*
	 * Purpose: Merges the arguments into one string by index and delimiter
	 * Input: delimiter, index, Strings
	 * Expected: 
	 * 			the coalesced string
	 */
	@Test
	public void mergeArgsDelimAtTest() {
		CommandTest test = new CommandTest();
		assertEquals("expected",test.mergeArgsDelimAt("@",0,"expected"));
		assertEquals("expected@test",test.mergeArgsDelimAt("@",0,"expected","test"));
		assertEquals("test@item",test.mergeArgsDelimAt("@",1,"expected","test","item"));
		assertEquals("test/item/great",test.mergeArgsDelimAt("/",1,"expected","test","item","great"));
	}
	/*
	 * Purpose: Merges the arguments into one string by delimiter
	 * Input: delimiter, Strings
	 * Expected: 
	 * 			the coalesced string
	 */
	@Test
	public void mergeArgsDelimTest() {
		CommandTest test = new CommandTest();
		assertEquals("expected",test.mergeArgsDelim("@","expected"));
		assertEquals("expected@test",test.mergeArgsDelim("@","expected","test"));
		assertEquals("expected@test@item",test.mergeArgsDelim("@","expected","test","item"));
		assertEquals("expected/test/item/great",test.mergeArgsDelim("/","expected","test","item","great"));
	}
}
