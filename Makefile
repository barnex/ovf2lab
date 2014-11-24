all:
	rm -f *.class
	javac *.java
	gcj -o a2 --main=A2 *.java
	astyle *.java
