/*
 * see license.txt 
 */
package seventh.game.type;

import java.util.List;

import leola.frontend.listener.EventDispatcher;
import seventh.game.Game;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.Players;
import seventh.game.Team;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetTeamStat;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public interface GameType {

	public static enum Type {
		TDM,
		OBJ,
		;
		
		public byte netValue() {
			return (byte)ordinal();
		}
		
		public static Type fromNet(byte value) {
			if(value<0 || value >= values().length) {
				return TDM;
			}
			
			return values()[value];
		}
		
		public static Type toType(String gameType) {
			Type result = TDM;
			if(gameType != null) {			
				if("obj".equalsIgnoreCase(gameType.trim())) {
					result = OBJ;
				}
			}
			
			return result;
		}
	}
	
	public static enum GameState {
		IN_PROGRESS,
		WINNER,
		TIE,
		;
		
		public byte netValue() {
			return (byte)ordinal();
		}
	}
	public void start(Game game);
	public GameState update(Game game, TimeStep timeStep);
		
	public void registerListeners(GameInfo game, EventDispatcher dispatcher);
	
	public Team getAlliedTeam();
	public Team getAxisTeam();
	
	public void playerJoin(Player player);
	public void playerLeft(Player player);
	
	public Team getTeam(Player player);
	public boolean switchTeam(Player player, byte teamId);
	
	public long getMatchTime();
	public int getMaxScore();
	public long getRemainingTime();
	public boolean isInProgress();
	public Type getType();
		
	public Player getNextPlayerToSpectate(Players players, Player spectator);
		
	public NetGameTypeInfo getNetGameTypeInfo();
	public NetTeamStat[] getNetTeamStats();
	
	public List<Vector2f> getAlliedSpawnPoints();
	public List<Vector2f> getAxisSpawnPoints();
}
