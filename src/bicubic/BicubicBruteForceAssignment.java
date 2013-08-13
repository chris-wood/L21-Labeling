import java.io.*;
import java.util.*;

public class BicubicBruteForceAssignment
{
	public BicubicBruteForceAssignment()
	{
	}
	
	public int determineMinLabelSpan(int[][] matrix, int size)
	{
		HashMap<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
		ArrayList<Integer> labels = new ArrayList<Integer>();
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		int maxDegree = 15;
        
		// Create label list to iterate through
		for (int i = 0; i <= maxDegree + 1; i++)
		{
			labels.add(i);
		}

		// Create the linked list of vertices
		for (int i = 0; i < size; i++)
		{
			vertices.add(i);
			labelMap.put(i, -1);
		}
		
		// Invoke the brute force assignment algorithm here
		int span = assignLabel(vertices, 0, labels, matrix, size, labelMap, maxDegree, Integer.MAX_VALUE);
		return span;
	}

	// Recursive method that attempts to assign labels to each vertex in the tree using a brute force approach
	public int assignLabel(ArrayList<Integer> vertices, int vertexIndex, ArrayList<Integer> labels, 
		int matrix[][], int numVertices, HashMap<Integer, Integer> labelMap, int maxDegree, int span)
	{
		if ((vertexIndex + 1) == numVertices) // base case
		{
			ListIterator<Integer> itr = labels.listIterator();
			while (itr.hasNext())
			{
				int label = itr.next();	
				labelMap.put(vertexIndex, label);
				
				// Now check against all vertices (p1s and p2s)
				boolean valid = checkLabels(matrix, numVertices, vertices, labelMap);
                if (valid)
                {
                	int newSpan = determineSpan(labelMap);
                	if (newSpan < span && newSpan < 7)
                	{
                		return newSpan;
                	}
                	else
                	{
                		return span;
                	}
                	// System.out.println("Valid label span = " + newSpan);
                	// return newSpan;
                }
                else
                {
                	return span;
                }
			}
		}
		else
		{
			// Try all label assignments
			ListIterator<Integer> itr = labels.listIterator();
			int minSpan = span;
			while (itr.hasNext())
			{
				int label = itr.next();
				labelMap.put(vertexIndex, label);
				int newSpan = assignLabel(vertices, vertexIndex + 1, labels, matrix, numVertices, labelMap, maxDegree, span);
				if (newSpan < minSpan)
				{
					minSpan = newSpan;
				}
			}
			return minSpan;
		}

		return span;
	}

	public int determineSpan(HashMap<Integer, Integer> labelMap)
	{
		int minSpan = Integer.MAX_VALUE;
		int maxSpan = 0;
		for (Integer v : labelMap.keySet())
		{
			int label = labelMap.get(v);
			if (label > maxSpan)
			{
				maxSpan = label;
			}
			if (label < minSpan)
			{
				minSpan = label;
			}
		}

		return maxSpan - minSpan;
	}

	public boolean checkLabels(int matrix[][], int size, ArrayList<Integer> vertices, HashMap<Integer, Integer> labelMap)
	{
		boolean valid = true;
		HashSet<Integer> onePathNeighbors; 
		HashSet<Integer> twoPathNeighbors;	

		ListIterator<Integer> itr = vertices.listIterator();
		while (itr.hasNext())
		{
			int vertex = itr.next();
			onePathNeighbors = findOnePathNeighbors(matrix, size, vertex);
			twoPathNeighbors = findTwoPathNeighbors(matrix, size, vertex);	

			// Must be 2 away!
            for (Integer opn : onePathNeighbors)
            {
                if (Math.abs(labelMap.get(opn) - labelMap.get(vertex)) < 2)
                {
                    valid = false;
                    break;
                }
            }
            
            // Must be 1 away! Only bother checking if we still consider this label to be valid
            for (Integer tpn : twoPathNeighbors)
            {
                if (Math.abs(labelMap.get(tpn) - labelMap.get(vertex)) < 1)
                {
                    valid = false;
                    break;
                }
            }

			if (valid == false)
			{
				break;
			}
		}
		
		return valid;
	}

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
	
	public static void main(String[] args)
	{
		// Check the command line arguments 		
		if (args.length != 2)
		{
			System.err.println("usage: java BruteForceAssignment mode file");
			return;
		}

		// assigner and mode fetch
		BicubicBruteForceAssignment assigner = new BicubicBruteForceAssignment();
		int mode = Integer.parseInt(args[0]);	

		switch (mode)
		{
			case 0: // file with adjacency matrix
				try
				{
					BufferedReader reader = new BufferedReader(new FileReader(args[1]));
		            
					// Now, read in the matrix dimensions
					int dimensions = Integer.parseInt(reader.readLine());
					int matrix[][] = new int[dimensions][dimensions];
					
					// Continue parsing in the rest of the data
					for (int i = 0; i < dimensions; i++)
					{
						String line = reader.readLine();
						String[] elements = line.split(" ");
						for (int j = 0; j < dimensions; j++)
						{
							matrix[i][j] = Integer.parseInt(elements[j]);
						}
					}
				
					// Run the brute force solver here
					System.out.println(assigner.determineMinLabelSpan(matrix, dimensions));
					
				}
				catch (IndexOutOfBoundsException ex1)
				{
					System.err.println("Error parsing adjacency matrix file.");
				}
				catch (NumberFormatException ex2)
				{
					System.err.println("Error parsing adjacency matrix file.");
				}
				catch (IOException ex3)
				{
					System.err.println("Error parsing adjacency matrix file.");
				}
				break;
			case 1: // G6 string
				int matrix[][] = G6Parser.parseG6(args[1]);
				System.out.println(assigner.determineMinLabelSpan(matrix, matrix.length)); // assumed to be square matrix, derp
				break;
			default:
				System.err.println("Mode: " + mode + " is not supported.");
				System.exit(-1);
		}
	}
}
