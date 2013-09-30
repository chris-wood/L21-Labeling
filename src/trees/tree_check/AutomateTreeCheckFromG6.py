#!/usr/bin/python

#
# File: AutomateTreeCheck.py
# Author: Christopher A. Wood, caw4567@rit.edu
# Description: TODO
#

import sys
import subprocess
import os
import shutil

# Declare variables for the delta+1 and delta+2 output
failDir = ""
passDir = ""
samples = ""
alg = ""

if (len(sys.argv) != 4):
	print "Usage: python AutomateTreeCheck.py passFile failDir input"
else:
	passfn = sys.argv[1]
	failfn = sys.argv[2]
	inputfn = sys.argv[3]

	# Open files to save output
	passf = open(passfn, 'w')
	failf = open(failfn, 'w')
	
	# Grab all files...
	inputf = open(inputfn, 'r')
	l = inputf.readline().strip()
	while len(l) > 0:
		g6 = l
		l = inputf.readline().strip()
		result = l

		# Only run verifier on trees that are shown to have a span of delta+2
		if ("false" in result):
			print >> sys.stderr, "Running: " + g6
			p = subprocess.Popen('java TreeChecker 1 ' + g6, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
			contains = False
			for line in p.stdout.readlines():
				try:
					print >> sys.stderr, "Out: " + line
					if "true" in line: # true means that it contained a forbidden subtree
						contains = True
				except:
					raise Exception("Something went wrong with graph: " + g6)

			if contains:
				print >> sys.stderr, "Passed: contained one of the subtrees"
				passf.write(g6 + "\n")
			else:
				print >> sys.stdout, "Failure case: " + g6
				print >> sys.stderr, "Failed: did not contain one of the subtrees"
				failf.write(g6 + "\n")

		# Advance to the next graph...
		l = inputf.readline().strip()

