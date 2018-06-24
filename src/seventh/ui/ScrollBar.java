/*
 * see license.txt 
 */
package seventh.ui;

import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.EventDispatcher;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnScrollBarListener;
import seventh.ui.events.ScrollBarEvent;

/**
 * Scroll bar allows for a viewport of a {@link Scrollable} pane.
 * 
 * @author Tony
 *
 */
public class ScrollBar extends Widget {

    /**
     * Scroll bar orientation
     * 
     * @author Tony
     *
     */
    public enum Orientation {
        Horizontal,
        Vertical,
    }
    
    private static final int BUTTON_ENDS_SIZE = 15;
    
    private Button top, bottom, handle;
    
    private float currentPosition;      
    private float adjustAmount;
    private int scrollIncrement;
    private Orientation orientation;
    
    private boolean isDragging;
    private Vector2f dragSpot, dragOffset;
    
    private Scrollable pane;
    
    /**
     * @param dispatcher
     * @param maxPosition
     */
    public ScrollBar(Scrollable pane, EventDispatcher dispatcher) {
        super(dispatcher);
    
        this.pane = pane;                
        this.orientation = Orientation.Vertical;        
        this.currentPosition = 0;
        this.adjustAmount = 0.1f;
        this.scrollIncrement = 1;
        
        this.dragSpot = new Vector2f();
        this.dragOffset = new Vector2f();
        this.isDragging = false;
        
        this.top = new Button();
        this.top.getBounds().setSize(15, 15);
        this.top.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                moveBy(-getAdjustAmount());
            }
        });
        
        this.bottom = new Button();
        this.bottom.getBounds().setSize(15, 15);
        this.bottom.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                moveBy(+getAdjustAmount());
            }
        });
        
        this.handle = new Button();
        
        addWidget(this.top);
        addWidget(this.bottom);
        addWidget(this.handle);
        
        calculateHandlePosition();
        
        addInputListener(new Inputs() {
            
            @Override
            public boolean scrolled(int amount) {
                if(amount < 0) {
                    moveBy(-getAdjustAmount());
                }
                else {
                    moveBy(+getAdjustAmount());
                }
                return true;                
            }
                        
            @Override
            public boolean touchDragged(int x, int y, int pointer) {                
                if(isDragging) {                    
                    switch(getOrientation()) {
                        case Horizontal: {
                            moveHandle((int) (x + dragOffset.x), y);
                            break;
                        }
                        case Vertical: {
                            moveHandle(x, (int) (y + dragOffset.y));
                            return true;
                        }
                    }
                }
                return super.touchDragged(x, y, pointer);
            }
            
            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                isDragging = false;
                return super.touchUp(x, y, pointer, button);
            }
            
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                // determine if we've clicked on the scroll bar
                if(getBounds().contains(x, y)) {
                    
                    // ignore if we're touching one of the three buttons
                    if(top.getScreenBounds().contains(x, y)) {
                        return false;
                    }
                    if(bottom.getScreenBounds().contains(x, y)) {
                        return false;
                    }
                    if(handle.getScreenBounds().contains(x, y)) {
                        isDragging = true;
                        dragSpot.set(x, y);
                        Vector2f handlePos = handle.getScreenPosition();
                        Vector2f.Vector2fSubtract(handlePos, dragSpot, dragOffset);
                        return true;
                    }
                    
                    // determine if we need to scroll up or down
                    final int handleX = handle.getScreenBounds().x;
                    final int handleY = handle.getScreenBounds().y;
                    switch(getOrientation()) {
                        case Horizontal: {
                            if(x < handleX) {
                                moveBy(-getAdjustAmount());
                            }
                            else if(x > handleX) {
                                moveBy(+getAdjustAmount());
                            }
                            break;
                        }
                        case Vertical: {
                            if(y < handleY) {
                                moveBy(-getAdjustAmount());
                            }
                            else if(y > handleY) {
                                moveBy(+getAdjustAmount());
                            }
                            break;
                        }
                    }
                    
                    return true;
                }
                
                return super.touchDown(x, y, pointer, button);
            }
        });
    }
    
    /**
     * @return the adjustAmount
     */
    private float getAdjustAmount() {
        return adjustAmount;
    }
    
    /**
     * @return the scrollIncrement
     */
    public int getScrollIncrement() {
        return scrollIncrement;
    }
    
    /**
     * @param scrollIncrement the scrollIncrement to set
     */
    public void setScrollIncrement(int scrollIncrement) {
        this.scrollIncrement = scrollIncrement;
        
        this.calculateHandlePosition();
    }
    
    /**
     * @return the currentPosition
     */
    public float getCurrentPosition() {
        return currentPosition;
    }
    
    
    /**
     * Move the handle by an amount (ranges from 0 - 1.0)
     * 
     * @param delta
     */
    public void moveBy(float delta) {        
        float newPosition = this.currentPosition + delta;
        if(delta != 0) {
            move(newPosition);
        }
    }
    
    
    /**
     * Move the handle to a position (0 - 1.0 based)
     * 
     * @param position
     */
    public void move(float position) {
        float previousPos = this.currentPosition;
        this.currentPosition = position;
        if(position < 0) {
            this.currentPosition = 0;
        }
        else if(position > 1f) {
            this.currentPosition = 1f;
        }
        
        calculateHandlePosition();            
        
        
        // only fire an event if we had a scroll motion;
        // if adjustAmount is 1 that means nothing to scroll
        if(this.adjustAmount < 1f) {
            int movementDelta = 0;
            
            // calculate the number of scrolls we actually took
            float delta = this.currentPosition - previousPos;
            float amount = 0;
            while(amount < Math.abs(delta)) {
                amount += this.adjustAmount;
                movementDelta++;
            }
            
            if(delta < 0) {
                movementDelta *= -1;
            }
            
            fireScrollBarEvent(new ScrollBarEvent(this, movementDelta));
        }
    }
    
    
    public void addScrollBarListener(OnScrollBarListener listener) {
        this.getEventDispatcher().addEventListener(ScrollBarEvent.class, listener);
    }
    
    
    /**
     * Moves the scroll bar handle to the desired position
     * 
     * @param x
     * @param y
     */
    public void moveHandle(int x, int y) {        
        final int handleSize = getHandleSize();
        switch(this.orientation) {
            case Horizontal: {
                // TODO
                break;
            }
            case Vertical: {
                int height = getBounds().height - (handleSize + BUTTON_ENDS_SIZE*2);
                if(height == 0) {
                    height = 1;
                }
                int targetY = y - (getBounds().y + BUTTON_ENDS_SIZE);                
                if(targetY < 0) {
                    targetY = 0; 
                }
                else if(targetY > height) {
                    targetY = height;
                }
                
                // We must ensure that the position is a valid position
                // based on the 'adjustAmount'
                float newPosition = (float)targetY / (float)height;
                float properPosition = 0;
                while(properPosition < newPosition) {
                    properPosition += this.adjustAmount;
                }
                
                newPosition = properPosition;
                
                move(newPosition);
                break;
            }
        }
    }
    
    @Override
    public void setTheme(Theme theme) {     
        super.setTheme(theme);
        this.top.setTheme(theme);
        this.bottom.setTheme(theme);
        this.handle.setTheme(theme);
    }
    
    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;        
        calculateHandlePosition();
    }
    
    
    /**
     * Calculates the percentage adjustments to the scrollbar
     */
    private void calculateAdjustments() {
        switch(this.orientation) {
            case Horizontal: {
                // TODO
            }
            case Vertical: {
                Rectangle viewport = pane.getViewport();
                int viewportHeight = viewport.height;
                int maxHeight = pane.getTotalHeight();                
                
                if(maxHeight <= 0 || maxHeight < viewportHeight) {
                    maxHeight = viewportHeight;
                }
                                
                if(this.scrollIncrement > 0) {
                    int scrollableArea = maxHeight - viewportHeight;
                    int numberOfScrolls = scrollableArea / this.scrollIncrement;
                    if( (scrollableArea % this.scrollIncrement) > 0) {
                        numberOfScrolls++;
                    }
                    
                    // 1 is weird, because we want to account for it (as in scroll one); but
                    // we overload adjustAmount 1 to mean no scroll, so this is a work-around
                    if(numberOfScrolls == 1) {
                        this.adjustAmount = 0.5f;
                    }
                    else if(numberOfScrolls > 0) {
                        this.adjustAmount = 1.0f / (float) numberOfScrolls;
                    }
                    else {
                        this.adjustAmount = 1f;
                    }
                }
            }
        }
    }
    
    
    /**
     * Calculates the scrollbar handle position, this must be invoked if
     * the {@link Scrollable} pane has adjusted in size
     */
    public void calculateHandlePosition() {        
        calculateAdjustments();
        
        switch(this.orientation) {
            case Horizontal: {
                // TODO
                break;
            }
            case Vertical: {
                int handleSize = getHandleSize();
                int height = getBounds().height - (handleSize + BUTTON_ENDS_SIZE*2);
                int offset = (int)((float)height * this.currentPosition);

                Rectangle bounds = this.handle.getBounds();
                bounds.x = 0;
                bounds.y = BUTTON_ENDS_SIZE + offset;
                bounds.width = getBounds().width;
                bounds.height = handleSize;
                
                this.top.getBounds().set(0, 0, getBounds().width, BUTTON_ENDS_SIZE);
                this.bottom.getBounds().set(0, getBounds().height - BUTTON_ENDS_SIZE, getBounds().width, BUTTON_ENDS_SIZE);
                break;
            }
        }
    }
    
    private int getHandleSize() {
        final int minHandleSize = BUTTON_ENDS_SIZE * 3;
        
        switch(this.orientation) {
            case Horizontal: {
                // TODO
                return minHandleSize;
            }
            case Vertical: {
                Rectangle viewport = pane.getViewport();
                int viewportHeight = viewport.height;
                int maxHeight = pane.getTotalHeight();                
                
                if(maxHeight <= 0 || maxHeight < viewportHeight) {
                    maxHeight = viewportHeight;
                }
                
                int handleSize = minHandleSize;
                if(maxHeight > 0) {
                    handleSize = (int) (((float)viewportHeight / (float) maxHeight) * (float)viewportHeight);
                }
                
                if(handleSize < minHandleSize) {
                    handleSize = minHandleSize;
                }
                if(handleSize > maxHeight) {
                    handleSize = maxHeight;
                }
                
                return handleSize - (BUTTON_ENDS_SIZE * 2);
            }
        }
        
        return minHandleSize;
    }
    
    protected void fireScrollBarEvent(ScrollBarEvent event) {
        this.getEventDispatcher().sendNow(event);
    }
    
    /**
     * @return the top
     */
    public Button getTopButton() {
        return top;
    }
    
    /**
     * @return the bottom
     */
    public Button getBottomButton() {
        return bottom;
    }
    
    /**
     * @return the handle
     */
    public Button getHandleButton() {
        return handle;
    }
    
    
}
