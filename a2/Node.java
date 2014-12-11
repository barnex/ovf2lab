package a2;

import java.io.PrintStream;

/** AST (Abstract Syntax Tree) node. */
public interface Node {
	void print(PrintStream out, int indent);
	Node simplify();
	Node[] children();
}
