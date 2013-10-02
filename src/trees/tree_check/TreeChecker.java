import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;

public class TreeChecker 
{
	public int[][] distTo;
	public int[][] adjacencyMatrix;
	public int dimension;
	public int majorLabel;
	public int[] degree;
	
	public TreeChecker(int[][] matrix, int size)
	{
		this.adjacencyMatrix = matrix;
		this.dimension = size;
		this.distTo = new int[size][size];
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

		// initialize distances to infinity
        for (int v = 0; v < size; v++) {
            for (int w = 0; w < size; w++) {
                distTo[v][w] = size + 1;
            }
        }

        // initialize distances using edge-weighted digraph's
        for (int v = 0; v < size; v++) {
        	for (int w = 0; w < size; w++) {
        		if (matrix[v][w] == 1) {
        			distTo[v][w] = 1;
        		}
        	}
        }

        // Floyd-Warshall updates
        for (int i = 0; i < size; i++) {
            // compute shortest paths using only 0, 1, ..., i as intermediate vertices
            for (int v = 0; v < size; v++) {
                for (int w = 0; w < size; w++) {
                    if (distTo[v][w] > distTo[v][i] + distTo[i][w]) {
                        distTo[v][w] = distTo[v][i] + distTo[i][w];
                    }
                }
            }
        }
	}
	
// TEST CASES

	// three majors connected in a row
	public boolean test_for_tree_1()  
	{
		boolean present = false;
		
		// Search all major vertices
		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {
				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {
						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
								if (distTo[v1][v2] == 1 && distTo[v1][v3] == 2 && distTo[v2][v3] == 1)
								{
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

	// minor in the middle connected to three or more majors
	public boolean test_for_tree_2()
	{
		boolean present = false;
		
		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {
				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {
						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
								for (int center = 0; center < degree.length; center++) {
									if (degree[center] < majorLabel && center != v3 && center != v2 && center != v1) {
										if (distTo[center][v1] == 1 && distTo[center][v2] == 1 && distTo[center][v3] == 1 && 
											distTo[v1][v2] == 2 && distTo[v1][v3] == 2 && distTo[v2][v3] == 2) {
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return present;
	}

	public boolean test_for_tree_3()
	{
		boolean present = false;

		for (int v = 0; v < degree.length; v++)
		{
			if (degree[v] == majorLabel) // the center major vertex
			{
				HashSet<Integer> twoNeighbors = findNeighbors(v, 2); // find all vertices in N2(v)
				int maxVertices = 0;
				for (Integer tpn : twoNeighbors) 
				{
					if (tpn != v && degree[tpn] == majorLabel) // count major vertices in N2(v)
					{
						maxVertices++;
					}
				}
				if (maxVertices >= (majorLabel - 1))
				{
					return true;
				}
			}
		}

		return present;
	}

	public boolean test_for_tree_4()
	{
		boolean present = false;

		// top (root) vertex of degree \Delta-2
		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel - 2) {

				// two minor vertices adjacent to the root
				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] != majorLabel && v1 != v2) {
						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] != majorLabel && v3 != v2 && v3 != v1) {

								// below are the major vertices (we need four of them for n <= 20)
								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {

										for (int v5 = 0; v5 < degree.length; v5++) {
											if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {

												for (int v6 = 0; v6 < degree.length; v6++) {
													if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {

														for (int v7 = 0; v7 < degree.length; v7++) {
															if (degree[v7] == majorLabel && v7 != v6 && v7 != v5 && v7 != v4 && v7 != v3 && v7 != v2 && v7 != v1) {
																if (
																	// root
																	distTo[v1][v2] == 1 && distTo[v1][v3] == 1 && distTo[v1][v4] == 2 && distTo[v1][v5] == 2 && distTo[v1][v6] == 2 && distTo[v1][v7] == 2 &&

																	// inner minor vertices
																	distTo[v2][v1] == 1 && distTo[v2][v3] == 2 && distTo[v2][v4] == 1 && distTo[v2][v5] == 1 && distTo[v2][v6] == 3 && distTo[v2][v7] == 3 &&
																	distTo[v3][v1] == 1 && distTo[v3][v2] == 2 && distTo[v3][v4] == 3 && distTo[v3][v5] == 3 && distTo[v3][v6] == 1 && distTo[v3][v7] == 1 &&

																	// major vertices
																	distTo[v4][v1] == 2 && distTo[v4][v2] == 1 && distTo[v4][v3] == 3 && distTo[v4][v5] == 2 && distTo[v4][v6] == 4 && distTo[v4][v7] == 4 &&
																	distTo[v5][v1] == 2 && distTo[v5][v2] == 1 && distTo[v5][v3] == 3 && distTo[v5][v4] == 2 && distTo[v5][v6] == 4 && distTo[v5][v7] == 4 &&
																	distTo[v6][v1] == 2 && distTo[v6][v2] == 3 && distTo[v6][v3] == 1 && distTo[v6][v4] == 4 && distTo[v6][v5] == 4 && distTo[v6][v7] == 2 &&
																	distTo[v7][v1] == 2 && distTo[v7][v2] == 3 && distTo[v7][v3] == 1 && distTo[v7][v4] == 4 && distTo[v7][v5] == 4 && distTo[v7][v6] == 2
																	)
																{
																	return true;
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
						}
					}
				}
			}
		}

		return present;
	}

	public boolean test_for_tree_5() {
		boolean present = false;

		if (majorLabel != 3) return false; // we don't expect higher cases

		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {

				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {

						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {

								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
										if (
											distTo[v1][v2] == 1 && distTo[v1][v3] == 5 && distTo[v1][v4] == 6 && 

											distTo[v2][v1] == 1 && distTo[v2][v3] == 4 && distTo[v2][v4] == 5 && 

											distTo[v3][v1] == 5 && distTo[v3][v2] == 4 && distTo[v3][v4] == 1 && 

											distTo[v4][v1] == 6 && distTo[v4][v2] == 5 && distTo[v4][v3] == 1 
											)
											return true; // PHEW>>>>>>>>
									}
								}
							}
						}
					}
				}
			}
		}

		return present;
	}

	public boolean test_for_tree_6()
	{
		boolean present = false;
		if (majorLabel != 3) { // we expect delta=3 to be the case...
			return false;
		}

		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {

				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {

						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
								
								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
										// now check the distances... that was absurd... but it guarantees to check all possible majors...
										if (
											distTo[v1][v2] == 4 && distTo[v1][v3] == 4 && distTo[v1][v4] == 4 && 

											distTo[v2][v1] == 4 && distTo[v2][v3] == 8 && distTo[v2][v4] == 8 && 

											distTo[v3][v1] == 4 && distTo[v3][v2] == 8 && distTo[v3][v4] == 8 && 

											distTo[v4][v1] == 4 && distTo[v4][v2] == 8 && distTo[v4][v3] == 8 
											)
											return true; // PHEW>>>>>>>>
									}
								}
							}
						}
					}
				}
			}
		}

