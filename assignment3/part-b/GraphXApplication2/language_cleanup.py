file = open("Question2_words.txt",'r')
output = open("graph_input.txt",'w')
lines = file.readlines();
count = 1;
for line in lines:
	text = str(count)+":"
	words = line.split(" ")
	for word in words:
		if("http" not in word and "#" not in word and word.isalnum()):
			text = text +" "+ word.lower().strip()
	output.write(text+"\n")
	count = count+1
file.close()
output.close()