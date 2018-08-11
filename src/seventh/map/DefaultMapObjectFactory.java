/*
 * see license.txt 
 */
package seventh.map;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.client.ClientGame;
import seventh.client.entities.ClientEntity;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.TextureUtil;
import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.vehicles.Vehicle;
import seventh.game.weapons.Bullet;
import seventh.map.Tile.CollisionMask;
import seventh.map.Tile.SurfaceType;
import seventh.math.OBB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;
import seventh.shared.JSON;

/**
 * @author Tony
 *
 */
public class DefaultMapObjectFactory implements MapObjectFactory {

    public static class ImageData {
        public String path;
        public int x, y, width, height;
    }
    
    public static class MapObjectDefinition {
        public String type;    
        public float width, height;
        public boolean isCollidable;
        public boolean allowRotation;
        public int heightMask;
        public String surfaceType;
        public ImageData image;
        public LeoObject onTouched;
        public LeoObject onLoad;
    }
    
    public static class TileDefinition {
        public int type;
        public int layer;
        public int tileId;
        
        public int collisionMaskId = CollisionMask.ALL_SOLID.ordinal();
        public int heightMask = 0;
        
        public int width = 32, height = 32;
    }
    
    private static class DefaultMapObject extends MapObject {
        
        private OBB obb;        
        private int heightMask;
        private boolean isCollidable;
        
        private boolean loadAssets;
        
        private Sprite sprite;
        private SurfaceType surfaceType;
        
        private LeoObject onTouched;
        private LeoObject onLoad;
        
        public DefaultMapObject(boolean loadAssets, MapObjectDefinition definition, MapObjectData data) {
            super(data);
            
            this.loadAssets = loadAssets;
            this.pos.set(data.x, data.y);
            
            this.surfaceType = SurfaceType.fromString(definition.surfaceType);
            
            Rectangle rect = new Rectangle((int)data.width, (int)data.height);
            rect.setLocation(pos);
            
            if(definition.width != 0 && definition.height != 0) {
                rect.setSize((int)definition.width, (int)definition.height);
            }
            
            this.onTouched = definition.onTouched;
            this.onLoad = definition.onLoad;
            
            this.rotation = data.rotation;
            float rotRadians = (float) Math.toRadians(data.rotation);
            
            this.obb = new OBB(rect);
            this.obb.rotateAround(pos, rotRadians);
            
            if(definition.allowRotation) {
                int length = (int)this.obb.length();
                this.bounds.setSize(length, length);
                this.bounds.centerAround(this.obb.getCenter());
            }
            else {
                this.bounds.set(rect);
            }
            
            this.heightMask = definition.heightMask;
            this.isCollidable = definition.isCollidable;
            
            if(data.properties != null) {
                if(data.properties.containsKeyByString("heightMask")) {            
                    String heightMask = data.properties.getString("heightMask");
                    this.heightMask = Integer.valueOf(heightMask);
                }
                if(data.properties.containsKeyByString("collidable")) {
                    this.isCollidable = true;
                }
                if(data.properties.containsKeyByString("onTouched")) {
                    this.onTouched = LeoObject.valueOf(data.properties.getString("onTouched"));
                }
                if(data.properties.containsKeyByString("onLoad")) {
                    this.onLoad = LeoObject.valueOf(data.properties.getString("onLoad"));
                }
            }
            
            // 
            // TODO: Manage these resources, must delete after map load,
            // and only load them again if they weren't loaded before
            //
            if(loadAssets && definition.image != null) {          
                ImageData image = definition.image;
                TextureRegion texture = TextureUtil.loadImage(image.path);
                if(image.width > 0) {
                    texture = TextureUtil.subImage(texture, image.x, image.y, image.width, image.height);                    
                }
                
                this.sprite = new Sprite(texture);
                //this.sprite.setPosition(data.x, data.y);
                this.sprite.setSize(rect.width, rect.height);
                this.sprite.setOrigin(0, 0); // TileD origin coordinates
                this.sprite.setRotation(data.rotation);                
            }
            
        }
                
