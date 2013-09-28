package cklabel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class for the Delta function in the CK labeling algorithm.
 * @author caw
 */
public class Delta 
{
	public boolean delta[][][][];
	public  int numLabels;
	public int numVertices;
	public  HashSet<Integer> labelSet;
	public Graph graph;
	
	/*
	 * Create the new delta map.
	 */
	public Delta(int numVertices, int numLabels)
	{		
		this.numLabels = numLabels;
		this.numVertices = numVertices;
	}
	
	/*
	 * Initialize the delta map with the smallest rooted trees (the leaves)
	 */
	public void init(Graph g)
	{
		graph = g;
		delta = new boolean[numVertices][numVertices][numLabels + 1][numLabels + 1];
		
		// The common X-set for the bipartite matching (the Y partite set)
		labelSet = new HashSet<Integer>();
		for (int i = 0; i <= numLabels; i++)
		{
			labelSet.add(i);
		}
		
		// Set table to false for everything first
		for (int u = 0; u < numVertices; u++)
		{
			for (int v = 0; v < numVertices; v++)
			{
				for (int a = 0; a <= numLabels; a++)
				{
					for (int b = 0; b <= numLabels; b++)
					{
						delta[u][v][a][b] = false;
					}
				}
			}
		}
		
		// Find all leaves and initialize them in the map to true
		/*for (Integer v : g.vertexSet)
		{
			int neighborCount = 0;
			int lastNeighbor = 0;
			for (int c = 0; c < g.vertexSet.size(); c++)	
			{
				if (g.adjacencyMatrix[v][c] == 1)
				{
					neighborCount++;
					lastNeighbor = c;
				}
			}
			
			// Handle the leaf nodes as follows:
			// Delta((u,v)(*,*) = true, for all T(u,v) of height 1 (the leaves),
			// where (*,*) means all pairs of labels a and b s.t. |a-b| >= 2.
			if (neighborCount == 1)
			{
				for (int a = 0; a < numLabels; a++)
				{
					for (int b = 0; b < numLabels; b++)
					{
						if (Math.abs(a - b) >= 2)
						{
							delta[lastNeighbor][v][a][b] = true;
							System.out.println("delta " + lastNeighbor + " " + v + " " + a + " " + b + " = true");
						}
					}
				}
			}
		}*/
		
		// Initialize all leaves to false
		for (int u = 0; u < g.dimension; u++)
		{
			for (int v = 0; v < g.dimension; v++)
			{
				if (g.subtreeHeight[u][v] == 1)
				{
					for (int a = 0; a <= numLabels; a++)
					{
						for (int b = 0; b <= numLabels; b++)
						{
							if (Math.abs(a - b) >= 2)
							{
								delta[u][v][a][b] = true;
								//System.out.println("delta " + u + " " + v + " " + a + " " + b + " = true");
							}
						}
					}
				}
			}
		}
	}
	
