/*
 * see license.txt 
 */
package seventh.game;


import seventh.game.entities.Entity;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.Fire;

/**
 * @author Tony
 *
 */
public class PlayerStatSystem {

    private class PlayerStat {
        Players players;
        
        /**
         * List of players that have damaged
         * this player
         */
        private Player[] playersWhoCausedDamage;
        
        /**
         * This player that we are keeping track of
         * who shot them (for tracking assists)
         */
        private int playerId;
        
        private int numberOfBulletsFired;
        private int numberOfHits;
        
        PlayerStat(Players players, int playerId) {
            this.players = players;
            this.playerId = playerId;
            this.playersWhoCausedDamage = new Player[players.maxNumberOfPlayers()];
        }
        
        private Entity getPlayer(Entity damager) {
            Entity player = null;
            if(damager.isPlayer()) {
                player = damager;
            }
            else if(damager instanceof Bullet) {
                Bullet bullet = (Bullet) damager;
                player = bullet.getOwner();
            }
            else if(damager instanceof Explosion) {
                Explosion explosion = (Explosion)damager;
                player = explosion.getOwner();
            }
            else if(damager instanceof Fire) {
                Fire fire = (Fire)damager;
                player = fire.getOwner();
            }
            
            return player;
        }

        public void onBulletFired() {
            this.numberOfBulletsFired++;
        }
        
        public void onDamaged(Entity damager) {
            Entity player = getPlayer(damager);
            
            // mark that the damager dealt damage            
            if(player!=null) {
                int damagerId = player.getId();
                if(this.players.isValidId(damagerId)) {
                    PlayerStat stat = stats[damagerId];
                    
                    Player damagerPlayer = this.players.getPlayer(damagerId);
                    if(!damagerPlayer.isTeammateWith(this.playerId)) {
                        // mark this damagers hit                    
                        stat.numberOfHits++;                               
                    
                        // mark that the damager dealt damage to this player
                        if(this.playerId != damagerId) {
                            this.playersWhoCausedDamage[damagerId] = damagerPlayer;
                        }
                    }
                    
    
                    if(stat.numberOfBulletsFired > 0) {
                        damagerPlayer.setHitPercentage( (float)stat.numberOfHits / (float)stat.numberOfBulletsFired);
                    }
                }
            }
        }
            
        public void onDeath(Entity killer) {
            Entity player = getPlayer(killer);
            
            // don't give an assist to the killer
            if(player!=null && this.players.isValidId(player.getId())) {
                this.playersWhoCausedDamage[player.getId()] = null;
            }
            
            for(int i = 0; i < this.playersWhoCausedDamage.length; i++) {
                Player p = this.playersWhoCausedDamage[i];
                if(p!=null) {
                    p.incrementAssists();
                }
                
                this.playersWhoCausedDamage[i] = null;
            }
        }
        
        public void reset() {
            for(int i = 0; i < this.playersWhoCausedDamage.length; i++) {                
                this.playersWhoCausedDamage[i] = null;
            }
        }
    }
    
    private Players players;
    private PlayerStat[] stats;
    
    /**
     * 
     */
    public PlayerStatSystem(final Game game) {
        this.players = game.getPlayers();
        this.stats = new PlayerStat[players.maxNumberOfPlayers()];
        for(int i = 0; i < stats.length; i++) {
            this.stats[i] = new PlayerStat(players, i);
        }
    }
    
    public void onRoundStarted() {
        for(int i = 0; i < stats.length; i++) {
            this.stats[i].reset();
        }
    }

    public void onPlayerKilled(PlayerKilledEvent event) {
        Player killed = event.getPlayer();
        killed.incrementDeaths();                    
        
        stats[killed.getId()].onDeath(event.getKilledBy());
        
        Player killer = players.getPlayer(event.getKillerId());                    
        if (killer != null) {

            // lose a point for team kills
            if (killed.getTeam().getId() == killer.getTeam().getId()) {
                killer.loseKill();
            }
            // lose a point for suicide
            else if (killed.getId() == killer.getId()) {
                killer.loseKill();
            } else {
                killer.incrementKills();
            }
        }        
    }
    
    public void onPlayerDamaged(Entity damaged, Entity damager, int damage) {
        if(damaged.isPlayer()) {
            this.stats[damaged.getId()].onDamaged(damager);
        }
    }
    
    public void onBulletFired(Entity owner) {
        if(owner.isPlayer()) {
            this.stats[owner.getId()].onBulletFired();
        }
    }
}
