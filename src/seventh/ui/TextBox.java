/*
 * see license.txt 
 */
package seventh.ui;

import java.util.ArrayList;
import java.util.List;

import leola.frontend.listener.EventDispatcher;
import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.client.sfx.Sounds;
import seventh.ui.Label.TextAlignment;
import seventh.ui.events.TextBoxActionEvent;
import seventh.ui.events.TextBoxActionListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/**
 * A simple text box
 * 
 * @author Tony
 *
 */
public class TextBox extends Widget implements Hoverable {

    private StringBuilder inputBuffer;
    private int cursorIndex;
    private boolean isCtrlDown;
    private boolean isHovering;
    
    private Label label;
    private Label textLbl;
    
    private int maxSize;    
    
    private List<TextBoxActionListener> textBoxActionListeners;
    
    /**
     * @param eventDispatcher
     */
    public TextBox(EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        
        this.textBoxActionListeners = new ArrayList<TextBoxActionListener>();
        this.inputBuffer = new StringBuilder();
        this.cursorIndex = 0;
        this.isCtrlDown = false;
        
        this.label = new Label();
        this.label.setHorizontalTextAlignment(TextAlignment.LEFT);
//        addWidget(label);
        
        this.textLbl = new Label();
        this.textLbl.setHorizontalTextAlignment(TextAlignment.LEFT);
//        addWidget(textLbl);
        
        this.maxSize = 32;
        
        addInputListener(new Inputs() {
            
            /* (non-Javadoc)
             * @see seventh.client.Inputs#mouseMoved(int, int)
             */
            @Override
            public boolean mouseMoved(int x, int y) {            
                super.mouseMoved(x, y);
                if ( ! isDisabled() ) {
                    if ( getScreenBounds().contains(x,y)) {
                        if(!hasFocus()) {
                            setHovering(true);
                        }
                        return false;
                    }
                }
                setHovering(false);                
                return false;                
            }
            
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {                
                if ( ! isDisabled() ) {
                    if ( getScreenBounds().contains(x,y)) {                                                                            
                        setFocus(true);                        
                        return false;                                                                
                    }
                }
                
                setFocus(false);
                return false;
            }

            /* (non-Javadoc)
             * @see seventh.client.Inputs#touchDown(int, int, int, int)
             */
            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {                
                return super.touchUp(x, y, pointer, button);                
            }
            
            public boolean keyDown(int key) {
                if(isDisabled()|| !hasFocus()) {
                    return false;
                }
                
                switch(key) {
                    case Keys.LEFT: {
                        cursorIndex--;
                        if(cursorIndex < 0) {
                            cursorIndex = 0;
                        }
                        
                        break;
                    }
                    case Keys.RIGHT: {
                        cursorIndex++;
                        if(cursorIndex >= inputBuffer.length()) {
                            cursorIndex = inputBuffer.length();
                        }
                        
                        break;
                    }            
                    case Keys.HOME: {
                        cursorIndex = 0;                    
                        break;
                    }
                    case Keys.END: {
                        cursorIndex = inputBuffer.length();                    
                        break;
                    }
                    case Keys.CONTROL_LEFT:
                    case Keys.CONTROL_RIGHT:
                        isCtrlDown = true;
                        break;
                    case Keys.V:
                        if(isCtrlDown) {
                            String contents = Gdx.app.getClipboard().getContents();
                            if(contents != null) {
                                inputBuffer.insert(cursorIndex, contents);
                                setText(inputBuffer.toString());                                
                            }
                        }
                        break;
                    default: {
                    }
                }
                return true;
            }
            
            @Override
            public boolean keyUp(int key) {
                if(isDisabled()|| !hasFocus()) {
                    return false;
                }
                
                switch(key) {                    
                    case Keys.CONTROL_LEFT:
                    case Keys.CONTROL_RIGHT:
                        isCtrlDown = false;
                        break;
                    default: {                    
                    }
                }
                return true;
            }
            
            @Override
            public boolean keyTyped(char key) {                        
                if(isDisabled() || !hasFocus()) {
                    return false;
                }
                
                switch(key) {        
                    case /*Keys.BACKSPACE*/8: {
                        if(cursorIndex>0) {
                            inputBuffer.deleteCharAt(--cursorIndex);                        
                            if(cursorIndex < 0) {
                                cursorIndex = 0;
                            }
                        }
                        Sounds.playGlobalSound(Sounds.uiKeyType);
                        break;
                    }
                    case /*Keys.FORWARD_DEL*/127: {
                        if(cursorIndex<inputBuffer.length()) {
                            inputBuffer.deleteCharAt(cursorIndex);
                        }
                        Sounds.playGlobalSound(Sounds.uiKeyType);
                        break;
                    }
                    case '\r':
                    case '\n': {        
                        fireTextBoxActionEvent(new TextBoxActionEvent(this, TextBox.this));
                        break;
                    }                
                    default: {
                        char c =key;
                        if(c>31&&c<127 && c != 96 && inputBuffer.length() < maxSize) {
                            inputBuffer.insert(cursorIndex, key);
                            cursorIndex++;                
                            Sounds.playGlobalSound(Sounds.uiKeyType);
                        }                    
                    }
                }
                return true;
            }
        });
    }

