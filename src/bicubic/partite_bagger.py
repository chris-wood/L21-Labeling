from sage import *
from parse_asc import *
import sys

fname = sys.argv[1]
graphs = build_graph_from_asc_file(fname)

index = 0
for G in graphs:
	P = G.plot()
	P.save(prefix + "_" + str(index) + ".png")
	print("Graph: " + prefix + "_" + str(index) + ".png")
	print(G.adjacency_matrix().str())
	print(G.chromatic_number())