	public boolean rootLabels(Graph graph, int a, int b)
	{
		boolean result = false;
		
		// Add all children of 'v' to the child set (the X partite set)
		HashSet<Integer> childSet = new HashSet<Integer>();
		HashSet<Edge> edges = new HashSet<Edge>();
		HashMap<Integer, Integer> childConvMap = new HashMap<Integer, Integer>();
		int childId = 0;
		
		int v = 0; // root is now the "child"
		
		if (graph.childMap.containsKey(v))
		{
			for (Integer w : graph.childMap.get(v))
			{
				//System.out.println("trying " + v + "'s child " + w);
				childConvMap.put(w, childId);
				childSet.add(childId);
				
				// Generate edges based on the delta map already built
				for (int c = 0; c <= numLabels; c++)
				{
					if (c != a)
					{
						//System.out.println("delta " + v + " " + w + " " + b + " " + c + " = " + delta[v][w][b][c]);
						if (delta[v][w][b][c] == true)
						{
							//System.out.println("adding edge between child " + w + " and label " + c);
							edges.add(new Edge(childId, c));
						}
					}
				}
				childId++;
			}
		}
		
		// Generate a bipartite matching (try to) and check the size
		BipartiteMatching matching = new BipartiteMatching(childSet, labelSet, edges);
		HashSet<Edge> mEdges = matching.getMatching();
		if (mEdges.size() == childSet.size())
		{
			return true;
			//System.out.println("delta " + u + " " + v + " " + a + " " + b + " = true");
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Check a new label scheme based on its children
	 */
	public void computeLabels(Graph graph, int u, int v, int a, int b)
	{
		//System.out.println("\ncomputing labels " + u + " " + v + " " + a + " " + b);
		
		// Add all children of 'v' to the child set (the X partite set)
		HashSet<Integer> childSet = new HashSet<Integer>();
		HashSet<Edge> edges = new HashSet<Edge>();
		HashMap<Integer, Integer> childConvMap = new HashMap<Integer, Integer>();
		int childId = 0;
		if (graph.childMap.containsKey(v))
		{
			for (Integer w : graph.childMap.get(v))
			{
				//System.out.println("trying " + v + "'s child " + w);
				childConvMap.put(w, childId);
				childSet.add(childId);
				
				// Generate edges based on the delta map already built
				for (int c = 0; c <= numLabels; c++)
				{
					if (c != a)
					{
						//System.out.println("delta " + v + " " + w + " " + b + " " + c + " = " + delta[v][w][b][c]);
						if (delta[v][w][b][c] == true)
						{
							//System.out.println("adding edge between child " + w + " and label " + c);
							edges.add(new Edge(childId, c));
						}
					}
				}
				childId++;
			}
		}
		
		// Generate a bipartite matching (try to) and check the size
		BipartiteMatching matching = new BipartiteMatching(childSet, labelSet, edges);
		HashSet<Edge> mEdges = matching.getMatching();
		if (mEdges.size() == childSet.size())
		{
			delta[u][v][a][b] = true;
			//System.out.println("delta " + u + " " + v + " " + a + " " + b + " = true");
		}
	}
	
	public void generateLabels(HashMap<Integer, Integer> map)
	{
		// Initialize everything to NULL to start
		for (int v = 0; v < graph.dimension; v++)
		{
			map.put(v, -1);
		}
		
		// Find the starting point for the u vertex
		for (Integer v : graph.childMap.get(0))
		{
			for (int a = 0; a <= numLabels; a++)
			{
				for (int b = 0; b <= numLabels; b++)
				{
					//System.out.println("exploring: 0 " + v + " " + a + " " + b);
					if (delta[0][v][a][b])
					{
						//System.out.println("starting with 0 - " + a);
						generateLabelMap(0, a, map);
					}
				}
			}
		}
	}
	
	public boolean generateLabelMap(int u, int aLabel, HashMap<Integer, Integer> map)
	{	
		// Go through u's valid children and try to build a label scheme
		HashMap<Integer, Integer> clone = new HashMap<Integer, Integer>(map);
		clone.put(u, aLabel);
		
		// Check to see if this addition doesn't violate anything
		//System.out.println("checking " + u + " - " + aLabel);
		if (validateLabels(graph.adjacencyMatrix, graph.dimension, clone))
		{
			// Add to the label map and then check all sub-trees
			//System.out.println("putting " + u + " " + aLabel);
			map.put(u, aLabel);
			HashMap<Integer, HashSet<Integer>> validMap = validChildren(u, aLabel);
			if (validMap.isEmpty() && !graph.childMap.containsKey(u))
			{
				//System.out.println("returning");
				return true;
			}
			else
			{
				for (Entry<Integer, HashSet<Integer>> set : validMap.entrySet()) // validChild: v -> b
				{
					for (Integer b : set.getValue())
					{
						if (generateLabelMap(set.getKey(), b, map))
						{
							break; // go to the next child - this one was satisfied
						}
					}
				}
			}
		}
		else
		{
			//System.out.println("violation");
		}
		
		return false;
	}
	
	public HashMap<Integer, HashSet<Integer>> validChildren(int u, int a)
	{
		HashMap<Integer, HashSet<Integer>> validMap = new HashMap<Integer, HashSet<Integer>>();
		
		// Traverse through u's children (if not leaf), v, and determine valid labels
		if (graph.childMap.containsKey(u))
		{
			for (Integer v : graph.childMap.get(u))
			{
				for (int b = 0; b <= numLabels; b++)
				{
					if (delta[u][v][a][b] == true)
					{
						if (validMap.containsKey(v))
						{
							validMap.get(v).add(b);
						}
						else
						{
							validMap.put(v,  new HashSet<Integer>());
							validMap.get(v).add(b);
						}
					}
				}
			}
		}
		
		return validMap;
	}
	/**
	 * Helper method that finds all immediate neighboring vertices of a vertex.
	 */ 
	public HashSet<Integer> findOnePathNeighbors(int matrix[][], int dimension, int vertex)
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();
        
		for (int col = 0; col < dimension; col++)
		{
			if (matrix[vertex][col] == 1)
			{
				neighbors.add(col);
			}
		}
        
		return neighbors;
	}
    
	/**
	 * Helper method that finds all vertices that are exactly a distance 2 away from the specified vertex.
	 */
	public HashSet<Integer> findTwoPathNeighbors(int matrix[][], int dimension, int vertex)
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();
		HashSet<Integer> temp;
        
		for (int col = 0; col < dimension; col++)
		{
			if (matrix[vertex][col] == 1)
			{
				temp = findOnePathNeighbors(matrix, dimension, col);
				temp.remove(vertex); // remove us from the set of neighbors
				for (Integer neighbor : temp)
				{
					neighbors.add(neighbor);
				}
			}
		}
        
		return neighbors;
	}
	
	/**
	 * Method that checks the validity of a given vertex label based on its surrounding vertices.
	 */ 
	public boolean validLabel(int[][] matrix, int size, int vertex, HashMap<Integer, Integer> labelMap)
	{
		boolean result = true;
		HashSet<Integer> onePathNeighbors = findOnePathNeighbors(matrix, size, vertex);
		HashSet<Integer> twoPathNeighbors = findTwoPathNeighbors(matrix, size, vertex);
		
		// Check all 1-path and 2-path neighbors
		// Must be 2 away!
		for (Integer opn : onePathNeighbors)
		{
			if (labelMap.get(opn) != -1 && Math.abs(labelMap.get(opn) - labelMap.get(vertex)) < 2)
			{
				result = false;
				break;
			}
		}
		
		// Must be 1 away! Only bother checking if we still consider this label to be valid
		if (result)
		{
			for (Integer tpn : twoPathNeighbors)
			{
				if (labelMap.get(tpn) != -1 && Math.abs(labelMap.get(tpn) - labelMap.get(vertex)) < 1)
				{
					result = false;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Method that checks the validity of a labelling scheme for a graph when its complete
	 */
	public boolean validateLabels(int[][] matrix, int size, HashMap<Integer, Integer> labelMap)
	{
		boolean result = true;
		
		for (int i = 0; i < size; i++)
		{
			if (labelMap.get(i) != -1)
			{
				result = validLabel(matrix, size, i, labelMap);
				if (result == false)
				{
					break;
				}
			}
		}
		
		return result;
	}
}
