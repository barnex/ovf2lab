package a2;

import java.io.PrintStream;
import java.util.ArrayList;

public final class StmtList extends AbsNode implements Node {

	public StmtList(String pos, ArrayList<Node> children) {
		super(pos, children.size());
		for (int i=0; i<this.child.length; i++) {
			this.child[i] = children.get(i);
		}
	}

	public void print(PrintStream out, int indent) {
		for(Node c: child) {
			c.print(out, indent);
			out.println();
		}
	}

	public Node simplify() {
		return this;
	}
}
