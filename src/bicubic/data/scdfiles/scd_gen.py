import sys
import subprocess
import os
import shutil
import glob
from parse_asc import *

if (len(sys.argv) != 2):
	print "Usage: python scnGen.py fileWithFileNames"
else:
	f = open(sys.argv[1], 'r')

	# Grab all files...
	for fname in f:
		fname = fname.strip()
		n, k = parse_params(fname)
		print >> sys.stderr, "Running: " + fname

		p = subprocess.Popen('./readscd ' + str(n) + ' ' + str(k), shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
		lines = []
		for line in p.stdout.readlines():
			line = line.strip()
			lines.append(line)
		fout = open(fname + ".asc", 'w')
		for line in lines:
			fout.write(line + "\n")
