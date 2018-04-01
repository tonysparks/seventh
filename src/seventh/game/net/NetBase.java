/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;

/**
 * @author Tony
 *
 */
public class NetBase extends NetEntity {               
    
    public NetBase(Type type) {
        this.type = type;
    }
    
    public NetBase() {
    }
    
    @Override
    public void read(IOBuffer buffer) {
        super.read(buffer);
    }
    

    @Override
    public void write(IOBuffer buffer) {
        super.write(buffer);
    }
}
