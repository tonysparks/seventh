package seventh.shared;

import java.util.Comparator;

public class NoSortStrategy implements SortStrategy {
	
	/**
     * do not sorting for null array
     * 
     * @param array
     * @param comp
     */
	public <T> void sort(T[] array, Comparator<T> comp) {
		return ;
	}
}
