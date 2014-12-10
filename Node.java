import java.util.ArrayList;
import java.io.PrintStream;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

// Every node in the AST implements this interface
interface Node {
	void print(PrintStream out, int indent);
	Node simplify();
}

// Block statement: list of statements separated by EOLs.
class BlockStmt implements Node {
	int line;
	ArrayList<Node> list;
	public BlockStmt(int line) {
		this.line = line;
		list = new ArrayList<Node>();
	}
	void add(Node e) {
		list.add(e);
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.println("{");
		for(Node e: list) {
			e.print(out, indent+1);
			out.println();
		}
		Parser.printIndent(out, indent);
		out.print("}");
	}
	public Node simplify() {
		for( int i=0; i<list.size(); i++) {
			list.set(i, list.get(i).simplify());
		}
		return this;
	}
}

// Assign statement "lhs op rhs", e.g.: a += b
class AssignStmt implements Node {
	int line;
	String op;
	Node lhs, rhs;
	AssignStmt(int line, String op) {
		this.line = line;
		this.op = op;
	}
	public void print(PrintStream out, int indent) {
		this.lhs.print(out, indent);
		out.print(this.op);
		this.rhs.print(out, 0);
	}
	public Node simplify() {
		lhs = lhs.simplify();
		rhs = rhs.simplify();
		return this;
	}
}

// Postfix statement "lhs op", e.g.: a++
class PostfixStmt implements Node {
	int line;
	String op;
	Node lhs;
	PostfixStmt(int line, Node lhs, String op) {
		this.line = line;
		this.lhs = lhs;
		this.op = op;
	}
	public void print(PrintStream out, int indent) {
		this.lhs.print(out, indent);
		out.print(this.op);
	}
	public Node simplify() {
		lhs = lhs.simplify();
		return this;
	}
}

// Call expression: f(arg1, arg2, ...)
class CallExpr implements Node {
	int line;
	Node f;
	Node[] args;
	CallExpr(int line, Node f) {
		this.line = line;
		this.f = f;
	}
	public void print(PrintStream out, int indent) {
		this.f.print(out, indent);
		out.print("(");
		for(int i=0; i<this.args.length; i++) {
			if (i>0) {
				out.print(", ");
			}
			this.args[i].print(out, 0);
		}
		out.print(")");
	}
	public Node simplify() {
		f = f.simplify();
		for(int i=0; i<args.length; i++) {
			args[i] = args[i].simplify();
		}
		return this;
	}
}

// Binary operator" x op y", e.g.: a + b
class BinOp implements Node {
	int line;
	String op;
	Node x, y;
	BinOp(int line, String op) {
		this.line = line;
		this.op = op;
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.print("(");
		this.x.print(out, 0);
		out.print(this.op);
		this.y.print(out, 0);
		out.print(")");
	}
	public Node simplify() {
		x = x.simplify();
		y = y.simplify();

		if(x instanceof IntLit && y instanceof IntLit) {
			long val = intOp(((IntLit)x).val, op, ((IntLit)y).val);
			return new IntLit(line, val);
		}
		if(x instanceof NumLit && y instanceof NumLit) {
			double val = floatOp( ((NumLit)x).value(), op, ((NumLit)y).value() );
			return new FloatLit(line, val);
		}
		return this;
	}
	// used for compile-time evaluation of (int op int)
	static long intOp(long x, String op, long y) {
		if (op.equals("+")) {
			return x+y;
		}
		if (op.equals("-")) {
			return x-y;
		}
		if (op.equals("*")) {
			return x*y;
		}
		if (op.equals("/")) {
			return x/y;
		}
		if (op.equals("%")) {
			return x%y;
		}
		if (op.equals("|")) {
			return x|y;
		}
		if (op.equals("&")) {
			return x&y;
		}
		throw new IllegalStateException("unknown op " + op);
	}
	// used for compile-time evaluation of (num op num)
	// where at least one number is float
	static double floatOp(double x, String op, double y) {
		if (op.equals("+")) {
			return x+y;
		}
		if (op.equals("-")) {
			return x-y;
		}
		if (op.equals("*")) {
			return x*y;
		}
		if (op.equals("/")) {
			return x/y;
		}
		if (op.equals("%")) {
			return x%y;
		}
		if (op.equals("^")) {
			return Math.pow(x, y);
		}
		throw new IllegalStateException("unknown op " + op);
	}
}

// Identifier, e.g.: "sin"
class Ident implements Node {
	int line;
	String name;
	Ident(int line, String name) {
		this.line = line;
		this.name = name;
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.print(this.name);
	}
	public Node simplify() {
		return this;
	}
}

// Integer literal, e.g.: "123"
class IntLit implements Node, NumLit {
	int line;
	long val;
	IntLit(int line, long val) {
		this.line = line;
		this.val = val;
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.print(this.val);
	}
	public Node simplify() {
		return this;
	}
	public double value() {
		return val;
	}
}

// Float literal, e.g.: "123e45"
class FloatLit implements  Node, NumLit {
	int line;
	double val;
	FloatLit(int line, double val) {
		this.line = line;
		this.val = val;
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.print(this.val);
	}
	public Node simplify() {
		return this;
	}
	public double value() {
		return val;
	}
}

interface NumLit {
	double value();
}

