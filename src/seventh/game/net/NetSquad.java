/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.PlayerClass;
import seventh.game.game_types.cmd.CommanderGameType;
import seventh.game.game_types.cmd.Squad;
import seventh.network.messages.BufferIO;
import seventh.shared.SeventhConstants;

/**
 * The {@link CommanderGameType} contains {@link Squad}s which have a number
 * of {@link PlayerClass}s.  This defines which players are which class.
 * 
 * @author Tony
 *
 */
public class NetSquad implements NetMessage {
    
    // The array index relates the player players ID    
    public PlayerClass[] playerClasses = new PlayerClass[SeventhConstants.MAX_PLAYERS];
        
    @Override
    public void read(IOBuffer buffer) {            
        int playerIndexBits = buffer.getIntBits(SeventhConstants.MAX_PLAYERS);
        for(int i = 0; i < SeventhConstants.MAX_PLAYERS; i++) {
            if(((playerIndexBits >> i) & 1) == 1) {                
                playerClasses[i] = BufferIO.readPlayerClassType(buffer); 
            }
        }
                
    }
    
    @Override
    public void write(IOBuffer buffer) {
        if(playerClasses != null) {
            int playerIndexBits = 0;
            for(int i = 0; i < playerClasses.length; i++) {
                if(playerClasses[i] != null) {
                    playerIndexBits = (playerIndexBits << i) | 1;
                }
            }
            
            buffer.putIntBits(playerIndexBits, SeventhConstants.MAX_PLAYERS);
            
            for(int i = 0; i < playerClasses.length; i++) {
                if(playerClasses[i] != null) {
                    BufferIO.writePlayerClassType(buffer, playerClasses[i]);
                }
            }
        }
        else {
            buffer.putIntBits(0, SeventhConstants.MAX_PLAYERS);
        }
    }
}
