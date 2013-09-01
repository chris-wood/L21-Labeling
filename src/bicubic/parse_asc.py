import sys
import random

def build_graph_from_asc(lines, n, k):
	from sage.all import *
	import sage.graphs.graph_plot
	''' Build the adjacency matrix and SAGE graph from the adjacency list
	format of the graph contained in the lines list.
	'''
	print(lines)
	rows = []
	for i in range(n):
		row = []
		connectedTo = lines[i].split(" : ")[1]
		for j in range(n):
			if str(j + 1) in connectedTo:
				row.append(1)
			else:
				row.append(0)
		rows.append(row)
	M = Matrix(rows)
	G = Graph(M)
	return G

def parse_params(fname):
	''' Retrieve n and k from the file name.
	'''
	params = fname.split("_")
	n = int(params[0])
	k = int(params[1])
	return n, k

def build_graph_from_asc_file(fname):
	''' Parse the parameters for the set of graphs in this file from the given
	values of n and k.
	'''
	# Parse the params
	n, k = parse_params(fname)

	f = open(fname, 'r')
	lines = []
	first = True
	block = True
	graphs = []
	for line in f:
		line = line.strip()
		print(line)
		if "Graph" in line and first == False:
			block = True
			graphs.append(build_graph_from_asc(lines, n, k))
			lines = []
		elif "Graph" in line:
			first = False
		elif "Taillenweite" in line:
			block = False
		elif len(line) > 0 and block:
			lines.append(line)
	graphs.append(build_graph_from_asc(lines, n, k))
	return graphs

def main():
	from sage.all import *
	import sage.graphs.graph_plot
	fname = sys.argv[1]
	graphs = build_graph_from_asc_file(fname)

	# Walk each graph, compute the chromatic number and save an image of the graph
	index = 0
	prefix = fname.split(".")[0]
	for G in graphs:
		# Save the graph png file
		P = G.plot()
		P.save(prefix + "_" + str(index) + ".png")

		# Display the graph properties (testing correctness here)
		print("Graph: " + str(index))
		print(G.adjacency_matrix().str())
		print(G.chromatic_number())

		# Onward!
		index = index + 1

if __name__ == "__main__":
	main()