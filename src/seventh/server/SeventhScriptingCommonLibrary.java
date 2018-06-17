/*
 * see license.txt 
 */
package seventh.server;


import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.Trigger;
import seventh.game.entities.Door;
import seventh.game.entities.Entity;
import seventh.game.game_types.obj.BombTargetObjective;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.map.Tile;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * Some common functions exposed to the scripting runtime
 * 
 * @author Tony
 *
 */
public class SeventhScriptingCommonLibrary {

    
    /**
     * Initializes a new {@link Vector2f}
     * 
     * @param x optional x component
     * @param y optional y component
     * @return the {@link Vector2f}
     */
    public static Vector2f newVec2(Double x, Double y) {
        if(x != null && y != null)
            return new Vector2f(x.floatValue(), y.floatValue());
        if(x != null)
            return new Vector2f(x.floatValue(), 0.0f);
        return new Vector2f();        
    }

    
    /**
     * Initializes a new {@link Rectangle}
     * 
     * @param x optional x component
     * @param y optional y component
     * @param w optional w component
     * @param h optional h component
     * @return the {@link Rectangle}
     */
    public static Rectangle newRect(Integer x, Integer y, Integer w, Integer h) {
        if (x != null && y != null && w != null && h != null) {
            return new Rectangle(x, y, w, h);
        }
        
        if (x != null && y != null) {
            return new Rectangle(0, 0, x, y);
        }
        
        return new Rectangle();
    }
    
    public static BombTargetObjective newBombTarget(float x, float y, String name, Boolean rotated) {
        return new BombTargetObjective(new Vector2f(x, y), name, rotated);
    }
    
    /**
     * Places a door in the world at the specified tileX/tileY coordinates
     * 
     * @param tileX
     * @param tileY
     * @param hinge
     */
    public static Door newDoor(Game game, int tileX, int tileY, String hinge, String pos) {
        if(game.getMap().checkTileBounds(tileX, tileY)) {
           return null; 
        }
        
        Tile tile = game.getMap().getTile(0, tileX, tileY);
        int offset = 0;
        if(pos != null) {
            switch(pos.toLowerCase()) {
            case "left":
                offset = Door.DOOR_WIDTH / 2;
                break;
            case "top":
                offset = Door.DOOR_WIDTH / 2;
                break;
            case "middle":
                offset += tile.getHeight() / 2; 
                break;
            case "bottom":
                offset = tile.getHeight() - Door.DOOR_WIDTH / 2;
                break;
            case "right":
                offset = tile.getHeight() - Door.DOOR_WIDTH / 2;
                break;
            }
        }
        
        float posX = 0;
        float posY = 0;
        
        float facingX = 0;
        float facingY = 0;
        
        switch(hinge.toLowerCase()) {
            case "n":
            case "north":
                posX = tile.getX() + offset;
                posY = tile.getY();
                
                facingX = 0;
                facingY = 1;
                
                break;
            case "e":
            case "east":
                posX = tile.getX() + tile.getWidth();
                posY = tile.getY() + offset;
                
                facingX = 1;
                facingY = 0;
                
                break;
            case "s":
            case "south":
                posX = tile.getX() + offset;
                posY = tile.getY() + tile.getHeight();
                
                facingX = 0;
                facingY = -1;
                
                break;
            case "w":
            case "west":
                posX = tile.getX();
                posY = tile.getY() + offset;
                
                facingX = -1;
                facingY = 0;
                
                break;
        }
        
        return game.newDoor(posX, posY, facingX, facingY);
    }

    /**
     * Binds an explosion to a tile.  If a bullet or an explosion intersects the tile
     * at the specified location.
     * 
     * @param game
     * @param tileX
     * @param tileY
     */
    public static void newExplosiveCrate(Game game, final int tileX, final int tileY, final LeoObject function) {
        if(game.getMap().checkTileBounds(tileX, tileY)) {
            Cons.println("Invalid tile position: " + tileX + ", " + tileY);
            return;
        }
        
        final Tile tile = game.getMap().getTile(0, tileX, tileY);                
        game.addTrigger(new Trigger() {
            
            Entity triggeredEntity = null;
            
            @Override
            public boolean checkCondition(Game game) {
                Entity[] entities = game.getEntities();
                for(int i = 0; i < entities.length; i++) {
                    Entity ent = entities[i];
                    if(ent != null) {
                        if(ent instanceof Bullet ||
                           ent instanceof Explosion) {
                            
                            if(tile.getBounds().intersects(ent.getBounds())) {
                                triggeredEntity = ent;
                                return true;
                            }
                        }
                    }                    
                }
                return false;
            }
            
            @Override
            public void execute(Game game) {
                if(triggeredEntity != null) {
                    game.newBigExplosion(new Vector2f(tile.getX(),tile.getY()), triggeredEntity, 15, 25, 1);
                }
                
                if(function != null) {
                    LeoObject result = function.call(triggeredEntity.asScriptObject());
                    if(result.isError()) {
                        Cons.println("*** Error calling trigger function for exploting crate: \n" + result);
                    }
                }
            }
        });
    }
}
