/*
 * see license.txt 
 */
package seventh.shared;

import java.util.List;

/**
 * @author Tony
 *
 */
public interface Console extends Logger {

	/**
	 * @param command
	 */
	public void addCommand(Command command);
	
	/**
	 * @param alias
	 * @param command
	 */
	public void addCommand(String alias, Command command);
	
	/**
	 * @param commandName
	 */
	public void removeCommand(String commandName);
	
	/**
	 * @param command
	 */
	public void removeCommand(Command command);
	
	/**
	 * @param commandName
	 * @return the Command if found, otherwise null
	 */
	public Command getCommand(String commandName);
	
	/**
	 * Attempts to find the best {@link Command} given the 
	 * partial name
	 * @param partialName
	 * @return a list of command names with the partialName
	 */
	public List<String> find(String partialName);

	/**
	 * @param logger
	 */
	public void setLogger(Logger logger);
	
	/**
	 * @param logger
	 */
	public void addLogger(Logger logger);
	
	/**
	 * Executes the commands on the game update to avoid
	 * concurrency issues.
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep);
	
	/**
	 * @param cmd
	 * @param args
	 */
	public void execute(String cmd, String ... args);
	
	/**
	 * Splits the input by " " and uses the first input as the command and the
	 * rest as args.
	 * 
	 * @param commandLine
	 * @see Console#execute(String, String...)
	 */
	public void execute(String commandLine);
}
