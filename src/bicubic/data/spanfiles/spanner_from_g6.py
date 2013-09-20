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
		line = f.readline().strip()
		n, k = parse_params(fname)
		low, high = parse_bounds(line)
		line = f.readline().strip()
		print >> sys.stderr, "Running: " + fname

		p = subprocess.Popen('java -classpath pj.jar:. ParallelL21Assignment 2 ' + str(low) + ' ' + str(high) + " " + fname, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
		lines = []
		for line in p.stdout.readlines():
			line = line.strip()
			lines.append(line)
		fout = open(fname + ".span", 'w')
		for line in lines:
			fout.write(line + "\n")
