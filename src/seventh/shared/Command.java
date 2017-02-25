/*
 * see license.txt 
 */
package seventh.shared;

/**
 * @author Tony
 *
 */
public abstract class Command {

    private String name;

    /**
     * @param name
     */
    public Command(String name) {
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Merges the arguments into one string
     * @param args
     * @return the coalesced string
     */
    protected String mergeArgs(String ...args) {
        return mergeArgsDelim("", args);
    }
    
    /**
     * Merges the arguments into one string
     * @param delimeter
     * @param args
     * @return the coalesced string
     */
    protected String mergeArgsDelim(String delimeter, String ...args) {
        return mergeArgsDelimAt(delimeter, 0, args);
    }
    
    /**
     * Merges the arguments into one string
     * @param delimeter
     * @param args
     * @return the coalesced string
     */
    protected String mergeArgsDelimAt(String delimeter, int index, String ...args) {
        StringBuilder sb = new StringBuilder();
        for(int i = index; i < args.length; i++) {
            if(i>index) sb.append(delimeter);
            sb.append(args[i]);
        }
        
        return sb.toString();
    }
    
    /**
     * @param console
     * @param args
     */
    public abstract void execute(Console console, String ... args);
}
