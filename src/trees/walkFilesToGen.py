import sys
from os import listdir
from os.path import isfile, join

# Get all of the files..
print(sys.argv[1])
onlyfiles = [ f for f in listdir(sys.argv[1]) if isfile(join(sys.argv[1],f)) ]
fout = open('files', 'w')
for f in onlyfiles:
	fout.write(f + "\n")
