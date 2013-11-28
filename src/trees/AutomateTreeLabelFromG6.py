#!/usr/bin/python

#
# File: AutomateTreeLabelFromG6.py
# Author: Christopher A. Wood, caw4567@rit.edu
# Description: TODO
#

import sys
import subprocess
import os
import shutil
import glob

print >> sys.stderr, len(sys.argv)
if (len(sys.argv) != 4):
	print "Usage: python AutomateLabel.py alg fileOfFiles file?"
else:
	alg = sys.argv[1]
	fof = sys.argv[2]
	tofile = int(sys.argv[3])

	fofn = open(fof, 'r')
	for fname in fofn:
		fname = fname.strip()
		if (len(fname) > 0):
			# Obtain the results
			print >> sys.stderr, "Running: " + fname
			tempout = open(fname + ".out", 'w')

			if (tofile == 0):
				p = subprocess.Popen('java ' + alg + " 1 " + fname, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
				lines = []
				for l in p.stdout.readlines():
					l = l.strip()
					lines.append(l) # the program will output two lines per graph: the g6 string and the span pass/fail flag

				# Parse the results and put the files in the appropriate files
				print >> sys.stderr, "Parsing results and dumping output of N files: " + str(len(lines) / 2)
				d1 = []
				d2 = []
				for i in range(0, len(lines), 2):
					if "true" in lines[i + 1]:
						d1.append(lines[i])
					else:
						d2.append(lines[i])

				# Write the results to the file
				print >> sys.stderr, "Dump output to " + fname + "_d1.txt and " + fname + "_d2.txt"
				d1f = open(fname + "_d1.txt", 'w')
				for g in d1:
					d1f.write(g + "\n")
				d2f = open(fname + "_d2.txt", 'w')
				for g in d2:
					d2f.write(g + "\n")
			else: # to avoid memory error...
				p = subprocess.Popen('java ' + alg + " 1 " + fname, shell=True, stdout=tempout, stderr=subprocess.STDOUT)
				retcode = p.wait()
				tempout.flush()
