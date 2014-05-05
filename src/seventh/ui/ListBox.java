/*
 * see license.txt 
 */
package seventh.ui;

import java.util.ArrayList;
import java.util.List;

import leola.frontend.listener.EventDispatcher;
import seventh.client.Inputs;

/**
 * @author Tony
 *
 */
public class ListBox extends Widget {
	public static interface ItemListener {
		void onItemAdded(Button button);
		void onItemRemove(Button button);
	}
	
	private List<Button> items;
	private ItemListener itemListener;
	
	private int index;
	
	/**
	 * @param eventDispatcher
	 */
	public ListBox(EventDispatcher eventDispatcher) {
		super(eventDispatcher);
		
		this.items = new ArrayList<>();
		addInputListener(new Inputs() {
			
			@Override
			public boolean scrolled(int amount) {
				if(amount < 0) {
					previousIndex();
				}
				else {
					nextIndex();
				}
				return true;
			}
		});
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
	 * Adds an item to the list
	 * @param button
	 */
	public void addItem(Button button) {
		this.items.add(button);
		addWidget(button);		
		
		if(this.itemListener!=null) {
			this.itemListener.onItemAdded(button);
		}
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
}
