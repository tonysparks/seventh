/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetGameUpdate;

/**
 * @author Tony
 *
 */
public class GameUpdateMessage extends AbstractNetMessage {
	public NetGameUpdate netUpdate;
	
	/**
	 * 
	 */
	public GameUpdateMessage() {
		super(BufferIO.GAME_UPDATE);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		netUpdate = new NetGameUpdate();
		netUpdate.read(buffer);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		netUpdate.write(buffer);
	}
}
