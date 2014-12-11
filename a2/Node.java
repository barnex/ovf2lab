package a2;

import java.io.PrintStream;

/** AST (Abstract Syntax Tree) node. */
public interface Node {

	// Position of the Node: "file:line".
	String pos();

	// Prints the entire AST rooted at this node.
	void print(PrintStream out, int indent);

	// Returns a simplified (optimized) version of this node,
	// assuming the children have already been optimized.
	// Used by Compiler.simplify() whic provides a recursive implementation.
	Node simplify();

	// This nodes children. Writing this array changes the children
	// (e.g. used by Compiler.simplify());
	Node[] children();
}
