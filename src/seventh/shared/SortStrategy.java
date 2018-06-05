package seventh.shared;

import java.util.Comparator;

public interface SortStrategy {
    public <T> void sort(T[] array, Comparator<T> comp);
}