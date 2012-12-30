package cklabel;
import java.io.*;

public class CKLabel
{
	public Delta delta;
	public Graph graph;
	public int height;
	public int majorLabel;
	
	/**
	 * Default, empty constructor.
	 */
	public CKLabel(Graph g)
	{
		this.graph = g;
		this.delta = new Delta(g.vertexSet.size(), g.maxDegree() + 1);
		this.majorLabel = g.maxDegree() + 1;
		delta.init(graph);
	}
	
	/**
	 * Run the CK algorithm to determine if this tree has a label span of
	 * delta+1 or delta+2
	 * @return true if delta+1, false otherwise
	 */
	public boolean runCK()
	{
		boolean result = false;
		
		// Kick off DP (recursively check each tree with a specific height)
		for (int height = 2; height <= graph.height; height++)
		{	
			for (int u = 0; u < graph.dimension; u++)
			{
				for (int v = 0; v < graph.dimension; v++)
				{
					if (graph.subtreeHeight[u][v] != -1 && graph.subtreeHeight[u][v] == height)
					{
						System.out.println("analyzing subtree T(" + u + "," + v + ")");
						//if (u != 0) // we should NOT ignore this, even if it does touch the root
						{
							for (int a = 0; a <= majorLabel; a++)
							{
								for (int b = 0; b <= majorLabel; b++)
								{
									if (b != a && b != (a+1) && b != (a-1))
									{
										//System.out.println("Trying " + a + " + " + b);
										delta.computeLabels(graph, u, v, a, b);
										
										if (delta.delta[u][v][a][b] == true)
										{
											System.out.println("True for (" + a + ", " + b + ")");
										}
										
										// Perform final check - we've reached the max height of the tree
										if (height == graph.height)
										{
											if (delta.delta[u][v][a][b] == true)
											{
												//System.out.println("valid = " + a + "," + b);
												result = true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		// No way the root was satisfied
		if (result == false)
		{
			return false;
		}
		else // Check to see if the labels converged at the root
		{
			for (int a = 0; a <= majorLabel; a++)
			{
				for (int b = 0; b <= majorLabel; b++)
				{
					//if (b != a && b != (a+1) && b != (a-1)) // we don't care about this guy since he's the root
					{
						// Check to see if this label pair satisfies the children
						System.out.println("checking " + a + "," + b + " for root");
						if (delta.rootLabels(graph, a, b))
						{
							return true;
						}
					}
				}
			}
			
			// Did not converge at the root
			return false;
		}
	}
	
	/**
	 * Main entry point of program
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Check the command line arguments 		
		if (args.length != 1)
		{
			System.err.println("usage: java CKLabel file");
			return;
		}
        
		// Try to create a buffer reader to parse the adjacency matrix
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            
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
		
			// Run the CK label algorithm here
			CKLabel labeler = new CKLabel(new Graph(matrix, dimensions));
			boolean success = labeler.runCK();
			System.out.println(success);
			
			// Now build the label scheme from this result
			/*if (success)
			{
				
				HashMap<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
				labeler.delta.generateLabels(labelMap);
				System.out.println(labelMap);
			}*/
		}
		catch (IndexOutOfBoundsException ex1)
		{
			System.err.println("Error parsing adjacency matrix file.");
			ex1.printStackTrace();
		}
		catch (NumberFormatException ex2)
		{
			System.err.println("Error parsing adjacency matrix file.");
			ex2.printStackTrace();
		}
		catch (IOException ex3)
		{
			System.err.println("Error parsing adjacency matrix file.");
			ex3.printStackTrace();
		}
	}
}

