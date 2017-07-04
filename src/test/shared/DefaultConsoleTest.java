package test.shared;

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
     *             IllegalArgumentException
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
     *             IllegalArgumentException
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
     *             IllegalArgumentException
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
     *             the added Command's map
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
     *             the Command having the name
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
    
    
    /*
     * Purpose: remove command in the Command's map by Command's name
     * Input: String
     * Expected: 
     *             the removed Command's map
     */
    @Test(expected=NullPointerException.class)
    public void removeCommandTest() throws NullPointerException {
        DefaultConsole test = new DefaultConsole();
        test.addCommand(new Command("test") {            
            @Override
            public void execute(Console console, String... args) {
               }
        });
        Command cmd = test.getCommand("test");
        assertEquals("test",cmd.getName());
        test.removeCommand("test");
        cmd = test.getCommand("test");
        assertEquals(null,cmd.getName());    
    }
    
    /*
     * Purpose: remove command in the Command's map by Command
     * Input: Command
     * Expected: 
     *             the removed Command's map
     */
    @Test(expected=NullPointerException.class)
    public void removeCommandTest2() throws NullPointerException {
        DefaultConsole test = new DefaultConsole();
        test.addCommand(new Command("test") {            
            @Override
            public void execute(Console console, String... args) {
               }
        });
        Command cmd = test.getCommand("test");
        assertEquals("test",cmd.getName());
        test.removeCommand((Command)null);
        test.removeCommand(cmd);
        cmd = test.getCommand("test");
        assertEquals(null,cmd.getName());    
    }
    

    /*
     * Purpose: find command in the Command's map by partial Command's name
     * Input: String
     * Expected: 
     *             the command
     */
    @Test
    public void findTest() {
        DefaultConsole test = new DefaultConsole();
        List<String> expected = new ArrayList<String>();
        expected.addAll(test.find("cmd"));
        Iterator<String> index = expected.iterator();
        assertEquals("cmdlist",index.next());        
    }
    

    /*
     * Purpose: find command in the Command's map by partial Command's name
     * Input: null
     * Expected: 
     *             NoSuchElementException
     */
    @Test(expected=NoSuchElementException.class)
    public void findTest2() throws NoSuchElementException {
        DefaultConsole test = new DefaultConsole();
        List<String> expected = new ArrayList<String>();
        expected.addAll(test.find(null));
        Iterator<String> index = expected.iterator();
        index.next();    
        
    }
    
}