		return present;
	}

	public boolean test_for_tree_7()
	{
		boolean present = false;
		if (majorLabel != 3) { // we expect delta=3 to be the case...
			return false;
		}

		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {

				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {

						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {

								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
										// now check the distances... that was absurd... but it guarantees to check all possible majors...
										if (
											distTo[v1][v2] == 1 && distTo[v1][v3] == 4 && distTo[v1][v4] == 4 && 

											distTo[v2][v1] == 1 && distTo[v2][v3] == 5 && distTo[v2][v4] == 5 && 

											distTo[v3][v1] == 4 && distTo[v3][v2] == 5 && distTo[v3][v4] == 8 && 

											distTo[v4][v1] == 4 && distTo[v4][v2] == 5 && distTo[v4][v3] == 8 
											)
											return true; // PHEW>>>>>>>>
									}
								}
							}
						}
					}
				}
			}
		}

		return present;
	}

	public boolean test_for_tree_8() 
	{
		boolean present = false;

		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {

				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {

						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {

								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {

										for (int v5 = 0; v5 < degree.length; v5++) {
											if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {

												if (
													distTo[v1][v2] == 1 && distTo[v1][v3] == 5 && distTo[v1][v4] == 9 && distTo[v1][v5] == 10 && 

													distTo[v2][v1] == 1 && distTo[v2][v3] == 4 && distTo[v2][v4] == 8 && distTo[v2][v5] == 9 && 

													distTo[v3][v1] == 5 && distTo[v3][v2] == 4 && distTo[v3][v4] == 4 && distTo[v3][v5] == 5 && 

													distTo[v4][v1] == 9 && distTo[v4][v2] == 8 && distTo[v4][v3] == 4 && distTo[v4][v5] == 1 && 

													distTo[v5][v1] == 10 && distTo[v5][v2] == 9 && distTo[v5][v3] == 5 && distTo[v5][v4] == 1 
													)
													return true; // PHEW>>>>>>>>
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

		return present;
	}

	public boolean test_for_tree_9() {
		boolean present = false;

		if (!(majorLabel == 3 || majorLabel == 4)) return false; // we don't expect higher cases

		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {

				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {

						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {

								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {

										for (int v5 = 0; v5 < degree.length; v5++) {
											if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
												if (
													distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 7 && distTo[v1][v5] == 9 &&

													distTo[v2][v1] == 2 && distTo[v2][v3] == 1 && distTo[v2][v4] == 5 && distTo[v2][v5] == 7 &&

													distTo[v3][v1] == 3 && distTo[v3][v2] == 1 && distTo[v3][v4] == 6 && distTo[v3][v5] == 8 &&

													distTo[v4][v1] == 7 && distTo[v4][v2] == 5 && distTo[v4][v3] == 6 && distTo[v4][v5] == 2 && 

													distTo[v5][v1] == 9 && distTo[v5][v2] == 7 && distTo[v5][v3] == 8 && distTo[v5][v4] == 2																			
													)
													return true; 
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

		return present;
	}

	public boolean test_for_tree_10() {
		boolean present = false;

		if (majorLabel != 4) return false; // we don't expect higher cases

		for (int v1 = 0; v1 < degree.length; v1++) {
			if (degree[v1] == majorLabel) {

				for (int v2 = 0; v2 < degree.length; v2++) {
					if (degree[v2] == majorLabel && v1 != v2) {

						for (int v3 = 0; v3 < degree.length; v3++) {
							if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {

								for (int v4 = 0; v4 < degree.length; v4++) {
									if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {

										if (
											distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 3 && 

											distTo[v2][v1] == 2 && distTo[v2][v3] == 3 && distTo[v2][v4] == 3 && 

											distTo[v3][v1] == 3 && distTo[v3][v2] == 3 && distTo[v3][v4] == 2 && 

											distTo[v4][v1] == 3 && distTo[v4][v2] == 3 && distTo[v4][v3] == 2 
											)
											return true; 
									}
								}
							}
						}
					}
				}
			}
		}

		return present;
	}
	
	// Determine distance between two vertices...
	public int distance(int v1, int v2)
	{
		return distTo[v1][v2];
	}

	// Find all neighbors at a given distance...
	public HashSet<Integer> findNeighbors(int v, int distance) {
		HashSet<Integer> neighbors = new HashSet<Integer>();
		for (int i = 0; i < dimension; i++) {
			if (distTo[v][i] == distance && v != i) {
				neighbors.add(i);
			}
		}
		return neighbors;
	}

	// Try every test case in the paper
	public boolean checkForGraphs() 
	{
		boolean present = false;

		present = test_for_tree_1();
		if (present) return true;

		present = test_for_tree_2();
		if (present) return true;

		present = test_for_tree_3();
		if (present) return true;

		present = test_for_tree_4();
		if (present) return true;	

		present = test_for_tree_5();
		if (present) return true;	

		present = test_for_tree_6();
		if (present) return true;	

		present = test_for_tree_7();
		if (present) return true;

		present = test_for_tree_8();
		if (present) return true;

		present = test_for_tree_9();
		if (present) return true;

		present = test_for_tree_10();
		if (present) return true;

		return false; // didn't find it in one of the specified forbidden subtrees
	}

	/**
	 * Main entry point of program
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		// Check the command line arguments 		
		if (args.length != 2)
		{
			System.err.println("usage: java TreeChecker mode file");
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
		HashSet<Integer> fours = check.findNeighbors(0, 4);

		int mode = Integer.parseInt(args[0]);
		if (mode == 0)
		{
			// Try to create a buffer reader to parse the adjacency matrix
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
		
				// Run the property checkers here
				TreeChecker checker = new TreeChecker(matrix, dimensions);
				boolean present = checker.checkForGraphs();
				System.out.println(present);
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
		else if (mode == 1)
		{
			int[][] matrix = G6Parser.parseG6(args[1]);
			TreeChecker checker = new TreeChecker(matrix, matrix.length);
			boolean present = checker.checkForGraphs();
			System.out.println(present);
		}
		else if (mode == 2)
		{
			System.err.println("Reading: " + args[1]);
			String g6 = "";
			Scanner scan = new Scanner(new File(args[1]));
			while (scan.hasNextLine()) {
				g6 = g6 + scan.nextLine();
			}
			System.err.println("Parsing: " + g6);
			int[][] matrix = G6Parser.parseG6(g6);
			TreeChecker checker = new TreeChecker(matrix, matrix.length);
			boolean present = checker.checkForGraphs();
			System.out.println(present);
		}
	}
}
