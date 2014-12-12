all:
	rm -f *.class
	javac -Xlint *.java
	gcj -o test --main=view.Test -Wall -Werror *.java ../ovf2/*.java
	astyle --indent=tab *.java > /dev/null 2> /dev/null
