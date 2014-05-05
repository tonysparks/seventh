/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.Team;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;

/**
 * Keeps interesting stats about the game
 * 
 * @author Tony
 *
 */
public class Stats implements PlayerKilledListener {

	private Zones zones;

	private ZoneStats[] zoneStats;
	private PlayerInfos players;
	/**
	 * 
	 */
	public Stats(GameInfo game, Zones zones) {
		this.players = game.getPlayerInfos();
		this.zones = zones;
		
		zoneStats = new ZoneStats[zones.getNumberOfZones()];
		for(int i = 0; i < zoneStats.length;i++) {
			zoneStats[i] = new ZoneStats();
		}
		
		game.getDispatcher().addEventListener(PlayerKilledEvent.class, this);
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.events.PlayerKilledListener#onPlayerKilled(seventh.game.events.PlayerKilledEvent)
	 */
	@Override
	public void onPlayerKilled(PlayerKilledEvent event) {
		
		Zone zone = zones.getZone(event.getPos());
		if(zone != null) {
			ZoneStats stats = zoneStats[zone.getId()];
			
			Player killed = event.getPlayer();
			if(killed.getTeamId()==Team.ALLIED_TEAM) {
				stats.addAlliedDeath();
			}
			else {
				stats.addAxisDeath();
			}
			
			PlayerInfo killer = players.getPlayerInfo(event.getKillerId());
			if (killer != null) {
				if(killer.getTeamId()==Team.ALLIED_TEAM) {
					stats.addAlliedKill();
				}
				else {
					stats.addAxisKill();
				}
			}
		}
	}

}
