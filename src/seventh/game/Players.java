/*
 * see license.txt 
 */
package seventh.game;

import java.util.Random;

import seventh.shared.SeventhConstants;

/**
 * Simple structure that holds the Players, this structure allows for
 * mutable operations on the list of Players.
 * 
 * @author Tony
 *
 */
public class Players implements PlayerInfos {
    
    public static interface PlayerIterator {
        public void onPlayer(Player player);
    }
    
    private Player[] players;
    private Random random;
    
    /**
     * 
     */
    public Players() {
        this.players = new Player[SeventhConstants.MAX_PLAYERS];
        this.random = new Random();
    }
    
    /**
     * @param id
     * @return true if the supplied ID is valid
     */
    private boolean isValidId(int id) {
        return (id >= 0  & id < this.players.length);
    }

    /**
     * @param id
     * @return true if the supplied ID is valid and if there is currently a player associated
     * with the ID
     */
    public boolean hasPlayer(int id) {
        return isValidId(id) && this.players[id] != null;
    }
    
    /**
     * @return the max number of players allowed
     */
    public int maxNumberOfPlayers() {
        return this.players.length;
    }
    
    /**
     * @param id
     * @return the {@link Player}
     */
    public Player getPlayer(int id) {
        if(isValidId(id)) {        
            return this.players[id];
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#getPlayerInfo(int)
     */
    @Override
    public PlayerInfo getPlayerInfo(int id) {
        return getPlayer(id);
    }
    
    /**
     * Adds a {@link Player}
     * @param player
     */
    public void addPlayer(Player player) {
        if(isValidId(player.getId())) {
            this.players[player.getId()] = player;
        }        
    }
    
    /**
     * Removes a {@link Player}
     * @param id
     * @return the {@link Player} if found
     */
    public Player removePlayer(int id) {
        Player player = null;
        if(isValidId(id)) {
            player = this.players[id];
            this.players[id] = null;
        }
        return player;
    }
    
    /**
     * Removes a {@link Player}
     * @param player
     * @return the {@link Player} if found
     */
    public Player removePlayer(Player player) {
        return removePlayer(player.getId());
    }
    
    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#getNumberOfPlayers()
     */
    @Override
    public int getNumberOfPlayers() {
        int sum = 0;
        for(int i = 0; i < this.players.length; i++) {
            if(this.players[i] != null) {
                sum++;
            }
        }
        return sum;
    }
    
    /**
     * Iterates through all the available players, invoking the 
     * {@link PlayerIterator} for each player.
     * @param it
     */
    public void forEachPlayer(PlayerIterator it) {
        for(int i = 0; i < this.players.length; i++) {
            Player player = this.players[i];
            if(player != null) {
                it.onPlayer(player);
            }
        }
    }
    

    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#forEachPlayerInfo(seventh.game.Players.PlayerInfoIterator)
     */
    @Override
    public void forEachPlayerInfo(PlayerInfoIterator it) {
        for(int i = 0; i < this.players.length; i++) {
            Player player = this.players[i];
            if(player != null) {
                it.onPlayerInfo(player);
            }
        }
    }
    
    /**
     * @return the underlying list of players.  For performance reasons
     * this array may contain empty slots, it is up to the client
     * to filter these.
     */
    public Player[] getPlayers() {
        return this.players;
    }
    

    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#getPlayerInfos()
     */
    @Override
    public PlayerInfo[] getPlayerInfos() {
        return this.players;
    }
    
    
    /**
     * Resets all of the players statistics
     */
    public void resetStats() {
        for(int i = 0; i < this.players.length; i++) {
            Player player = this.players[i];
            if(player != null) {
                player.resetStats();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#getRandomAlivePlayer()
     */
    @Override
    public Player getRandomAlivePlayer() {        
        int startingIndex = random.nextInt(players.length);
        return FindAlivePlayer(startingIndex);
    }
    
    public Player FindAlivePlayer(int start){
    	int PlayerSize = players.length;
        for(int i = 0; i < PlayerSize; i++) {
            Player player = players[(start + i) % PlayerSize];
            if(IsAlivePlayer(player)) {
                return player;
            }
        }
        return null;
    }
    
    public boolean IsAlivePlayer(Player player){
    	return player != null && player.isAlive();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#getPrevAlivePlayerFrom(seventh.game.Player)
     */
    
    
    @Override
    public Player getPrevAlivePlayerFrom(Player oldPlayer) {
        if(oldPlayer == null ) return getRandomAlivePlayer();
        int PlayerSize = players.length;
        int nextPlayerIndex = SetNextPlayerIndex(oldPlayer, PlayerSize);
        return FindPrevAlivePlayer(nextPlayerIndex, oldPlayer, PlayerSize);
    }
    
    public Player FindPrevAlivePlayer(int nextPlayerIndex, Player oldPlayer, int PlayerSize){
        for(int i = 0; i < this.players.length; i++) {
            Player player = this.players[nextPlayerIndex];
            if(IsAlivePlayer(player, oldPlayer)) {
                    return player;
            }
            nextPlayerIndex = ChangePlayerIndex(nextPlayerIndex, PlayerSize);
        }
        return null;
    }
    
    public boolean IsAlivePlayer(Player player, Player oldPlayer){
    	return player != null && player.isAlive() && player != oldPlayer;
    }
    
    
    public int SetNextPlayerIndex(Player oldPlayer, int PlayerSize){
    	int nextPlayerIndex = (oldPlayer.getId() -1) % PlayerSize;
    	return IsUnderPlayerIndex(nextPlayerIndex, PlayerSize);
    }
    
    public int ChangePlayerIndex(int nextPlayerIndex, int PlayerSize){
    	int nextIndex = nextPlayerIndex;
    	return IsUnderPlayerIndex(nextIndex, PlayerSize);
    }
    
    public int IsUnderPlayerIndex(int nextPlayerIndex, int PlayerSize){
    	if(nextPlayerIndex < 0)
    		return Math.max(PlayerSize-1, 0);
    	return nextPlayerIndex;
    }
    
    
    /* (non-Javadoc)
     * @see seventh.game.PlayerInfos#getNextAlivePlayerFrom(seventh.game.Player)
     */
    @Override
    public Player getNextAlivePlayerFrom(Player oldPlayer) {
        if(oldPlayer == null ) return getRandomAlivePlayer();
        
        int nextPlayerIndex = (oldPlayer.getId() + 1) % players.length;
        
        for(int i = 0; i < this.players.length; i++) {
            Player player = this.players[nextPlayerIndex];
            if(player != null) {
                        
                if(player.isAlive() && player != oldPlayer) {
                    return player;
                }
            }    
            
            nextPlayerIndex = (nextPlayerIndex + 1) % players.length;
        }
        
        
        return null;
    }
    
}
