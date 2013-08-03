package cklabel;
import java.util.ArrayList;
import java.util.HashSet;

public class BipartiteMatching 
{	
	// Enum used to label edges during augmenting path search
	public enum Label {L1, L2, L3};
	
	// Data structures that maintain the bipartite graph
	HashSet<Integer> xSet = new HashSet<Integer>();
	HashSet<Integer> ySet = new HashSet<Integer>();
	HashSet<Integer> vertexSet = new HashSet<Integer>();
	int vertexSize;
	int xMax;
	HashSet<Edge> edgeSet = new HashSet<Edge>();
	
	// The set of edges in the matching itself
	HashSet<Edge> matching = new HashSet<Edge>();
	
	// The maximum flow variable that is modified in FF algorithm
	public int maxflow = 0;
	
	// Data structures used for FF algorithm
	Label[] label;
	int[][] capacity;
	int[][] flow;
	int[] pred;
	
	/**
	 * Create a bipartite matching using the FF algorithm to solve
	 * for the maximum flow.
	 * 
	 * @param x - X set of vertices in bipartite graph
	 * @param y - Y set of vertices in bipartite graph
	 * @param edges - the set of connecting edges between X and Y
	 */
	public BipartiteMatching(HashSet<Integer> x, HashSet<Integer> y, HashSet<Edge> edges)
	{
		xMax = 0;
		for (Integer v : x)
		{
			if (v > xMax)
			{
				xMax = v;
			}
			xSet.add(v);
		}
		for (Integer v : y)
		{
			ySet.add(v + (xMax + 1));
		}
		vertexSet.addAll(xSet);
		vertexSet.addAll(ySet);
		vertexSize = x.size() + y.size();
		edgeSet = edges;
		
		// debug
		/*System.out.print("\nx set: ");
		for (Integer v : xSet)
			System.out.print(v + " ");
		System.out.print("\ny set: ");
		for (Integer v : ySet)
			System.out.print(v + " ");
		System.out.println("\nxMax = " + xMax);*/
		
		// Create IDs for the source and sink
		int sVal = vertexSize; 
		int tVal = vertexSize + 1;
		//System.out.println("source = " + sVal + ", sink = " + tVal);
		
		// Make directed edges with the appropriate capacities and flows
		capacity = new int[vertexSize + 2][vertexSize + 2];
		flow = new int[vertexSize + 2][vertexSize + 2];
		label = new Label[vertexSize + 2];
		pred = new int[vertexSize + 2];
		for (Integer v : xSet)
		{
			flow[sVal][v] = 0;
			capacity[sVal][v] = 1;
		}
		for (Integer v : ySet)
		{
			flow[v][tVal] = 0;
			capacity[v][tVal] = 1;
		}
		for (Edge e : edges)
		{
			if (xSet.contains(e.vertexA))
			{
				flow[e.vertexA][e.vertexB + (xMax + 1)] = 0;
				capacity[e.vertexA][e.vertexB + (xMax + 1)] = 1;
			}
			//else
			//{
			//	flow[e.vertexB][e.vertexA] = 0;
			//	capacity[e.vertexB][e.vertexA] = 1;
			//}
		}
		
		// Now run the FF algorithm using these definitions.
		runFF(sVal, tVal);
	}
	
	/**
	 * Retrieve the matching from this bipartite graph.
	 * 
	 * @return edges in bipartite matching
	 */
	public HashSet<Edge> getMatching()
	{
		HashSet<Edge> mEdges = new HashSet<Edge>();
		
		// Traverse flow array and create edges from those with a flow of 1
		for (Integer x : xSet)
		{
			for (Integer y : ySet)
			{
				if (flow[x][y] == 1)
				{
					//System.out.println("adding edge " + x + " " + y);
					mEdges.add(new Edge(x, y));
				}
			}
		}
		
		return mEdges;
	}
	
	/**
	 * Run the Ford Fulkerson algorithm using the specified source
	 * and sink vertices. 
	 * 
	 * @precondition The flow and capacity data structures are 
	 * already configured appropriately before invoking this method
	 * @param source
	 * @param sink
	 */
	public void runFF(int source, int sink)
	{
		maxflow = 0;
		
		while (bfs(source, sink))
		{
			int push = Integer.MAX_VALUE; // assume obnoxious large number to start
			
			// Traverse the pred list to find the minimum flow we can push through
			for (int u = vertexSize + 1; pred[u] >= 0; u = pred[u])
			{
				int flowVal = capacity[pred[u]][u] - flow[pred[u]][u];
				push = flowVal < push ? flowVal : push;
			}
			
			// Push this flow through the path
			for (int u = vertexSet.size() + 1; pred[u] >= 0; u = pred[u])
			{
				flow[pred[u]][u] += push;
			    flow[u][pred[u]] -= push;
			}
			
			maxflow += push;
		}
	}
	
	/**
	 * Run a BFS on the tree to search for an augmenting path.
	 * 
	 * @param start
	 * @param end
	 */
	private boolean bfs(int start, int end)
	{	
		// Assume all vertices get value of L1
		for (Integer v : vertexSet)
		{
			label[v] = Label.L1; 
		}
		label[start] = Label.L1;
		label[end] = Label.L1;
		
		// Enqueue the first element to start the search
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(start);
		label[start] = Label.L2;
		pred[start] = -1;
		
		// Perform the traversal over all node paths
		boolean atEnd = false;
		while (!queue.isEmpty() && !atEnd)
		{
			int u = queue.remove(0);
			label[u] = Label.L3;
			if (u == end)
				atEnd = true;
			for (int v = 0; v < vertexSet.size() + 2 && !atEnd; v++)
			{
				// If the vertex is not visited (label == L1) and positive residual, visit it
				if (label[v] == Label.L1 && (capacity[u][v] - flow[u][v] > 0))
				{
					queue.add(v);
					label[v] = Label.L2;
					pred[v] = u;
				}
			}
		}
		
		return label[end] == Label.L3; // we found the target on an augmenting path
	}
}