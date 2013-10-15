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

if (len(sys.argv) != 3):
	print "Usage: python cross_chi_span.py <chi_dir> <span_dir>"
else:
	chi_dir = sys.argv[1]
	span_dir = sys.argv[2]

	# Collect chi files
	chi_files = []
	for dirpath, dnames, fnames in os.walk(chi_dir):
		for f in fnames:
			if f.endswith(".chi"):
				chi_files.append((f, os.path.join(dirpath, f)))

	# Collect span files
	span_files = []
	for dirpath, dnames, fnames in os.walk(span_dir):
		for f in fnames:
			if f.endswith(".span"):
				span_files.append((f, os.path.join(dirpath, f)))

	# Intersect chi/span files and then write the spreadsheet information
	data = [] # tuple of (n, k, g6, chi, span)
	for (fc, fp1) in chi_files:
		nc, kc = parse_params(fc)
		for (fs, fp2) in span_files:
			ns, ks = parse_params(fs)
			if (nc == ns and kc == ks):

				# Build up the span data
				span_data = [] # tuples of ID to span
				f = open(fp2, 'r')
				l = f.readline().strip() # Skip first line (output from PJ library)
				l = f.readline().strip()
				g6count = 0
				while len(l) > 0:
					index = l.rfind(",")
					g6 = l[0:index]
					span = l[index + 1:len(l)]
					span_data.append((g6count, g6, span))
					g6count = g6count + 1

					# Advance to next graph
					l = f.readline().strip() # skip the actual label
					l = f.readline().strip() # this store the next graph data
					l = f.readline().strip() # Skip the full piece of data...

				# Build up chi data for this set of graphs
				chi = 0 # first set of data will be preceded by the chi value
				f = open(fp1, 'r')
				for l in f:
					if "chi" in l:
						chi = int(l[l.find(":") + 1:len(l)])
					else:
						graphId = int(l[l.rfind("_") + 1:l.rfind(".")])

						# Find the matching g6 string
						for (gc, g, s) in span_data:
							if (gc == graphId):
								data.append((nc, kc, chi, s, g))

	# Print each piece of data!
	for tup in data:
		print str(tup[0]) + "," + str(tup[1]) + "," + str(tup[2]) + "," + str(tup[3]) + "," + str(tup[4])
