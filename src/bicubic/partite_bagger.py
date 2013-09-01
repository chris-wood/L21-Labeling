from sage import *
from parse_asc import *
import sys

fname = sys.argv[1]
graphs = build_graph_from_asc_file(fname)

partite_bags = {}

index = 0
for G in graphs:
	# Save a picture of the graph so this can be checked!
	P = G.plot()
	P.save(prefix + "_" + str(index) + ".png")
	t = G.chromatic_number()

	# Update the map
	if not (t in partite_bags):
		partite_bags[t] = []
	partite_bags[t].append((G, prefix + "_" + str(index) + ".png"))

# Display the graphs in each bag
for t in partite_bags:
	print("chi: " + str(t))
	for (G, fn) in partite_bags[t]:
		print(fn)
