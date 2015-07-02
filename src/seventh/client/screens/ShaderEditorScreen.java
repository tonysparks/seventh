/*
 * see license.txt 
 */
package seventh.client.screens;

import com.badlogic.gdx.Input.Keys;

import seventh.client.Inputs;
import seventh.client.Screen;
import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.ShaderTest;
import seventh.shared.TimeStep;

/**
 * Tool for tweaking Animations
 * 
 * @author Tony
 *
 */
public class ShaderEditorScreen implements Screen {
	
	private SeventhGame app;
	private ShaderTest test;
	
	private Inputs inputs = new Inputs() {

	    @Override
	    public boolean keyUp(int key) {
	        if(key==Keys.ESCAPE) {
	            app.goToMenuScreen();
	            return true;
	        }
	        return super.keyUp(key);
	    }
	    
		@Override
		public boolean keyTyped(char key) {
			if(key=='r') {
				test = new ShaderTest();
				return true;
			}
			
			return super.keyTyped(key);
		}
	};
	
	/**
	 * 
	 */
	public ShaderEditorScreen(SeventhGame app) {
		this.app = app;		
	}
	
	
	@Override
	public void enter() {
		test = new ShaderTest();
	}

	@Override
	public void exit() {
		if(test!=null) {
			test.destroy();
		}
	}
	
	@Override
	public void destroy() {
		if(test!=null) {
			test.destroy();
		}
	}
	
	@Override
	public void update(TimeStep timeStep) {
		this.test.update(timeStep);	
	}


	@Override
	public void render(Canvas canvas) {
		this.test.render(canvas, null, 0);
	}
	
	@Override
	public Inputs getInputs() {
		return inputs;
	}

}
