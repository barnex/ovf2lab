import java.io.Reader;
import java.io.IOException;
import java.lang.Character;

// Scanner tokenizes input text.
final class Scanner {

	Reader in;         // input stream
	String filename;   // file name to document token position
	int line, pos;     // token line and position in file
	int current, next; // current and next character
	StringBuilder buf; // builds token value

	// Constructs a scanner that tokenizes the content delivered by in.
	// filename is used to report token positions
	Scanner(String filename, Reader in) throws IOException {
		this.in = in;
		this.buf = new StringBuilder();
		this.filename = filename;
		this.advance(); // set up current, next character
		this.advance();
		this.pos = 1;
		this.line = 1;
	}

	// next() advances currentToken, nextToken by one token.
	public Token next() throws IOException {
		this.skipWhitespace();

		Token t = new Token();
		t.line = this.line;
		t.pos = this.pos;
		t.file = this.filename;

		this.buf.setLength(0);
		t.type = scanToken();

		t.value = buf.toString();
		if(t.type == Token.EOL) {
			t.value = "\\n";
		}

		return t;
	}

	// scan the token staring at current position,
	// append value to buf but not yet to token.value.
	// returns the token type.
	int scanToken() throws IOException {
		if (this.current == -1) {
			return Token.EOF;
		}
		if (isEOL(this.current) || this.current == ';') {
			this.consumeEOL();
			return Token.EOL;
		}
		if (this.current == '/' && this.next == '/') {
			this.consumeLine();
			return Token.COMMENT;
		}
		if (isAlpha(this.current)) {
			this.consumeWord();
			return Token.IDENT;
		}
		if (isNum(this.current) || (this.current == '.' && isNum(this.next))) {
			this.consumeNumber();
			return Token.NUMBER;
		}
		if (this.current == '"') {
			this.consumeQuotedString();
			return Token.STRING;
		}
		if (this.current == '(') {
			this.consumeChar();
			return Token.LPAREN;
		}
		if (this.current == ')') {
			this.consumeChar();
			return Token.RPAREN;
		}
		if (this.current == '=') {
			this.consumeChar();
			return Token.ASSIGN;
		}
		if (match(this.current, "+-*/%^")) {
			this.consumeChar();
			// collate +=, -=, *=, /=, ^=
			if (this.current == '=') {
				this.consumeChar();
				return Token.ASSIGN;
			}
			return Token.BINOP;
		}
		if (match(this.current, "&|=")) {
			// collate &&, ||, ==
			if(this.current == this.next) {
				this.consumeChar();
			}
			this.consumeChar();
			return Token.BINOP;
		}
		if(match(this.current, "<>")) {
			this.consumeChar();
			// collate >=, <=
			if (this.current == '=') {
				this.consumeChar();
			}
			return Token.BINOP;
		}

		// else: unsupported character
		this.consumeChar();
		return Token.INVALID;
	}

	// advances current and next characters by one.
	void advance() throws IOException {
		this.current = this.next;
		this.next = this.in.read();
		this.pos++;
	}

	// after skipWhitespace, the current character is not whitespace.
	void skipWhitespace() throws IOException {
		while (isWhitespace(this.current)) {
			this.advance();
		}
	}

	// consume entire line
	void consumeLine() throws IOException {
		// take care to properly consume both LF and CRLF endings
		while(!isEOL(this.current)) {
			consumeChar();
		}
		consumeEOL();
	}

	// consume an "\n" or "\r\n", append to token.value
	void consumeEOL() throws IOException {
		boolean consumed = false;
		if (this.current == '\r') {
			this.consumeChar();
			consumed = true;
		}
		if (this.current == '\n') {
			this.consumeChar();
			consumed = true;
		}
		if (consumed) {
			this.line++;
			this.pos = 1;
			return;
		}
		if (this.current == ';') {
			this.consumeChar();
			return;
		}
		panic("not at EOL");
	}

	// consume a number, including exponential notation.
	// Malformed input is not caught here, but will cause
	// a parse error.
	void consumeNumber()throws IOException {
		this.consume("0123456789.");
		if (this.current == 'E' || this.current == 'e') {
			this.consumeChar();
		} else {
			return;
		}

		if (this.current == '-' || this.current == '+') {
			this.consumeChar();
		}
		this.consumeNumber();
	}

	// consumes a quoted string, including the quotes.
	// the consumed value may be an unterminated string,
	// which should be caught as a parse error later.
	void consumeQuotedString() throws IOException {
		this.consumeChar();
		while(this.current != '"' && !isEOL(this.current) && this.current != -1) {
			this.consumeChar();
		}
		if(this.current == '"') {
			this.consumeChar();
		}

	}

	// consume digits 0-9
	void consumeDigits() throws IOException {
		while (isNum(this.current)) {
			this.consumeChar();
		}
	}

	// consume a word of alphanumeric characters
	void consumeWord() throws IOException {
		while (isAlphaNum(this.current)) {
			this.consumeChar();
		}
	}

	// consume the current character
	void consumeChar() throws IOException {
		if (!isChar(this.current)) {
			panic("not a char");
		}
		this.append((char)(this.current));
		this.advance();
	}

	// consume as many characters contained in pattern as possible.
	// return whether at least one character has been consumed.
	// E.g.:
	// 	consume("0123456789.");
	// consumes as many digits and periods as possible.
	boolean consume(String pattern) throws IOException {
		if (!isChar(this.current)) {
			return false;
		}

		boolean consumed = false;
		int i=0;
		while (i<pattern.length()) {
			if (pattern.charAt(i) == this.current) {
				this.consumeChar();
				consumed = true;
				i = 0;
			}
			i++;
		}
		return consumed;
	}

	// returns true if character c occurs in pattern
	static boolean match(int c, String pattern) {
		if (c < 0) {
			return false;
		}
		char chr = (char)(c);
		for (int i=0; i<pattern.length(); i++) {
			if (pattern.charAt(i) == chr) {
				return true;
			}
		}
		return false;
	}

	// append character c to stringbuilder
	void append(int c) {
		if (c < 0) {
			panic("illegal char");
		}
		this.buf.append((char)(c));
	}

	// does c represent a character (not EOL)?
	static boolean isChar(int c) {
		return c > 0;
	}

	// is c alphanumeric?
	static boolean isAlphaNum(int c) {
		return isAlpha(c) || isNum(c);
	}

	// is c a valid letter for an identifier?
	static boolean isAlpha(int c) {
		return isChar(c) && (Character.isLetter((char)(c)) || c == '_');
	}

	// is c a digit ?
	static boolean isNum(int c) {
		return isChar(c) && Character.isDigit((char)(c));
	}

	// is c whitespace?
	static boolean isWhitespace(int c) {
		return match(c, " \t");
	}

	// is c a linebreak?
	static boolean isEOL(int c) {
		return (c == '\n' || c == '\r');
	}

	// panic on internal error (bug)
	static void panic(String msg) throws IllegalStateException {
		throw new IllegalStateException(msg);
	}
}


