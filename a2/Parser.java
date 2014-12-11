package a2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

// Parser transforms an input file into an Abstract Syntax Tree (AST).
public final class Parser {

	/** Parses the contents read from in.
	    filename only serves to report file:line positions. */
	public static Node parse(String filename, InputStream in) throws IOException, Error {
		Parser p = new Parser(filename, in);
		return p.parseScript();
	}

	/** Parses a single source line, for interactive interpreter. */
	public static Node parseLine(String line) throws Error {
		if (line == null){
			throw new NullPointerException();
		}
		line = line + ";";
		InputStream in = new ByteArrayInputStream(line.getBytes());
		try {
			Parser p = new Parser("stdin", in);
			return p.parseScript();
		} catch(IOException e) {
			// ByteArrayInputStream should not throw IOException
			panic(e.toString());
			return null;
		}
	}

	Scanner scanner;
	Token token, next; // current and next (peeked) token

	private Parser(String filename, InputStream in) throws IOException {
		this.scanner = new Scanner(filename, new InputStreamReader(in));
		this.token = this.scan();
		this.next = this.scan();
	}


	// Parsing

	// parse script file, as if we're inside a block statement
	Node parseScript() throws Error {
		BlockStmt l = new BlockStmt(line()) ;
		skipEOL();
		while (token.type != Token.EOF) {
			l.add(parseStmt());
			expect(Token.EOL);
			skipEOL();
		}
		return l;
	}

	// parse a block statement
	Node parseBlockStmt() throws Error {
		BlockStmt l = new BlockStmt(line()) ;
		consume(Token.LBRACE);
		while (token.type != Token.RBRACE) {
			l.add(parseStmt());
			consume(Token.EOL);
		}
		consume(Token.RBRACE);
		return l;
	}

	// parse a statement
	Node parseStmt() throws Error {
		skipEOL();
		if(this.token.type == Token.LBRACE) {
			return parseBlockStmt();
		}
		Node expr = parseExpr();
		if (this.token.type == Token.ASSIGN) {
			AssignStmt ass = new AssignStmt(line(), token.value);
			ass.lhs = expr;
			advance(); // consume operator
			ass.rhs = parseExpr();
			return ass;
		}
		if (this.token.type == Token.COLONEQUALS) {
			// TODO
		}
		if (this.token.type == Token.POSTFIX) {
			PostfixStmt s = new PostfixStmt(line(), expr, token.value);
			advance(); // consume postfix operator
			return s;
		}
		return expr;
	}

	// parse a compound expression, honor operator precedence.
	Node parseExpr() throws Error {
		// make list of operands and operators, left to right
		// operators do not have children set yet.
		ArrayList<Node> l = new ArrayList<Node>();
		l.add(parseOperand());
		while (this.token.type == Token.BINOP) {
			l.add(new BinOp(line(), token.value));
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
	Node parseOperand() throws Error {
		if (token.type == Token.NUMBER) {
			return parseNumber();
		}
		Node expr = null;
		if (token.type == Token.IDENT) {
			expr = parseIdent();
		} else if (token.type == Token.LPAREN) {
			expr = parseParenthesizedExpr();
		}

		// append successive function calls, e.g.: f(a)(b)(c)
		while (token.type == Token.LPAREN) {
			CallExpr call = new CallExpr(line(), expr);
			call.args = parseArgList();
			expr = call;
		}
		if (expr == null) {
			error("expected operand, found: " + token);
		}
		return expr;
	}

	// parse argument list (arg1, arg2, ...)
	Node[] parseArgList() throws Error {
		consume(Token.LPAREN);

		ArrayList<Node>args = new ArrayList<Node>();

		// TODO: a bit messy, could use a re-write
		for (;;) {
			if (this.token.type == Token.RPAREN) {
				advance();
				Node[] a = new Node[args.size()];
				for(int i=0; i<a.length; i++) {
					a[i]=args.get(i);
				}
				return a;
			}
			args.add(parseExpr());
			if (token.type != Token.RPAREN) {
				consume(Token.COMMA);
				if(token.type == Token.RPAREN) {
					error("unexpected )");
				}
			}
		}
	}

	// parse a parenthesized expression
	Node parseParenthesizedExpr() throws Error {
		consume(Token.LPAREN);
		Node inside = parseExpr();
		consume(Token.RPAREN);
		return inside;
	}

	// parse identifier
	Node parseIdent() throws Error {
		expect(Token.IDENT);
		Node ident = new Ident(line(), token.value);
		advance();
		return ident;
	}

	// parse number
	Node parseNumber() throws Error {
		try {
			long v = Long.parseLong(token.value);
			Node n = new IntLit(line(), v);
			advance();
			return n;
		} catch(NumberFormatException e) {
			// it's not an int, try float
		}

		try {
			double v = Double.parseDouble(token.value);
			Node n = new FloatLit(line(), v);
			advance();
			return n;
		} catch(NumberFormatException e) {
			// it's not a float either, so it's not a valid number
		}

		error("malformed number: " + token);
		return null;
	}


	// Tokenizing

	String pos() {
		return scanner.pos();
	}

	int line() {
		return scanner.line();
	}

	// Advances by one token.
	void advance() throws Error {
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

	// check that we are at a token with type,
	// and adance
	void consume(int tokenType) throws Error {
		expect(tokenType);
		advance();
	}

	// scan next token but skip comments
	Token scan() throws IOException {
		Token t = this.scanner.next();
		while (t.type == Token.COMMENT) {
			t = this.scanner.next();
		}
		return t;
	}

	void skipEOL() throws Error {
		while (token.type == Token.EOL) {
			advance();
		}
	}

	// Error handling

	// expect the current token to be of type tokenType,
	// fatal error if not.
	void expect(int tokenType) throws Error {
		if (this.token.type != tokenType) {
			error("expected " + Token.typeName(tokenType) + ", found: " + this.token.value);
		}
	}

	// add error with position information of current token + msg.
	void error(String msg) throws Error {
		throw new Error(pos() + ": " + msg);
	}

	// exit with compiler bug
	static void panic(String msg) {
		System.err.println(msg);
		System.exit(3);
	}

	// print indent number of tabs (used by Node.print)
	static void printIndent(PrintStream out, int indent) {
		for(int i=0; i<indent; i++) {
			out.print('\t');
		}
	}
}


