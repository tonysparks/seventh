/*
 * see license.txt 
 */
package seventh.client.inputs;

import seventh.math.Vector2f;

import com.badlogic.gdx.InputProcessor;

/**
 * Handles Player input
 * 
 * @author Tony
 *
 */
public class Inputs implements InputProcessor {

	private boolean[] keys;
	private boolean[] mouseButtons;
	
	private Vector2f mousePosition;
		
	
	/**
	 * 
	 */
	public Inputs() {
		this.keys = new boolean[512];
		this.mouseButtons = new boolean[5];
		
		this.mousePosition = new Vector2f();
	}
	
	/**
	 * Clears the state of all keys
	 */
	public void clearKeys() {
		for(int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}

	/**
	 * Clears the state of all buttons
	 */
	public  void clearButtons() {
		for(int i = 0; i < mouseButtons.length;i++) {
			mouseButtons[i] = false;
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public boolean isKeyDown(int key) {
		return this.keys[key];
	}
	
	public boolean isButtonDown(int button) {
		return this.mouseButtons[button];
	}
	
	/**
	 * @return the mousePosition
	 */
	public Vector2f getMousePosition() {
		return mousePosition;
	}
	
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#scrolled(int)
	 */
	@Override
	public boolean scrolled(int amount) {	
		return false;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
	 */
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		this.mousePosition.set(x,y);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#mouseMoved(int, int)
	 */
	@Override
	public boolean mouseMoved(int x, int y) {
		this.mousePosition.set(x,y);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
	 */
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {		
		if(button < this.mouseButtons.length) {
			this.mouseButtons[button] = true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
	 */
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {					
		if(button < this.mouseButtons.length) {
			this.mouseButtons[button] = false;
		}
	
		return false;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyDown(int)
	 */
	@Override
	public boolean keyDown(int key) {		
		if(key>=0 && key<this.keys.length) {
			this.keys[key] = true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyUp(int)
	 */
	@Override
	public boolean keyUp(int key) {		
		if(key>=0 && key<this.keys.length) {
			this.keys[key] = false;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
	 */
	@Override
	public boolean keyTyped(char key) {	
		return false;
	}

}
