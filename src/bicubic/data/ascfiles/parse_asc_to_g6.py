import sys
import random
from sage.all import *

# Parse a file containing a list of adjacency matrices and output the corresponding G6 strings...
# This uses Sage to do G6 parsing
# Run: sage -python parse_asc_to_g6.py asc_file

def build_graphs_from_asc_file(fname, n, k):
	f = open(fname, 'r')
	lines = []
	first = True
	block = True
	graphs = []
	
	# Parse each one!
	line = ""
	line = f.readline().strip()
	while len(line) > 0:
		index = int(line)
		rows = []
		for i in range(n):
			line = f.readline().strip()
			entries = line.split(" ")
			row = []
			for e in entries:
				e = int(e)
				row.append(e)
			rows.append(row)
		M = Matrix(rows)
		G = Graph(M)
		graphs.append(G)
		line = f.readline().strip()
	return graphs
	
def parse_params(fname):
	''' Retrieve n and k from the file name.
	'''
	params = fname.split("_")
	n = int(params[0])
	k = int(params[1])
	return n, k

def main():
	fname = sys.argv[1]
	filesin = open(fname, 'r')
	for filename in filesin:
		filename = filename.strip()
		print >> sys.stderr, "Parsing: " + str(filename)
		n, k = parse_params(filename)
		graphs = build_graphs_from_asc_file(filename, n, k)
	
		# Write the g6 string format to stdout, nothing fancy...
		outfile = open(filename + ".g6", 'w')
		for g in graphs:
			outfile.write(g.graph6_string() + "\n")
			#print(g.graph6_string())

if __name__ == "__main__":
	main()
