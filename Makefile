all:
	rm -f *.class
	javac *.java
	gcj -o ovf2lab --main=OVF2Lab -Wall -Werror *.java
	astyle --indent=tab *.java > /dev/null 2> /dev/null
