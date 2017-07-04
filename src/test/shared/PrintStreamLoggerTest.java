package test.shared;

import static org.junit.Assert.*;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.PrintStreamLogger;

/*
 * Purpose: test PrintStreamLogger constructor
 * Input: PrintStream stream
 * Expected: 

 */


public class PrintStreamLoggerTest {
    
    private PrintStreamLogger printlogger;
    private PrintStream stream;

    @Before
    public void setUp() throws Exception {
        printlogger = new PrintStreamLogger(stream);
    }
    
    
    @Test
    public void ConstructTest(){
        printlogger = new PrintStreamLogger(stream);
    }




}
