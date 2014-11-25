import java.io.Reader;
import java.io.IOException;
import java.lang.Character;

// Scanner tokenizes input text.
final class Scanner {

	Reader in;
	int current, next; // current and next character
	StringBuilder buf;
	Token token;


	Scanner(Reader in) throws IOException {
		this.in = in;
		this.advance(); // sets next, not yet current
		this.advance();
		this.buf = new StringBuilder();
	}

	// next parses and returns the next token in the stream.
	// After an EOF has been emitted, subsequent calls return null.
	void next() throws IOException {
		if (this.current == -1 && this.next == -1) {
			this.token = null; // we're past EOF
			return;
		}

		this.token = new Token();
		this.buf.setLength(0);
		scanToken();
		this.token.value = buf.toString();
	}

	// scan the token staring at current position,
	// append value to buf but not yet to token.value;
	private void scanToken() throws IOException {
		this.skipWhitespace();

		if (this.current == -1) {
			this.token.type = Token.EOF;
			return;
		}

		if (isLinebreak(this.current)) {
			this.token.type = Token.EOL;
			this.consumeEOL();
			return;
		}

		if (isAlpha(this.current)) {
			this.token.type = Token.WORD;
			this.consumeWord();
			return;
		}

		if (isNum(this.current)) {
			this.token.type = Token.NUMBER;
			this.consumeNumber();
			return;
		}

		// else:
		this.consumeChar();
	}



	// advances current, next by one character.
	void advance() throws IOException {
		this.current = this.next;
		this.next = this.in.read();
	}

	// after skipWhitespace, the current character is not whitespace.
	void skipWhitespace() throws IOException {
		while (isWhitespace(this.current)) {
			this.advance();
		}
	}

	// consume an "\n" or "\r\n", append to token.value
	void consumeEOL() throws IOException {
		if (this.current == '\r') {
			this.consumeChar();
		}
		if (this.current == '\n') {
			this.consumeChar();
			return;
		}
		panic("not at EOL");
	}

	// consume a number, including exponential notation.
	// Malformed input is not caught here, but will cause
	// a parse error.
	void consumeNumber()throws IOException {
		this.consumeDigits();
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

	void consumeChar() throws IOException {
		if (!isChar(this.current)) {
			panic("not a char");
		}
		this.append((char)(this.current));
		this.advance();
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
		return isChar(c) && Character.isLetter((char)(c));
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


