/*
 * see license.txt 
 */
package seventh.graph;

/**
 * List of edges for a {@link GraphNode}
 * 
 * @author Tony
 *
 */
public class Edges<E, T> {

	public static enum Directions {
		N(1<<0),
		E(1<<1),
		S(1<<2),
		W(1<<3),
		NE(1<<4),
		SE(1<<5),
		SW(1<<6),
		NW(1<<7),
		;
		
		private static final Directions[] values = values();
		private int mask;
		
		Directions(int m) {
			this.mask = m;
		}
		
		/**
		 * @return the mask
		 */
		public int getMask() {
			return mask;
		}
		
		public static Directions fromIndex(int index) {
			return values[index];
		}

		public static boolean isCardinal(int index) {
			return index>=0 && index < NE.ordinal();
		}
		
		public static boolean isInterCardinal(int index) {
			return index < 8 && index > W.ordinal();
		}
	}
	
	private Edge<E, T>[] edges;
	
	@SuppressWarnings("unchecked")
	public Edges() {
		this.edges = new Edge[8];
	}
	
	public int size() {
		return this.edges.length;
	}
	
	public void addEdge(Directions dir, Edge<E, T> edge) {
		this.edges[dir.ordinal()] = edge;
	}
	
	public Edge<E,T> get(int index) {
		return this.edges[index];
	}
	
	public Edge<E,T> get(Directions dir) {
		return this.edges[dir.ordinal()];
	}
	

}