        @Override
        public void onLoad(Game game) {
            if(this.onLoad != null) {         
                LeoObject func = null;
                if(this.onLoad.isFunction()) {
                    func = this.onLoad;
                }
                else {
                    func = game.getGameType().getRuntime().get(this.onLoad.toString());
                }
                
                if(func!=null) {
                    LeoObject result = func.call(game.asScriptObject(), asScriptObject());
                    if(result.isError()) {
                        Cons.println("*** ERROR: Error onLoad MapObject: " + result);
                    }                    
                }                                
            }
        }
        
        @Override
        public void onClientLoad(ClientGame game) {
            if(this.onLoad != null) {         
                LeoObject func = null;
                if(this.onLoad.isFunction()) {
                    func = this.onLoad;
                }
                else {
                    func = game.getRuntime().get(this.onLoad.toString());
                }
                
                if(func!=null) {
                    LeoObject result = func.call(game.asScriptObject(), asScriptObject()); 
                    if(result.isError()) {
                        Cons.println("*** ERROR: Error onLoad MapObject: " + result);
                    }
                }                                
            }
        }
        
        @Override
        public void destroy() {
            if(loadAssets) {
                if(this.sprite != null) {
                    this.sprite.getTexture().dispose();
                }
            }
        }
        
        @Override
        public Vector2f getCenterPos() {
            this.centerPos.set(this.obb.getCenter());
            return this.centerPos;
        }
        
        @Override
        public boolean isCollidable() {         
            return this.isCollidable;
        }
        
        @Override
        public SurfaceType geSurfaceType() {
            return this.surfaceType;
        }
        
        
        @Override
        public boolean isTouching(Rectangle bounds) {
            if(!this.bounds.intersects(bounds)) {
                return false;
            }

            if(this.rotation == 0) {
                return true;
            }
            
            return this.obb.expensiveIntersects(bounds);
        }
        
        @Override
        public boolean isTouching(Entity ent) {
            if(!this.bounds.intersects(ent.getBounds())) {
                return false;
            }
            
            if(ent instanceof Vehicle) {
                Vehicle vehicle = (Vehicle)ent;
                return vehicle.getOBB().intersects(bounds);
            }
            
            if(ent instanceof Bullet) {
                Bullet bullet = (Bullet)ent;
                if(bullet.getOwnerHeightMask() == this.heightMask) {
                    if(this.rotation == 0) {
                        return true;
                    }
                    
                    return this.obb.expensiveIntersects(bullet.getBounds());
                }
                return false;
            }
            
            if(this.rotation == 0) {
                return true;
            }
            
            return this.obb.expensiveIntersects(ent.getBounds());
        }
        
        @Override
        public boolean onTouch(Game game, Entity ent) {
            if(this.onTouched != null) {         
                LeoObject func = null;
                if(this.onTouched.isFunction()) {
                    func = this.onTouched;
                }
                else {
                    func = game.getGameType().getRuntime().get(this.onTouched.toString());
                }
                
                if(func!=null) {
                    LeoObject result = func.call(game.asScriptObject(), ent.asScriptObject(), asScriptObject());
                    if(result.isError()) {
                        Cons.println("*** ERROR: Error touching MapObject: " + result);
                    }
                    
                    return result.isTrue();
                }                                
            }
            
            return true;
        }
        
        @Override
        public boolean onClientTouch(ClientGame game, ClientEntity ent) {
            if(this.onTouched != null) {         
                LeoObject func = null;
                if(this.onTouched.isFunction()) {
                    func = this.onTouched;
                }
                else {
                    func = game.getRuntime().get(this.onTouched.toString());
                }
                
                if(func!=null) {
                    LeoObject result = func.call(game.asScriptObject(), ent.asScriptObject(), asScriptObject()); 
                    if(result.isError()) {
                        Cons.println("*** ERROR: Error touching MapObject: " + result);
                    }
                    
                    return result.isTrue();
                }                                
            }
            
            return true;
        }
        
