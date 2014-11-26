import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

final class Parser {

	Scanner scanner;
	Token token, next;
	Node ast;
	ArrayList<String> errors;

	Parser() {

	}

	void parseFile(String filename) throws FileNotFoundException, IOException {
		Reader reader = new FileReader(new File(filename));
		this.scanner = new Scanner(filename, reader);
		this.errors = new ArrayList<String>();

		try {
			this.ast = parseIdent();
		} catch(Bailout e) {
			// nothing to do
		}

		if (this.errors.size() > 0) {
			this.printErrors(System.err);
		} else {
			this.ast.print(System.out);
		}
	}

	Token token() {
		return this.scanner.currentToken;
	}

	Token next() {
		return this.scanner.nextToken;
	}

	void advance() throws Bailout {
		try {
			this.scanner.next();
		} catch(IOException e) {
			fatal(e.toString());
		}
	}

	Node parseIdent() throws Bailout {
		this.expect(Token.IDENT);
		Node ident = new Ident(token());
		this.advance();
		return ident;
	}

	// expect the current token to be of type tokenType,
	// fatal error if not.
	void expect(int tokenType) throws Bailout {
		if (this.token().type != tokenType) {
			this.fatal(this.token().pos() + ": expected" + Token.typeName(tokenType));
		}
	}

	void fatal(String error) throws Bailout {
		this.errors.add(error);
		bailout();
	}

	void printErrors(PrintStream out) {
		for(String err: this.errors) {
			out.println(err);
		}
	}

	static void bailout() throws Bailout {
		throw new Bailout();
	}
}

interface Node {
	void print(PrintStream out);
}

abstract class AbsNode {
	Token token;
	AbsNode(Token token) {
		this.token = token;
	}
}

final class Ident extends AbsNode implements Node {
	Ident(Token t) {
		super(t);
	}
	public void print(PrintStream out) {

	}
}

// Bailout is throw internally to abort parsing on a fatal error.
final class Bailout extends Throwable {
	private static final long serialVersionUID = 1L; // sigh
	Bailout() {
		super("parser bailout");
	}
}
