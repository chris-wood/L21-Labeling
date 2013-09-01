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
		for line in p.stdout.readlines():
			line = line.strip()
			print line