        @Override
        public void render(Canvas canvas, Camera camera, float alpha) {
            if(this.loadAssets && this.sprite != null) {
                Vector2f cameraPos = camera.getRenderPosition(alpha);
                float x = this.pos.x - cameraPos.x;
                float y = this.pos.y - cameraPos.y;
                this.sprite.setPosition(x, y);
                canvas.drawRawSprite(this.sprite);
                //canvas.drawRect(bounds.x - cameraPos.x, bounds.y - cameraPos.y, bounds.width, bounds.height, 0xff00ff00);
                
                //center.x-width/2f, center.y+height/2f
                //obb.setLocation(/*pos.x +*/ center.x-bounds.width/2f, /*pos.y +*/ center.y+bounds.height/2f);
                                
//                DebugDraw.drawRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xffffff00);
//                DebugDraw.fillRectRelative((int)obb.center.x, (int)obb.center.y, 5, 5, 0xffff0000);
            }
            //DebugDraw.drawOOBRelative(obb, 0xff00ff00);
            //DebugDraw.drawRectRelative(getBounds(), 0xff00ff00);
        }
    }
    
    
    private Map<String, MapObjectDefinition> objectDefinitions;
    private Map<Integer, TileDefinition> tileDefinitions;
    
    private boolean loadAssets;
    /**
     * 
     */
    public DefaultMapObjectFactory(Leola runtime, String mapFile, boolean loadAssets) throws Exception {
        this.loadAssets = loadAssets;
        this.objectDefinitions = new HashMap<>();
        this.tileDefinitions = new HashMap<>();
        
        loadObjectFile(runtime, "./assets/maps/map-objects.leola");
        loadObjectFile(runtime, mapFile + ".objects.leola");        
    }

    private void loadObjectFile(Leola runtime, String fileName) throws Exception {
        File objectsFile = new File(fileName);
        if(objectsFile.exists()) {
            String contents = new String(Files.readAllBytes(objectsFile.toPath()));
            LeoMap objectData = JSON.parseJson(runtime, contents).as();
            
            if(objectData != null) {
                LeoArray objects = objectData.getArray("objects");
                for(LeoObject object : objects) {
                    MapObjectDefinition definition = LeoObject.fromLeoObject(object, MapObjectDefinition.class);
                    this.objectDefinitions.put(definition.type.toLowerCase(), definition);
                }
                
                LeoArray tiles = objectData.getArray("tiles");
                int typeIndex = 0;
                for(LeoObject tile : tiles) {
                    TileDefinition definition = LeoObject.fromLeoObject(tile, TileDefinition.class);
                    definition.type = typeIndex;
                    
                    this.tileDefinitions.put(definition.type, definition);
                    
                    typeIndex++;
                }
            }
        }
    }
    
    /**
     * @return the objectDefinitions
     */
    public Map<String, MapObjectDefinition> getObjectDefinitions() {
        return objectDefinitions;
    }
    
    /**
     * @return the tileDefinitions
     */
    public Map<Integer, TileDefinition> getTileDefinitions() {
        return tileDefinitions;
    }
    
    @Override
    public MapObject createMapObject(MapObjectData data) {
        MapObjectDefinition definition = this.objectDefinitions.get(data.type.toLowerCase());
        if(definition == null) {
            return null;
        }
        
        return new DefaultMapObject(loadAssets, definition, data);
    }
    
    @Override
    public Tile createMapTile(TilesetAtlas atlas, TileData data) {
        TileDefinition definition = this.tileDefinitions.get(data.type);
        if(definition == null) {
            return null;
        }
        
        Tile tile = new Tile(loadAssets ? atlas.getTile(definition.tileId) : null, definition.layer, definition.width, definition.height);
        tile.setPosition(data.tileX * definition.width, data.tileY * definition.height);
        tile.setIndexPosition(data.tileX, data.tileY);
        tile.setCollisionMaskById(definition.collisionMaskId);
        tile.setHeightMask(definition.heightMask);
        tile.setFlips(false, false, false);
        tile.setType(data.type);
        
        return tile;
    }
}
