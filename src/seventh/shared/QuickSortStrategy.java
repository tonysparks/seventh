package seventh.shared;

import java.util.Comparator;

public class QuickSortStrategy implements SortStrategy {
	public <T> void sort(T[] array, Comparator<T> comp) {
		quickSort(array, comp, 0, array.length-1);
	}
	
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
	
	private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
