package seventh.shared;

import java.util.Comparator;

public interface SortStrategy {
    
    /**
     * Sorts the supplied array by the {@link Comparator}
     * 
     * @param array
     * @param comp
     * @return the supplied array
     */
    public <T> void sort(T[] array, Comparator<T> comp);
}