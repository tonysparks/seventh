/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import harenet.messages.NetMessageFactory;
import seventh.game.PlayerClass;
import seventh.game.entities.Entity.State;
import seventh.game.entities.Entity.Type;
import seventh.game.net.NetBomb;
import seventh.game.net.NetBombTarget;
import seventh.game.net.NetBullet;
import seventh.game.net.NetDoor;
import seventh.game.net.NetDroppedItem;
import seventh.game.net.NetEntity;
import seventh.game.net.NetExplosion;
import seventh.game.net.NetFire;
import seventh.game.net.NetFlag;
import seventh.game.net.NetLight;
import seventh.game.net.NetPlayer;
import seventh.game.net.NetPlayerPartial;
import seventh.game.net.NetRocket;
import seventh.game.net.NetSmoke;
import seventh.game.net.NetTank;
import seventh.game.weapons.Weapon.WeaponState;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * Some {@link IOBuffer} utilities
 * 
 * @author Tony
 *
 */
public class BufferIO {
    public static final byte BOMB_DIARMED  = 1;
    public static final byte BOMB_EXPLODED = 2;
    public static final byte BOMB_PLANTED  = 3;
    
    public static final byte CLIENT_DISCONNECTED = 4;
    public static final byte CLIENT_READY        = 5;
    
    public static final byte CONNECT_ACCEPTED = 6;
    public static final byte CONNECT_REQUEST  = 7;
    
    public static final byte GAME_ENDED  = 8;
    public static final byte GAME_READY  = 9;
    public static final byte GAME_STATS  = 10;
    public static final byte GAME_PARTIAL_STATS = 11;
    public static final byte GAME_UPDATE = 12;
    
    public static final byte PLAYER_CONNECTED = 13;
    public static final byte PLAYER_DISCONNECTED = 14;
    public static final byte PLAYER_KILLED = 15;
    public static final byte PLAYER_SPAWNED = 16;
    public static final byte PLAYER_SWITCH_TEAM = 17;
    public static final byte PLAYER_NAME_CHANGE = 18;
    public static final byte PLAYER_WEAPON_CLASS_CHANGE = 19;
    public static final byte PLAYER_CLASS_CHANGE = 20;
    public static final byte PLAYER_SPEECH = 21;
    public static final byte PLAYER_COMMANDER = 22;    
    public static final byte PLAYER_AWARD  = 23;
    public static final byte PLAYER_SWITCH_TILE  = 24;
    public static final byte PLAYER_INPUT = 25;
    
    public static final byte ROUND_ENDED   = 26;
    public static final byte ROUND_STARTED = 27;
    public static final byte SPECTATING_PLAYER = 28;
    
    public static final byte TEAM_TEXT   = 29;
    public static final byte TEXT        = 30;
    
    public static final byte RCON_MESSAGE       = 31;
    public static final byte RCON_TOKEN_MESSAGE = 32;
    
    public static final byte AI_COMMAND = 33;
    
    public static final byte TILE_REMOVED  = 34;
    public static final byte TILES_REMOVED = 35;
    
    public static final byte TILE_ADDED  = 36;
    public static final byte TILES_ADDED = 37;
    
    public static final byte FLAG_CAPTURED = 38;
    public static final byte FLAG_RETURNED = 39;
    public static final byte FLAG_STOLEN   = 40;
    
    public static final byte GAME_EVENT  = 41;
    
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
                            
