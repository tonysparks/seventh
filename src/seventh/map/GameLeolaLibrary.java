/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import leola.vm.Leola;
import leola.vm.lib.LeolaIgnore;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoNamespace;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Camera2d;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class GameLeolaLibrary implements LeolaLibrary {

	private Leola runtime;
	
	/* (non-Javadoc)
	 * @see leola.vm.lib.LeolaLibrary#init(leola.vm.Leola, leola.vm.types.LeoNamespace)
	 */
	@Override
	@LeolaIgnore
	public void init(Leola leola, LeoNamespace namespace) throws Exception {
		runtime = leola;
		LeoNamespace game = this.runtime.getOrCreateNamespace("game");
		game.store(this);

		namespace.put("game", game);
	}
	
	public Map loadMap(String mapFile, boolean loadAssets) throws Exception {
		File file = new File(mapFile);
		String contents = loadFileContents(file);
		contents = "return " + contents.replace(":", "->"); /* converts to leola map format */
		
		LeoMap mapData = runtime.eval(contents).as();
		MapLoader mapLoader = new TiledMapLoader();
		Map map = mapLoader.loadMap(mapData, loadAssets);
		
		return map;
	}
	
	private String loadFileContents(File file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			StringBuilder sb = new StringBuilder((int)raf.length());
			String line = null;
			do {
				line = raf.readLine();
				if ( line != null) {
					sb.append(line).append("\n");
				}
				
			} while(line != null);
			
			return sb.toString();
		}
		finally {
			raf.close();
		}
		
	}
	
	/**
	 * Creates a new {@link Camera}
	 * @param map
	 * @return
	 */
	public Camera newCamera(Integer mapWidth, Integer mapHeight) {
		Camera camera = new Camera2d();
		if(mapWidth != null && mapHeight != null) {
			camera.setWorldBounds(new Vector2f(mapWidth, mapHeight));
		}
				
		return camera;
	}

}
