package cklabel;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class that stores the representation of a graph for ease
 * of manipulation (build from an adjacency matrix).
 */
public class Graph
{
	public int[][] adjacencyMatrix;
	public int dimension;
	public HashSet<Integer> vertexSet = new HashSet<Integer>();
	public HashSet<Edge> edgeSet = new HashSet<Edge>();
	
	// Implicit tree fields
	int root;
	int height;
	public int[] pred;
	HashMap<Integer, HashSet<Integer>> childMap =
			new HashMap<Integer, HashSet<Integer>>(); // vertex -> children vertex set
	HashMap<Integer, HashSet<Integer>> subtreeMap = 
			new HashMap<Integer, HashSet<Integer>>(); // tree height -> root vertex
	
	// New subtree structures
	public HashMap<Integer, HashMap<Integer, HashSet<Integer>>> subtrees = 
			new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
	// Height array
	public int[][] subtreeHeight;
	
	public Graph(int[][] matrix, int size)
	{
		dimension = size;
		adjacencyMatrix = matrix;
		root = 0;
		pred = new int[size];
		subtreeHeight = new int[size][size];
		
		// Init subtree height (-1) invalid, filled in later
		for (int u = 0; u < size; u++)
		{
			for (int v = 0; v < size; v++)
			{
				subtreeHeight[u][v] = -1;
			}
		}
		
		// Build the vertex and edge set
		for (int i = 0; i < size; i++)
		{
			vertexSet.add(i);
		}
		
		// Build the edge set
		for (int r = 0; r < size; r++)
		{
			for (int c = 0; c < size; c++)
			{
				if (matrix[r][c] == 1)
				{
					edgeSet.add(new Edge(r, c));
				}
			}
		}
		
		// Now build the tree
		height = height(root, null);
		buildTree();
		buildTreeHeights();
		buildSubTrees();
		
		// Displaying child map
		/*
		System.out.println("Graph child map:");
		for (Integer v : childMap.keySet())
		{
			System.out.println("Children of " + v);
			for (Integer c : childMap.get(v))
			{
				System.out.print(c + " ");
			}
			System.out.println();
		}
		System.out.println("Graph vertex heights");
		for (Integer h : subtreeMap.keySet())
		{
			System.out.println("vertices with height " + h);
			for (Integer v : subtreeMap.get(h))
			{
				System.out.print(v + " ");
			}
			System.out.println();
		}
		*/
	}
	
	/*
	 * Build the subtrees of this graph (all T(u,v)'s)
	 */
	private void buildTreeHeights()
	{
		// Generate all valid height subtrees
		for (int i = 1; i <= height; i++)
		{
			for (int v = 0; v < vertexSet.size(); v++)
			{
				//System.out.println("considering vertex " + v + " - " + heightNode(v));
				if (heightNode(v) == i)
				{
					if (!subtreeMap.containsKey(i))
					{
						subtreeMap.put(i, new HashSet<Integer>());
					}
					//System.out.println("vertex " + v + " has height " + i);
					subtreeMap.get(i).add(v);
				}
			}
		}
	}
	
	private void buildSubTrees()
	{
		// Build all sub trees for each vertex
		for (Integer r : vertexSet)
		{
			// Main rooted tree is already built!
			if (r != 0)
			{
				for (Integer v : vertexSet)
				{
					if (childMap.containsKey(v))
					{
						if (childMap.get(v).contains(r))
						{
							subtrees.put(v, new HashMap<Integer, HashSet<Integer>>());
							subtrees.get(v).put(r, new HashSet<Integer>());
							subtrees.get(v).get(r).add(v);
							subtrees.get(v).get(r).add(r);
							subtrees.get(v).get(r).addAll(addChildrenToTree(r, new HashSet<Integer>()));
							subtreeHeight[v][r] = 1 + heightNode(r);
							
							// debug
							/*System.out.println("subtree for root (" + v + "," + r + "):");
							for (Integer vertex : subtrees.get(v).get(r))
							{
								System.out.print(vertex + " ");
							}
							System.out.println("height = " + subtreeHeight[v][r]);
							*/
						}
					}
				}
			}
		}
	}
	
	private HashSet<Integer> addChildrenToTree(int r, HashSet<Integer> children)
	{
		if (childMap.containsKey(r))
		{
			for (Integer c : childMap.get(r))
			{
				children.add(c);
				children.addAll(addChildrenToTree(c, children));
			}
		}
		
		return children;
	}
	
	/*
	 * Build up the tree from the adjacency matrix and root node
	 */
	private void buildTree()
	{
		visitNode(root, null);
	}
	
	/*
	 * Visit each node and populate the tree data structures
	 */
	private void visitNode(int vertex, HashSet<Integer> visited)
	{
		if (visited == null)
		{
			visited = new HashSet<Integer>();
			visited.add(vertex);
			pred[vertex] = -1;
		}
		
		for (int v = 0; v < dimension; v++)
		{
			if (adjacencyMatrix[vertex][v] == 1 && !visited.contains(v))
			{
				if (!childMap.containsKey(vertex))
				{
					childMap.put(vertex, new HashSet<Integer>());
				}
				childMap.get(vertex).add(v);
				pred[v] = vertex;
				visited.add(v);
				visitNode(v, visited);
			}
		}
	}
	
	/*
	 * Determine the maximum degree of the graph
	 */
	public int maxDegree()
	{
		int max = 0;
		for (int r = 0; r < dimension; r++)
		{
			int sum = 0;
			for (int c = 0; c < dimension; c++)
			{
				sum += adjacencyMatrix[r][c];
			}
			if (sum > max)
			{
				max = sum;
			}
		}
		return max;
	}
	
	// Recursively generate the height of this tree
	public int height(int vertex, HashSet<Integer> visited)
	{
		if (visited == null)
		{
			visited = new HashSet<Integer>();
			visited.add(vertex);
		}
		
		// Check the height of all children
		int heightChild = 0;
		int maxHeight = -1;
		for (int v = 0; v < dimension; v++)
		{
			if (adjacencyMatrix[vertex][v] == 1 && !visited.contains(v))
			{
				visited.add(v);
				heightChild = height(v, visited);
				if (heightChild > maxHeight)
				{
					maxHeight = heightChild;
				}
			}
		}
		
		return 1 + maxHeight;
	}
	
	/*
	 * Generate the height from nodes using the pre-built pred and child
	 * map data structures
	 */
	public int heightNode(int vertex)
	{
		if (!childMap.containsKey(vertex))
		{
			return 0;
		}
		else
		{
			int max = 0;
			int height = 0;
			for (Integer c : childMap.get(vertex))
			{
				height = heightNode(c);
				if (height > max)
				{
					max = height;
				}
			}
			return 1 + max;
		}
	}
}