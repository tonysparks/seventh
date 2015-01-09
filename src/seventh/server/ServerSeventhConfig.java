/*
 * see license.txt 
 */
package seventh.server;

import java.util.ArrayList;
import java.util.List;

import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import leola.vm.types.LeoString;
import seventh.game.type.GameType;
import seventh.shared.Config;
import seventh.shared.MapList;
import seventh.shared.SeventhConfig;
import seventh.shared.SeventhConstants;

/**
 * The server configuration file
 * 
 * @author Tony
 *
 */
public class ServerSeventhConfig extends SeventhConfig {
	
	
	
	/**
	 * @param configurationPath
	 * @param configurationRootNode
	 * @throws Exception
	 */
	public ServerSeventhConfig(String configurationPath, String configurationRootNode) throws Exception {
		super(configurationPath, configurationRootNode);
	}

	/**
	 * @param config
	 */
	public ServerSeventhConfig(Config config) {
		super(config);
	}
	
	/**
	 * @return true if the debugger is enabled
	 */
	public boolean isDebuggerEnabled() {
		return this.config.getBool("debugger", "enabled");
	}
	
	/**
	 * @return the class path
	 */
	public String getDebuggerClasspath() {
		return this.config.getString("debugger", "classpath");
	}
	
	/**
	 * @return the debugger class name
	 */
	public String getDebuggerClassName() {
		return this.config.getStr("", "debugger", "class_name");
	}
	
	/**
	 * @return the listening port
	 */
	public int getPort() {
		return this.config.getInt(SeventhConstants.DEFAULT_PORT, "net", "port");
	}
	
	/**
	 * @return the map listings
	 */
	public List<String> getMapListings() {
		
		 
		/* Allow for a subset of the maps to be
		 * cycled through 
		 */
		if(this.config.has("map_list")) {
			LeoArray mapList = this.config.get("map_list").as();
			List<String> maps = new ArrayList<String>(mapList.size());
			for(LeoObject m : mapList) {
				maps.add(m.toString());
			}
			return maps;
		}
					
		/* this just allows ALL maps that are in the maps
		 * directory
		 */
		return MapList.getMapListing();
		
	}
	
	/**
	 * Sets the map listings
	 * 
	 * @param maps
	 */
	public void setMapListings(List<String> maps) {
		LeoArray mapList = new LeoArray(maps.size());
		for(String map : maps) {
			mapList.add(LeoString.valueOf(map));
		}
		
		this.config.set(mapList, "map_list");
	}
	
	/**
	 * @return the weapons
	 */
	public LeoObject getWeapons() {
		return this.config.get("weapons");
	}
	
	/**
	 * @param weapons
	 */
	public void setWeapons(LeoObject weapons) {
		this.config.set(weapons, "weapons");
	}
	
	/**
	 * @return the rcon password
	 */
	public String getRconPassword() {
		return this.config.getStr("brett_favre", "rcon_password");
	}
	
	/**
	 * @param rconPassword
	 */
	public void setRconPassword(String rconPassword) {
		this.config.set(rconPassword, "rcon_password");
	}
	
	/**
	 * @return the server name
	 */
	public String getServerName() {
		return this.config.getStr("The Seventh Server", "server_name");
	}
	
	/**
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.config.set(serverName, "server_name");
	}
	
	
	/**
	 * @return the match time -- the max amount of time a match should last
	 */
	public long getMatchTime() {
		return this.config.getInt(20, "sv_matchtime") * 60 * 1000L;
	}
	
	public void setMatchTime(long matchTime) {
		this.config.set(matchTime, "sv_matchtime");
	}
	
	/**
	 * @return the max score needed to win
	 */
	public int getMaxScore() {
		return this.config.getInt(50, "sv_maxscore");
	}
	
	public void setMaxScore(int maxscore) {
		this.config.set(maxscore, "sv_maxscore");
	}
	
	
	/**
	 * @return the max number of players allowed on this server
	 */
	public int getMaxPlayers() {
		return this.config.getInt(12, "sv_maxplayers");
	}
	
	/**
	 * Sets the max number of players
	 * 
	 * @param maxPlayers
	 */
	public void setMaxPlayers(int maxPlayers) {
		this.config.set(maxPlayers, "sv_maxplayers");
	}
	
	/**
	 * @return the game type
	 */
	public GameType.Type getGameType() {
		return GameType.Type.toType(this.config.getStr("tdm", "sv_gametype"));
	}
	
	public void setGameType(GameType.Type gameType) {
		this.config.set(gameType.name(), "sv_gametype");
	}				
	
	public int getServerFrameRate() {
		return this.config.getInt(20, "sv_framerate");
	}
	public void setServerFrameRate(int fps) {
		this.config.set(fps, "sv_framerate");
	}
}
