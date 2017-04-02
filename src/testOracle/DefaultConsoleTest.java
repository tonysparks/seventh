package testOracle;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import seventh.shared.Command;
import seventh.shared.Console;
import seventh.shared.DefaultConsole;
import seventh.shared.Logger;
import seventh.shared.SysOutLogger;
import seventh.shared.TimeStep;

public class DefaultConsoleTest{
	/*
	 * Purpose: add command to the Command's map
	 * Input: null
	 * Expected: 
	 * 			IllegalArgumentException
	 */
	
	@Test(expected=IllegalArgumentException.class)
	public void addCommandTest1()  throws IllegalArgumentException {
		DefaultConsole test = new DefaultConsole();
		test.addCommand(null);		
	}
	/*
	 * Purpose: add command to the Command's map
	 * Input: Command(null)
	 * Expected: 
	 * 			IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void addCommandTest2()  throws IllegalArgumentException {
		DefaultConsole test = new DefaultConsole();
		test.addCommand(new Command(null) {            
            @Override
            public void execute(Console console, String... args) {
            }
        });	
	}
	
	/*
	 * Purpose: add command to the Command's map
	 * Input: String and null
	 * Expected: 
	 * 			IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void addCommandTest3()  throws IllegalArgumentException {
		DefaultConsole test = new DefaultConsole();
		test.addCommand("test",null);
	}
	

	/*
	 * Purpose: add command to the Command's map
	 * Input: Command
	 * Expected: 
	 * 			the added Command's map
	 */
	@Test
	public void addCommandTest4(){
		DefaultConsole test = new DefaultConsole();
		
		test.addCommand(new Command("test") {            
        @Override
        public void execute(Console console, String... args) {
	            }
        });

	Command cmd = test.getCommand("test");
	assertEquals("test",cmd.getName());		
	}
	

	/*
	 * Purpose: get command in the Command's map by Command's name
	 * Input: String
	 * Expected: 
	 * 			the Command having the name
	 */
	@Test
	public void getCommandTest() {
		DefaultConsole test = new DefaultConsole();
		Command DefaultCmd = test.getCommand("cmdlist");
		assertEquals("cmdlist",DefaultCmd.getName());
		DefaultCmd = test.getCommand("sleep");
		assertEquals("sleep",DefaultCmd.getName());
				
		test.addCommand(new Command("test") {            
            @Override
            public void execute(Console console, String... args) {
		            }
	        });

		Command cmd = test.getCommand("test");
		assertEquals("test",cmd.getName());		
	}
}

