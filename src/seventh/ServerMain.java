/*
 * see license.txt 
 */
package seventh;

import java.util.Scanner;

import leola.vm.Leola;
import seventh.server.GameServer;
import seventh.server.ServerSeventhConfig;
import seventh.shared.Config;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.ConsoleFrame;
import seventh.shared.DefaultConsole;
import seventh.shared.Scripting;

/**
 * Main entry point for the server
 * 
 * @author Tony
 *
 */
public class ServerMain {
	
	private static final int DEFAULT_PORT = 9844;
	
	private static final String BANNER =
	"\n\n\n\t\t*** The Seventh Server ***\n" +
	"\t\t    5d Studios (c)\n\n"
	;
	
	public static void main(final String [] args) {
		final Console console = new DefaultConsole();
		Cons.setImpl(console);
		

		final Thread gameThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					int port = DEFAULT_PORT;
					
					Leola runtime = Scripting.newRuntime();
					ServerSeventhConfig config = new ServerSeventhConfig(new Config("./seventh/server_config.leola", "server_config", runtime));
					GameServer server = new GameServer(config, console, runtime);
					if (args.length > 0) {
						try {
							port =  Integer.parseInt(args[0]);
						}
						catch(Exception e) {}
					}
					else {
						// TODO Use port from config file
						//port = server.getp
					}
					server.start(port);
				} 
				catch (Exception e) {
					console.println("An error occured with the server: " + e);
				}
			}
		});
		
		if (isCmdLineOnly(args)) {
			gameThread.start();
			setupCommandLine(console);
		}
		else {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new ConsoleFrame("The Seventh Server", BANNER, console);
					gameThread.start();
				}
			});
					
		}
	}

	private static boolean isCmdLineOnly(String [] args) {		
		for(int i = 0; i < args.length; i++) {			
			if(args[i].trim().equalsIgnoreCase("-c")) {
				return true;
			}
		}
		return false;
	}
	
	private static void setupCommandLine(Console console) {
		Scanner scanner = new Scanner(System.in);
		try {
			while(scanner.hasNext()) {
				String in = scanner.nextLine();
				if(in != null && in.length() > 0) {
					console.execute(in.trim());
				}
			}
		}
		finally {
			scanner.close();
		}
	}
}
