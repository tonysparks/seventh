/*
 * 
 */
package seventh;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import seventh.client.SeventhGame;
import seventh.shared.Config;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Main entry point for the client game
 * 
 * @author Tony
 *
 */
public class ClientMain {
	
	/**
	 * Client configuration file location
	 */
	private static final String CLIENT_CFG_PATH = "./seventh/client_config.leola";		
	
	/**
	 * Loads the client configuration file.
	 * 
	 * @return the client configuration file
	 * @throws Exception
	 */
	private static Config loadConfig() throws Exception {
		Config config = new Config(CLIENT_CFG_PATH, "client_config");
		return config;
	}
	
	
	/**
	 * Attempts to find the best display mode
	 * @param config 
	 * @return the best display mode
	 * @throws Exception
	 */
	private static DisplayMode findBestDimensions(Config config) throws Exception {
		
		if( config.getBool("video", "width") && 
		    config.getBool("video", "height")) {
			
			int width = config.getInt("video", "width");
			int height = config.getInt("video", "height");
			
			DisplayMode[] modes = LwjglApplicationConfiguration.getDisplayModes();
			for(DisplayMode mode : modes) {
				if(mode.width == width && mode.height == height) {
					return mode;
				}
			}
		}
				
		return LwjglApplicationConfiguration.getDesktopDisplayMode();
	}
	
	/**
	 * Handle the exception -- piping out to a log file
	 * @param e
	 */
	private static void catchException(Throwable e) {
		try {
			PrintStream out = new PrintStream(new File("./seventh_error.log"));
			try {
				e.printStackTrace(out);
			}
			finally {
				out.close();
			}
		}
		catch(Exception e1) {
			e1.printStackTrace();
		}
		finally {
			System.exit(1);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Config config = null;
		try {
			
			/*
			 * LibGDX spawns another thread which we don't have access
			 * to for catching its exceptions
			 */
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					if(t.getName().equals("LWJGL Application")) {
						catchException(e);
					}
				}
			});
			
			config = loadConfig();
			DisplayMode displayMode = findBestDimensions(config);
			
			LwjglApplicationConfiguration.disableAudio = true;			
			LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			
			cfg.setFromDisplayMode(displayMode);
			cfg.fullscreen = config.getBool("video", "fullscreen");
			cfg.title = "The Seventh " + SeventhGame.getVersion();
			cfg.forceExit = true;
			cfg.resizable = false;
			cfg.useGL20 = true;			
			
			if(!cfg.fullscreen) {
				cfg.width = 1024;
				cfg.height = 768;
			}
			
			new LwjglApplication(new SeventhGame(config), cfg);			
			
		}
		catch(Exception e) {
			catchException(e);
		}
		finally {
	//		System.exit(0);
			if(config!=null) {
				//config.save(CLIENT_CFG_PATH);
			}
		}
	}

}
