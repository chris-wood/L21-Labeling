from sage import *
from parse_asc import *
import sys

files = sys.argv[1]
filesIn = open(files, 'r')
for fname in filesIn:
	fname = fname.strip()
	prefix = fname.split(".")[0]
	graphs = build_graph_from_asc_file(fname)

	partite_bags = {}

	index = 0
	for G in graphs:
		# Save a picture of the graph so this can be checked!
		# P = G.plot()
		# P.save(prefix + "_" + str(index) + ".png")
		t = G.chromatic_number()
		print >> sys.stderr, str(prefix) + " (" + str(index) + "): " + str(t)

		# Update the map
		if not (t in partite_bags):
			partite_bags[t] = []
		partite_bags[t].append((G, prefix + "_" + str(index) + ".png"))
		index = index + 1

	# Display the graphs in each bag
	for t in partite_bags:
		print("chi: " + str(t))
		for (G, fn) in partite_bags[t]:
			print(fn)
