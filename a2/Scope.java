package a2;

import java.util.HashMap;

/** Scope keeps track of declarations. */
public final class Scope {
	Scope parent;  // parent scope, if any
	HashMap<String, Symbol> sym;

	Scope() {
		sym = new HashMap<String, Symbol>();
	}

	void resolve(Node n) throws Error {
		Node[] c = n.children();
		for(int i=0; i<c.length; i++) {
			resolve(c[i]);
		}
		if (n instanceof Ident) {
			Ident ident = (Ident)(n);
			ident.sym = find(ident.name);
			if (ident.sym == null) {
				throw new Error(n.pos() + " undefined: " + ident.name);
			}
		}
	}

	Symbol find(String name) {
		Symbol s = sym.get(name);
		if (s == null && parent != null) {
			s = parent.find(name);
		}
		return s;
	}
}

final class Symbol {

}
