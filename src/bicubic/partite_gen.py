from sage import *
import sys

n = int(sys.argv[1])
t = int(sys.argv[2])

for G in sage.graphs.nauty_geng(n):
	if G.chromatic_number() == t:
		print(G)