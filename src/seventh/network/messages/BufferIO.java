/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import harenet.messages.NetMessageFactory;
import seventh.game.Entity.Type;
import seventh.game.net.NetBomb;
import seventh.game.net.NetBullet;
import seventh.game.net.NetDroppedItem;
import seventh.game.net.NetEntity;
import seventh.game.net.NetExplosion;
import seventh.game.net.NetFire;
import seventh.game.net.NetLight;
import seventh.game.net.NetPlayer;
import seventh.game.net.NetPlayerPartial;
import seventh.game.net.NetRocket;
import seventh.game.net.NetTank;
import seventh.math.Vector2f;

/**
 * Some {@link IOBuffer} utilities
 * 
 * @author Tony
 *
 */
public class BufferIO {
	public static final byte BOMB_DIARMED = 1;
	public static final byte BOMB_EXPLODED = 2;
	public static final byte BOMB_PLANTED = 3;
	
	public static final byte CLIENT_DISCONNECTED = 4;
	public static final byte CLIENT_READY = 5;
	
	public static final byte CONNECT_ACCEPTED = 6;
	public static final byte CONNECT_REQUEST = 7;
	
	public static final byte GAME_ENDED = 8;
	public static final byte GAME_READY = 9;
	public static final byte GAME_STATS = 10;
	public static final byte GAME_PARTIAL_STATS = 11;
	public static final byte GAME_UPDATE = 12;
	
	public static final byte PLAYER_CONNECTED = 13;
	public static final byte PLAYER_DISCONNECTED = 14;
	public static final byte PLAYER_KILLED = 15;
	public static final byte PLAYER_SPAWNED = 16;
	public static final byte PLAYER_SWITCH_TEAM = 17;
	public static final byte PLAYER_NAME_CHANGE = 18;
	public static final byte PLAYER_WEAPON_CLASS_CHANGE = 19;
	public static final byte PLAYER_SPEECH = 20;
	
	
	public static final byte ROUND_ENDED = 21;
	public static final byte ROUND_STARTED = 22;
	public static final byte SPECTATING_PLAYER = 23;
	public static final byte TEAM_TEXT = 24;
	public static final byte TEXT = 25;
	public static final byte PLAYER_INPUT = 26;
	
	public static final byte RCON_MESSAGE = 27;
	public static final byte RCON_TOKEN_MESSAGE = 28;
	
	public static final byte AI_COMMAND = 29;
	
	public static final byte TILE_REMOVED = 30;
	public static final byte TILES_REMOVED= 31;
	
	/**
	 * The Seventh {@link NetMessageFactory} implementation
	 * 
	 * @author Tony
	 *
	 */
	public static class SeventhNetMessageFactory implements NetMessageFactory {
		
