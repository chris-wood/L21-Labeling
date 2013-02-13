#!/usr/bin/python

#
# File: AutomateLabel.py
# Author: Christopher A. Wood, caw4567@rit.edu
# Description: TODO
#

import sys
import subprocess
import os
import shutil
import glob

# Declare variables for the delta+1 and delta+2 output
d1out = ""
d2out = ""
samples = ""
alg = ""

if (len(sys.argv) != 5):
	print "Usage: python AutomateLabel.py d1out d2out samples alg"
else:
	d1out = sys.argv[1]
	d2out = sys.argv[2]
	samples = sys.argv[3]
	alg = sys.argv[4]

	# Grab all files...
	"""
	listing = []
	while (True):
		try:
			listing = os.listdir(samples)
			break
		except:
			print "os.listdir() failed"
	"""
	#for file in listing:
	fileList = open('files', 'r')
	while True:
		# Get the extension and check it
		file = fileList.readline().rstrip('\n')
		fileName, fileExtension = os.path.splitext(file)
		if (fileExtension == '.amf'):
		
			# Run the L21 algorithm on all samples
			print "running " + file
			p = subprocess.Popen('java ' + alg + ' ' + samples + '/' + file, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
			delta1 = False
			for line in p.stdout.readlines():
				try:
					print(line)
					if ("true" in line):
						delta1 = True
						print "delta+1"
					else:
						print "delta+2"
				except:
					print "uhoh"
					raise Exception("Failed on file: " + str(file))

			# Copy the file over to the correct location to run the graph generation program
			if (delta1 == True):
				shutil.copy2(samples + '/' + file, d1out + '/' + file)
			else:
				shutil.copy2(samples + '/' + file, d2out + '/' + file)

		
