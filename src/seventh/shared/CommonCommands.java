/*
 * see license.txt 
 */
package seventh.shared;

/**
 * @author Tony
 *
 */
public class CommonCommands {

    public static void addCommonCommands(Console console) {
        console.addCommand(new ExecCommand());
    }
}
