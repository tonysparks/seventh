package seventh.shared;

public final class SortStrategyFactory {
	
	/**
     * Return SortStrategy for the supplied array
     * 
     * @param array
     * @return SortStrategy
     */
	public static <T> SortStrategy getSortStrategy(T[] array) {
		if (array == null || array.length == 0)
            return new NoSortStrategy();
		else
			return new QuickSortStrategy();
	}
}
