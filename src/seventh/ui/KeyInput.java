/*
 * see license.txt 
 */
package seventh.ui;

import com.badlogic.gdx.Input.Keys;

import leola.frontend.listener.EventDispatcher;
import seventh.client.inputs.Inputs;

/**
 * @author Tony
 *
 */
public class KeyInput extends Widget {

    private int key;
    private String keymap;
    private boolean isDone, isCancelled;
    /**
     * @param eventDispatcher
     */
    public KeyInput(EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        reset();
        
        addInputListener(new Inputs() {
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#keyDown(int)
             */
            @Override
            public boolean keyDown(int k) {
                if(isDisabled()) {
                    return false;
                }
                
                if(k == Keys.ESCAPE) {
                    isCancelled = true;
                    return true;
                }
                
                super.keyDown(k);
                key = k;                
                return true;
            }
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#keyUp(int)
             */
            @Override
            public boolean keyUp(int k) {        
                if(isDisabled()) {
                    return false;
                }
                
                super.keyUp(k);                
                isDone = true;
                return true;
            }
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#mouseMoved(int, int)
             */
            @Override
            public boolean mouseMoved(int x, int y) {                            
                return true;
            }
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#touchDragged(int, int, int)
             */
            @Override
            public boolean touchDragged(int x, int y, int pointer) {            
                return true;
            }
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#touchUp(int, int, int, int)
             */
            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                if(isDisabled()) {
                    return false;
                }
                
                
                if (!super.touchUp(x, y, pointer, button) ) {
                    isDone = true;    
                }
                return true;
            }
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#touchDown(int, int, int, int)
             */
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if(isDisabled()) {
                    return false;
                }
                
                key = button;
                super.touchDown(x, y, pointer, button);
                return true;
            }
        });
    }

    /**
     * 
     */
    public KeyInput() {
        this(new EventDispatcher());        
    }

    public void reset() {
        this.isCancelled = false;
        this.isDone = false;
        this.key = -1;
        this.keymap = null;
    }
    
    /**
     * @param keymap the keymap to set
     */
    public void setKeymap(String keymap) {
        this.keymap = keymap;
    }
    
    /**
     * @return the keymap
     */
    public String getKeymap() {
        return keymap;
    }
    
    /**
     * @return the isCancelled
     */
    public boolean isCancelled() {
        return isCancelled;
    }
    
    /**
     * @return the isDone
     */
    public boolean isDone() {
        return isDone;
    }
    
    /**
     * @return the key
     */
    public int getKey() {
        return key;
    }
    
}
