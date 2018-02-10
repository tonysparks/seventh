/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.Team;
import seventh.game.events.BombExplodedEvent;
import seventh.game.events.BombExplodedListener;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.PlayerSpawnedEvent;
import seventh.game.events.PlayerSpawnedListener;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Keeps interesting statistics about the game
 * 
 * @author Tony
 *
 */
public class Stats implements PlayerKilledListener, PlayerSpawnedListener, BombExplodedListener, Updatable, Debugable {

    private Zones zones;
    private PlayerInfos players;
    
    private int numberOfAlliesAlive;
    private int numberOfAxisAlive;
    
    private int numberOfBombsExploded;
    private int totalNumberOfBombTargets;
    
    private Zone[] topFiveDeadliest;
    
    /**
     * @param game
     * @param zones
     */
    public Stats(GameInfo game, Zones zones) {
        this.players = game.getPlayerInfos();
        this.zones = zones;
        this.totalNumberOfBombTargets = game.getBombTargets().size();
        
        this.topFiveDeadliest = new Zone[5];
        
        game.getDispatcher().addEventListener(PlayerKilledEvent.class, this);
        game.getDispatcher().addEventListener(PlayerSpawnedEvent.class, this);
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        calculateTop5DeadliesZones();
    }
    
    /**
     * @return the Zone which contains the most amount of deaths
     */
    public Zone getDeadliesZone() {
//        Zone deadliest = null;
//        int killCount = 0;
//        
//        Zone[][] zs = this.zones.getZones();
//        for(int y = 0; y < zs.length; y++) {
//            for(int x = 0; x < zs[y].length; x++) {                
//                Zone zone = zs[y][x];
//                if(zone.isHabitable()) {
//                    ZoneStats s = zone.getStats();
//                    
//                    int totalKilled = s.getTotalKilled();
//                    if(deadliest == null || killCount < totalKilled) {
//                        deadliest = s.getZone();
//                        killCount = totalKilled;
//                    }
//                }
//            }
//        }
//        
//        return deadliest;
        if(this.topFiveDeadliest[0]==null) {
            return this.zones.getZone(0, 0);
        }
        return this.topFiveDeadliest[0];
    }
    
    private boolean isAlreadyInTop5(Zone zone) {
        for(int j = 0; j < this.topFiveDeadliest.length; j++) {
            if(this.topFiveDeadliest[j]!=null&&this.topFiveDeadliest[j].getId()==zone.getId()) {
                return true;
            }
        }
        return false;        
    }
    
    public Zone[] getTop5DeadliesZones() {
        return this.topFiveDeadliest;
    }
    
    private void calculateTop5DeadliesZones() {
        Zone[][] zs = this.zones.getZones();
        for(int y = 0; y < zs.length; y++) {
            for(int x = 0; x < zs[y].length; x++) {                
                Zone zone = zs[y][x];
                if(zone.isHabitable() && !isAlreadyInTop5(zone)) {
                    ZoneStats s = zone.getStats();
                    
                    int totalKilled = s.getTotalKilled();
                    int positionToSet = -1;
                    
                    //for(int i = 0; i < this.topFiveDeadliest.length; i++) {
                    for(int i = this.topFiveDeadliest.length-1; i >= 0; i--) {    
                        if(this.topFiveDeadliest[i]==null) {
                            positionToSet = i;
                            continue;
                        }
                                
                        if(this.topFiveDeadliest[i].getStats().getTotalKilled() < totalKilled) {                                
                            positionToSet = i;                            
                        }
                        else {
                            break;
                        }
                    }
                    
                    if(positionToSet>-1) {
                        if(positionToSet<4) { 
                            for(int i = this.topFiveDeadliest.length-1; i > positionToSet; i--) {
                                Zone tmp = this.topFiveDeadliest[i-1];
                                this.topFiveDeadliest[i] = tmp;
                            }    
                        }
                        
                        this.topFiveDeadliest[positionToSet] = zone;
                        
                    }
                }
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.game.events.BombExplodedListener#onBombExplodedEvent(seventh.game.events.BombExplodedEvent)
     */
    @Override
    public void onBombExplodedEvent(BombExplodedEvent event) {
        this.numberOfBombsExploded++;
    }
    
    /**
     * @return the totalNumberOfBombTargets
     */
    public int getTotalNumberOfBombTargets() {
        return totalNumberOfBombTargets;
    }
    
    /**
     * @return the numberOfBombsExploded
     */
    public int getNumberOfBombsExploded() {
        return numberOfBombsExploded;
    }
    
    /**
     * @return the number of Allied players that are alive
     */
    public int getNumberOfAlliesAlive() {
        return numberOfAlliesAlive;
    }
    
    /**
     * @return the number of Axis players that are alive
     */
    public int getNumberOfAxisAlive() {
        return numberOfAxisAlive;
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.game.events.PlayerSpawnedListener#onPlayerSpawned(seventh.game.events.PlayerSpawnedEvent)
     */
    @Override
    public void onPlayerSpawned(PlayerSpawnedEvent event) {
        switch(event.getPlayer().getTeamId()) {
            case Team.ALLIED_TEAM_ID:
                numberOfAlliesAlive++;
                break;
            case Team.AXIS_TEAM_ID:
                numberOfAxisAlive++;
                break;                    
        }
    }
    
    
    
    /* (non-Javadoc)
     * @see seventh.game.events.PlayerKilledListener#onPlayerKilled(seventh.game.events.PlayerKilledEvent)
     */
    @Override
    public void onPlayerKilled(PlayerKilledEvent event) {
        
        Zone zone = zones.getZone(event.getPos());
        if(zone != null) {
            ZoneStats stats = zone.getStats();
            
            Player killed = event.getPlayer();
            if(killed.getTeamId()==Team.ALLIED_TEAM_ID) {
                stats.addAlliedDeath();
            }
            else {
                stats.addAxisDeath();
            }
            
            PlayerInfo killer = players.getPlayerInfo(event.getKillerId());
            if (killer != null) {
                if(killer.getTeamId()==Team.ALLIED_TEAM_ID) {
                    stats.addAlliedKill();
                }
                else {
                    stats.addAxisKill();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation me = new DebugInformation();
        // TODO
        return me;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDebugInformation().toString();
    }
}
