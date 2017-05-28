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

        quicksort(array, comp, 0, array.length - 1);
        return array;
    }

    private static <T> void quicksort(T[] array, Comparator<T> comp, int low, int high) {
        int i = low;
        int j = high;

        T pivot = array[low + (high - low) / 2];
        while (i <= j) {
            while (comp.compare(array[i], pivot) < 0) {
                i++;
            }
            while (comp.compare(array[j], pivot) > 0) {
                j--;
            }

            if (i <= j) {
                swap(array, i, j);
                i++;
                j--;
            }
        }

        if (low < j) {
            quicksort(array, comp, low, j);
        }

        if (i < high) {
            quicksort(array, comp, i, high);
        }
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
