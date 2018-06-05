package seventh.shared;

public class SortStrategyFactory {
	
	public <T> SortStrategy getSortStrategy(T[] array) {
		if (array == null || array.length == 0)
            return new NoSortStrategy();
		else
			return new QuickSortStrategy();
	}
}
