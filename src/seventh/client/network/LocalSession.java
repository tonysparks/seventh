/*
 * see license.txt
 */
package seventh.client.network;

/**
 * The local player, that is, the player which this game session will be issued for.
 * 
 * @author Tony
 *
 */
public class LocalSession {

    private int playerId;
    private long rconToken;
    
    /**
     * 
     */
    public LocalSession() {
        this.playerId = -1;
    }

    /**
     * @return the rconToken
     */
    public long getRconToken() {
        return rconToken;
    }
    
    /**
     * @param rconToken the rconToken to set
     */
    public void setRconToken(long rconToken) {
        this.rconToken = rconToken;
    }
    
    /**
     * Assigns a player ID, which denotes a new gaming session.
     * 
     * @param playerId
     */
    public void newSessionPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    /**
     * @return the player's ID for the current active game session
     */
    public int getSessionPlayerId() {
        return this.playerId;
    }
    
    /**
     * Determines if this local player is valid and active.
     * @return true if valid
     */
    public boolean isValid() {
        return this.playerId > -1;
    }
    
    /**
     * Invalidates this players local session
     */
    public void invalidate() {
        this.playerId = -1;
    }
}
