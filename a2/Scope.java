import java.util.Map;

/** Scope keeps track of declarations. */
public final class Scope {
	Scope parent;  // parent scope, if any
	Map<String, Symbol> decl;
}

final class Symbol {

}
