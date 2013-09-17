import sys
from sage.all import *

# Partite bagger from G6 string input (REQUIRES SAGE!)
# Run: sage -python partite_bagger_from_g6.py g6file
# where g6file is a list of g6 strings for graphs corresponding to the n/k values from the filename
# e.g. g6file might be 08_3_3.scd.asc.g6

def parse_params(fname):
	''' Retrieve n and k from the file name.
	'''
	params = fname.split("_")
	n = int(params[0])
	k = int(params[1])
	return n, k

files = sys.argv[1]
filesIn = open(files, 'r')
for fname in filesIn:
	print >> sys.stderr, "Parsing: " + str(fname)
	fname = fname.strip()
	prefix = fname.split(".")[0]
	
	n, k = parse_params(fname)
	f = open(fname, 'r')
	
	graphs = []
	for g6str in f:
		g6str = g6str.strip()
		print >> sys.stderr, "Read G6: " + str(g6str)
		G = Graph("" + str(g6str))
		graphs.append(G)
	
	partite_bags = {}
	index = 0
	for G in graphs:
		# Save a picture of the graph so this can be checked!
		# P = G.plot()
		# P.save(prefix + "_" + str(index) + ".png")
		t = G.chromatic_number()

		# Update the map
		if not (t in partite_bags):
			partite_bags[t] = []
		partite_bags[t].append((G, prefix + "_" + str(index) + ".png"))
		index = index + 1

	# Display the graphs in each bag
	fout = open(fname + ".chi", 'w')
	for t in partite_bags:
		print("chi: " + str(t))
		fout.write("chi: " + str(t) + "\n")
		for (G, fn) in partite_bags[t]:
			print(fn)
			fout.write(str(fn) + "\n")
	
