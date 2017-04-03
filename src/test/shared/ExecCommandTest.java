package test.shared;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.Logger;
import seventh.shared.Command;
import seventh.shared.Console;
import seventh.shared.DefaultConsole;
import seventh.shared.ExecCommand;

public class ExecCommandTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * Test case for ExecCommand
	 * Check the file path and read it 
	 */
	@Test
	public void testExecute() {
		ExecCommand EC = new ExecCommand();
		Console console = new DefaultConsole();
		EC.execute(console, "new file");
		EC.execute(console, "/Users/hyeongjukim/Desktop/new.txt");
	}

	
}
