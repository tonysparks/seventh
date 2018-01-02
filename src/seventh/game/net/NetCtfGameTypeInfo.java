/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.BufferIO;

/**
 * @author Tony
 *
 */
public class NetCtfGameTypeInfo extends NetGameTypeInfo {

    public Rectangle axisHomeBase;
    public Rectangle alliedHomeBase;
    
    public Vector2f axisFlagSpawn;
    public Vector2f alliedFlagSpawn;
    
    public NetCtfGameTypeInfo() {        
    }

    @Override
    public void read(IOBuffer buffer) {
        super.read(buffer);
        
        this.axisHomeBase = BufferIO.readRect(buffer);
        this.alliedHomeBase = BufferIO.readRect(buffer);
        
        this.axisFlagSpawn = BufferIO.readVector2f(buffer);
        this.alliedFlagSpawn = BufferIO.readVector2f(buffer);
    }
    
    @Override
    public void write(IOBuffer buffer) {     
        super.write(buffer);
        
        BufferIO.writeRect(buffer, this.axisHomeBase);
        BufferIO.writeRect(buffer, this.alliedHomeBase);
        
        BufferIO.writeVector2f(buffer, this.axisFlagSpawn);
        BufferIO.writeVector2f(buffer, this.alliedFlagSpawn);
    }
}
