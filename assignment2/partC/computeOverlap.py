# filenames = ['Output/ass2-q3-b-30.txt', 'Output/ass2-q3-b-60.txt', 'Output/ass2-q3-b-100.txt', 
# 'Output/ass2-q3-b-500.txt', 'Output/ass2-q3-a-sliding.txt']

filenames = ['/home/ubuntu/grader_assign2/partC/data/ass2-q3-b-30.txt', '/home/ubuntu/grader_assign2/partC/data/ass2-q3-b-60.txt', '/home/ubuntu/grader_assign2/partC/data/ass2-q3-b-100.txt', '/home/ubuntu/grader_assign2/partC/data/ass2-q3-b-500.txt']

f1 = open('/home/ubuntu/grader_assign2/partC/data/ass2-q3-a-tumbling.txt')

windows = set()

for line in f1:
	line = line.replace("(", "").replace(")", "")
	windows.add(line)

for filename in filenames:
	f2 = open(filename)

	numCommonWindows = 0
	numLines = 0
	for line in f2:
		numLines += 1
		line = line.replace("(", "").replace(")", "")
		if line in windows:
			numCommonWindows += 1

	print filename + ": " + str(numCommonWindows) + "/ " + str(numLines) 



