package a2;

import java.util.ArrayList;
import java.io.PrintStream;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

abstract class AbsNode {
	String pos;
	Node[] children;

	AbsNode(String pos) {
		this.pos = pos;
		this.children = new Node[0];
	}

	Node[] children() {
		return this.children;
	}

	String pos() {
		return this.pos;
	}
}

// Block statement: list of statements separated by EOLs.
class BlockStmt extends AbsNode implements Node {

	public BlockStmt(String pos, ArrayList<Node> children) {
		super(pos);
		this.children = new Node[children.size()];
		for (int i=0; i<this.children.length; i++) {
			this.children[i] = children.get(i);
		}
	}

	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.println("{");
		for(Node e: children) {
			e.print(out, indent+1);
			out.println();
		}
		Parser.printIndent(out, indent);
		out.print("}");
	}

	public Node simplify() { // TODO: rm
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
			double val = floatOp( ((NumLit)x).floatValue(), op, ((NumLit)y).floatValue() );
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
	public double floatValue() {
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
	public double floatValue() {
		return val;
	}
}

interface NumLit {
	double floatValue();
}

