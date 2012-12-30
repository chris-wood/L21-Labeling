package cklabel;
public class DirectedEdge 
{
	public int head;
	public int tail;

	public DirectedEdge(int t, int h) 
	{
		head = h;
		tail = t;
	}

	public boolean equals(DirectedEdge e) 
	{
		if (head == e.head)
			if (tail == e.tail)
				return true;
		return false;
	}
}