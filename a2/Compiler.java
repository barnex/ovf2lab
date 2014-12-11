package a2;

public final class Compiler {

	static void resolve(Node n, Scope s) throws Error {
		if (n instanceof BlockStmt) {
			BlockStmt b = (BlockStmt)(n);
			//assert(b.scope.parent == s);
			for(Node c: b.child) {
				resolve(c, b.scope);
			}
		} else {
			s.resolve(n);
		}
	}

	// recursively simplify AST rooted at N
	static Node simplify(Node n) {
		Node[] c = n.children();
		for (int i=0; i<c.length; i++) {
			c[i] = simplify(c[i]);
		}
		return n.simplify();
	}
}

