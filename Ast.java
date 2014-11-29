import java.util.ArrayList;
import java.io.PrintStream;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

// Every node in the AST implements this interface
interface Node {
	void print(PrintStream out);
}

abstract class AbsNode {

	String file;
	int line, pos;

	AbsNode(Token token) {
		this.file = token.file;
		this.line = token.line;
		this.pos = token.pos;
	}
}

class ExprList extends AbsNode implements Node {
	ArrayList<Node> list;
	public ExprList(Token t) {
		super(t);
		this.list = new ArrayList<Node>();
	}
	void add(Node e) {
		this.list.add(e);
	}
	public void print(PrintStream out) {
		for(Node e: this.list) {
			e.print(out);
			out.println();
		}
	}
}

class BinOp extends AbsNode implements Node {
	String op;
	Node x, y;
	BinOp(Token t, String op) {
		super(t);
		this.op = op;
	}
	public void print(PrintStream out) {
		out.print("(");
		this.x.print(out);
		out.print(this.op);
		this.y.print(out);
		out.print(")");
	}
}

class Ident extends AbsNode implements Node {
	String name;
	Ident(Token t, String name) {
		super(t);
		this.name = name;
	}
	public void print(PrintStream out) {
		out.print(this.name);
	}
}

class IntLit extends AbsNode implements Node {
	long value;
	IntLit(Token t, long value) {
		super(t);
		this.value = value;
	}
	public void print(PrintStream out) {
		out.print(this.value);
	}
}

class FloatLit extends AbsNode implements  Node {
	double value;
	FloatLit(Token t, double value) {
		super(t);
		this.value = value;
	}
	public void print(PrintStream out) {
		out.print(this.value);
	}
}


