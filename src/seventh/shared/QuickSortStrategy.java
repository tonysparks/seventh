package seventh.shared;

import java.util.Comparator;

public class QuickSortStrategy implements SortStrategy {
    
    /**
     * Sorts the supplied array by the quickSort method
     * 
     * @param array
     * @param comp
     */
    public <T> void sort(T[] array, Comparator<T> comp) {
        quickSort(array, comp, 0, array.length-1);
    }
    
    /**
     * Sorts the supplied array by the {@link Comparator}
     * 
     * @param array
     * @param comp
     * @return the supplied array
     */
    private static <T> void quickSort(T[] array, Comparator<T> comp, int low, int high) {
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
            quickSort(array, comp, low, j);
        }

        if (i < high) {
            quickSort(array, comp, i, high);
        }
    }
    
    /**
     * Swap the elements in array
     * 
     * @param array
     * @param i
     * @param j
     */
    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
