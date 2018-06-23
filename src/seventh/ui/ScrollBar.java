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
 * @author Tony
 *
 */
public class ScrollBar extends Widget {

    public enum Orientation {
        Horizontal,
        Vertical,
    }
    
    private static final int BUTTON_ENDS_SIZE = 15;
    
    private Button top, bottom, handle;
    
    private float currentPosition;      
    private float adjustAmount;
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
        
        // do
        this.handle = new Button();
        
        addWidget(this.top);
        addWidget(this.bottom);
        addWidget(this.handle);
        
        calculateHandlePos();
        
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
     * @param adjustAmount the adjustAmount to set
     */
    public void setAdjustAmount(float adjustAmount) {
        this.adjustAmount = adjustAmount;
    }
    
    /**
     * @return the adjustAmount
     */
    public float getAdjustAmount() {
        return adjustAmount;
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
        
        calculateHandlePos();            
        
        int movementDelta = 0;
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
    
    
    public void addScrollBarListener(OnScrollBarListener listener) {
        this.getEventDispatcher().addEventListener(ScrollBarEvent.class, listener);
    }
    
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
                
                float newPosition = (float)targetY / (float)height;
//                float delta = newPosition - this.currentPosition;
//                if(delta < 0) {
//                    delta = -this.adjustAmount;
//                }
//                else if(delta > 0) {
//                    delta = this.adjustAmount;
//                }
                
                float a = 0;
                while(a < newPosition) {
                    a += this.adjustAmount;
                }
                
                //newPosition = a;
                
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
        calculateHandlePos();
    }
    
    private void calculateHandlePos() {        
        //final int handleSize = BUTTON_ENDS_SIZE * 2;
        switch(this.orientation) {
            case Horizontal: {
                // TODO
//                int width = getBounds().width;
//                int offset = (int)((float)width * this.currentPosition);
//                Rectangle bounds = this.handle.getBounds();
//                bounds.x = offset;
//                bounds.y = getBounds().y;
//                bounds.width = 30;
//                bounds.height = 15;        
//                
//                this.top.getBounds().setLocation(getBounds().x, getBounds().y);
//                this.bottom.getBounds().setLocation(getBounds().x + getBounds().width, getBounds().y);
                break;
            }
            case Vertical: {
                int handleSize = getHandleSize();
                int height = getBounds().height - (handleSize + BUTTON_ENDS_SIZE*2);
                int offset = (int)((float)height * this.currentPosition);
                System.out.println("Pos: " + this.currentPosition + " offset: " + offset + " Adj: " + this.adjustAmount);
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
        switch(this.orientation) {
            case Horizontal: {
                // TODO
                return 15;
            }
            case Vertical: {
                Rectangle viewport = pane.getViewport();
                int maxHeight = pane.getTotalHeight() - (BUTTON_ENDS_SIZE * 2);                
                int viewportHeight = viewport.height  - (BUTTON_ENDS_SIZE * 2);
                
                if(maxHeight <= 0) {
                    maxHeight = viewportHeight;
                }
                
                int handleSize = BUTTON_ENDS_SIZE * 2;
                if(maxHeight > 0) {
                    handleSize = (int) (((float)viewportHeight / (float) maxHeight) * (float)viewportHeight);
                }

                return handleSize;
            }
        }
        
        return 15;
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
