/*
 * see license.txt 
 */
package seventh.ui;

import java.util.ArrayList;
import java.util.List;

import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.EventDispatcher;
import seventh.ui.Label.TextAlignment;
import seventh.ui.events.ListHeaderChangedEvent;
import seventh.ui.events.ListItemChangedEvent;
import seventh.ui.events.OnListHeaderChangedListener;
import seventh.ui.events.OnListItemChangedListener;

/**
 * @author Tony
 *
 */
public class ListBox extends Widget implements Scrollable {
    
    private List<Button> items;
    private List<Button> columnHeaders;
    
    private int index;
    private int headerHeight;
    private int headerMargin;
    private int margin;
    
    private Rectangle viewport;
    
    /**
     * @param eventDispatcher
     */
    public ListBox(EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        
        this.headerHeight = 30;
        this.headerMargin = 10;
        
        this.items = new ArrayList<>();
        this.columnHeaders = new ArrayList<>();
        
        this.viewport = new Rectangle();
        this.margin = 5;
    }

    /**
     * 
     */
    public ListBox() {
        this(new EventDispatcher());
    }
        
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * Updates to the next index
     */
    public void nextIndex() {
        this.index++;
        if(this.index >= this.items.size()) {
            if(this.items.isEmpty()) {
                this.index = 0; 
            }
            else {
                this.index = this.items.size() - 1;
            }
        }
    }
    
    /**
     * Updates to the previous index.
     */
    public void previousIndex() {
        this.index--;
        if(this.index < 0) {
            this.index = 0;
        }
    }
    
    public void addListItemChangedListener(OnListItemChangedListener listener) {
        this.getEventDispatcher().addEventListener(ListItemChangedEvent.class, listener);
    }
    
    public void addListHeaderChangedListener(OnListHeaderChangedListener listener) {
        this.getEventDispatcher().addEventListener(ListHeaderChangedEvent.class, listener);
    }
    
    
    /**
     * Adds a button for column header
     * 
     * @param button
     */
    public ListBox addColumnHeader(String header, int width) {
        Button button = new Button();
        button.getBounds().setSize(width, 15);
        button.setText(header);    
        button.setTextSize(12);
        button.setHoverTextSize(15);
        button.setEnableGradiant(false);            
        button.setTheme(getTheme());
        button.getTextLabel().setHorizontalTextAlignment(TextAlignment.LEFT);
        button.getTextLabel().setForegroundColor(0xffffffff);
        this.columnHeaders.add(button);
        
        calculateHeaderPositions();
        
        addWidget(button);
        
        this.getEventDispatcher().sendNow(new ListHeaderChangedEvent(this, button, true));
        
        return this;
    }
    
    private void calculateHeaderPositions() {
        Vector2f pos = new Vector2f(10, 20);        
        
        for(Button button : this.columnHeaders) {
            button.getBounds().setLocation(pos);
            pos.x += button.getBounds().width;
        }
    }
    
    /**
     * Adds an item to the list
     * @param button
     */
    public ListBox addItem(Button button) {
        this.items.add(button);
        addWidget(button);        
        
        this.getEventDispatcher().sendNow(new ListItemChangedEvent(this, button, true));
        
        return this;
    }
    
    
    /**
     * Removes items
     * @param button
     */
    public void remoteItem(Button button) {
        this.items.remove(button);
        remoteInternalItem(button);
    }
    
    /**
     * Removes items
     * @param button
     */
    private void remoteInternalItem(Button button) {                
        this.getEventDispatcher().sendNow(new ListItemChangedEvent(this, button, false));
        
        removeWidget(button);        
    }
    
    /**
     * Remove all items
     */
    public void removeAll() {
        for(Button item : this.items) {
            remoteInternalItem(item);
        }
        this.items.clear();
    }
    
    
    /**
     * @return the items
     */
    public List<Button> getItems() {
        return items;
    }
    
    /**
     * @return the columnHeaders
     */
    public List<Button> getColumnHeaders() {
        return columnHeaders;
    }
    
    /**
     * @return the margin
     */
    public int getMargin() {
        return margin;
    }
    
    /**
     * @param margin the margin to set
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }
    
    /**
     * @return the headerHeight
     */
    public int getHeaderHeight() {
        return headerHeight;
    }
    
    /**
     * @param headerHeight the headerHeight to set
     */
    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }
    
    /**
     * @return the headerMargin
     */
    public int getHeaderMargin() {
        return headerMargin;
    }
    
    /**
     * @param headerMargin the headerMargin to set
     */
    public void setHeaderMargin(int headerMargin) {
        this.headerMargin = headerMargin;
    }
    
    @Override
    public Rectangle getViewport() {
        this.viewport.set(getBounds());
        this.viewport.height -= (getHeaderHeight());// + getHeaderMargin());
        return this.viewport;
    }
    
    @Override
    public int getTotalHeight() {
        int sum = 0;
        for(int i = 0; i < this.items.size(); i++) {
            sum += this.items.get(i).getBounds().height + this.margin;
        }
        return sum;
    }
    
    @Override
    public int getTotalWidth() {    
        return getBounds().width;
    }
}
