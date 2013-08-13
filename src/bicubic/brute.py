#!/usr/bin/python

#
# File: brute.py
# Author: Christopher A. Wood, caw4567@rit.edu
#

import sys
import subprocess
import os
import shutil
import glob

if (len(sys.argv) != 3):
	print "Usage: python brute.py infile outfile"
else:
	# Input params
	infileName = sys.argv[1] 
	outfileName = sys.argv[2]

	# Files to use
	graphList = open(infileName, 'r')
	outfile = open(outfileName, 'w')

	# Run the brute force span generator for each graph in the file
	for g6String in graphList:
		g6String = g6String.strip()
		if (len(g6String) > 0):
			print >> sys.stderr, "Running: " + g6String
			p = subprocess.Popen('java BicubicBruteForceAssignment 1 ' + g6String, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
			span = ""
			for line in p.stdout.readlines():
				print >> sys.stderr, line.strip()
				try:
					if len(line) > 0:
						span = line.strip()
						break
				except:
					raise Exception("Failed on graph: " + str(g6String))
			outfile.write(g6String + "," + span + "\n")
