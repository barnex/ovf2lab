import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

// Parser transforms an input file into an Abstract Syntax Tree (AST).
final class Parser {

	public Node ast;                  // contains the AST root after successful parsing
	public ArrayList<String> errors;  // contains syntax errors after parse error

	Scanner scanner;
	Token token, next; // current and next (peeked) token
	boolean debug = false;


	public void parseFile(String filename) throws FileNotFoundException, IOException {
		Reader reader = new FileReader(new File(filename));
		this.scanner = new Scanner(filename, reader);
		this.token = this.scan();
		this.next = this.scan();
		this.errors = new ArrayList<String>();

		// Normal parsing throws Bailout exception upon syntax error,
		// catch it and add to errors list.
		try {
			parse();
		} catch(Bailout e) {
			// with debug: print where bailout came from
			if (debug) {
				printErrors(System.err);
				e.printStackTrace();
			}
			// bailout without reporting an error is a bug
			if (this.errors.size() == 0) {
				this.errors.add("BUG: parser bailout at " + token.pos());
				e.printStackTrace();
				System.exit(2);
			}
			this.ast = null;
			// nothing to do, errors are this.errors.
		}
	}

	// Parsing

	void parse() throws Bailout {
		this.ast = parseExprList();  // TODO: blockstmt
	}

	Node parseExprList() throws Bailout {
		ExprList l = new ExprList(token) ;
		skipEOL();
		while (token.type != Token.EOF) {
			l.add(parseExpr());
			expect(Token.EOL);
			skipEOL();
		}
		return l;
	}

	// operators ordered by precedence, for parseExpr.
	static final String[][] precedence = {
		{"^"},
		{"*",  "/",  "%",  "<<",  ">>",  "&"},
		{"+",  "-" , "|"},
		{"==",  "!=",  "<",  "<=",  ">",  ">=" },
		{"&&"},
		{"||"},
		{"=", "+=", "-=", "*=", "/=", "%=", "^="}
	};

	// parse a compound expression, honor operator precedence.
	Expr parseExpr() throws Bailout {
		// make list of operands and operators, left to right
		// operators do not have children set yet.
		ArrayList<Expr> l = new ArrayList<Expr>();
		l.add(parseOperand());
		while (this.token.type == Token.BINOP) {
			l.add(new BinOp(this.token, this.token.value));
			this.advance();
			l.add(parseOperand());
		}

		// fast return if there's no operators
		if (l.size() == 1) {
			return l.get(0);
		}

		// in order of precedence, have each operator eat up his left and right neighbor
		for (int pr=0; pr<precedence.length; pr++) {
			for (String op: precedence[pr]) {
				for (int i=0; i<l.size(); i++) {
					Expr e = l.get(i);
					if (e instanceof BinOp && ((BinOp)(e)).op.equals(op)) {
						((BinOp)(e)).x = l.get(i-1);
						((BinOp)(e)).y = l.get(i+1);
						l.remove(i-1);
						l.remove(i); //remove element i+1, now at pos i
						i--; //
					}
				}
			}
		}

		// unless precedence list is incomplete, there should be no more operators left
		assert(l.size() == 1);
		return l.get(0);
	}


	// parses operand expression, stops at binary operator (+,-,*,...)
	Expr parseOperand() throws Bailout {
		if (token.type == Token.NUMBER) {
			return parseNumber();
		}
		if (token.type == Token.IDENT) {
			return parseIdent();
		}
		if (token.type == Token.LPAREN) {
			return parseParenthesizedExpr();
		}
		error("expected operand, found: " + this.token);
		return null;
	}

	// parse a parenthesized expression
	Expr parseParenthesizedExpr() throws Bailout {
		expect(Token.LPAREN);
		advance();
		Expr inside = parseExpr();
		expect(Token.RPAREN);
		advance();
		return inside;
	}

	// parse identifier
	Expr parseIdent() throws Bailout {
		expect(Token.IDENT);
		Expr ident = new Ident(token, token.value);
		advance();
		return ident;
	}

	// parse number
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
			this.token = this.next;
			this.next = this.scan();
		} catch(IOException e) {
			error(e.toString());
		}
		if (token.type == Token.INVALID) {
			error("invalid character: " + token.value);
		}
	}

	// scan next token but skip comments
	Token scan() throws IOException {
		Token t = this.scanner.next();
		while (t.type == Token.COMMENT) {
			t = this.scanner.next();
		}
		return t;
	}

	void skipEOL() throws Bailout {
		while (token.type == Token.EOL) {
			advance();
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

class BinOp extends AbsNode implements Expr, Node {
	String op;
	Expr x, y;
	BinOp(Token t, String op) {
		super(t);
		this.op = op;
	}
	public void print(PrintStream out) {
		out.print("(");
		this.x.print(out);
		out.print(this.op);
		this.y.print(out);
		out.print(")");
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
