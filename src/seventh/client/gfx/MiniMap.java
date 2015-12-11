/*
 * see license.txt 
 */
package seventh.client.gfx;

import java.util.HashMap;
import java.util.List;

import seventh.client.ClientBombTarget;
import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientPlayers;
import seventh.client.ClientTeam;
import seventh.client.weapon.ClientWeapon;
import seventh.game.weapons.Weapon.State;
import seventh.map.Layer;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Draws a mini map of the current map
 * 
 * @author Tony
 *
 */
public class MiniMap implements Renderable {

	private ClientGame game;
	private TextureRegion miniMap;
	private long gameClock;	
	private int mapColor;
	
	/**
	 */
	public MiniMap(ClientGame game) {
		this.game = game;
		Map map = game.getMap();
		
		java.util.Map<TextureRegion, Integer> cache = new HashMap<TextureRegion, Integer>();
		
		int size = 10;
		
		int ratioWidth = map.getMapWidth()/size;
		int ratioHeight = map.getMapHeight()/size;
		
		Pixmap pix = TextureUtil.createPixmap(ratioWidth, ratioHeight);

		int width = map.getTileWidth()/size;
		int height = map.getTileHeight()/size;
		
		Layer[] layers = map.getBackgroundLayers();
		for(int y = 0; y < map.getTileWorldHeight(); y++) {
			for(int x = 0; x < map.getTileWorldWidth(); x++) {
				Tile topTile = null;
				for(int i = 0; i < layers.length; i++) {
					Tile tile = layers[i].getRow(y).get(x); 
					if(tile != null) {
						topTile = tile;
					}
				}
				
				if(topTile != null) {
					TextureRegion tex = topTile.getImage();
					
					if(!cache.containsKey(tex)) {
						TextureData data = tex.getTexture().getTextureData();
						data.prepare();
						Pixmap p = data.consumePixmap();
						
						
						int r = 0, g = 0, b = 0;
						for(int px = 0; px < p.getWidth(); px++) {
							for(int py = 0; py < p.getHeight(); py++) {
								int color = p.getPixel(px, py);
								r += (color >> 24) & 0xff;
								g += (color >> 16) & 0xff;
								b += (color >> 8) & 0xff;
							}
						}
						
						
						int numberOfPixels = p.getWidth() * p.getHeight();
						r /= numberOfPixels;
						g /= numberOfPixels;
						b /= numberOfPixels;
												
						int blendedColor = Color.toIntBits(r, g, b, 100);

						p.dispose();
						data.disposePixmap();
						
						cache.put(tex, blendedColor);
					}
					
					
					int blendedColor = cache.get(tex);
					pix.setColor(blendedColor);
					pix.fillRectangle(x*width, y*height, width, height);
					
				}
			}
		}
		
		this.miniMap = new TextureRegion(new Texture(pix));
		this.miniMap.flip(false, true);
		
		this.mapColor = 0xff_ff_ff_ff;
		
	}
	
	/**
	 * @param mapAlpha the mapAlpha to set
	 */
	public void setMapAlpha(int mapAlpha) {		
		this.mapColor = Colors.setAlpha(mapColor, mapAlpha);
	}
	
	/**
	 * @param mapColor the mapColor to set
	 */
	public void setMapColor(int mapColor) {
		this.mapColor = mapColor;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		gameClock = timeStep.getGameClock();
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		canvas.drawImage(miniMap, 0, 0, mapColor);
		
		Map map = game.getMap();
		
		float xr = (float)miniMap.getRegionWidth() / (float)map.getMapWidth();
		float yr = (float)miniMap.getRegionHeight() / (float)map.getMapHeight();
		
		xr -= 0.01f;
		yr -= 0.01f;
		
		ClientPlayers players = game.getPlayers();
		for(int i = 0; i < players.getMaxNumberOfPlayers(); i++) {
			ClientPlayer ent = players.getPlayer(i);
			if(ent != null && ent.isAlive() && (ent.getEntity().getLastUpdate() > gameClock-1000)) {
				ClientTeam team = ent.getTeam();
				Vector2f p = ent.getEntity().getCenterPos();
				
				int x = (int)(xr * p.x);
				int y = (int)(yr * p.y);
				
				ClientWeapon weapon = ent.getEntity().getWeapon();
				if(weapon != null) {
					if(weapon.getState().equals(State.FIRING)) {
						canvas.fillCircle(3.0f, x-1, y-1, 0xfaffff00);		
					}
				}
				
				
				canvas.fillCircle(2.0f, x, y, team.getColor());
			}
		}
		
		/* Draw the bomb targets */
		List<ClientBombTarget> targets = game.getBombTargets();
		for(int i = 0; i < targets.size(); i++) {
			ClientBombTarget target = targets.get(i);
			Vector2f p = target.getCenterPos();
			
			int x = (int)(xr * p.x);
			int y = (int)(yr * p.y);
						
			if(target.isAlive()) {
										
				if(target.isBombPlanted()) {
					canvas.fillCircle(4.0f, x-1, y-1, 0xfaFF3300);
				}
				canvas.fillCircle(3.0f, x, y, 0xfa009933);
			}
//			else {
//				canvas.fillCircle(3.0f, x, y, 0xfaFF3300);
//			}
		}
		
		Vector2f cameraPos = camera.getPosition();
		

		int x = (int)Math.ceil(xr * cameraPos.x + 5);
		int y = (int)Math.ceil(yr * cameraPos.y + 5);
		int width = (int)Math.ceil(camera.getViewPort().width * xr);
		int height = (int)Math.ceil(camera.getViewPort().height * yr);
		canvas.drawRect(x, y, width, height, 0x01fffffff);
	}

}
