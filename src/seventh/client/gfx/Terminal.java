/*
 * see license.txt 
 */
package seventh.client.gfx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import seventh.client.ClientSeventhConfig;
import seventh.client.inputs.Inputs;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Console;
import seventh.shared.Logger;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.Updatable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/**
 * @author Tony
 *
 */
public class Terminal implements Updatable, Logger {

    static class Selection {
        Vector2f start = new Vector2f();
        Vector2f end = new Vector2f();
        boolean isSelecting=false;
    }
    
    private static final int MAX_HEIGHT = 500;
    private static final int MAX_TEXT_HISTORY = 2000;
    
    private boolean isActive, isCollapsing, isOpening;
    private Rectangle background;
    private int backgroundColor;
    private int foregroundColor;
    
    private Timer blinkTimer;
    private boolean showCursor;
    
    private List<String> textBuffer;
    private List<String> cmdHistory;
    private StringBuilder inputBuffer;
    private int cursorIndex;
    private int scrollY, scrollPosition;    
    private int cmdHistoryIndex;
    
    private DeactivateCallback callback;
    private Console console;
    
    private boolean queuedClear;
    private boolean isCtrlDown;
    
    private Selection selection;
    private GlythData glythData;
    
    /**
     * 
     * @author Tony
     *
     */
    public static interface DeactivateCallback {
        void onDeactived(Terminal terminal);
    }
    
