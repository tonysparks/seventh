/*
 * see license.txt 
 */
package seventh.server;

import harenet.api.Connection;
import seventh.game.Player;



/**
 * Represents a Remote Client
 * 
 * @author Tony
 *
 */
public class RemoteClient {

    private Connection conn;    
    private Player player;
    private boolean isReady;
    
    private long rconToken;
    private boolean isRconAuthenticated;
    
    /**
     * @param network
     */
    public RemoteClient(Connection conn) {
        this.conn = conn;
        this.player = new Player(conn.getId());                
        this.isReady = false;        
        
        this.rconToken = ServerContext.INVALID_RCON_TOKEN;
        this.isRconAuthenticated = false;
    }
    
    /**
     * @param rconToken the rconToken to set
     */
    public void setRconToken(long rconToken) {
        this.rconToken = rconToken;
    }
    
    /**
     * @return the rconToken
     */
    public long getRconToken() {
        return rconToken;
    }
    
    /**
     * @return true if this remote client has a valid rcon token
     */
    public boolean hasRconToken() {
        return this.rconToken != ServerContext.INVALID_RCON_TOKEN;
    }
    
    /**
     * @return the isRconAuthenticated
     */
    public boolean isRconAuthenticated() {
        return isRconAuthenticated;
    }
    
    /**
     * @param isRconAuthenticated the isRconAuthenticated to set
     */
    public void setRconAuthenticated(boolean isRconAuthenticated) {
        this.isRconAuthenticated = isRconAuthenticated;
    }
    
    /**
     * @return the isReady
     */
    public boolean isReady() {
        return isReady;
    }
    
    /**
     * @param isReady the isReady to set
     */
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }
    
    public Connection getConnection() {
        return this.conn;
    }
    
    public int getId() {
        return player.getId();
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.player.setName(name);
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return this.player.getName();
    }

}
