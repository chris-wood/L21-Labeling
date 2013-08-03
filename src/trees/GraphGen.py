#!/usr/bin/python

#
# File: resultsgen.py
# Author: Christopher A. Wood, caw4567@rit.edu
# Description: TODO
#

# Not sure why this is happening...
import pydot
import sys
import os.path

# Set up a list of valid files
files = list()
for fileName in sys.argv[1:len(sys.argv)]:
	if os.path.exists(fileName) and os.path.isfile(fileName):
		files.append(fileName)

# Go through and generate a report for each file
for fileName in files:
	infile = open(fileName, "r")
	numberOfRows = int(infile.readline())
	
	matrix = list()
	# Iteartively build the adjacency matrix for the graph
	for x in range(0,numberOfRows):
		line = infile.readline()
		stringValues = line.split()
		values = list()
		for val in stringValues:
			values.append(int(val))
		#print values
		matrix.append(values)
    
    # Use the adjacency matrix to construct the graph
	graph = pydot.graph_from_adjacency_matrix(matrix, node_prefix='v', directed=False)

    # Just write the graph now
	graph.write_png(fileName + '.png')

# Done