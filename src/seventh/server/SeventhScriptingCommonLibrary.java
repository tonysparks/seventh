/*
 * see license.txt 
 */
package seventh.server;


import seventh.game.Game;
import seventh.game.Trigger;
import seventh.game.entities.Entity;
import seventh.game.type.BombTargetObjective;
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
        if(x!=null&&y!=null)
            return new Vector2f(x.floatValue(),y.floatValue());
        if(x!=null)
            return new Vector2f(x.floatValue(),0.0f);
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
     * Binds an explosion to a tile.  If a bullet or an explosion intersects the tile
     * at the specified location.
     * 
     * @param game
     * @param x
     * @param y
     */
    public static void newExplosiveCrate(Game game, final int x, final int y) {
        final Tile tile = game.getMap().getWorldTile(0, x, y);
        if(tile==null) {
            Cons.println("Invalid tile position: " + x + ", " + y);
            return;
        }
        
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
                if(triggeredEntity!=null) {
                    game.newBigExplosion(new Vector2f(x,y), triggeredEntity, 15, 25, 1);
                }
            }
        });
    }
}
