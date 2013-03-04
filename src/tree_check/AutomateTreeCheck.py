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

if (len(sys.argv) != 5):
	print "Usage: python AutomateTreeCheck.py passDir failDir samples alg"
else:
	passDir = sys.argv[1]
	failDir = sys.argv[2]
	samples = sys.argv[3]
	alg = sys.argv[4]
	
	# Grab all files...
	listing = os.listdir(samples)
	for file in listing:
		
		# Get the extension and check it
		fileName, fileExtension = os.path.splitext(file)
		if (fileExtension == '.amf'): # yeah, don't forget this... >.<
			
			# Run the L21 algorithm on all samples
			print "Running: " + file
			p = subprocess.Popen('java ' + alg + ' ' + samples + '/' + file, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
			testPass = True
			for line in p.stdout.readlines():
				try:
					if ("true" in line):
						testPass = False # it had the property, so test failed
				except:
					raise Exception("Something went wrong with file: " + str(file))
					#testPass = False
			
			# Copy the file over to the correct location for continual or manual inspection
			if (testPass == True):
				print("Passed (doesn't contain the forbidden subtree")
				shutil.copy2(samples + '/' + file, passDir + '/' + file)
			else:
				print("Failed (contains the forbidden subtree")
				shutil.copy2(samples + '/' + file, failDir + '/' + file)
