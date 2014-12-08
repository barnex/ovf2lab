import java.io.Reader;
import java.io.IOException;
import java.lang.Character;

// Scanner tokenizes input text.
final class Scanner {

	Reader in;               // input stream
	String file;             // file name to document token position
	int current, next;       // current and next character
	int startLine, lastLine; //
	StringBuilder buf;       // builds token value

	// Constructs a scanner that tokenizes the content delivered by in.
	// filename is used to report token positions
	Scanner(String filename, Reader in) throws IOException {
		this.in = in;
		this.buf = new StringBuilder();
		this.file = filename;
		this.lastLine = 1;
		this.advance(); // set up current, next character
		this.advance();
	}

	// next() advances currentToken, nextToken by one token.
	Token next() throws IOException {
		startLine = lastLine;
		skipWhitespace();

		Token t = new Token();

		buf.setLength(0);
		t.type = scanToken();

		t.value = buf.toString();
		if(t.type == Token.EOL) {
			t.value = "\\n";
		}

		return t;
	}

	String pos() {
		return file + ":" + line();
	}

	int line() {
		return startLine;
	}

	// scan the token staring at current position,
	// append value to buf but not yet to token.value.
	// returns the token type.
	int scanToken() throws IOException {
		// EOF
		if (current == -1) {
			return Token.EOF;
		}
		// EOL
		if (isEOL(current) || current == ';') {
			consumeEOL();
			return Token.EOL;
		}
		// slash slash comment
		if (current == '/' && next == '/') {
			consumeLine();
			return Token.COMMENT;
		}
		// identifier
		if (isAlpha(current)) {
			consumeWord();
			return Token.IDENT;
		}
		// number literal
		if (isNum(current) || (current == '.' && isNum(next))) {
			consumeNumber();
			return Token.NUMBER;
		}
		// string literal
		if (current == '"') {
			consumeQuotedString();
			return Token.STRING;
		}
		// parenthesis
		if (current == '(') {
			consumeChar();
			return Token.LPAREN;
		}
		if (current == ')') {
			consumeChar();
			return Token.RPAREN;
		}
		if (current == '{') {
			consumeChar();
			return Token.LBRACE;
		}
		if (current == '}') {
			consumeChar();
			return Token.RBRACE;
		}
		// comma
		if (current == ',') {
			consumeChar();
			return Token.COMMA;
		}
		// declare-assign ':='
		if (current == ':' && next == '=') {
			consumeChar();
			consumeChar();
			return Token.COLONEQUALS;
		}
		// assign: =
		if (current == '=' && next != '=') {
			consumeChar();
			return Token.ASSIGN;
		}
		// binop ==
		if (current == '=' && next == '=') {
			consumeChar();
			consumeChar();
			return Token.BINOP;
		}
		// binop !=
		if (current == '!' && next == '=') {
			consumeChar();
			consumeChar();
			return Token.ASSIGN;
		}
		// assign: +=, -=, *=, /=, %=, ^=
		if (match(current, "+-*/%^") && next == '=') {
			consumeChar();
			consumeChar();
			return Token.ASSIGN;
		}
		// postfix: ++, --
		if (match(current, "+-") && (next == current)) {
			consumeChar();
			consumeChar();
			return Token.POSTFIX;
		}
		// bin ops: +, -, *, /, %, ^
		if (match(current, "+-*/%^") && next != '=') {
			consumeChar();
			return Token.BINOP;
		}
		// bin ops: &, |, &&, ||
		if (match(current, "&|")) {
			// collate &&, ||, ==
			if(current == next) {
				consumeChar();
			}
			consumeChar();
			return Token.BINOP;
		}
		// bin ops: <, >, >=, <=
		if(match(current, "<>")) {
			consumeChar();
			// collate >=, <=
			if (current == '=') {
				consumeChar();
			}
			return Token.BINOP;
		}

		// else: unsupported character
		consumeChar();
		return Token.INVALID;
	}

	// advances current and next characters by one.
	void advance() throws IOException {
		if (this.current == '\n') {
			this.lastLine++;
		}
		this.current = this.next;
		this.next = this.in.read();
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
		this.consumeDigits();
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
				i = -1; // becomes 0 next the line
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


