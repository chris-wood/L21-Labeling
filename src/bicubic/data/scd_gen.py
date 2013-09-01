import sys
import subprocess
import os
import shutil
import glob

if (len(sys.argv) != 2):
	print "Usage: python scnGen.py fileWithFileNames"
else:
	f = open(sys.argv[1], 'r')

	# Grab all files...
	for fname in f:
		fname = fname.strip()


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