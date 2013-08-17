import java.io.*;
import java.util.*;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedLong;
import edu.rit.pj.reduction.SharedInteger;
import edu.rit.pj.reduction.IntegerOp;
import edu.rit.pj.Comm;
import edu.rit.pj.BarrierAction;

public class ParallelL21Assignment
{
	static HashMap<Integer, Integer> finalLabelMap = new HashMap<Integer, Integer>();
	static ParallelTeam team = new ParallelTeam();
	static ArrayList<Integer> labels;
	static ArrayList<Integer> vertices;
	static SharedInteger span;

	public ParallelL21Assignment()
	{
	}
	
	public int determineMinLabelSpan(final int[][] matrix, final int size, final int max) throws Exception
	{
		labels = new ArrayList<Integer>();
		vertices = new ArrayList<Integer>();
		final int maxDegree = max;
        
		// Create label list to iterate through
		for (int i = 0; i <= maxDegree; i++)
		{
			labels.add(i);
		}

		// Create the linked list of vertices
		for (int i = 0; i < size; i++)
		{
			vertices.add(i);
			// labelMap.put(i, -1);
		}

		span = new SharedInteger(Integer.MAX_VALUE);
		team.execute(new ParallelRegion() // linear combinations get computed in parallel - but can we push parallelism to the outer loop?
		{
			public void run() throws Exception // There are many, many combinations (potentially...)
			{
				execute (0, labels.size() - 1, new IntegerForLoop()
				{
					
					HashMap<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
					HashMap<Integer, Integer> localFinalMap = new HashMap<Integer, Integer>();
					int minSpan = Integer.MAX_VALUE;

					public IntegerSchedule schedule()
					{
						return IntegerSchedule.guided();
					}

					// Initialize the label map...
					public void start() throws Exception
					{
						// Create the linked list of vertices
						for (int i = 0; i < size; i++)
						{
							labelMap.put(i, -1);
							localFinalMap.put(i, -1);
						}
					}

					public void run (int first, int last) throws Exception
					{
						for (int t_ii = first; t_ii <= last; t_ii++)
						{

							// while (itr.hasNext())
							{
								int label = labels.get(t_ii);
								labelMap.put(0, label);
								int newSpan = assignLabel(vertices, 1, labels, matrix, size, labelMap, localFinalMap, maxDegree, minSpan);
								if (newSpan < minSpan && newSpan != Integer.MAX_VALUE)
								{
									minSpan = newSpan;
									// System.out.println("New span: " + minSpan);
								}
							}
							// return minSpan;

						}
					}

					public void finish()
					{
						if (minSpan < span.get())
						{
							finalLabelMap = new HashMap<Integer, Integer>();
		            		for (Integer v : localFinalMap.keySet())
		            		{
		            			finalLabelMap.put(v, localFinalMap.get(v));
		            		}
							span.set(minSpan);
						}
					}
				});
			}
		});
		
		// Invoke the brute force assignment algorithm here
		// int span = assignLabel(vertices, 0, labels, matrix, size, labelMap, maxDegree, Integer.MAX_VALUE);
		// span = span == Integer.MAX_VALUE ? -1 : span;
		return span.get();
	}

	// Recursive method that attempts to assign labels to each vertex in the tree using a brute force approach
	public int assignLabel(ArrayList<Integer> vertices, int vertexIndex, ArrayList<Integer> labels, 
		int matrix[][], int numVertices, HashMap<Integer, Integer> labelMap, HashMap<Integer, Integer> localFinalMap, int maxDegree, int span)
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
                	if (newSpan < span)
                	{
	            		for (Integer v : labelMap.keySet())
	            		{
	            			localFinalMap.put(v, labelMap.get(v));
	            		}
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
				int newSpan = assignLabel(vertices, vertexIndex + 1, labels, matrix, numVertices, labelMap, localFinalMap, maxDegree, span);
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
	
	public static void main(String[] args) throws Exception // I'm just being lazy now...
	{
		Comm.init(args);

		// Check the command line arguments 		
		if (args.length != 4)
		{
			System.err.println("usage: java ParallelL21Assignment mode maxLow maxHigh (file|string)");
			System.err.println("	mode = 0: AMF file");
			System.err.println("	mode = 1: G6 string");
			System.err.println("	mode = 2: file of G6 strings");
			return;
		}

		// assigner and mode fetch
		ParallelL21Assignment assigner = new ParallelL21Assignment();
		int mode = Integer.parseInt(args[0]);	

		// These two arguments specify the range of labels we should try... 
		// Performing the exhaustive search for the span grows exponentially as these grow apart.
		int maxLabelLow = Integer.parseInt(args[1]);
		int maxLabelHigh = Integer.parseInt(args[2]);

		// Shared....
		int span = -1;
		int matrix[][];

		switch (mode)
		{
			case 0: // file with adjacency matrix
				try
				{
					BufferedReader reader = new BufferedReader(new FileReader(args[3]));
		            
					// Now, read in the matrix dimensions
					int dimensions = Integer.parseInt(reader.readLine());
					matrix = new int[dimensions][dimensions];
					
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
					span = -1;
					for (int i = maxLabelLow; i <= maxLabelHigh; i++)
					{
						span = assigner.determineMinLabelSpan(matrix, dimensions, i); 
						if (span == -1) break;
					}
					System.out.println(span);
					System.out.println(finalLabelMap);
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
				matrix = G6Parser.parseG6(args[3]);
				// System.out.println(assigner.determineMinLabelSpan(matrix, matrix.length, maxLabelLow)); // assumed to be square matrix, derp
				span = -1;
				for (int i = maxLabelLow; i <= maxLabelHigh; i++)
				{
					span = assigner.determineMinLabelSpan(matrix, matrix.length, i); // assumed to be square matrix, derp	
					if (span == -1) break;
				}
				System.out.println(span);
				System.out.println(finalLabelMap);
				break;
			case 2: // file of G6 strings
				BufferedReader reader = new BufferedReader(new FileReader(args[3]));
				String line = reader.readLine();
				do
				{
					matrix = G6Parser.parseG6(line);
					span = -1;
					for (int i = maxLabelLow; i <= maxLabelHigh; i++)
					{
						span = assigner.determineMinLabelSpan(matrix, matrix.length, i); // assumed to be square matrix, derp	
						if (span == -1) break;
					}
					System.err.println(line + "," + span);
					System.err.println(finalLabelMap);
					System.out.println(line + "," + span + "," + finalLabelMap.toString());
					line = reader.readLine();
				} while (line != null && line.length() > 0);

				// Close up shop
				reader.close();
				break;
			default:
				System.err.println("Mode: " + mode + " is not supported.");
				System.exit(-1);
		}
	}
}
