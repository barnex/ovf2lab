package a2;

import java.io.PrintStream;

// Every node in the AST implements this interface
public interface Node {
	void print(PrintStream out, int indent);
	Node simplify();
}
