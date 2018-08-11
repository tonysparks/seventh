/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.entities.ClientPlayerEntity;
import seventh.client.gfx.Art;
import seventh.client.gfx.PlayerSprite;
import seventh.client.gfx.RenderFont;
import seventh.game.entities.Entity;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;

/**
 * @author Tony
 *
 */
public class ClientPlayer {

    private ClientPlayerEntity entity;
    private ClientTeam team;
    private NetPlayerStat stats;
    
    private int id;
    private String name;
    private String plainName;
    
    private int spectatingPlayerId;
    
    private PlayerSprite axisSprite;
    private PlayerSprite alliedSprite;

    private int activeTileId;
    
    private boolean isCommander;
    
    /**
     * 
     */
    public ClientPlayer(String name, int playerId) {
        this.name = name;
        this.plainName = RenderFont.getDecodedText(name);
        this.id = playerId;
        this.stats = new NetPlayerStat();
        this.team = ClientTeam.NONE;
        this.spectatingPlayerId = Entity.INVALID_ENTITY_ID;
        
        reloadGraphics();
    }

    public void reloadGraphics() {
        this.axisSprite = new PlayerSprite(this, Art.axisBodyModel, Art.axisWalkModel, Art.axisCrouchLegs, Art.axisSprintModel);
        this.alliedSprite = new PlayerSprite(this, Art.alliedBodyModel, Art.alliedWalkModel, Art.alliedCrouchLegs, Art.alliedSprintModel);
    }
    
    public void updateStats(NetPlayerStat state) {
        this.stats= state;
        this.name = state.name;        
        this.plainName = RenderFont.getDecodedText(this.name);
        //this.isCommander = state.isCommander;        
        
        if(this.team.getId() != this.stats.teamId) {
            changeTeam(ClientTeam.fromId(this.stats.teamId));            
        }
    }
    
    public void updatePartialStats(NetPlayerPartialStat state) {
        this.stats.deaths = state.deaths;
        this.stats.kills = state.kills;        
    }
    
    /**
     * @param activeTileId the activeTileId to set
     */
    public void setActiveTileId(int activeTileId) {
        this.activeTileId = activeTileId;
    }
    
    /**
     * @return the activeTileId
     */
    public int getActiveTileId() {
        return activeTileId;
    }

    
    public int getId() {
        return this.id;
    }
    
    /**
     * @return this players name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the plainName
     */
    public String getPlainName() {
        return plainName;
    }
    
    public int getKills() {
        return this.stats.kills;
    }
    
    public int getAssists() {
        return this.stats.assists;
    }
    
    public int getDeaths() {
        return this.stats.deaths;
    }
    
    public int getHitPercentage() {
        return this.stats.hitPercentage;
    }    
    
    public int getPing() {
        return this.stats.ping;
    }
    
    /**
     * @return the alliedSprite
     */
    public PlayerSprite getAlliedSprite() {
        return alliedSprite;
    }
    
    /**
     * @return the axisSprite
     */
    public PlayerSprite getAxisSprite() {
        return axisSprite;
    }
    
    
    /**
     * @return the team
     */
    public ClientTeam getTeam() {
        return team;
    }
    
    public void changeTeam(ClientTeam team) {
        this.team = team;
        if(isAlive()) {
            this.entity.changeTeam(team);
        }
    }
    
    /**
     * @param entity the entity to set
     */
    public void setEntity(ClientPlayerEntity entity) {
        if(this.entity!=entity) {
            this.entity = entity;
            
            this.alliedSprite.reset(this);
            this.axisSprite.reset(this);
            
            if(this.entity != null && team!= null) {
                this.entity.changeTeam(getTeam());
            }
        }
    }
    
    /**
     * @return the entity
     */
    public ClientPlayerEntity getEntity() {
        return entity;
    }
    
    /**
     * @return if this player is a bot 
     */
    public boolean isBot() {
        return this.stats.isBot;
    }
    
    /**
     * @return if this player is Alive
     */
    public boolean isAlive() {
        return entity != null && entity.isAlive();
    }
    
    /**
     * @return if this player is operating a vehicle
     */
    public boolean isOperatingVehicle() {
        return isAlive() && entity.isOperatingVehicle();
    }
    
    /**
     * @return true if this player is spectating
     */
    public boolean isSpectating() {
        return this.team == null || this.team == ClientTeam.NONE || this.spectatingPlayerId != Entity.INVALID_ENTITY_ID;
    }
    
    /**
     * @return if this player is only a spectator (doesn't belong to a team)
     */
    public boolean isPureSpectator() {
        return isSpectating() && this.team == ClientTeam.NONE;
    }
    
    
    /**
     * @return the isCommander
     */
    public boolean isCommander() {
        return isCommander;
    }
    
    /**
     * @param isCommander the isCommander to set
     */
    public void setCommander(boolean isCommander) {
        this.isCommander = isCommander;
    }
    
    /**
     * @param spectatingPlayerId the spectatingPlayerId to set
     */
    public void setSpectatingPlayerId(int spectatingPlayerId) {
        this.spectatingPlayerId = spectatingPlayerId;
    }
    
    public void stopSpectatingPlayer() {
        setSpectatingPlayerId(Entity.INVALID_ENTITY_ID);
    }
    
    /**
     * @return the spectatingPlayerId
     */
    public int getSpectatingPlayerId() {
        return spectatingPlayerId;
    }
    
    /**
     * If this player is currently controlling an
     * entity, this ID will be returned.  If this player
     * is currently spectating another entity, that entity
     * ID will be returned.
     * 
     * @return the ID of the currently viewed entity.
     */
    public int getViewingEntityId() {
        if(isAlive()) {
            return this.entity.getId();
        }
        return getSpectatingPlayerId();
    }
}
