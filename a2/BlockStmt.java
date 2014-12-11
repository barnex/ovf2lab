package a2;

import java.io.PrintStream;
import java.util.ArrayList;

// Block statement: list of statements separated by EOLs.
public class BlockStmt extends AbsNode implements Node {

	Scope scope;

	public BlockStmt(String pos, ArrayList<Node> children, Scope scope) {
		super(pos, children.size());
		this.scope = scope;
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

