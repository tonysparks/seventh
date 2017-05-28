/*
 * see license.txt 
 */
package seventh.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Tony
 *
 */
public class DefaultConsole implements Console {        
    static class QueuedExecution {
        String command;
        String[] args;
        
        public QueuedExecution(String command, String[] args) {        
            this.command = command;
            this.args = args;
        }
        
        
    }
    
    private Map<String, Command> commands;
    private List<Logger> loggers;
    private Queue<QueuedExecution> queuedCommands;
    
    private long sleepTime;
    
    /**
     * @param logger
     */
    public DefaultConsole(Logger logger) {
        this.commands = new HashMap<String, Command>();
        this.loggers = new ArrayList<Logger>();
        this.loggers.add(logger);
        this.queuedCommands = new ConcurrentLinkedQueue<>();
        
        
        addCommand(new Command("cmdlist") {            
            @Override
            public void execute(Console console, String... args) {
                console.println("\n");
                for(String commandName : commands.keySet()) {
                    console.println(commandName);
                }
            }
        });
        
        addCommand(new Command("sleep") {                    
            @Override
            public void execute(Console console, String... args) {
                try {
                    if(args.length != 1) {
                        console.println("<usage> sleep [msec]");
                    }
                    else {
                        sleepTime = Long.parseLong(args[0]);
                    }
                }
                catch(Exception e) {
                    console.println("*** Must be a valid long value");
                }
            }
        });
    }
    
    /**
     */
    public DefaultConsole() {
        this(new SysOutLogger());
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Console#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        if(sleepTime > 0) {
            sleepTime -= timeStep.getDeltaTime();
        }
        else {
        
            while(!this.queuedCommands.isEmpty()) {
                QueuedExecution exe = this.queuedCommands.poll();
                executeCommand(exe);
                
                /* if we encountered a sleep command,
                 * break out
                 */
                if(sleepTime > 0) {
                    break;
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see shared.Console#addCommand(shared.Command)
     */
    public void addCommand(Command command) {
        if(command==null) {
            throw new IllegalArgumentException("The command can not be null!");            
        }
        
        addCommand(command.getName(), command);
    }

    /* (non-Javadoc)
     * @see shared.Console#addCommand(java.lang.String, shared.Command)
     */
    public void addCommand(String alias, Command command) {
        if(alias == null) {
            throw new IllegalArgumentException("The command alias can not be null!");
        }
        
        if(command==null) {
            throw new IllegalArgumentException("The command can not be null!");            
        }
        
        this.commands.put(alias.toLowerCase(), command);
    }

    /* (non-Javadoc)
     * @see shared.Console#removeCommand(java.lang.String)
     */
    public void removeCommand(String commandName) {
        this.commands.remove(commandName.toLowerCase());
    }

    /* (non-Javadoc)
     * @see shared.Console#removeCommand(shared.Command)
     */
    public void removeCommand(Command command) {
        if(command!=null) {
            removeCommand(command.getName());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see shared.Console#getCommand(java.lang.String)
     */
    public Command getCommand(String commandName) {
        return this.commands.get(commandName.toLowerCase());
    }

    /* (non-Javadoc)
     * @see shared.Console#find(java.lang.String)
     */
    public List<String> find(String partialName) {
        List<String> matches = new ArrayList<String>();
                
        if(partialName != null) {
            String partialNameLower = partialName.toLowerCase();
            Set<String> names = this.commands.keySet();
            for(String cmdName : names) {
                if(cmdName.startsWith(partialNameLower)) {
                    matches.add(cmdName);
                }
            }
        }
        
        return matches;
    }
    
    /* (non-Javadoc)
     * @see shared.Console#addLogger(shared.Logger)
     */
    public void addLogger(Logger logger) {
        this.loggers.add(logger);
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Console#removeLogger(seventh.shared.Logger)
     */
    @Override
    public void removeLogger(Logger logger) {
        this.loggers.remove(logger);
    }
    
    /* (non-Javadoc)
     * @see shared.Console#setLogger(shared.Logger)
     */
    public void setLogger(Logger logger) {
        this.loggers.clear();
        addLogger(logger);
    }
    
    /* (non-Javadoc)
     * @see shared.Logger#print(java.lang.Object)
     */
    public void print(Object msg) {
        int size = loggers.size();
        for(int i = 0; i < size; i++) {
            loggers.get(i).print(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see shared.Logger#printf(java.lang.Object, java.lang.Object[])
     */
    public void printf(Object msg, Object... args) {
        int size = loggers.size();
        for(int i = 0; i < size; i++) {
            loggers.get(i).printf(msg, args);
        }
    }
    
    /* (non-Javadoc)
     * @see shared.Logger#println(java.lang.Object)
     */
    public void println(Object msg) {
        int size = loggers.size();
        for(int i = 0; i < size; i++) {
            loggers.get(i).println(msg);
        }
    }

    private void executeCommand(QueuedExecution exe) {
        String commandName = exe.command;
        String[] args = exe.args;
        
        Command cmd = getCommand(commandName);
        if(cmd != null) {
            try {
                cmd.execute(this, args);
            }
            catch(Exception e) {
                println("*** Error executing command: " + e);
            }
        }
        else {
            println("*** Command not found: " + commandName);
        }
    }
    
    /* (non-Javadoc)
     * @see shared.Console#execute(java.lang.String, java.lang.String[])
     */
    public void execute(String commandName, String... args) {
        this.queuedCommands.add(new QueuedExecution(commandName, args));
    }

    /* (non-Javadoc)
     * @see palisma.shared.Console#execute(java.lang.String)
     */
    @Override
    public void execute(String commandLine) {
        String cmd = null;
        String[] args = commandLine.split(" ");
        switch(args.length) {
            case 0: return;
            case 1: cmd = args[0];
                args= new String[0];
                break;
            default: {
                cmd = args[0];
                String[] args2 = new String[args.length-1];
                for(int i = 0; i < args2.length; i++) {
                    args2[i] = args[i+1]; 
                }
                args = args2;
            }        
        }
        
        execute(cmd, args);    
    }
}
