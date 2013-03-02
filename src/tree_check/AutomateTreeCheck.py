
<!-- saved from url=(0086)https://lhk-labelling.googlecode.com/svn/trunk/src/tree_check/src/AutomateTreeCheck.py -->
<html><head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></head><body cz-shortcut-listen="true"><pre style="word-wrap: break-word; white-space: pre-wrap;">#!/usr/bin/python

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
		if (fileExtension == '.amf'):
			
			# Run the L21 algorithm on all samples
			print "running " + file
			p = subprocess.Popen('java ' + alg + ' ' + samples + '/' + file, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
			testPass = True
			for line in p.stdout.readlines():
				try:
					if (line == "true\n"):
						testPass = False # it had the property, so test failed
				except:
					testPass = False
			
			# Copy the file over to the correct location to run the graph generation program
			if (testPass == True):
				shutil.copy2(samples + '/' + file, passDir + '/' + file)
			else:
				shutil.copy2(samples + '/' + file, failDir + '/' + file)

</pre></body></html>