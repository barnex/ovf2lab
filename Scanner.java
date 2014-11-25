import java.io.Reader;
import java.io.IOException;
import java.lang.Character;

// Scanner tokenizes input text.
final class Scanner {

    Reader in;
    int current, next; // current and next character
	Stringbuilder buf;
	String tval;

	// token types
    static final int TT_INVALID = 0;
    static final int TT_EOF = 1;
    static final int TT_EOL = 2;
    static final int TT_WORD = 3;
    static final int TT_NUMBER = 4;
    static final int TT_LPAREN = 5;
    static final int TT_RPAREN = 6;
    //static final String[] ttName = {"INVALID", "EOF", "EOL", "WORD", "NUMBER", "(", ")"};

    Scanner(Reader in) throws IOException {
        this.in = in;
        this.advance(); // sets next, not yet current
    }

    // next parses and returns the next token in the stream.
    // After an EOF has been emitted, subsequent calls return null.
    void next() throws IOException {
        this.advance();

        if (this.current == -1 && this.next == -1) {
            this.token = null; // we're past EOF
            return;
        }

        this.token = new Token();

		this.skipWhitespace();

        // check EOF
        if (this.current == -1) {
            this.token.type = Token.EOF;
            return;
        }

		if (isLinebreak(this.current)){
			this.token.type = Token.EOL;
			this.skipCRLF();
		}

        //if (Character.isLetter(ch)) {
        //    tok.type = Token.WORD;
        //    tok.val = "" + ch;

        //    while (isAlphaNum(this.peek())) {
        //        tok.val += (char)(this.read());
        //    }
        //    return tok;
        //}

        this.token.val = "" + (char)(this.current);
    }



    // advances current, next by one character.
    void advance() throws IOException {
        this.current = this.next;
        this.next = this.in.read();
    }

	// after skipWhitespace, the current character is not whitespace.
	void skipWhitespace() throws IOException{
		while (isWhitespace(this.current)){
			this.advance();
		}
	}

	// when at a CR, also consume the LF
	void skipCRLF() throws IOException{
		if (this.current == '\r' && this.next == '\n'){
			this.advance();
		}
	}

    static boolean isAlphaNum(int c) {
        if (c < 0) {
            return false;
        }
        return Character.isLetterOrDigit((char)(c));
    }

	static boolean isWhitespace(int c){
		if (c == -1 || c == '\r' || c == '\n') {return false;}
		return Character.isWhitespace((char)(c));	
	}

	static boolean isLinebreak(int c){
		return (c == '\n' || c == '\r');
	}
}


