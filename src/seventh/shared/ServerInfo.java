/*
 * see license.txt 
 */
package seventh.shared;

import java.util.ArrayList;
import java.util.List;

import leola.vm.exceptions.LeolaRuntimeException;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import leola.vm.types.LeoUserFunction;
import seventh.game.Player;
import seventh.game.Team;
import seventh.game.type.GameType;
import seventh.server.GameSession;
import seventh.server.ServerContext;
import seventh.server.ServerSeventhConfig;

/**
 * Server information for finding a game
 * 
 * @author Tony
 *
 */
public class ServerInfo {
	
	private String address;
	private int port;
	
	private String serverName;
	private String gameType;
	private String mapName;
	
	private List<String> axis, allies;
	
	/**
	 * @param obj
	 */
	public ServerInfo(LeoObject obj) {
		this.axis = new ArrayList<>();
		this.allies = new ArrayList<>();
		parseEntry(obj);
	}
	
	
	/**
	 * @param context
	 */
	public ServerInfo(ServerContext context) {
		/*
		 * TODO - Move this code out to a Util class
		 * inside the server code, eventually we should 
		 * make three projects: shared, client, server
		 */
		
		this.axis = new ArrayList<>();
		this.allies = new ArrayList<>();
		
		ServerSeventhConfig config = context.getConfig();
		this.address = config.getAddress();
		this.port = context.getPort();
		this.serverName = config.getServerName();
		if(context.hasGameSession()) {
			GameSession session = context.getGameSession();
			GameType gameType = session.getGameType();
			
			this.gameType = gameType.getType().name();
			this.mapName = session.getMap().getMapFileName();
			
			Team alliedTeam = gameType.getAlliedTeam();
			for(Player player : alliedTeam.getPlayers()) {
				this.allies.add(player.getName());
			}
			
			Team axisTeam = gameType.getAxisTeam();
			for(Player player : axisTeam.getPlayers()) {
				this.axis.add(player.getName());
			}
		}
	}
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * @return the gameType
	 */
	public String getGameType() {
		return gameType;
	}
	
	/**
	 * @return the allies
	 */
	public List<String> getAllies() {
		return allies;
	}
	
	/**
	 * @return the axis
	 */
	public List<String> getAxis() {
		return axis;
	}
	
	/**
	 * @return the mapName
	 */
	public String getMapName() {
		return mapName;
	}

	private void parseEntry(LeoObject object) {		
		
		try {			
			this.address = object.getObject("address").toString();
			this.port = Integer.parseInt(object.getObject("port").toString());
			this.serverName = object.getObject("server_name").toString();
			this.gameType = object.getObject("game_type").toString();
			this.mapName = object.getObject("map").toString();
			
			LeoArray leoAxis = object.getObject("axis").as();
			leoAxis.foreach(new LeoUserFunction() {				
				@Override
				public LeoObject xcall(LeoObject arg1) throws LeolaRuntimeException {
					axis.add(arg1.toString());
					return LeoObject.FALSE;
				}
			});
			LeoArray leoAllies = object.getObject("allies").as();
			leoAllies.foreach(new LeoUserFunction() {				
				@Override
				public LeoObject xcall(LeoObject arg1) throws LeolaRuntimeException {
					allies.add(arg1.toString());
					return LeoObject.FALSE;
				}
			});
			
		}
		catch(Exception e) {
			Cons.println("*** ERROR: Parsing the ServerInfo: " + e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		LeoMap map = new LeoMap();
		map.putByString("address", LeoObject.valueOf(getAddress()));
		map.putByString("port", LeoObject.valueOf(getPort()));
		map.putByString("server_name", LeoObject.valueOf(getServerName()));
		map.putByString("game_type", LeoObject.valueOf(getGameType()));
		
		LeoArray leoAxis = new LeoArray();
		for(String player : this.axis) {
			leoAxis.add(LeoObject.valueOf(player));
		}
		map.putByString("axis", leoAxis);
		
		LeoArray leoAllies = new LeoArray();
		for(String player : this.allies) {
			leoAllies.add(LeoObject.valueOf(player));
		}
		map.putByString("allies", leoAllies);
		return map.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + port;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerInfo other = (ServerInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
	
}
