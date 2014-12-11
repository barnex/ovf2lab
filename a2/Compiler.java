package a2;

final class Compiler {

	// recursively simplify AST rooted at N
	static Node simplify(Node n) {
		Node[] c = n.children();
		for (int i=0; i<c.length; i++) {
			c[i] = simplify(c[i]);
		}
		return n.simplify();
	}
}

