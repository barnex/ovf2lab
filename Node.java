import java.util.ArrayList;
import java.io.PrintStream;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

// Every node in the AST implements this interface
interface Node {
	void print(PrintStream out);
}

// Abstract node base class provides position information
abstract class AbsNode {

	String file;
	int line, pos;

	AbsNode(Token token) {
		this.file = token.file;
		this.line = token.line;
		this.pos = token.pos;
	}
}

// Block statement: list of statements separated by EOLs.
class BlockStmt extends AbsNode implements Node {
	ArrayList<Node> list;
	public BlockStmt(Token t) {
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

// Assign statement "lhs op rhs", e.g.: a += b
class AssignStmt extends AbsNode implements Node {
	String op;
	Node lhs, rhs;
	AssignStmt(Token t, String op) {
		super(t);
		this.op = op;
	}
	public void print(PrintStream out) {
		this.lhs.print(out);
		out.print(this.op);
		this.rhs.print(out);
	}
}

// Postfix statement "lhs op", e.g.: a++
class PostfixStmt extends AbsNode implements Node {
	String op;
	Node lhs;
	PostfixStmt(Token t, Node lhs, String op) {
		super(t);
		this.lhs = lhs;
		this.op = op;
	}
	public void print(PrintStream out) {
		this.lhs.print(out);
		out.print(this.op);
	}
}

// Binary operator" x op y", e.g.: a + b
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

// Identifier, e.g.: "sin"
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

// Integer literal, e.g.: "123"
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

// Float literal, e.g.: "123e45"
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


