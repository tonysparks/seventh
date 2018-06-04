/*
 * see license.txt 
 */
package seventh.shared;

import java.util.Comparator;

/**
 * Simple array utilities
 * 
 * @author Tony
 *
 */
public class Arrays {
	private static SortStrategy sortStrategy;
	
	/**
     * Set the strategy.
     * 
     * @param newSortStrategy
     */
	public void setStrategy(SortStrategy newSortStrategy) {
		this.sortStrategy = newSortStrategy;
	}
	
    /**
     * Counts the amount of used elements in the array
     * 
     * @param array
     * @return the sum of used elements in the array
     */
    public static <T> int usedLength(T[] array) {
        int sum = 0;
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    sum++;
                }
            }

        }
        return sum;
    }

    /**
     * Clears out the array, null'ing out all elements
     * 
     * @param array
     */
    public static <T> void clear(T[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                array[i] = null;
            }
        }
    }

    /**
     * Sorts the supplied array by the {@link Comparator}
     * 
     * @param array
     * @param comp
     * @return the supplied array
     */
    public static <T> T[] sort(T[] array, Comparator<T> comp) {
        if (array == null || array.length == 0) {
            return array;
        }

        sortStrategy.sort(array, comp, 0, array.length - 1);
        return array;
    }
}
