import java.util.ArrayList;
import java.io.PrintStream;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

// Every node in the AST implements this interface
interface Node {
	void print(PrintStream out);
	Node simplify();
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
	public Node simplify() {
		for( int i=0; i<list.size(); i++) {
			list.set(i, list.get(i).simplify());
		}
		return this;
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
	public Node simplify() {
		lhs = lhs.simplify();
		rhs = rhs.simplify();
		return this;
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
	public Node simplify() {
		lhs = lhs.simplify();
		return this;
	}
}

// Call expression: f(arg1, arg2, ...)
class CallExpr extends AbsNode implements Node {
	Node f;
	Node[] args;
	CallExpr(Token t, Node f) {
		super(t);
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
	public Node simplify() {
		x = x.simplify();
		y = y.simplify();

		if(x instanceof IntLit && y instanceof IntLit) {
			long val = intOp(((IntLit)x).val, op, ((IntLit)y).val);
			return new IntLit(val);
		}
		if(x instanceof NumLit && y instanceof NumLit) {
			double val = floatOp( ((NumLit)x).value(), op, ((NumLit)y).value() );
			return new FloatLit(val);
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
		throw new IllegalStateException("unknown op " + op);
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
	public Node simplify() {
		return this;
	}
}

// Integer literal, e.g.: "123"
class IntLit extends AbsNode implements Node, NumLit {
	long val;
	IntLit(Token t, long val) {
		super(t);
		this.val = val;
	}
	IntLit(long val) {
		super(new Token()); // no pos info
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
class FloatLit extends AbsNode implements  Node, NumLit {
	double val;
	FloatLit(Token t, double val) {
		super(t);
		this.val = val;
	}
	FloatLit(double val) {
		super(new Token()); // no pos info
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


