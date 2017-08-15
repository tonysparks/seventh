/*
 * see license.txt 
 */
package seventh.game.type.cmd;

import seventh.game.Player;
import seventh.game.Team;
import seventh.game.net.NetSquad;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * The AIGroup contains 
 * 
 * @author Tony
 *
 */
public class Squad implements Updatable {

    public static final int MAX_FIRETEAMS = 3;
    
    private Team team;
    private FireTeam able, 
                     baker, 
                     charlie;
    
    private NetSquad netSquad;
    
    /**
     * @param team
     * @param able
     * @param baker
     * @param charlie
     */
    public Squad(Team team) {
        this.team = team;
        this.able    = team.getId() == Team.ALLIED_TEAM_ID ? FireTeam.ALLIED_ABLE : FireTeam.AXIS_ABLE;
        this.baker   = team.getId() == Team.ALLIED_TEAM_ID ? FireTeam.ALLIED_BACKER : FireTeam.AXIS_BACKER;
        this.charlie = team.getId() == Team.ALLIED_TEAM_ID ? FireTeam.ALLIED_CHARLIE : FireTeam.AXIS_CHARLIE;
        
        this.netSquad = new NetSquad();
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.able.checkTeamLeaderStatus();
        this.baker.checkTeamLeaderStatus();
        this.charlie.checkTeamLeaderStatus();
    }
    
    /**
     * @return the team
     */
    public Team getTeam() {
        return team;
    }
    /**
     * @return the able
     */
    public FireTeam getAble() {
        return able;
    }
    /**
     * @return the baker
     */
    public FireTeam getBaker() {
        return baker;
    }
    /**
     * @return the charlie
     */
    public FireTeam getCharlie() {
        return charlie;
    }
    
    /**
     * Adds a player to the aIGroup
     * 
     * @param player
     * @return true if this player can be added
     */
    public boolean addPlayer(Player player) {
        return this.able.addMember(player)    || 
               this.baker.addMember(player)   ||
               this.charlie.addMember(player);        
    }
    
    public void removePlayer(Player player) {        
        if(!this.able.removeMember(player)) {
            if(!this.baker.removeMember(player)) {
                this.charlie.removeMember(player);
            }
        }
    }

    /**
     * @return the netSquad
     */
    public NetSquad getNetSquad() {
        netSquad.squad[0] = able.getNetFireTeam();
        netSquad.squad[1] = baker.getNetFireTeam();
        netSquad.squad[2] = charlie.getNetFireTeam();
        return netSquad;
    }
}
