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
	boolean debug = true;


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

	// parse the entire file
	void parse() throws Bailout {
		this.ast = parseBlockStmt();
	}

	// parse a block statement
	Node parseBlockStmt() throws Bailout {
		BlockStmt l = new BlockStmt(token) ;
		skipEOL();
		while (token.type != Token.EOF) {
			l.add(parseStmt());
			expect(Token.EOL);
			skipEOL();
		}
		return l;
	}

	// parse a statement
	Node parseStmt() throws Bailout {
		Node expr = parseExpr();
		if (this.token.type == Token.ASSIGN) {
			AssignStmt ass = new AssignStmt(this.token, this.token.value);
			ass.lhs = expr;
			advance(); // consume operator
			ass.rhs = parseExpr();
			return ass;
		}
		if (this.token.type == Token.POSTFIX) {
			PostfixStmt s = new PostfixStmt(this.token, expr, this.token.value);
			advance(); // consume postfix operator
			return s;
		}
		return expr;
	}

	// parse a compound expression, honor operator precedence.
	Node parseExpr() throws Bailout {
		// make list of operands and operators, left to right
		// operators do not have children set yet.
		ArrayList<Node> l = new ArrayList<Node>();
		l.add(parseOperand());
		while (this.token.type == Token.BINOP) {
			l.add(new BinOp(this.token, this.token.value));
			this.advance();
			l.add(parseOperand());
		}

		assert((l.size()-1)%2 == 0);

		// fast return if there's no operators
		if (l.size() == 1) {
			return l.get(0);
		}

		// in order of precedence, have each operator eat up his left and right neighbor
		for (int pr=0; pr<precedence.length; pr++) {
			for (int i=0; i<l.size(); i++) {
				Node e = l.get(i);
				if (!(e instanceof BinOp)) {
					continue;
				}
				BinOp b = (BinOp)(e);
				if (b.x != null) { // binop already connected
					continue;
				}
				for (String op: precedence[pr]) {
					if (b.op.equals(op)) {
						b.x = l.get(i-1);
						b.y = l.get(i+1);
						l.remove(i-1);
						l.remove(i); //remove element i+1, now at pos i
						i=0; // TODO: backtrack
					}
				}
			}
		}

		// unless precedence list is incomplete, there should be no more operators left
		assert(l.size() == 1);
		return l.get(0);
	}

	// operators ordered by precedence, for parseExpr.
	static final String[][] precedence = {
		{"^"},
		{"*",  "/",  "%",  "<<",  ">>",  "&"},
		{"+",  "-" , "|"},
		{"==",  "!=",  "<",  "<=",  ">",  ">=" },
		{"&&"},
		{"||"}
	};



	// parses operand expression, stops at binary operator (+,-,*,...)
	Node parseOperand() throws Bailout {
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
	Node parseParenthesizedExpr() throws Bailout {
		expect(Token.LPAREN);
		advance();
		Node inside = parseExpr();
		expect(Token.RPAREN);
		advance();
		return inside;
	}

	// parse identifier
	Node parseIdent() throws Bailout {
		expect(Token.IDENT);
		Node ident = new Ident(token, token.value);
		advance();
		return ident;
	}

	// parse number
	Node parseNumber() throws Bailout {
		try {
			long v = Long.parseLong(token.value);
			Node n = new IntLit(token, v);
			advance();
			return n;
		} catch(NumberFormatException e) {
			// it's not an int, try float
		}

		try {
			double v = Double.parseDouble(token.value);
			Node n = new FloatLit(token, v);
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


// Bailout is throw internally to abort parsing on a fatal error.
final class Bailout extends Throwable {
	private static final long serialVersionUID = 1L; // sigh
	Bailout() {
		super("parser bailout");
	}
}
