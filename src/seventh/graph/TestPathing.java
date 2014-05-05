/*
 *  see license.txt
 */
package seventh.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seventh.graph.GraphSearch.SearchCondition;

/**
 * @author Tony
 *
 */
public class TestPathing {

	static class NodeData {
		int x, y;
		
		NodeData(int x, int y) {
			this.x = x; this.y = y;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {		
			return "[ " + x + "," + y + " ]";
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodeData other = (NodeData) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
		
	}
	static class EdgeData {		
	}
	
	static final int[][] MAP = 
	{
		//0  1  2  3  4  5  6  7  8
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0},//0
		{ 0, 0, 0, 1, 1, 0, 0, 0, 0},//1
		{ 0, 0, 0, 0, 1, 0, 0, 0, 0},//2
		{ 0, 0, 0, 0, 1, 0, 0, 0, 0},//3
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0},//4
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0},//5
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		TestPathing path = new TestPathing();
		
		final NodeData start = new NodeData(8,3);
		final NodeData findMe = new NodeData(1,2);
		
		Map<NodeData, GraphNode<NodeData, EdgeData>> graph = path.constructGraph(MAP);
		GraphNode<NodeData, EdgeData> node = graph.get(start);
		GraphSearch<NodeData, EdgeData> search = new DepthFirstGraphSearch<NodeData, EdgeData>();
				
		
		final int[] iterations = {0};
		GraphNode<NodeData, EdgeData> result = search.search(node, new SearchCondition<TestPathing.NodeData, TestPathing.EdgeData>() {
			
			@Override
			public boolean foundItem(GraphNode<NodeData, EdgeData> node) {
				//System.out.println("Checking: " + node.getValue());
				iterations[0]++;
				return findMe.equals(node.getValue());
			}
		});
		
		
		if(result != null ) {
			System.out.println("Found: " + result.getValue() + " in " + iterations[0] + " iterations.");
		}
		else {
			System.out.println("No result found.");
		}
		
		
		GraphSearchPath<NodeData, EdgeData> searchPath = new AStarGraphSearch<TestPathing.NodeData, TestPathing.EdgeData>(){
			/* (non-Javadoc)
			 * @see graph.AStarGraphSearch#heuristicEstimateDistance(graph.GraphNode, graph.GraphNode)
			 */
			@Override
			protected int heuristicEstimateDistance(
					GraphNode<NodeData, EdgeData> currentNode,
					GraphNode<NodeData, EdgeData> goal) {			
				int distance = ((currentNode.getValue().x - goal.getValue().x) *
							    (currentNode.getValue().x - goal.getValue().x)) +
							   ((currentNode.getValue().y - goal.getValue().y) *
							    (currentNode.getValue().y - goal.getValue().y));
				
				return distance;
			}
		};
		List<GraphNode<NodeData, EdgeData>> resultPath = searchPath.search(node, result);
		if(resultPath==null || resultPath.isEmpty()) {
			System.out.println("No path found");
		}
		else {
			for(GraphNode<NodeData, EdgeData> n:resultPath) {
				System.out.println("Visit node: " + n.getValue());
			}
			
			for(int y = 0; y < MAP.length; y++) {
				System.out.println("+-+-+-+-+-+-+-+-+-+");
				for(int x = 0; x < MAP[y].length; x++ ) {
					boolean isPath = false;
					for(GraphNode<NodeData, EdgeData> n:resultPath) {
						isPath = n.getValue().equals(new NodeData(x,y));
						if(isPath)break;
					}
					
					if(MAP[y][x] > 0) {
						System.out.print(isPath ? "|E" : "|#");
					}
					else {
						System.out.print(isPath ? "|*" : "| ");
					}
				}
				System.out.println("|");
			}
			System.out.println("+-+-+-+-+-+-+-+-+-+");
		}

	}
	
	public Map<NodeData, GraphNode<NodeData, EdgeData>> constructGraph(int[][] map) throws Exception {
		Map<NodeData, GraphNode<NodeData, EdgeData>> nodes = new HashMap<TestPathing.NodeData, GraphNode<NodeData,EdgeData>>();
		
		// first build all graph nodes.
		for(int y = 0; y < map.length; y++) {
			for(int x = 0; x < map[y].length; x++ ) {
				if (map[y][x]==0) 
				{
					NodeData data = new NodeData(x,y);
					GraphNode<NodeData, EdgeData> node = new GraphNode<NodeData, EdgeData>(data);
					nodes.put(data, node);
				}
			}
		}
		
		
		// now let's build the edge nodes
		for(GraphNode<NodeData, EdgeData> node : nodes.values()) {
			NodeData data = node.getValue();
			
			GraphNode<NodeData, EdgeData> nw = nodes.get(new NodeData(data.x - 1, data.y - 1));
			GraphNode<NodeData, EdgeData> n = nodes.get(new NodeData(data.x, data.y - 1));
			GraphNode<NodeData, EdgeData> ne = nodes.get(new NodeData(data.x + 1, data.y - 1));
			
			GraphNode<NodeData, EdgeData> e = nodes.get(new NodeData(data.x + 1, data.y));
			
			GraphNode<NodeData, EdgeData> se = nodes.get(new NodeData(data.x + 1, data.y + 1));
			GraphNode<NodeData, EdgeData> s = nodes.get(new NodeData(data.x, data.y + 1));
			GraphNode<NodeData, EdgeData> sw = nodes.get(new NodeData(data.x - 1, data.y + 1));
			
			GraphNode<NodeData, EdgeData> w = nodes.get(new NodeData(data.x - 1, data.y));
			
			if (nw != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, nw, new EdgeData()));
			}
			if (n != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, n, new EdgeData()));
			}
			if (ne != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, ne, new EdgeData()));
			}
			if (e != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, e, new EdgeData()));
			}
			if (se != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, se, new EdgeData()));
			}
			if (s != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, s, new EdgeData()));
			}
			if (sw != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, sw, new EdgeData()));
			}
			if (w != null) {
				node.addEdge(new Edge<TestPathing.NodeData, TestPathing.EdgeData>(node, w, new EdgeData()));
			}
		}
		
		
		return nodes;
	}

}
