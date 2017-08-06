/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.type.cmd.Squad;

/**
 * @author Tony
 *
 */
public class NetSquad implements NetMessage {

    public NetFireTeam[] squad;
    
    public NetSquad() {
        this.squad = new NetFireTeam[Squad.MAX_FIRETEAMS];
    }

    @Override
    public void read(IOBuffer buffer) {
        for(int i = 0; i < this.squad.length; i++) {
            this.squad[i] = new NetFireTeam();
            this.squad[i].read(buffer);
        }
    }
    
    @Override
    public void write(IOBuffer buffer) {
        for(int i = 0; i < this.squad.length; i++) {
            this.squad[i].write(buffer);
        }
    }
}
