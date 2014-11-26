import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

final class Parser {

	Scanner scanner;
	Node ast;
	ArrayList<String> errors;

	public void parseFile(String filename) throws FileNotFoundException, IOException {
		Reader reader = new FileReader(new File(filename));
		this.scanner = new Scanner(filename, reader);
		this.errors = new ArrayList<String>();

		try {
			parse();
		} catch(Bailout e) {
			// nothing to do
		}
	}

	// Parsing

	void parse() throws Bailout{
		this.ast = parseExpr();
	}

	Node parseExpr() throws Bailout{
		if (token().type == Token.NUMBER){
			return parseNumber();
		}
		
		error("expecting expression, found: " + token());
		return null;
	}

	Node parseNumber() throws Bailout{
		try{
			long v = Long.parseLong(token().value);
			return new IntLit(v);
		}catch(NumberFormatException e){
			// so it's not an int
		}

		try{
			double v = Double.parseDouble(token().value);
			return new FloatLit(v);
		}catch(NumberFormatException e){
			// so it's not a float
		}

		error("malformed number: " + token());
		return null;
	}

	Node parseIdent() throws Bailout {
		this.expect(Token.IDENT);
		Node ident = new Ident(token());
		this.advance();
		return ident;
	}

	// Tokenizing

	// Returns the current token.
	Token token() {
		return this.scanner.currentToken;
	}

	// Peeks the next token.
	Token next() {
		return this.scanner.nextToken;
	}

	// Advances by one token.
	void advance() throws Bailout {
		try {
			this.scanner.next();
		} catch(IOException e) {
			this.error(e.toString());
		}
	}

	// Error handling

	// expect the current token to be of type tokenType,
	// fatal error if not.
	void expect(int tokenType) throws Bailout {
		if (this.token().type != tokenType) {
			this.error("expected " + Token.typeName(tokenType) + ", found: " + this.token().value);
		}
	}

	// add error with position information of current token + msg.
	void error(String msg) throws Bailout{
		this.errors.add(this.token().pos() + ": " + msg);
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
	Token token;
	AbsNode(Token token) {
		this.token = token;
	}
}

class IntLit extends AbsNode implements Node{
	long value;
	IntLit(long value){
		this.value = value;
	}
	public void print(PrintStream out) {
		out.println(this.value);
	}
}

class FloatLit extends AbsNode implements Node{
	double value;
	FloatLit(double value){
		this.value = value;
	}
	public void print(PrintStream out) {
		out.println(this.value);
	}
}

class Ident extends AbsNode implements Node {
	Ident(Token t) {
		super(t);
	}
	public void print(PrintStream out) {
		out.println(super.token.value);
	}
}

// Bailout is throw internally to abort parsing on a fatal error.
final class Bailout extends Throwable {
	private static final long serialVersionUID = 1L; // sigh
	Bailout() {
		super("parser bailout");
	}
}
