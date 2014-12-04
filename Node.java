import java.util.ArrayList;
import java.io.PrintStream;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

// Every node in the AST implements this interface
interface Node {
	void print(PrintStream out);
	Node simplify();
}

// Block statement: list of statements separated by EOLs.
class BlockStmt implements Node {
	Pos pos;
	ArrayList<Node> list;
	public BlockStmt(Pos p) {
		pos = p;
		list = new ArrayList<Node>();
	}
	void add(Node e) {
		list.add(e);
	}
	public void print(PrintStream out) {
		for(Node e: list) {
			e.print(out);
			out.println();
		}
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
	Pos pos;
	String op;
	Node lhs, rhs;
	AssignStmt(Pos p, String op) {
		this.pos = p;
		this.op = op;
	}
	public void print(PrintStream out) {
		this.lhs.print(out);
		out.print(this.op);
		this.rhs.print(out);
	}
	public Node simplify() {
		lhs = lhs.simplify();
		rhs = rhs.simplify();
		return this;
	}
}

// Postfix statement "lhs op", e.g.: a++
class PostfixStmt implements Node {
	Pos pos;
	String op;
	Node lhs;
	PostfixStmt(Pos p, Node lhs, String op) {
		this.pos = p;
		this.lhs = lhs;
		this.op = op;
	}
	public void print(PrintStream out) {
		this.lhs.print(out);
		out.print(this.op);
	}
	public Node simplify() {
		lhs = lhs.simplify();
		return this;
	}
}

// Call expression: f(arg1, arg2, ...)
class CallExpr implements Node {
	Pos pos;
	Node f;
	Node[] args;
	CallExpr(Pos p, Node f) {
		this.pos = p;
		this.f = f;
	}
	public void print(PrintStream out) {
		this.f.print(out);
		out.print("(");
		for(int i=0; i<this.args.length; i++) {
			if (i>0) {
				out.print(", ");
			}
			this.args[i].print(out);
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
	Pos pos;
	String op;
	Node x, y;
	BinOp(Pos p, String op) {
		this.pos = p;
		this.op = op;
	}
	public void print(PrintStream out) {
		out.print("(");
		this.x.print(out);
		out.print(this.op);
		this.y.print(out);
		out.print(")");
	}
	public Node simplify() {
		x = x.simplify();
		y = y.simplify();

		if(x instanceof IntLit && y instanceof IntLit) {
			long val = intOp(((IntLit)x).val, op, ((IntLit)y).val);
			return new IntLit(this.pos, val);
		}
		if(x instanceof NumLit && y instanceof NumLit) {
			double val = floatOp( ((NumLit)x).value(), op, ((NumLit)y).value() );
			return new FloatLit(this.pos, val);
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
	Pos pos;
	String name;
	Ident(Pos p, String name) {
		this.pos = p;
		this.name = name;
	}
	public void print(PrintStream out) {
		out.print(this.name);
	}
	public Node simplify() {
		return this;
	}
}

// Integer literal, e.g.: "123"
class IntLit implements Node, NumLit {
	Pos pos;
	long val;
	IntLit(Pos p, long val) {
		this.pos = p;
		this.val = val;
	}
	public void print(PrintStream out) {
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
	Pos pos;
	double val;
	FloatLit(Pos p, double val) {
		this.pos = p;
		this.val = val;
	}
	public void print(PrintStream out) {
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


