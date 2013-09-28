package cklabel;
import java.util.*;
import java.io.*;

public class G6Parser
{
    
    public G6Parser()
    {
    }
    
    // Returns a list of binary strings (1/0) - really the easiest way to avoid all the bitwise overhead
    // and problems with integer overflow
    public LinkedList<LinkedList<Integer>> parseGraph(String line)
    {
        LinkedList<LinkedList<Integer>> list = new LinkedList<LinkedList<Integer>>(); // will be of size N-1
        LinkedList<Integer> columnNumbers = new LinkedList<Integer>();
        LinkedList<Integer> columnBits = new LinkedList<Integer>();
        
        // Retrieve the order of the graph
        char numColumns = line.charAt(0);
        int numVertices = (int)(numColumns) - 63; // 63 according to G6 spec in nauty documentation
        
        // Retrieve unmodified versions of the columns
        for (int i = 1; i < line.length(); i++)
        {
            int num = (int)(line.charAt(i)) - 63;
            columnNumbers.add(num);
            columnBits.addAll(convertToWord(num));
        }
        
        // Next, parse the remaining values based on this number
        int element = 0;
        for (int column = 1; column < numVertices; column++)
        {
            LinkedList<Integer> finalBits = new LinkedList<Integer>();
            for (int i = 0; i < column; i++)
            {
                finalBits.add(columnBits.get(element++));
            }
            list.add(finalBits);
        }
        
        return list;
    }
    
    // Strip out the bits of a column vector
    public LinkedList<Integer> convertToWord(int num)
    {
        LinkedList<Integer> bits = new LinkedList<Integer>();
        
        bits.add((num & (1 << 5)) >> 5);
        bits.add((num & (1 << 4)) >> 4);
        bits.add((num & (1 << 3)) >> 3);
        bits.add((num & (1 << 2)) >> 2);
        bits.add((num & (1 << 1)) >> 1);
        bits.add((num & (1 << 0)) >> 0);
        
        return bits;
    }

    public static int[][] parseG6(String g6)
    {
    	G6Parser parser = new G6Parser();
    	LinkedList<LinkedList<Integer>> list = parser.parseGraph(g6);
    	// Construct the adjacency matrix
		int dimension = list.size() + 1;
		int[][] matrix = new int[dimension][dimension];

		// Fill in the principle diagonal with 0s
		for (int i = 0; i < dimension; i++)
		{
		    matrix[i][i] = 0;
		}

		// Now fill out the upper portion of the matrix
		for (int column = 1; column < dimension; column++)
		{
		    for (int row = 0; row < list.get(column - 1).size(); row++)
		    {
		        matrix[row][column] = list.get(column - 1).get(row);
		    }
		}

		// Mirror the matrix
		for (int row = 0; row < dimension; row++)
		{
		    for (int col = row + 1; col < dimension; col++)
		    {
		        matrix[col][row] = matrix[row][col];
		    }
		}

		// Return the resulting adjacency matrix
		return matrix;
    }
    
	public static void main(String[] args)
	{
		// algorithm:
		// 1. convert each character in the string to an integer
		// 2. take first char in string and subtract 63 to get n (dimension of adjacency matrix)
		// 3. break the remaining characters into 6-bit wide objects
		// 4. subtract 63 from each string
		// 5. assign the bits in the columns to the adjacency matrix
		
		if (args.length == 1)
		{
      		// Try to create a buffer reader to parse the adjacency matrix
      		try
      		{
      			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                
		        // Create our parser
		        G6Parser parser = new G6Parser();
		        
		        // File-name sequence number
		        int fileNumber = 0;
		        
		        // Parse each line in the nauty output file and create an adjacency matrix for each
		        String line = "";
		        while ((line = reader.readLine()) != null)
		        {
		            System.out.println("Parsing: " + line);
		            LinkedList<LinkedList<Integer>> list = parser.parseGraph(line);
		            
		            // debug
		            /*
		            int c = 1;
		            System.out.println("Column vectors:");
		            for (LinkedList<Integer> vector : list)
		            {
		                System.out.print("Column " + c++ + ": ");
		                for (Integer bit : vector)
		                {
		                    System.out.print(bit + " ");
		                }
		                System.out.println();
		            }
		             */
		            
		            // Construct the adjacency matrix
		            int dimension = list.size() + 1;
		            int[][] matrix = new int[dimension][dimension];
		            
		            // Fill in the principle diagonal with 0s
		            for (int i = 0; i < dimension; i++)
		            {
		                matrix[i][i] = 0;
		            }
		            
		            // Now fill out the upper portion of the matrix
		            for (int column = 1; column < dimension; column++)
		            {
		                for (int row = 0; row < list.get(column - 1).size(); row++)
		                {
		                    matrix[row][column] = list.get(column - 1).get(row);
		                }
		            }
		            
		            // Mirror the matrix
		            for (int row = 0; row < dimension; row++)
		            {
		                for (int col = row + 1; col < dimension; col++)
		                {
		                    matrix[col][row] = matrix[row][col];
		                }
		            }
		            
		            // debug
		            /*System.out.println("A(G) final");
		            for (int row = 0; row < dimension; row++)
		            {
		                for (int col = 0; col < dimension; col++)
		                {
		                    System.out.print(matrix[row][col] + " ");
		                }
		                System.out.println();
		            }*/
		            
		            // Write the matrix to a file
		            // File-name conventions: N<5>_n.amf, where n is the sequence number
		            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("N" + dimension + "_" + args[0] + "_" + fileNumber++ + ".amf")));
		            writer.println(dimension);
		            for (int r = 0; r < dimension; r++)
		            {
		                for (int c = 0; c < dimension; c++)
		                {
		                    writer.print(matrix[r][c] + " ");
		                }
		                writer.println();
		            }
		            writer.flush();
				writer.close();

				System.out.println("File " + fileNumber + " processed.");
		        }
      		}
      		catch (IndexOutOfBoundsException ex1)
      		{
      			System.err.println("Error: IOB.");
			ex1.printStackTrace();
      		}
      		catch (NumberFormatException ex2)
      		{
      			System.err.println("Error: Number format.");
			ex2.printStackTrace();
      		}
      		catch (IOException ex3)
      		{
      			System.err.println("Error: I/O exception when parsing file.");
			ex3.printStackTrace();
      		}
		}
	}
}
