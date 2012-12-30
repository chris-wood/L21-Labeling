package cklabel;
public class Edge
{
	public int vertexA;
	public int vertexB;
	
	public Edge(int a, int b)
	{
		vertexA = a;
		vertexB = b;
	}
	
	public boolean equals(Edge e)
	{
		if (vertexA == e.vertexA)
			if (vertexB == e.vertexB)
				return true;
			else 
				return false;
		else if (vertexA == e.vertexB)
			if (vertexB == e.vertexA)
				return true;
			else
				return false;
		else
			return false;
	}
}