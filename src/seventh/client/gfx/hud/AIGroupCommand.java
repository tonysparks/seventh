/*
 * see license.txt 
 */
package seventh.client.gfx.hud;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.game.net.NetFireTeam;

/**
 * @author Tony
 *
 */
public abstract class AIGroupCommand {

    private int shortcutKey;
    private String description;
    private AIShortcut cmd;
    
    /**
     * @param shortcutKey 
     */
    public AIGroupCommand(int shortcutKey, String description, AIShortcut cmd) {
        this.shortcutKey = shortcutKey;
        this.description = description;
        this.cmd = cmd;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the shortcutKey
     */
    public int getShortcutKey() {
        return shortcutKey;
    }    
    
    public void execute(ClientGame game) {        
        NetFireTeam fireTeam = game.getLocalPlayersFireTeam();
        if(fireTeam!=null) {
            for(int i = 0; i < fireTeam.memberPlayerIds.length; i++) {
                ClientPlayer aiPlayer = game.getPlayers().getPlayer(fireTeam.memberPlayerIds[i]);
                if(aiPlayer!=null) {
                    cmd.execute(game.getApp().getConsole(), game, aiPlayer);
                }
            }
        }
    }

}
