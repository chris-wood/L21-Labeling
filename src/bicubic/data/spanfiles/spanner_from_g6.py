import sys
import subprocess
import os
import shutil
import glob

def parse_params(fname):
	''' Retrieve n and k from the file name.
	'''
	params = fname.split("_")
	n = int(params[0])
	k = int(params[1])
	return n, k

def parse_bounds(line):
	''' Retrieve label span lower and upper bounds
	'''
	data = line.split(" ")
	low = int(data[0])
	high = int(data[1])
	return low, high

if (len(sys.argv) != 2):
	print "Usage: python spanner_from_g6.py fileWithFileNames"
else:
	f = open(sys.argv[1], 'r')

	# Grab all files...
	line = f.readline().strip()
	while len(line) > 0:
		fname = line
		print >> sys.stderr, "Running: " + fname
		line = f.readline().strip()
		n, k = parse_params(fname)
		low, high = parse_bounds(line)
		line = f.readline().strip()

		p = subprocess.Popen('java -classpath pj.jar:. ParallelL21Assignment 2 ' + str(low) + ' ' + str(high) + " " + fname, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
		lines = []
		for l in p.stdout.readlines():
			l = l.strip()
			lines.append(l)
		fout = open(fname + ".span", 'w')
		for l in lines:
			fout.write(l + "\n")