    /**
     * 
     */
    public TextBox() {    
        this(new EventDispatcher());
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#destroy()
     */
    @Override
    public void destroy() {    
        super.destroy();
        
        this.label.destroy();
        this.textLbl.destroy();
    }
    
    /**
     * Distributes the {@link TextBoxActionEvent} to each listener
     * @param event
     */
    protected void fireTextBoxActionEvent(TextBoxActionEvent event) {
        for(TextBoxActionListener l : this.textBoxActionListeners) {
            l.onEnterPressed(event);
        }
    }
    
    public void addTextBoxActionListener(TextBoxActionListener l) {
        this.textBoxActionListeners.add(l);
    }
    
    public void removeTextBoxActionListener(TextBoxActionListener l) {
        this.textBoxActionListeners.remove(l);
    }
    
    /**
     * @param isHovering the isHovering to set
     */
    private void setHovering(boolean isHovering) {
                
        if(isHovering) {            
            if(!this.isHovering) {
                Sounds.playGlobalSound(Sounds.uiHover);
            }
        }        
        this.isHovering = isHovering;
    }
    
    /**
     * @return the isHovering
     */
    @Override
    public boolean isHovering() {
        return isHovering;
    }
    
    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * @return the maxSize
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * @param text
     */
    public void setText(String text) {
        if(text.length() > maxSize) {
            text = text.substring(0, maxSize);
        }
        
        this.inputBuffer.delete(0, this.inputBuffer.length());
        this.inputBuffer.append(text);
        this.cursorIndex = this.inputBuffer.length();
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#setFocus(boolean)
     */
    @Override
    public void setFocus(boolean focus) {    
        super.setFocus(focus);
        if(focus) {
            Sounds.playGlobalSound(Sounds.uiSelect);
        }
    }
    
    /**
     * @return the text in the textbox
     */
    public String getText() {
        return this.inputBuffer.toString();
    }
    
    /**
     * @return the label
     */
    public Label getLabel() {
        return label;
    }
    
    /**
     * @return the textLbl
     */
    public Label getTextLabel() {
        textLbl.setText(getText());
        return textLbl;
    }
    
    /**
     * Sets the text of the label
     * @param labelText
     */
    public void setLabelText(String labelText) {
        this.label.setText(labelText);
    }
    
    /**
     * @return the label text
     */
    public String getLabelText() {
        return this.label.getText();
    }

    public void setFont(String font) {
        this.label.setFont(font);
        this.textLbl.setFont(font);
    }
    
    public void setTextSize(float size) {
        this.label.setTextSize(size);
        this.textLbl.setTextSize(size);
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#setTheme(seventh.client.gfx.Theme)
     */
    @Override
    public void setTheme(Theme theme) {    
        super.setTheme(theme);
        this.label.setTheme(theme);
        this.textLbl.setTheme(theme);
    }
    
    /**
     * @return the cursorIndex
     */
    public int getCursorIndex() {
        return cursorIndex;
    }
}
