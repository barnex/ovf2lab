all:
	rm -f *.class
	javac *.java
	astyle --indent=tab *.java > /dev/null 2> /dev/null
	gcj -o ovf2 --main=OVF2 -Wall -Werror *.java
