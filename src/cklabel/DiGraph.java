package cklabel;
import java.util.HashSet;

/**
 * Class that stores the representation of a directed graph for ease
 * of manipulation (build from an adjacency matrix).
 */
public class DiGraph
{
	int[][] adjacencyMatrix;
	int dimension;
	HashSet<Integer> vertexSet = new HashSet<Integer>();
	HashSet<DirectedEdge> edgeSet = new HashSet<DirectedEdge>();
	
	public DiGraph(int[][] matrix, int size)
	{
		dimension = size;
		adjacencyMatrix = matrix;
		
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
					edgeSet.add(new DirectedEdge(r, c));
				}
			}
		}
	}
}