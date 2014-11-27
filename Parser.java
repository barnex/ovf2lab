import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

final class Parser {

	Scanner scanner;
	Token token, next; // current and next (peeked) token
	Node ast;
	ArrayList<String> errors;

	public void parseFile(String filename) throws FileNotFoundException, IOException {
		Reader reader = new FileReader(new File(filename));
		this.scanner = new Scanner(filename, reader);
		this.token = this.scanner.currentToken;
		this.next = this.scanner.nextToken;
		this.errors = new ArrayList<String>();

		try {
			parse();
		} catch(Bailout e) {
			// nothing to do
		}
	}

	// Parsing

	void parse() throws Bailout {
		this.ast = parseExprList();  // TODO: blockstmt
	}

	Node parseExprList() throws Bailout {
		ExprList l = new ExprList(token) ;
		for (; token.type != Token.EOF; advance()) {
			l.add(parseExpr());
		}
		return l;
	}

	Expr parseExpr() throws Bailout {
		while (token.type == Token.EOL) {
			advance();
		}

		if (token.type == Token.NUMBER) {
			return parseNumber();
		}
		if (token.type == Token.IDENT) {
			return parseIdent();
		}

		error("expecting expression, found: " + token);
		return null;
	}

	Expr parseIdent() throws Bailout {
		expect(Token.IDENT);
		Expr ident = new Ident(token, token.value);
		advance();
		return ident;
	}

	Expr parseNumber() throws Bailout {
		try {
			long v = Long.parseLong(token.value);
			Expr n = new IntLit(token, v);
			advance();
			return n;
		} catch(NumberFormatException e) {
			// it's not an int, try float
		}

		try {
			double v = Double.parseDouble(token.value);
			Expr n = new FloatLit(token, v);
			advance();
			return n;
		} catch(NumberFormatException e) {
			// it's not a float either, so it's not a valid number
		}

		error("malformed number: " + token);
		return null;
	}


	// Tokenizing

	// Advances by one token.
	void advance() throws Bailout {
		try {
			this.scanner.next();
			this.token = this.scanner.currentToken;
			this.next = this.scanner.nextToken;
		} catch(IOException e) {
			error(e.toString());
		}
	}

	// Error handling

	// expect the current token to be of type tokenType,
	// fatal error if not.
	void expect(int tokenType) throws Bailout {
		if (this.token.type != tokenType) {
			error("expected " + Token.typeName(tokenType) + ", found: " + this.token.value);
		}
	}

	// add error with position information of current token + msg.
	void error(String msg) throws Bailout {
		this.errors.add(this.token.pos() + ": " + msg);
		bailout();
	}

	// Throw Bailout, stop parsing.
	static void bailout() throws Bailout {
		throw new Bailout();
	}

	public void printErrors(PrintStream out) {
		for(String err: this.errors) {
			out.println(err);
		}
	}
}

// ast

interface Node {
	void print(PrintStream out);
}

abstract class AbsNode {

	String file;
	int line, pos;

	AbsNode(Token token) {
		this.file = token.file;
		this.line = token.line;
		this.pos = token.pos;
	}
}

interface Expr {
	void print(PrintStream out);
	// eval()
}

class ExprList extends AbsNode implements Node {
	ArrayList<Expr> list;
	public ExprList(Token t) {
		super(t);
		this.list = new ArrayList<Expr>();
	}
	void add(Expr e) {
		this.list.add(e);
	}
	public void print(PrintStream out) {
		for(Expr e: this.list) {
			e.print(out);
			out.println();
		}
	}
}

class Ident extends AbsNode implements Expr, Node {
	String name;
	Ident(Token t, String name) {
		super(t);
		this.name = name;
	}
	public void print(PrintStream out) {
		out.print(this.name);
	}
}

class IntLit extends AbsNode implements Expr, Node {
	long value;
	IntLit(Token t, long value) {
		super(t);
		this.value = value;
	}
	public void print(PrintStream out) {
		out.print(this.value);
	}
}

class FloatLit extends AbsNode implements Expr, Node {
	double value;
	FloatLit(Token t, double value) {
		super(t);
		this.value = value;
	}
	public void print(PrintStream out) {
		out.print(this.value);
	}
}


// Bailout is throw internally to abort parsing on a fatal error.
final class Bailout extends Throwable {
	private static final long serialVersionUID = 1L; // sigh
	Bailout() {
		super("parser bailout");
	}
}
