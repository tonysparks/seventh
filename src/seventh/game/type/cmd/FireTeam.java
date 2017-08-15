/*
 * see license.txt 
 */
package seventh.game.type.cmd;

import seventh.game.Player;
import seventh.game.Team;
import seventh.game.entities.Entity;
import seventh.game.net.NetFireTeam;

/**
 * Fire Team consists of a Leader and up to three other members
 * 
 * @author Tony
 *
 */
public class FireTeam {

    /**
     * Max number of team members
     */
    public static final int MAX_MEMBERS = 4;
    
    public static final FireTeam ALLIED_ABLE = new FireTeam("Able", 1);
    public static final FireTeam ALLIED_BACKER = new FireTeam("Baker", 2);
    public static final FireTeam ALLIED_CHARLIE = new FireTeam("Charlie", 3);
    
    public static final FireTeam AXIS_ABLE = new FireTeam("Able", 1);
    public static final FireTeam AXIS_BACKER = new FireTeam("Baker", 2);
    public static final FireTeam AXIS_CHARLIE = new FireTeam("Charlie", 3);
    
    /**
     * Get the {@link FireTeam}
     * 
     * @param team
     * @param id
     * @return the associated {@link FireTeam}
     */
    public static final FireTeam byId(Team team, int id) {
        if(team!=null) {
            if(team.getId() == Team.ALLIED_TEAM_ID) {
                switch(id) {
                    case 1: return ALLIED_ABLE;
                    case 2: return ALLIED_BACKER;
                    case 3: return ALLIED_CHARLIE;
                }
            }
            else if(team.getId() == Team.AXIS_TEAM_ID) {
                switch(id) {
                case 1: return AXIS_ABLE;
                case 2: return AXIS_BACKER;
                case 3: return AXIS_CHARLIE;
            }
        }
        }
        return null;
    }
    
    private final String name;
    private final int id;
    
    private int teamLeaderId;
    private Player teamLeader;
    private Player[] members;
    
    private NetFireTeam netFireTeam;
    
    /**
     * @param name
     * @param id
     */
    public FireTeam(String name, int id) {
        this.name = name;
        this.id = id;
        this.members = new Player[MAX_MEMBERS];
        this.teamLeaderId = Entity.INVALID_ENTITY_ID;
        
        this.netFireTeam = new NetFireTeam();
        this.netFireTeam.id = id;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Checks to see if the team leader is alive,
     * if he isn't, this will promote another team
     * leader.
     * 
     * @return true if there is a team leader; false if
     * everyone is dead.
     */
    public boolean checkTeamLeaderStatus() {
        // if the team leader is alive, no need to find a new leader
        if(this.teamLeader != null && this.teamLeader.isAlive()) {
            return true;
        }
        
        ////
        // the guy is dead, we need to promote someone
        ////
        
        // first try to promote a human player
        for(int i = 0; i < MAX_MEMBERS; i++) {
            Player player = this.members[i];
            if(player != null && player.isAlive() && !player.isBot()) {
                setTeamLeaderId(player.getId());
                return true;
            }
        }
        
        
        // no humans, pick an AI player
        for(int i = 0; i < MAX_MEMBERS; i++) {
            Player player = this.members[i];
            if(player != null && player.isAlive()) {
                setTeamLeaderId(player.getId());
                return true;
            }
        }
        
        return false;
        
    }

    /**
     * @return the members
     */
    public Player[] getMembers() {
        return members;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the teamLeaderId
     */
    public int getTeamLeaderId() {
        return teamLeaderId;
    }
    
    /**
     * @param teamLeaderId the teamLeaderId to set
     */
    public void setTeamLeaderId(int teamLeaderId) {
        this.teamLeaderId = teamLeaderId;
        for(int i = 0; i < MAX_MEMBERS; i++) {
            Player player = this.members[i];
            if(player != null) {
                if(player.getId() == teamLeaderId) {
                    this.teamLeader = player;
                }
            }
        }
    }
    
    /**
     */
    public void clearMembers() {
        for(int i = 0; i < MAX_MEMBERS; i++) {
            this.members[i] = null;
        }
    }
        
    /**
     * Adds the {@link Player} to the {@link FireTeam}
     * 
     * @param player
     * @return true if added
     */
    public boolean addMember(Player player) {
        for(int i = 0; i < MAX_MEMBERS; i++) {
            if(this.members[i] == null || (this.members[i].getId() == player.getId())) {
                this.members[i] = player;
                return true;
            }
        }
        return false;
    }
    
    public boolean removeMember(Player player) {
        for(int i = 0; i < MAX_MEMBERS; i++) {
            if(this.members[i] != null && this.members[i].getId() == player.getId()) {
                this.members[i] = null;
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return the netFireTeam
     */
    public NetFireTeam getNetFireTeam() {
        netFireTeam.teamLeaderPlayerId = this.teamLeaderId;
        
        int j = 0;
        for(int i = 0; i < MAX_MEMBERS; i++) {
            Player player = this.members[i];
            if(player != null && player.getId() != this.teamLeaderId) {
                netFireTeam.memberPlayerIds[j++] = player.getId(); 
            }
        }
        
        return netFireTeam;
    }
}