		/*
		 * (non-Javadoc)
		 * @see harenet.messages.NetMessageFactory#readNetMessage(harenet.IOBuffer)
		 */
		public NetMessage readNetMessage(IOBuffer buffer) {
			NetMessage message = null;
							
			byte type = buffer.get();
			switch(type) {
				case BOMB_DIARMED: message = new BombDisarmedMessage();
					break;
				case BOMB_EXPLODED: message = new BombExplodedMessage();
					break;
				case BOMB_PLANTED: message = new BombPlantedMessage();
					break;
				case CLIENT_DISCONNECTED : message = new ClientDisconnectMessage();
					break;
				case CLIENT_READY: message = new ClientReadyMessage();
					break;
				case CONNECT_ACCEPTED: message = new ConnectAcceptedMessage();
					break;
				case CONNECT_REQUEST: message = new ConnectRequestMessage();
					break;
				case GAME_ENDED: message = new GameEndedMessage();
					break;
				case GAME_READY: message = new GameReadyMessage();
					break;
				case GAME_STATS: message = new GameStatsMessage();
					break;
				case GAME_PARTIAL_STATS: message = new GamePartialStatsMessage();
					break;
				case GAME_UPDATE: message = new GameUpdateMessage();
					break;
				case PLAYER_CONNECTED: message = new PlayerConnectedMessage();
					break;
				case PLAYER_DISCONNECTED: message = new PlayerDisconnectedMessage();
					break;
				case PLAYER_KILLED: message = new PlayerKilledMessage();
					break;
				case PLAYER_SPAWNED: message = new PlayerSpawnedMessage();
					break;
				case PLAYER_SWITCH_TEAM : message = new PlayerSwitchTeamMessage();
					break;
				case PLAYER_NAME_CHANGE: message = new PlayerNameChangeMessage();
					break;
				case PLAYER_WEAPON_CLASS_CHANGE: message = new PlayerSwitchWeaponClassMessage();
					break;
				case PLAYER_SPEECH: message = new PlayerSpeechMessage();
					break;
				case PLAYER_INPUT: message = new PlayerInputMessage();
                    break;					
				case ROUND_ENDED: message = new RoundEndedMessage();
					break;
				case ROUND_STARTED: message = new RoundStartedMessage();
					break;
				case SPECTATING_PLAYER: message = new SpectatingPlayerMessage();
					break;
				case TEAM_TEXT: message = new TeamTextMessage();
					break;
				case TEXT: message = new TextMessage();
					break;				
				case RCON_MESSAGE: message = new RconMessage();
					break;
				case RCON_TOKEN_MESSAGE: message = new RconTokenMessage();
					break;
				case AI_COMMAND: message = new AICommandMessage();
					break;				
				case TILE_REMOVED: message = new TileRemovedMessage();
				    break;
				case TILES_REMOVED: message = new TilesRemovedMessage();
				    break;
				default: throw new IllegalArgumentException("Unknown type: " + type);
			}
			
			message.read(buffer);
			
			return message;
		}
	}
	
	
	public static void write(IOBuffer buffer, Vector2f v) {
		buffer.putFloat(v.x);
		buffer.putFloat(v.y);
	}

	public static Vector2f readVector2f(IOBuffer buffer) {
		Vector2f v = new Vector2f();
		v.x = buffer.getFloat();
		v.y = buffer.getFloat();
		
		return v;
	}
	
	public static void write(IOBuffer buffer, String str) {
		
		byte[] chars = str.getBytes();
		int len = chars.length;
		buffer.putUnsignedByte(len);
		for(byte i = 0; i < len; i++) {
			buffer.put(chars[i]);
		}		
	}
	
	public static String readString(IOBuffer buffer) {
		int len = buffer.getUnsignedByte();
		byte[] chars = new byte[len];
		for(byte i = 0; i < len; i++) {
			chars[i] = buffer.get();
		}
		
		return new String(chars);
	}
	
	public static NetEntity readEntity(IOBuffer buffer) {
		NetEntity result = null;
		
		byte type = buffer.get();
		buffer.position(buffer.position() - 1); /* so the entity can re-read the type */
		
		Type entType = Type.fromNet(type);
		switch(entType) {
			case ROCKET:
				result = new NetRocket();
				break;
			case NAPALM_GRENADE:
			case GRENADE:			
			case BULLET: {
				result = new NetBullet();
				break;
			}
			case DROPPED_ITEM: {
				result = new NetDroppedItem();
				break;
			}
			case BOMB: {
				result = new NetBomb();
				break;
			}
			case BOMB_TARGET: {
				result = new NetEntity();
				break;
			}
			case PLAYER_PARTIAL: {
				result = new NetPlayerPartial();
				break;
			}
			case PLAYER: {
				result = new NetPlayer();			
				break;
			}
			case EXPLOSION: {
				result = new NetExplosion();
				break;
			}			
			case FIRE: {
				result = new NetFire();
				break;
			}
			case LIGHT_BULB: {
				result = new NetLight();
				break;
			}
			case TANK: {
				result = new NetTank();
				break;
			}
			default: {
				result = new NetEntity();				
			}
		}
		
		result.read(buffer);
		return result;
	}
}
