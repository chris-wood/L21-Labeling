import sys
import random

def build_graph_from_asc(lines, nn, k):
	from sage.all import *
	import sage.graphs.graph_plot
	''' Build the adjacency matrix and SAGE graph from the adjacency list
	format of the graph contained in the lines list.
	'''
	print >> sys.stderr, lines
	# print(nn)
	# print(k)
	rows = []
	for i in range(nn):
		row = []
		connectedTo = lines[i].split(" : ")[1].split(" ")
		for j in range(nn):
			# if str(j + 1) in connectedTo:
			found = False
			for k in connectedTo:
				if str(j + 1) == k:
					found = True
					break
			if found:
				row.append(1)
			else:
				row.append(0)
		rows.append(row)
	# print >> sys.stderr, rows
	for r in rows:
		print >> sys.stderr, r
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
	# print(n)
	# print(k)

	f = open(fname, 'r')
	lines = []
	first = True
	block = True
	graphs = []
	for line in f:
		line = line.strip()
		# print(line)
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
		chi = G.chromatic_number()
		fout = open(prefix + ".bag_out", 'w')
		print("Graph: " + str(index))
		print >> sys.stderr, "Graph: " + str(index)
		fout.write("Graph: " + str(index) + "\n")
		# print(G.adjacency_matrix().str())
		# print >> sys.stderr, G.adjacency_matrix().str()
		print(chi)
		print >> sys.stderr, chi
		fout.write(str(chi) + "\n")

		# Onward!
		index = index + 1

if __name__ == "__main__":
	main()