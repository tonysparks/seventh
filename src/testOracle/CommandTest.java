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
	
}
