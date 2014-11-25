import java.io.Reader;
import java.io.IOException;
import java.lang.Character;

// Scanner tokenizes input text.
final class Scanner {

	public Token currentToken, nextToken; // current and next (peeked) token.

	private Reader in;         // input stream
	private String filename;   // file name to document token position
	private int line, pos;     // token line and position in file
	private int current, next; // current and next character
	private StringBuilder buf; // builds token value

	// Constructs a scanner that tokenizes the content delivered by in.
	// filename is used to report token positions
	Scanner(String filename, Reader in) throws IOException {
		this.in = in;
		this.buf = new StringBuilder();
		this.filename = filename;
		this.advance(); // sets next, not yet current
		this.advance();
		this.pos = 1;
		this.line = 1;
		this.next();
	}

	// next() advances currentToken, nextToken by one token.
	// After an EOF has been emitted, subsequent tokens are null.
	void next() throws IOException {
		Token t = null;
		this.buf.setLength(0);

		if (this.current == -1 && this.next == -1) {
			t = null;
		} else {
			t = new Token();
			this.skipWhitespace();
			int line = this.line;
			int pos = this.pos;
			t.type = scanToken();
			t.value = buf.toString();
			t.line = line;
			t.pos = pos;
			t.file = this.filename;
		}

		this.currentToken = nextToken;
		this.nextToken = t;
	}

	// scan the token staring at current position,
	// append value to buf but not yet to token.value.
	// returns the token type.
	private int scanToken() throws IOException {
		if (this.current == -1) {
			return Token.EOF;
		}

		if (isLinebreak(this.current)) {
			this.consumeEOL();
			return Token.EOL;
		}

		if (isAlpha(this.current)) {
			this.consumeWord();
			return Token.WORD;
		}

		if (isNum(this.current) || (this.current == '.' && isNum(this.next))) {
			this.consumeNumber();
			return Token.NUMBER;
		}

		// else:
		this.consumeChar();
		return Token.INVALID; // TODO
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

	// is c a letter?
	static boolean isAlpha(int c) {
		return isChar(c) && (Character.isLetter((char)(c)) || c == '_');
	}

	// is c a digit ?
	static boolean isNum(int c) {
		return isChar(c) && Character.isDigit((char)(c));
	}

	// is c whitespace?
	static boolean isWhitespace(int c) {
		if (c == -1 || c == '\r' || c == '\n') {
			return false;
		}
		return Character.isWhitespace((char)(c));
	}

	// is c a linebreak?
	static boolean isLinebreak(int c) {
		return (c == '\n' || c == '\r');
	}

	// panic on internal error (bug)
	static void panic(String msg) throws IllegalStateException {
		throw new IllegalStateException(msg);
	}
}


