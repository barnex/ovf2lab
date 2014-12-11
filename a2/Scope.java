package a2;

import java.util.HashMap;

/** Scope keeps track of declarations. */
public final class Scope {
	Scope parent;  // parent scope, if any
	HashMap<String, Symbol> sym;

	Scope() {
		sym = new HashMap<String, Symbol>();
	}

	Symbol find(String name) {
		Symbol s = sym.get(name);
		if (s == null && parent != null) {
			s = parent.find(name);
		}
		return s;
	}

	void declare(Ident ident) throws Error { // TODO: type
		Symbol s = sym.get(ident.name);
		if (s!=null) {
			throw new Error(ident.pos() + ": already defined: " + ident.name);
		}
		sym.put(ident.name, new Symbol()); // TODO: symbol
	}
}

final class Symbol {

}