    private Inputs inputs = new Inputs() {
        
        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            if(button == 0) {
                selection.start.set(x, y);
                selection.isSelecting = true;
            }
            
            return super.touchDown(x, y, pointer, button);
        }
        
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            if(button == 0) {
                selection.end.set(x, y);
                selection.isSelecting = false;
                copySelection(selection);
            }
            return super.touchUp(x, y, pointer, button);
        }
        
        @Override
        public boolean touchDragged(int x, int y, int pointer) {
            if(selection.isSelecting) {
                selection.end.set(x, y);
            }
            
            return super.touchDragged(x, y, pointer);
        }
        
        @Override
        public boolean scrolled(int amount) {        
            if(!isActive) {
                return false;
            }
            
            if(amount > 0) {
                scrollPosition -= 15;
            }
            else if (amount < 0) {
                scrollPosition += 15;                    
            }
                        
            return true;
        }
        
        
        
        public boolean keyDown(int key) {
            if(!isActive) {
                return false;
            }
            
            switch(key) {
                case Keys.UP: {
                    if(!cmdHistory.isEmpty()) {                                            
                        setInputText(cmdHistory.get(cmdHistoryIndex));
                        cmdHistoryIndex--;
                        if(cmdHistoryIndex < 0) {
                            cmdHistoryIndex = cmdHistory.size()-1;
                        }
                    }
                    
                    break;
                }
                case Keys.DOWN: {
                    if(!cmdHistory.isEmpty()) {
                        
                        setInputText(cmdHistory.get(cmdHistoryIndex));
                        cmdHistoryIndex++;
                        if(cmdHistoryIndex >= cmdHistory.size()) {
                            cmdHistoryIndex = 0;
                        }
                    }
                    
                    break;
                }
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
//                case Keys.BACKSPACE: {
//                    if(cursorIndex>0) {
//                        inputBuffer.deleteCharAt(--cursorIndex);                        
//                        if(cursorIndex < 0) {
//                            cursorIndex = 0;
//                        }
//                    }
//                    
//                    break;
//                }
//                case Keys.FORWARD_DEL: {
//                    if(cursorIndex<inputBuffer.length()) {
//                        inputBuffer.deleteCharAt(cursorIndex);
//                    }
//                    
//                    break;
//                }
                case Keys.HOME: {
                    cursorIndex = 0;                    
                    break;
                }
                case Keys.END: {
                    cursorIndex = inputBuffer.length();                    
                    break;
                }
                case Keys.PAGE_UP: {
                    scrollY = 1;                    
                    break;
                }
                case Keys.PAGE_DOWN: {
                    scrollY = -1;                    
                    break;
                }
                case Keys.TAB: {
                    String inputText = inputBuffer.toString();
                    List<String> cmdNames = console.find(inputText);
                    if(!cmdNames.isEmpty()) {
                    
                        if(cmdNames.size() == 1) {                        
                            setInputText(cmdNames.get(0) + " ");
                        }
                        else {
                            console.println("");
                            
                            for(String cmd : cmdNames) {
                                console.println(cmd);
                            }
                            console.println("");
                            
                            setInputText(findMaxMatchingChars(inputText, cmdNames));
                        }
                    }
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
                            cursorIndex += contents.length();    
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
            if(!isActive) {
                return false;
            }
            
            switch(key) {
                case Keys.PAGE_UP: {
                    scrollY = 0;                    
                    break;
                }
                case Keys.PAGE_DOWN: {
                    scrollY = 0;
                    break;
                }
                case Keys.CONTROL_LEFT:
                case Keys.CONTROL_RIGHT:
                    isCtrlDown = false;
                    break;
                default: {                    
                }
            }
            return true;
        }
        
        public boolean keyTyped(char key) {                        
            if(!isActive) {
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
                    
                    break;
                }
                case /*Keys.FORWARD_DEL*/127: {
                    if(cursorIndex<inputBuffer.length()) {
                        inputBuffer.deleteCharAt(cursorIndex);
                    }
                    
                    break;
                }
                case '\r':
                case '\n': {
                    String command = inputBuffer.toString();                                    
                                        
                    cmdHistory.add(command);
                    cmdHistoryIndex = cmdHistory.size()-1;
                    
                    setInputText("");
                    
                    console.execute(command);                    
                    break;
                }                
                default: {
                    char c =key;
                    if(c>31&&c<127 && c != 96) {
                        inputBuffer.insert(cursorIndex, key);
                        cursorIndex++;                        
                    }                    
                }
            }
            return true;
        }
    };
    
    /**
     * @param console
     * @param config
     */
    public Terminal(Console console, ClientSeventhConfig config) {
        this.console = console;
        this.console.addLogger(this);
        
        this.isActive = false;
        
        this.blinkTimer = new Timer(true, 500);
        this.showCursor = true;    
        
        this.background = new Rectangle();
        this.backgroundColor = config.getConsoleBackgroundColor();
        this.foregroundColor = config.getConsoleForegroundColor();
        
        this.textBuffer = new LinkedList<String>();
        this.inputBuffer = new StringBuilder();
        this.cmdHistory = new ArrayList<String>();
        
        this.selection = new Selection();
    }

    /**
     * Attempts to find the common characters for the matched command names
     * 
     * Always assumes that cmdNames contains at least 1 entry (should be at least 2).
     * 
     * @param buffer
     * @param cmdNames
     * @return the command
     */
    private String findMaxMatchingChars(String buffer, List<String> cmdNames) {        
        
        String firstCmd = cmdNames.get(0);
        
        final int bufferLength = buffer.length();
        int numberOfMatchedChars = 0;
        
        boolean hasMatches = true;                
        while(hasMatches) {                    
            final int positionToCheck = numberOfMatchedChars + bufferLength;
                        
            if(firstCmd.length() > positionToCheck) {
                char c = cmdNames.get(0).charAt(positionToCheck);
                for(int i = 1; i < cmdNames.size(); i++) {
                    String secondCmd = cmdNames.get(i);
                    
                    if(secondCmd.length() <= positionToCheck || 
                       secondCmd.charAt(positionToCheck) != c) {
                        hasMatches = false;
                        break;
                    }
                }
                
                if(hasMatches) {
                    numberOfMatchedChars++;
                }
            }
            else {
                hasMatches = false;
            }
        }
        
        String result = firstCmd.substring(0, buffer.length() + numberOfMatchedChars);
        
        return result;
    }
    
    /**
     * @param text
     */
    public void setInputText(String text) {
        inputBuffer.delete(0, inputBuffer.length());
        inputBuffer.append(text);
        this.cursorIndex = inputBuffer.length();
    }
    
    /**
     * @return the input text
     */
    public String getInputText() {
        return inputBuffer.toString();
    }
    
    /**
     * @param text
     */
    public void appendInputText(String text) {
        setInputText(inputBuffer.toString() + text);
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Logger#print(java.lang.Object)
     */
    @Override
    public void print(Object msg) {
        if(msg!=null) {
            String message = msg.toString();
            String[] split = message.split("\n");
            for(String n : split) {
                this.textBuffer.add(n.replace("\t", "   "));
            }
        }    
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Logger#printf(java.lang.Object, java.lang.Object[])
     */
    @Override
    public void printf(Object msg, Object... args) {
        String str = String.format(msg.toString(), args);
        println(str);
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Logger#println(java.lang.Object)
     */
    @Override
    public void println(Object msg) {
        if(msg!=null) {
            String message = msg.toString();
            String[] split = message.split("\n");
            if(split.length> 0) {
                for(String n : split) {
                    this.textBuffer.add(n.replace("\t", "   "));
                }
            }
            else {
                this.textBuffer.add("");
            }
        }        
    }
    
    
    /**
     * Clears the terminal window
     */
    public void clear() {
        this.queuedClear = true;
    }
    
    private void copySelection(Selection selection) {
        if(this.glythData != null) {
            int textHeight = this.glythData.getHeight("W");
            if(textHeight == 0) return;
            
            int displayHeight = background.height + textHeight*4;

            int lineStartY = -1;
            int lineEndY   = -1;

            int bodyTextHeight = this.textBuffer.size() * textHeight;
            int textNotOnScreenHeight = bodyTextHeight - displayHeight;
            
            //if(selection.start.y < displayHeight) 
            {
                lineStartY = textNotOnScreenHeight + (int)selection.start.y;
                lineStartY /= textHeight;
            }
            
            //if(selection.end.y < displayHeight) 
            {
                lineEndY = textNotOnScreenHeight + (int)selection.end.y;
                lineEndY /= textHeight;
                //lineEndY = this.textBuffer.size() - lineEndY;
            }
            
            System.out.println(this.textBuffer.size() + "::" + lineStartY + ":" + lineEndY + "::" + bodyTextHeight);
            
            int bufferSize = this.textBuffer.size();
            if(lineStartY > -1 && lineStartY < bufferSize && lineEndY > -1 && lineEndY < bufferSize) {
                int max = Math.max(lineStartY, lineEndY);
                int min = Math.min(lineStartY, lineEndY);
                
                StringBuilder sb = new StringBuilder();
                while(min < max) {
                    String line = this.textBuffer.get(min++);
                    sb.append(line).append("\n");                
                    
                }
                
                System.out.println(sb);
            }
            
        }
    }
    
    /**
     * if we queued up a clear buffer, lets
     * go ahead and do so.
     */
    private void queuedClear() {
        if(queuedClear) {
            this.textBuffer.clear();
            this.scrollPosition = 0;
            this.scrollY = 0;
            this.queuedClear = false;
        }
    }
    
    /**
     * @return the terminal input
     */
    public Inputs getInputs() {
        return inputs;
    }
    
    public void open() {
        if(!this.isActive) {
            toggle();
        }
    }
    
    /**
     * Toggles the console
     * @return true if opening, false if closing
     */
    public boolean toggle() {        
        Sounds.playGlobalSound(Sounds.uiSelect);
        
        if(!this.isActive) {
            this.isActive = true;
            this.isOpening = true;            
            return true;
        }
        
        this.isCollapsing = true;                
        return false;
    }
    
    /**
     * @param callback
     */
    public void setOnDeactive(DeactivateCallback callback) {
        this.callback = callback;
    }
    
    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Chomps the size if too big
     */
    private void checkOutputSize() {
        if(this.textBuffer.size() > MAX_TEXT_HISTORY) {
            int range = this.textBuffer.size() - MAX_TEXT_HISTORY;
            while(range > 0) {
                this.textBuffer.remove(0);
                range--;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see leola.live.Updatable#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {        
        queuedClear();
        
        checkOutputSize();
        
        if(this.isOpening) {
            if(background.height < MAX_HEIGHT) {
                background.height += 100;                
            }
            else {
                this.isOpening = false;
            }
        }
        
        if(this.isCollapsing) {
            if(background.height > 0) {
                background.height -= 50;
            }
            else {
                this.isActive = false;
                this.isCollapsing = false;
                if(this.callback != null) {
                    this.callback.onDeactived(this);
                }
            }
        }
        
        
        this.blinkTimer.update(timeStep);
        if(this.blinkTimer.isTime()) {
            this.showCursor = !this.showCursor;
        }
        
        if(scrollY>0) {
            scrollPosition += 5;
        }
        else if(scrollY<0) {
            scrollPosition -= 5;
        }
    }
    
    /**
     * Renders the text
     * @param canvas
     */
    public void render(Canvas canvas, float alpha) {
        background.width = canvas.getWidth() + 50;
        
        canvas.fillRect(background.x, background.y, background.width, background.height, backgroundColor);
                        
        canvas.begin();
        canvas.setFont("Courier New", 14);
        canvas.boldFont();
        
        // capture the font glyth data so
        // we can correctly make selections
        if( this.glythData == null ) {
            this.glythData = canvas.getGlythData();
        }
        
        int textHeight = canvas.getHeight("W");
        int outputHeight = background.height - textHeight - 5;
        

        int y = outputHeight - 10 + scrollPosition;
        int x = 10;
        boolean itemsSkipped = false;
        int size = this.textBuffer.size();
        for(int i = size-1; i >= 0; i--) {
            if(y>outputHeight-10) {
                y -= textHeight;                
                itemsSkipped = true;
                continue;
            }
            
            if(y<0) break;
            
            String rowText = this.textBuffer.get(i);
            RenderFont.drawShadedString(canvas, rowText, x, y, foregroundColor);
            
            y -= textHeight;
        }
        
        if(itemsSkipped) {
            String notificationText = "^  ^  ^  ^  ^  ^  ^  ^";
            int width = canvas.getWidth(notificationText);
            RenderFont.drawShadedString(canvas, notificationText, background.width/2 - width/2, outputHeight - 5, foregroundColor);
        }
        canvas.end();
        
        canvas.fillRect(background.x, outputHeight, background.width, textHeight+5, 0xff070ff);
        
        int fadeColor = 0xffacacca;
        canvas.drawLine(background.x, outputHeight-1, background.width, outputHeight-1, 0);
        canvas.drawLine(background.x, outputHeight, background.width, outputHeight, fadeColor);
        
        canvas.drawLine(background.x, background.height-1, background.width, background.height-1, fadeColor);
        canvas.drawLine(background.x, background.height, background.width, background.height, 0);
        
        int inputTextColor = 0xffffffff;
        
        canvas.begin();
        int inputBufferY = background.height - textHeight/2;
        RenderFont.drawShadedString(canvas, "> ", x, inputBufferY, 0xffffffff);
        RenderFont.drawShadedString(canvas, this.inputBuffer.toString(), x + canvas.getWidth("> "), inputBufferY, inputTextColor);
        
        if(showCursor) 
        {
            int textWidth = canvas.getWidth("> " + this.inputBuffer.substring(0, this.cursorIndex));
            RenderFont.drawShadedString(canvas, "_", x + textWidth, inputBufferY, inputTextColor);
        }
                
        canvas.end();
    }
    
}
