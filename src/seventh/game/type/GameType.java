/*
 * see license.txt 
 */
package seventh.game.type;

import java.util.List;

import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Players;
import seventh.game.Team;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetTeamStat;
import seventh.math.Vector2f;
import seventh.shared.Debugable;
import seventh.shared.EventDispatcher;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public interface GameType extends Debugable {

    public static enum Type {
        TDM("Team Death Match"),
        OBJ("Objective Based Match"),
        CTF("Capture The Flag"),
        CMD("Commander"),
        SVR("Survivor")
        ;
        
        private String displayName;
        
        /**
         * 
         */
        private Type(String displayName) {
            this.displayName = displayName;
        }
        
        /**
         * @return the displayName
         */
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * @return the network value
         */
        public byte netValue() {
            return (byte)ordinal();
        }
        
        public static int numOfBits() {
            return 4;
        }
        
        private static final Type[] values = values();
        
        public static Type fromNet(byte value) {
            if(value<0 || value >= values.length) {
                return TDM;
            }
            
            return values[value];
        }
        
        public static Type toType(String gameType) {
            Type result = TDM;
            if(gameType != null) {            
                if("obj".equalsIgnoreCase(gameType.trim())) {
                    result = OBJ;
                }
                else if ("ctf".equalsIgnoreCase(gameType.trim())) {
                    result = CTF;
                }
                else if ("cmd".equalsIgnoreCase(gameType.trim())) {
                    result = CMD;
                }
                else if("svr".equalsIgnoreCase(gameType.trim())) {
                    result = SVR;
                }
            }
            
            return result;
        }
    }
    
    public static enum GameState {
        INTERMISSION,
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
        
    public void registerListeners(Game game, EventDispatcher dispatcher);
    
    public Team getAlliedTeam();
    public Team getAxisTeam();
    
    public void playerJoin(Player player);
    public void playerLeft(Player player);
    
    public Team getTeam(Player player);
    public Team getEnemyTeam(Player player);
    public boolean switchTeam(Player player, byte teamId);
    
    public long getMatchTime();
    public int getMaxScore();
    public long getRemainingTime();
    public boolean isInProgress();
    public Type getType();
        
    public Player getNextPlayerToSpectate(Players players, Player spectator);
    public Player getPrevPlayerToSpectate(Players players, Player spectator);
        
    public NetGameTypeInfo getNetGameTypeInfo();
    public NetTeamStat getAlliedNetTeamStats();
    public NetTeamStat getAxisNetTeamStats();
    
    public List<Vector2f> getAlliedSpawnPoints();
    public List<Vector2f> getAxisSpawnPoints();
}
