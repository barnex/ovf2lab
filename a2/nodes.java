package a2;

import java.io.PrintStream;
import java.util.ArrayList;

// This file defines multiple classes that composite the Abstract Syntax Tree (AST).

abstract class AbsNode {
	String pos;
	Node[] child;

	AbsNode(String pos, int nChildren) {
		this.pos = pos;
		this.child = new Node[nChildren];
	}

	public Node[] children() {
		return this.child;
	}

	public String pos() {
		return this.pos;
	}
}


// Block statement: list of statements separated by EOLs.
class BlockStmt extends AbsNode implements Node {

	public BlockStmt(String pos, ArrayList<Node> children) {
		super(pos, children.size());
		for (int i=0; i<this.child.length; i++) {
			this.child[i] = children.get(i);
		}
	}

	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		if (child.length == 0) {
			out.println("{}");
			return;
		}
		out.println("{");
		for(Node c: child) {
			c.print(out, indent+1);
			out.println();
		}
		Parser.printIndent(out, indent);
		out.print("}");
	}

	public Node simplify() {
		return this;
	}
}


// Assign statement "lhs op rhs", e.g.: a += b
class AssignStmt extends AbsNode implements Node {
	String op;
	AssignStmt(String pos, String op) {
		super(pos, 2);
		this.op = op;
	}
	public void print(PrintStream out, int indent) {
		child[0].print(out, indent);
		out.print(this.op);
		child[1].print(out, 0);
	}
	public Node simplify() {
		return this;
	}
}

// Postfix statement "lhs op", e.g.: a++
class PostfixStmt extends AbsNode implements Node {
	String op;
	PostfixStmt(String pos, Node lhs, String op) {
		super(pos, 1);
		this.op = op;
		child[0] = lhs;
	}
	public void print(PrintStream out, int indent) {
		child[0].print(out, indent);
		out.print(this.op);
	}
	public Node simplify() {
		return this;
	}
}

// Call expression: f(arg1, arg2, ...)
class CallExpr extends AbsNode implements Node {
	CallExpr(String pos, Node f, Node[] args) {
		super(pos, 1+args.length);
		child[0] = f;
		for(int i=0; i<args.length; i++) {
			child[i+1] = args[i];
		}
	}
	Node arg(int i) {
		return child[i+1];
	}
	int nArg() {
		return child.length-1;
	}
	public void print(PrintStream out, int indent) {
		child[0].print(out, indent);
		out.print("(");
		for(int i=0; i<nArg(); i++) {
			if (i>0) {
				out.print(", ");
			}
			arg(i).print(out, 0);
		}
		out.print(")");
	}
	public Node simplify() {
		return this;
	}
}

// Binary operator" x op y", e.g.: a + b
class BinOp extends AbsNode implements Node {

	String op;

	BinOp(String pos, String op) {
		super(pos, 2);
		this.op = op;
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.print("(");
		child[0].print(out, 0);
		out.print(this.op);
		child[1].print(out, 0);
		out.print(")");
	}
	public Node simplify() {
		Node x = child[0];
		Node y = child[1];
		if(x instanceof IntLit && y instanceof IntLit) {
			long val = intOp(((IntLit)x).val, op, ((IntLit)y).val);
			return new IntLit(pos, val);
		}
		if(x instanceof NumLit && y instanceof NumLit) {
			double val = floatOp( ((NumLit)x).floatValue(), op, ((NumLit)y).floatValue() );
			return new FloatLit(pos, val);
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
class Ident extends AbsNode implements Node {
	String name;
	Symbol sym; // points to the meaning of the identifier once its name has been resolved by Scope.resolve().
	Ident(String pos, String name) {
		super(pos, 0);
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
class IntLit extends AbsNode implements Node, NumLit {
	long val;
	IntLit(String pos, long val) {
		super(pos, 0);
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
class FloatLit extends AbsNode implements  Node, NumLit {
	double val;
	FloatLit(String pos, double val) {
		super(pos, 0);
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

class Nop extends AbsNode implements Node {
	Nop(String pos) {
		super(pos, 0);
	}
	public void print(PrintStream out, int indent) {
		Parser.printIndent(out, indent);
		out.print(";");
	}
	public Node simplify() {
		return this;
	}
}
