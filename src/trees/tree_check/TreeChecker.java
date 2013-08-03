import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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

        // System.out.println("Size = " + size);
        // for (int u = 0; u < size; u++) {
        // 	System.out.printf("Distance from %d to all%n", u);
        // 	for (int v = 0; v < size; v++) {
        // 		System.out.println(v + " - " + distTo[u][v]);
        // 	}
        // 	System.out.println("----");
        // }
	}
	
// TEST CASES!!!

	// three majors connected in a row
	public boolean test_for_tree_1()  
	{
		boolean present = false;

		System.out.println("Checking for p3");
		
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

// same forbidden subtree as in build3 algorithm, and also the algorithm 
	// public boolean testThreeStarD3()
	// {
	// 	boolean present = false;
	// 	if (majorLabel != 3) { // we expect delta=3 to be the case...
	// 		return false;
	// 	}
		
	// 	for (int v = 0; v < degree.length; v++)
	// 	{
	// 		if (degree[v] == majorLabel) // we only expect delta=3 to be victim to these cases, but do majorlabel anyway
	// 		{
	// 			int majorAtThreeCount = 0;
	// 			for (int j = 0; j < degree.length; j++) 
	// 			{
	// 				if (v != j)
	// 				{
	// 					if (distTo[v][j] == 4 && degree[j] == majorLabel) 
	// 					{
	// 						majorAtThreeCount++;
	// 					}
	// 				}
	// 			}
	// 			if (majorAtThreeCount >= 3) 
	// 			{
	// 				return true; 
	// 			} 
	// 		}
	// 	}	
	// 	return present;
	// }
// same as above but the center has a delta neighbor and then is the endpoint for two delta-segments of length 4
	// public boolean testThreeStarCloseD3()
	// {
	// 	boolean present = false;
	// 	if (majorLabel != 3) { // we expect delta=3 to be the case...
	// 		return false;
	// 	}

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 									if (
	// 										distTo[v1][v2] == 1 && distTo[v1][v3] == 4 && distTo[v1][v4] == 4 && 

	// 										distTo[v2][v1] == 1 && distTo[v2][v3] == 5 && distTo[v2][v4] == 5 && 

	// 										distTo[v3][v1] == 4 && distTo[v3][v2] == 5 && distTo[v3][v4] == 8 && 

	// 										distTo[v4][v1] == 4 && distTo[v4][v2] == 5 && distTo[v4][v3] == 8 
	// 										)
	// 										return true; // PHEW>>>>>>>>
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testConnectedJointStart() {
	// 	boolean present = false;

	// 	if (!(majorLabel == 3 || majorLabel == 4)) return false; // we don't expect higher cases

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 									if (
	// 										distTo[v1][v2] == 2 && distTo[v1][v3] == 4 && distTo[v1][v4] == 4 && 

	// 										distTo[v2][v1] == 2 && distTo[v2][v3] == 4 && distTo[v2][v4] == 4 && 

	// 										distTo[v3][v1] == 4 && distTo[v3][v2] == 4 && distTo[v3][v4] == 2 && 

	// 										distTo[v4][v1] == 4 && distTo[v4][v2] == 4 && distTo[v4][v3] == 2 
	// 										)
	// 										return true; // PHEW>>>>>>>>
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testTightConnectedJointStart() {
	// 	boolean present = false;

	// 	if (!(majorLabel == 3 || majorLabel == 4)) return false; // we don't expect higher cases

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 									if (
	// 										distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 3 && 

	// 										distTo[v2][v1] == 2 && distTo[v2][v3] == 3 && distTo[v2][v4] == 3 && 

	// 										distTo[v3][v1] == 3 && distTo[v3][v2] == 3 && distTo[v3][v4] == 2 && 

	// 										distTo[v4][v1] == 3 && distTo[v4][v2] == 3 && distTo[v4][v3] == 2 
	// 										)
	// 										return true; // PHEW>>>>>>>>
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}
	// }

	// public boolean testForTree_1() 
	// {
	// 	boolean present = false;

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											for (int v6 = 0; v6 < degree.length; v6++) {
	// 												if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {
	// 													// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 													if (
	// 														distTo[v1][v2] == 1 && distTo[v1][v3] == 5 && distTo[v1][v4] == 7 && distTo[v1][v5] == 6 && distTo[v1][v6] == 3 &&

	// 														distTo[v2][v1] == 1 && distTo[v2][v3] == 4 && distTo[v2][v4] == 6 && distTo[v2][v5] == 5 && distTo[v2][v6] == 2 &&

	// 														distTo[v3][v1] == 5 && distTo[v3][v2] == 4 && distTo[v3][v4] == 2 && distTo[v3][v5] == 1 && distTo[v3][v6] == 6 &&

	// 														distTo[v4][v1] == 7 && distTo[v4][v2] == 6 && distTo[v4][v3] == 2 && distTo[v4][v5] == 3 && distTo[v4][v6] == 8 &&

	// 														distTo[v5][v1] == 6 && distTo[v5][v2] == 5 && distTo[v5][v3] == 1 && distTo[v5][v4] == 3 && distTo[v5][v6] == 7 &&

	// 														distTo[v6][v1] == 3 && distTo[v6][v2] == 2 && distTo[v6][v3] == 6 && distTo[v6][v4] == 8 && distTo[v6][v5] == 7 
	// 														)
	// 														return true; // PHEW>>>>>>>>
	// 												}
	// 											}
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_2() 
	// {
	// 	boolean present = false;

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											for (int v6 = 0; v6 < degree.length; v6++) {
	// 												if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {
	// 													// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 													if (
	// 														distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 7 && distTo[v1][v5] == 8 && distTo[v1][v6] == 10 &&

	// 														distTo[v2][v1] == 2 && distTo[v2][v3] == 1 && distTo[v2][v4] == 5 && distTo[v2][v5] == 6 && distTo[v2][v6] == 8 &&

	// 														distTo[v3][v1] == 3 && distTo[v3][v2] == 1 && distTo[v3][v4] == 4 && distTo[v3][v5] == 5 && distTo[v3][v6] == 7 &&

	// 														distTo[v4][v1] == 7 && distTo[v4][v2] == 5 && distTo[v4][v3] == 4 && distTo[v4][v5] == 1 && distTo[v4][v6] == 3 &&

	// 														distTo[v5][v1] == 8 && distTo[v5][v2] == 6 && distTo[v5][v3] == 5 && distTo[v5][v4] == 1 && distTo[v5][v6] == 2 &&

	// 														distTo[v6][v1] == 10 && distTo[v6][v2] == 8 && distTo[v6][v3] == 7 && distTo[v6][v4] == 3 && distTo[v6][v5] == 2 
	// 														)
	// 														return true; // PHEW>>>>>>>>
	// 												}
	// 											}
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_3() 
	// {
	// 	boolean present = false;

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											//for (int v6 = 0; v6 < degree.length; v6++) {
	// 											//	if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {
	// 													// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 													if (
	// 														distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 6 && distTo[v1][v5] == 7 && 

	// 														distTo[v2][v1] == 2 && distTo[v2][v3] == 1 && distTo[v2][v4] == 4 && distTo[v2][v5] == 5 && 

	// 														distTo[v3][v1] == 3 && distTo[v3][v2] == 1 && distTo[v3][v4] == 5 && distTo[v3][v5] == 6 && 

	// 														distTo[v4][v1] == 6 && distTo[v4][v2] == 4 && distTo[v4][v3] == 5 && distTo[v4][v5] == 1 && 

	// 														distTo[v5][v1] == 7 && distTo[v5][v2] == 5 && distTo[v5][v3] == 6 && distTo[v5][v4] == 1 
	// 														)
	// 														return true; // PHEW>>>>>>>>
	// 												//}
	// 											//}
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_4() 
	// {
	// 	boolean present = false;

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											for (int v6 = 0; v6 < degree.length; v6++) {
	// 												if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {
	// 													// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 													if (
	// 														distTo[v1][v2] == 3 && distTo[v1][v3] == 6 && distTo[v1][v4] == 5 && distTo[v1][v5] == 9 && distTo[v1][v6] == 10 &&

	// 														distTo[v2][v1] == 3 && distTo[v2][v3] == 3 && distTo[v2][v4] == 2 && distTo[v2][v5] == 6 && distTo[v2][v6] == 7 &&

	// 														distTo[v3][v1] == 6 && distTo[v3][v2] == 3 && distTo[v3][v4] == 1 && distTo[v3][v5] == 5 && distTo[v3][v6] == 6 &&

	// 														distTo[v4][v1] == 5 && distTo[v4][v2] == 2 && distTo[v4][v3] == 1 && distTo[v4][v5] == 4 && distTo[v4][v6] == 5 &&

	// 														distTo[v5][v1] == 9 && distTo[v5][v2] == 6 && distTo[v5][v3] == 5 && distTo[v5][v4] == 4 && distTo[v5][v6] == 1 &&

	// 														distTo[v6][v1] == 10 && distTo[v6][v2] == 7 && distTo[v6][v3] == 6 && distTo[v6][v4] == 5 && distTo[v6][v5] == 1 
	// 														)
	// 														return true; // PHEW>>>>>>>>
	// 												}
	// 											}
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_5() 
	// {
	// 	boolean present = false;

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											for (int v6 = 0; v6 < degree.length; v6++) {
	// 												if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {
	// 													// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 													if (
	// 														distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 5 && distTo[v1][v5] == 7 && distTo[v1][v6] == 8 &&

	// 														distTo[v2][v1] == 2 && distTo[v2][v3] == 1 && distTo[v2][v4] == 3 && distTo[v2][v5] == 5 && distTo[v2][v6] == 6 &&

	// 														distTo[v3][v1] == 3 && distTo[v3][v2] == 1 && distTo[v3][v4] == 2 && distTo[v3][v5] == 4 && distTo[v3][v6] == 5 &&

	// 														distTo[v4][v1] == 5 && distTo[v4][v2] == 3 && distTo[v4][v3] == 2 && distTo[v4][v5] == 6 && distTo[v4][v6] == 7 &&

	// 														distTo[v5][v1] == 7 && distTo[v5][v2] == 5 && distTo[v5][v3] == 4 && distTo[v5][v4] == 6 && distTo[v5][v6] == 1 &&

	// 														distTo[v6][v1] == 8 && distTo[v6][v2] == 6 && distTo[v6][v3] == 5 && distTo[v6][v4] == 7 && distTo[v6][v5] == 1 
	// 														)
	// 														return true; // PHEW>>>>>>>>
	// 												}
	// 											}
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_6() {
	// 	boolean present = false;

	// 	if (!(majorLabel == 3 || majorLabel == 4)) return false; // we don't expect higher cases

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 											if (
	// 												distTo[v1][v2] == 2 && distTo[v1][v3] == 3 && distTo[v1][v4] == 7 && distTo[v1][v5] == 9 &&

	// 												distTo[v2][v1] == 2 && distTo[v2][v3] == 1 && distTo[v2][v4] == 5 && distTo[v2][v5] == 7 &&

	// 												distTo[v3][v1] == 3 && distTo[v3][v2] == 1 && distTo[v3][v4] == 6 && distTo[v3][v5] == 8 &&

	// 												distTo[v4][v1] == 7 && distTo[v4][v2] == 5 && distTo[v4][v3] == 6 && distTo[v4][v5] == 2 && 

	// 												distTo[v5][v1] == 9 && distTo[v5][v2] == 7 && distTo[v5][v3] == 8 && distTo[v5][v4] == 2																			
	// 												)
	// 												return true; // PHEW>>>>>>>>
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_7() {
	// 	boolean present = false;

	// 	if (!(majorLabel == 3)) return false; // we don't expect higher cases

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 									if (
	// 										distTo[v1][v2] == 1 && distTo[v1][v3] == 5 && distTo[v1][v4] == 6 && 

	// 										distTo[v2][v1] == 1 && distTo[v2][v3] == 4 && distTo[v2][v4] == 5 && 

	// 										distTo[v3][v1] == 5 && distTo[v3][v2] == 4 && distTo[v3][v4] == 1 && 

	// 										distTo[v4][v1] == 6 && distTo[v4][v2] == 5 && distTo[v4][v3] == 1 
	// 										)
	// 										return true; // PHEW>>>>>>>>
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

	// public boolean testForTree_8() 
	// {
	// 	boolean present = false;

	// 	for (int v1 = 0; v1 < degree.length; v1++) {
	// 		if (degree[v1] == majorLabel) {
	// 			for (int v2 = 0; v2 < degree.length; v2++) {
	// 				if (degree[v2] == majorLabel && v1 != v2) {
	// 					for (int v3 = 0; v3 < degree.length; v3++) {
	// 						if (degree[v3] == majorLabel && v3 != v2 && v3 != v1) {
	// 							for (int v4 = 0; v4 < degree.length; v4++) {
	// 								if (degree[v4] == majorLabel && v4 != v3 && v4 != v2 && v4 != v1) {
	// 									for (int v5 = 0; v5 < degree.length; v5++) {
	// 										if (degree[v5] == majorLabel && v5 != v4 && v5 != v3 && v5 != v2 && v5 != v1) {
	// 											//for (int v6 = 0; v6 < degree.length; v6++) {
	// 											//	if (degree[v6] == majorLabel && v6 != v5 && v6 != v4 && v6 != v3 && v6 != v2 && v6 != v1) {
	// 													// now check the distances... that was absurd... but it guarantees to check all possible majors...
	// 													if (
	// 														distTo[v1][v2] == 1 && distTo[v1][v3] == 5 && distTo[v1][v4] == 9 && distTo[v1][v5] == 10 && 

	// 														distTo[v2][v1] == 1 && distTo[v2][v3] == 4 && distTo[v2][v4] == 8 && distTo[v2][v5] == 9 && 

	// 														distTo[v3][v1] == 5 && distTo[v3][v2] == 4 && distTo[v3][v4] == 4 && distTo[v3][v5] == 5 && 

	// 														distTo[v4][v1] == 9 && distTo[v4][v2] == 8 && distTo[v4][v3] == 4 && distTo[v4][v5] == 1 && 

	// 														distTo[v5][v1] == 10 && distTo[v5][v2] == 9 && distTo[v5][v3] == 5 && distTo[v5][v4] == 1 
	// 														)
	// 														return true; // PHEW>>>>>>>>
	// 												//}
	// 											//}
	// 										}
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}

	// 	return present;
	// }

// END TEST CASES
	
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

		

		// present = testThreeStarD3();
		// if (present) return true;

		// present = testThreeStarCloseD3();
		// if (present) return true;

		// present = testConnectedJointStart();
		// if (present) return true;

		// present = testTightConnectedJointStart();
		// if (present) return true;

// 		present = testForTree_1(); // WORKS, tested with testForTree_1.amf
// 		if (present) return true;

// 		present = testForTree_2(); // WORKS, tested with testForTree_2.amf
// 		if (present) return true;

// 		present = testForTree_3(); // WORKS, tested with testForTree_3.amf
// 		if (present) return true;

// 		present = testForTree_4(); // WORKS, tested with testForTree_3.amf
// 		if (present) return true;

// // NO TEST CASES BELOW THIS LINE --- NEED TO WRITE SOME

// 		present = testForTree_5(); // UNTESTED
// 		if (present) return true;

// 		present = testForTree_6(); // UNTESTED
// 		if (present) return true;

// 		present = testForTree_7(); // UNTESTED	
// 		if (present) return true;

// 		present = testForTree_8(); // UNTESTED
// 		if (present) return true;

		// TODO: TESTS FOR THE REMAINING CASES

		return false; // didn't find it earlier...
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
		HashSet<Integer> fours = check.findNeighbors(0, 4);
		// System.out.println(fours);
  //       System.out.println("ASDASD");


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

			// TODO: test cases for each of these graphs
		
			// Run the property checkers here
			//System.out.println("Dimension = " + dimensions);
			TreeChecker checker = new TreeChecker(matrix, dimensions);
			boolean present = checker.checkForGraphs();
			System.out.println(present);

			// present = checker.testDeltaMinusOneNeighbors();
			// present = checker.testDeltaMinusOneNeighbors();
			// present = checker.test_for_tree_1();
			// present = checker.test_for_tree_2();
			// present = checker.testThreeStarD3();
			// present = checker.testThreeStarCloseD3();
			// present = checker.testConnectedJointStart();
			// present = checker.testTightConnectedJointStart();


			//System.out.println(checker.testSplitStar());

			/*boolean result = checker.testForSubdividedP5();
			if (result)
			{
				result = checker.test_for_tree_1();
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
