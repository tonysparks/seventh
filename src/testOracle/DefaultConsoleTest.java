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
	
}

