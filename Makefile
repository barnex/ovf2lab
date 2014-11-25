all:
	rm -f *.class
	javac *.java
	gcj -o e2 --main=E2 -Wall -Werror *.java
	astyle --indent=tab *.java
