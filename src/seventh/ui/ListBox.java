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

/**
 * @author Tony
 *
 */
public class ListBox extends Widget implements Scrollable {
    public static interface ItemListener {
        void onItemAdded(Button button);
        void onItemRemove(Button button);
    }
    public static interface ColumnHeaderListener {
        void onHeaderAdded(Button button);
        void onHeaderRemove(Button button);
    }
    
    private List<Button> items;
    private ItemListener itemListener;
    private ColumnHeaderListener headerListener;
    
    private List<Button> columnHeaders;
    
    private int index;
    private int headerHeight;
    private Rectangle viewport;
    
    /**
     * @param eventDispatcher
     */
    public ListBox(EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        
        this.headerHeight = 40;
        this.items = new ArrayList<>();
        this.columnHeaders = new ArrayList<>();
        
        this.viewport = new Rectangle();
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
    
    
    /**
     * @param itemListener the itemListener to set
     */
    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }
    
    /**
     * @return the itemListener
     */
    public ItemListener getItemListener() {
        return itemListener;
    }
    
    /**
     * @param headerListener the headerListener to set
     */
    public void setHeaderListener(ColumnHeaderListener headerListener) {
        this.headerListener = headerListener;
    }
    
    /**
     * @return the headerListener
     */
    public ColumnHeaderListener getHeaderListener() {
        return headerListener;
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
        
        if(this.headerListener != null) {
            this.headerListener.onHeaderAdded(button);
        }
        
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
        
        if(this.itemListener != null) {
            this.itemListener.onItemAdded(button);
        }
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
        if(this.itemListener != null) {
            this.itemListener.onItemRemove(button);
        }
        
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
    
    @Override
    public Rectangle getViewport() {
        this.viewport.set(getBounds());
        this.viewport.height -= getHeaderHeight();
        return this.viewport;
    }
    
    @Override
    public int getTotalHeight() {
        int sum = 0;
        for(int i = 0; i < this.items.size(); i++) {
            sum += this.items.get(i).getBounds().height;
        }
        return sum - getHeaderHeight();
    }
    
    @Override
    public int getTotalWidth() {    
        return getBounds().width;
    }
}