            byte type = buffer.getByteBits(6); // must match AbstractNetMessage.write
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
                case PLAYER_CLASS_CHANGE: message = new PlayerSwitchPlayerClassMessage();
                    break;
                case PLAYER_SPEECH: message = new PlayerSpeechMessage();
                    break;
                case PLAYER_SWITCH_TILE: message = new PlayerSwitchTileMessage();
                    break;
                case PLAYER_INPUT: message = new PlayerInputMessage();
                    break;                    
                case PLAYER_COMMANDER: message = new PlayerCommanderMessage();
                    break;
                case PLAYER_AWARD: message = new PlayerAwardMessage();
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
                case TILE_ADDED: message = new TileAddedMessage();
                    break;
                case TILES_ADDED: message = new TilesAddedMessage();
                    break;
                case FLAG_CAPTURED: message = new FlagCapturedMessage();
                    break;
                case FLAG_RETURNED: message = new FlagReturnedMessage();
                    break;
                case FLAG_STOLEN: message = new FlagStolenMessage();
                    break;
                case GAME_EVENT: message = new GameEventMessage();
                    break;
                default: throw new IllegalArgumentException("Unknown type: " + type);
            }
            
            message.read(buffer);
            
            return message;
        }
    }

    
    public static void writePos(IOBuffer buffer, float worldPos) {
        writePos(buffer, (int) worldPos);
    }
    
    public static void writePos(IOBuffer buffer, int worldPos) {
        buffer.putIntBits(worldPos, 13);
    }
    
    public static int readPos(IOBuffer buffer) {
        return buffer.getIntBits(13);
    }
    
    public static void writeWeaponState(IOBuffer buffer, WeaponState state) {
        buffer.putByteBits(state.netValue(), WeaponState.numOfBits());
    }
    
    public static WeaponState readWeaponState(IOBuffer buffer) {
        byte state = buffer.getByteBits(WeaponState.numOfBits());
        return WeaponState.fromNet(state);
    }
    
    public static void writeState(IOBuffer buffer, State state) {
        buffer.putByteBits(state.netValue(), State.numOfBits());
    }
    
    public static State readState(IOBuffer buffer) {
        byte state = buffer.getByteBits(State.numOfBits());
        return State.fromNetValue(state);
    }

    
    public static void writeType(IOBuffer buffer, Type type) {
        buffer.putByteBits(type.netValue(), Type.numOfBits());
    }
    
    public static Type readType(IOBuffer buffer) {
        byte type = buffer.getByteBits(Type.numOfBits());
        return Type.fromNet(type);
    }
    
    public static int numPlayerIdBits() {
        return 5;
    }
    
    public static void writePlayerId(IOBuffer buffer, int playerId) {
        buffer.putIntBits(playerId, numPlayerIdBits());
    }
    
    public static int readPlayerId(IOBuffer buffer) {
        return buffer.getIntBits(numPlayerIdBits());
    }
    
    public static void writeTeamId(IOBuffer buffer, byte teamId) {
        buffer.putByteBits(teamId, 2);
    }
    
    public static byte readTeamId(IOBuffer buffer) {
        return buffer.getByteBits(2);
    }
        
    public static void writeAngle(IOBuffer buffer, int degrees) {
        int bangle = (degrees * 256) / 360;
        buffer.putUnsignedByte(bangle);
    }
        
    public static short readAngle(IOBuffer buffer) {
        int bangle = buffer.getUnsignedByte();
        int degree = (bangle * 360) / 256;
        return (short)degree;
    }
    
    public static Vector2f readVector2f(IOBuffer buffer) {
        Vector2f v = new Vector2f();
        v.x = readPos(buffer);
        v.y = readPos(buffer);
        
        return v;
    }
    
    public static void writeVector2f(IOBuffer buffer, Vector2f v) {
        writePos(buffer, v.x);
        writePos(buffer, v.y);
    }
    
    public static Rectangle readRect(IOBuffer buffer) {
        Rectangle r = new Rectangle();
        r.x = readPos(buffer);
        r.y = readPos(buffer);
        r.width = readPos(buffer);
        r.height = readPos(buffer);
        return r;
    }
    
    public static void writeRect(IOBuffer buffer, Rectangle r) {
        writePos(buffer, r.x);
        writePos(buffer, r.y);
        writePos(buffer, r.width);
        writePos(buffer, r.height);
    }
    
    public static void writeString(IOBuffer buffer, String str) {
        
        byte[] chars = str.getBytes();
        int len = chars.length;
        buffer.putUnsignedByte(len);
        for(int i = 0; i < len; i++) {
            buffer.putByte(chars[i]);
        }        
    }
    
    public static String readString(IOBuffer buffer) {
        int len = buffer.getUnsignedByte();
        byte[] chars = new byte[len];
        for(int i = 0; i < len; i++) {
            chars[i] = buffer.getByte();
        }
        
        return new String(chars);
    }
    
    public static void writeBigString(IOBuffer buffer, String str) {
        
        byte[] chars = str.getBytes();
        int len = chars.length;
        buffer.putShort((short)len);
        for(int i = 0; i < len; i++) {
            buffer.putByte(chars[i]);
        }        
    }
    
    public static String readBigString(IOBuffer buffer) {
        int len = buffer.getShort();
        byte[] chars = new byte[len];
        for(int i = 0; i < len; i++) {
            chars[i] = buffer.getByte();
        }
        
        return new String(chars);
    }
    
    public static int readTileType(IOBuffer buffer) {
        return buffer.getIntBits(8);
    }
    
    public static void writeTileType(IOBuffer buffer, int tileType) {
        buffer.putIntBits(tileType, 8);
    }
    
    public static void writePlayerClassType(IOBuffer buffer, PlayerClass playerClass) {
        buffer.putByteBits(PlayerClass.toNet(playerClass), 3);
    }
    
    public static PlayerClass readPlayerClassType(IOBuffer buffer) {
        return PlayerClass.fromNet(buffer.getByteBits(3));
    }
    
    public static NetEntity readEntity(IOBuffer buffer) {
        NetEntity result = null;
        
        byte type = buffer.getByteBits(Type.numOfBits());
        /* so the entity can re-read the type */
        buffer.bitPosition(buffer.bitPosition() - Type.numOfBits());
        
        Type entType = Type.fromNet(type);
        switch(entType) {
            case ROCKET:
                result = new NetRocket();
                break;
            case SMOKE_GRENADE:
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
                result = new NetBombTarget();
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
            case SMOKE: {
                result = new NetSmoke();
                break;
            }
            case LIGHT_BULB: {
                result = new NetLight();
                break;
            }
            case DOOR: {
                result = new NetDoor();
                break;
            }
            case SHERMAN_TANK: {
                result = new NetTank(Type.SHERMAN_TANK);
                break;
            }
            case PANZER_TANK: {
                result = new NetTank(Type.PANZER_TANK);
                break;
            }
            case ALLIED_FLAG: {
                result = new NetFlag(Type.ALLIED_FLAG);
                break;
            }
            case AXIS_FLAG: {
                result = new NetFlag(Type.AXIS_FLAG);
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
