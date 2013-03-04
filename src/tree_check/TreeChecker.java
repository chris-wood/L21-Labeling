import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class TreeChecker 
{
	public int[][] adjacencyMatrix;
	public int dimension;
	public int majorLabel;
	public int[] degree;
	
	public TreeChecker(int[][] matrix, int size)
	{
		this.adjacencyMatrix = matrix;
		this.dimension = size;
		degree = new int[size];
		majorLabel = 0;
		
		// Set vertex degrees
		for (int v = 0; v < size; v++)
		{
			int sum = 0;
			for (int c = 0; c < size; c++)
			{
				sum += matrix[v][c];
			}
			degree[v] = sum;
			if (degree[v] > majorLabel)
			{
				majorLabel = degree[v];
			}
		}
	}
	
	public boolean testStar()
	{
		boolean present = false;
		
		for (int i = 0; i < degree.length; i++)
		{
			if (degree[i] < majorLabel)
			{
				// get all 1path neighbors, if there are n >= 3 neighbors, present = true
				HashSet<Integer> neighbors = findOnePathNeighbors(i);
				int majorCount = 0;
				for (Integer n : neighbors)
				{
					if (degree[n] == majorLabel)
					{
						majorCount++;
					}
				}
				if (majorCount >= 3)
				{
					present = true;
					break;
				}
			}
		}
		
		return present;
	}
	
	public boolean testMajorP3()
	{
		boolean present = false;
		
		// Search all major vertices
		for (int i = 0; i < degree.length; i++)
		{
			if (degree[i] == majorLabel)
			{
				if (majorNeighborhoodSize(i, null) >= 3)
				{
					present = true;
					break;
				}
			}
		}
		
		return present;
	}
	
	public int majorNeighborhoodSize(int vertex, HashSet<Integer> visited)
	{
		if (visited == null)
		{
			visited = new HashSet<Integer>();
		}
		
		visited.add(vertex);
		int size = 1;
		
		for (int v = 0; v < dimension; v++)
		{
			if (adjacencyMatrix[vertex][v] == 1 && !visited.contains(v) && degree[v] == majorLabel)
			{
				size += majorNeighborhoodSize(v, visited);
			}
		}
		
		return size;
	}
	
	public boolean testDeltaMinusOneNeighbors()
	{
		boolean present = false;
		
		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == majorLabel)
			{
				HashSet<Integer> twoNeighbors = findTwoPathNeighbors(v);
				int maxVertices = 0;
				for (Integer tpn : twoNeighbors)
				{
					if (degree[tpn] == majorLabel)
					{
						maxVertices++;
					}
				}
				if (maxVertices >= (majorLabel - 1))
				{
					present = true;
					break;
				}
			}
		}
		
		return present;
	}
	
	public boolean testThreeStar()
	{
		boolean present = false;
		
		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == 3) // we only expect delta=3 to be victim to these cases
			{
				HashSet<Integer> ones = findOnePathNeighbors(v);
				HashSet<Integer> threes = newFindFour(v);
			
				// Check to see if threes has 3 deltas (with delta = 3)
				int count = 0;
				for (Integer n : threes)
				{
					if (degree[n] == 3)
					{
						count++;
					}
				}
				if (count >= 3)
				{
					//System.out.println("1");
					present = true;
					return true;
				}
				else // check for immediate delta and then 2 threes
				{
					boolean deltaN = false;
					int vertex = -1;
					for (Integer n : ones)
					{
						if (degree[n] == 3)
						{
							deltaN = true;
							vertex = n;

							count = 0;
							for (Integer u : threes)
							{
								//System.out.println("four away = " + n);
								if (degree[u] == 3)
								{
									count++;
								}
							}
							if (count >= 2)
							{
								//System.out.println("2 - " + v);
								present = true;
								return true;
							}
						}
					}
				}
			}
		}
		
		return present;
	}
	
	public boolean testSplitStar()
	{
		boolean present = false;
		
		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == 3) // we only expect delta=3 to be victim to these cases
			{
				HashSet<Integer> ones = findOnePathNeighbors(v);
				HashSet<Integer> threes = newFindFour(v);
				HashSet<Integer> fours = newFindFive(v);
				
				int oneCount = 0;
				int threeCount = 0;
				int fourCount = 0;
				
				for (Integer n : ones)
				{
					//System.out.println("ones - " + v + "," + n);
					if (degree[n] == 3)
					{
						//System.out.println(n);
						oneCount++;
					}
				}
				
				for (Integer n : threes)
				{
					//System.out.println("threes - " + v + "," + n);
					if (degree[n] == 3)
					{
						//System.out.println(n);
						threeCount++;
					}
				}
				
				for (Integer n : fours)
				{
					//System.out.println("fours - " + v + "," + n);
					if (degree[n] == 3)
					{
						//System.out.println(n);
						fourCount++;
					}
				}
				
				if (oneCount >= 1 && threeCount >= 1 && fourCount >= 1)
				{
					present = true;
					return present;
				}
			}
		}
		
		return present;
	}
	
	// case where there are two delta-path segments of 3 nodes (i.e. one minor in middle) that 
	// are connected by the minors
	public boolean testJoinedDeltaPaths()
	{
		boolean present = false;
		
		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == 4) // we only expect delta=4/5 to be victim to these cases
								// but delta=5 yields n>20 nodes in the tree
			{
				HashSet<Integer> twos = findTwoPathNeighbors(v);
				HashSet<Integer> threes = findThreePathNeighbors(v);
				
				boolean oneInTwo = false;
				for (Integer n : twos)
				{
					if (degree[n] == 4)
					{
						oneInTwo = true;
					}
				}
				
				// There is a delta that is two away, not check for the other side
				if (oneInTwo)
				{
					boolean foundOneInThrees = false;
					for (Integer n : threes)
					{
						if (degree[n] == 4)
						{
							foundOneInThrees = true;
							
							// Now check to see if there is one two away from this delta,
							// and that it is also in three. if so we have a winner
							HashSet<Integer> otherTwos = findTwoPathNeighbors(n);
							for (Integer vertex : otherTwos)
							{
								if (degree[vertex] == 4 && threes.contains(vertex))
								{
									present = true;
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return present;
	}
	
	// case where there are two delta-path segments of 3 nodes (i.e. one minor in middle) that 
	// are connected by the minors (with one more minor in between those two)
	public boolean testLongerJoinedDeltaPaths()
	{
		boolean present = false;
		
		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == 4) // we only expect delta=4/5 to be victim to these cases
								// but delta=5 yields n>20 nodes in the tree
			{
				HashSet<Integer> twos = findTwoPathNeighbors(v);
				HashSet<Integer> fours = findThreePathNeighbors(v);
				
				boolean oneInTwo = false;
				for (Integer n : twos)
				{
					if (degree[n] == 4)
					{
						oneInTwo = true;
					}
				}
				
				// There is a delta that is two away, not check for the other side
				if (oneInTwo)
				{
					boolean foundOneInFours = false;
					for (Integer n : fours)
					{
						if (degree[n] == 4)
						{
							foundOneInFours = true;
							
							// Now check to see if there is one two away from this delta,
							// and that it is also in three. if so we have a winner
							HashSet<Integer> otherTwos = findTwoPathNeighbors(n);
							for (Integer vertex : otherTwos)
							{
								if (degree[vertex] == 4 && fours.contains(vertex))
								{
									present = true;
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return present;
	}
	
	// this is the following tree:
	// d - d - m - m - m - d - m - m - m - d - d
	public boolean testJoinedDeltaSegmentsPendant()
	{
		boolean present = false;
		
		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == 3) // we only expect delta=4/5 to be victim to these cases
								// but delta=5 yields n>20 nodes in the tree
			{
				HashSet<Integer> fours = findFourPathNeighbors(v);
				HashSet<Integer> fives = findFivePathNeighbors(v);
				
				boolean twoFours = false;
				boolean twoFives = false;
				
				for (Integer four : fours)
				{
					if (degree[four] == 4)
					{
						// TODO: continue this, but need a distance method...
					}
				}
			}
		}
		
		return present;
	}
	
	public int distance(int v1, int v2)
	{
		int d = 0;
		HashSet<Integer> visited = new HashSet<Integer>();
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(v1);
		
		while (queue.size() > 0)
		{
			int current = queue.remove(0);
			visited.add(current);
			for (int i = 0; i < this.adjacencyMatrix[0].length; i++)
			{
				if (adjacencyMatrix[current][i] == 1)
				{
					if (!visited.contains(i))
					{
						queue.add(i);
					}
				}
			}
		}
		
		return d;
	}

	/*
	 * Helper method that finds all immediate neighboring vertices of a vertex.
	 */
	public HashSet<Integer> findOnePathNeighbors(int vertex) 
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();

		for (int col = 0; col < dimension; col++) 
		{
			if (adjacencyMatrix[vertex][col] == 1) 
			{
				neighbors.add(col);
			}
		}

		return neighbors;
	}

	/*
	 * Helper method that finds all vertices that are exactly a distance 2 away
	 * from the specified vertex.
	 */
	public HashSet<Integer> findTwoPathNeighbors(int vertex) 
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();
		HashSet<Integer> temp;

		for (int col = 0; col < dimension; col++) 
		{
			if (adjacencyMatrix[vertex][col] == 1) 
			{
				temp = findOnePathNeighbors(col);
				temp.remove(vertex); // remove us from the set of neighbors
				for (Integer neighbor : temp) 
				{
					neighbors.add(neighbor);
				}
			}
		}

		return neighbors;
	}
	
	/*
	 * Helper method that finds all vertices that are exactly a distance 3 away
	 * from the specified vertex.
	 */
	public HashSet<Integer> findThreePathNeighbors(int vertex) 
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();

		for (int col = 0; col < dimension; col++) 
		{
			if (adjacencyMatrix[vertex][col] == 1)
			{
				HashSet<Integer> temp = findOnePathNeighbors(col);
				temp.remove(vertex);
				
				for (Integer n : temp)
				{
					HashSet<Integer> temp2 = findOnePathNeighbors(n);
					temp2.remove(col);
					for (Integer opn : temp)
					{
						temp2.remove(opn);
					}
					for (Integer tpn : temp2)
					{
						neighbors.add(tpn);
					}
				}
			}
		}

		return neighbors;
	}

	public HashSet<Integer> newFindFour(int vertex)
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();

		HashSet<Integer> d1 = findOnePathNeighbors(vertex);
		d1.remove(vertex);
		for (Integer n1 : d1)
		{
			HashSet<Integer> d2 = findOnePathNeighbors(n1);
			d2.remove(n1);
			d2.remove(vertex);
			for (Integer n2 : d2)
			{
				HashSet<Integer> d3 = findOnePathNeighbors(n2);
				d3.remove(n2);
				d3.remove(vertex);
				d3.remove(n1);
				for (Integer n3 : d3)
				{
					HashSet<Integer> d4 = findOnePathNeighbors(n3);
					d4.remove(n2);
					d4.remove(vertex);
					d4.remove(n1);
					d4.remove(n3);

					for (Integer n4 : d4)
					{
						neighbors.add(n4);
					}
				}
			}
		}

		return neighbors;
	}
	
	public HashSet<Integer> findFourPathNeighbors(int vertex)
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();
		
		for (int col = 0; col < dimension; col++) 
		{
			if (adjacencyMatrix[vertex][col] == 1)
			{
				HashSet<Integer> temp = findOnePathNeighbors(col);
				temp.remove(vertex);
				
				for (Integer n : temp)
				{
					HashSet<Integer> temp2 = findOnePathNeighbors(n);
					temp2.remove(col);
					for (Integer opn : temp)
					{
						temp2.remove(opn);
					}
					
					for (Integer tpn : temp2)
					{
						HashSet<Integer> fourSet = findOnePathNeighbors(tpn);
						fourSet.remove(n); // remove where we came from
						fourSet.removeAll(temp2);
						for (Integer fpn : fourSet)
						{
							neighbors.add(fpn);
						}
					}
				}
			}
		}
		
		return neighbors;
	}

	public HashSet<Integer> newFindFive(int vertex)
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();

		HashSet<Integer> d1 = findOnePathNeighbors(vertex);
		d1.remove(vertex);
		for (Integer n1 : d1)
		{
			HashSet<Integer> d2 = findOnePathNeighbors(n1);
			d2.remove(n1);
			d2.remove(vertex);
			for (Integer n2 : d2)
			{
				HashSet<Integer> d3 = findOnePathNeighbors(n2);
				d3.remove(n2);
				d3.remove(vertex);
				d3.remove(n1);
				for (Integer n3 : d3)
				{
					HashSet<Integer> d4 = findOnePathNeighbors(n3);
					d4.remove(n2);
					d4.remove(vertex);
					d4.remove(n1);
					d4.remove(n3);

					for (Integer n4 : d4)
					{
						HashSet<Integer> d5 = findOnePathNeighbors(n4);
						d5.remove(n2);
						d5.remove(vertex);
						d5.remove(n1);
						d5.remove(n3);
						d5.remove(n4);
						for (Integer n5 : d5)
						{
							neighbors.add(n5);
						}
					}
				}
			}
		}

		return neighbors;
	}
	
	public HashSet<Integer> findFivePathNeighbors(int vertex)
	{
		HashSet<Integer> neighbors = new HashSet<Integer>();
		
		for (int col = 0; col < dimension; col++) 
		{
			if (adjacencyMatrix[vertex][col] == 1)
			{
				HashSet<Integer> temp = findOnePathNeighbors(col);
				temp.remove(vertex);
				
				for (Integer n : temp)
				{
					HashSet<Integer> temp2 = findOnePathNeighbors(n);
					temp2.remove(col);
					for (Integer opn : temp)
					{
						temp2.remove(opn);
					}
					
					for (Integer tpn : temp2)
					{
						HashSet<Integer> fourSet = findOnePathNeighbors(tpn);
						fourSet.remove(n); // remove where we came from
						fourSet.removeAll(temp2);
						for (Integer fpn : fourSet)
						{
							HashSet<Integer> fiveSet = findOnePathNeighbors(fpn);
							fiveSet.remove(tpn); // remove where we came from
							fiveSet.removeAll(fourSet);
							for (Integer fivePn : fiveSet)
							{
								neighbors.add(fivePn);
							}
						}
					}
				}
			}
		}
		
		return neighbors;
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
			System.err.println("usage: java TreeChecker file");
			return;
		}

		// test newFindFour
		int[][] testMatrix = 
		{
			{0, 1, 0, 0, 0, 0},
			{1, 0, 1, 0, 0, 0},
			{0, 1, 0, 1, 0, 0},
			{0, 0, 1, 0, 1, 1},
			{0, 0, 0, 1, 0, 0},
			{0, 0, 0, 1, 0, 0}
		};
		TreeChecker check = new TreeChecker(testMatrix, 6);
		HashSet<Integer> fours = check.newFindFour(0);
		System.out.println(fours);
        
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
		
			// Run the property checkers here
			TreeChecker checker = new TreeChecker(matrix, dimensions);
			System.out.println(checker.testDeltaMinusOneNeighbors());
			System.out.println(checker.testMajorP3());
			System.out.println(checker.testStar());
			System.out.println(checker.testThreeStar());



			//System.out.println(checker.testSplitStar());

			/*boolean result = checker.testForSubdividedP5();
			if (result)
			{
				result = checker.testMajorP3();
				if (result)
				{
					
				}
			}
			System.out.println(result);*/
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
