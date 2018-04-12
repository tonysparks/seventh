/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class NetCommanderGameTypeInfo extends NetGameTypeInfo {

    public NetSquad alliedSquad;
    public NetSquad axisSquad;

    @Override
    public void read(IOBuffer buffer) {
        super.read(buffer);
        this.alliedSquad = new NetSquad();
        this.alliedSquad.read(buffer);
        
        this.axisSquad = new NetSquad();
        this.axisSquad.read(buffer);
        
    }
    
    @Override
    public void write(IOBuffer buffer) {     
        super.write(buffer);
        
        this.alliedSquad.write(buffer);
        this.axisSquad.write(buffer);        
    }
}
