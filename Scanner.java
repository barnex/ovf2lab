import java.io.Reader;
import java.io.IOException;
import java.lang.Character;

// Scanner tokenizes input text.
final class Scanner {

    Reader in;
    int peeked;                                  // next character
    private static final int UNINITIALIZED = -2; // nothing peeked yet
    boolean eof;                                 // EOF emitted?

    Scanner(Reader in) {
        this.in = in;
        this.peeked = UNINITIALIZED;
    }

    // next parses and returns the next token in the stream.
    // After an EOF has been emitted, subsequent calls return null.
    Token next() throws IOException {
        if (this.eof) {
            return null;
        }

        Token tok = new Token();
        int c = this.read();

        // skip leading whitespace
        while (c!=-1 && Character.isWhitespace((char)(c))) {
            c = this.read();
        }

        // check EOF
        if (c == -1) {
            tok.type = Token.EOF;
            this.eof = true;
            return tok;
        }

        char ch = (char)(c);
        if (Character.isLetter(ch)) {
            tok.type = Token.WORD;
            tok.val = "" + ch;

            while (isAlphaNum(this.peek())) {
                tok.val += (char)(this.read());
            }
            return tok;
        }

        tok.val = "" + ch;
        return tok;
    }

    static boolean isAlphaNum(int c) {
        if (c < 0) {
            return false;
        }
        return Character.isLetterOrDigit((char)(c));
    }


    // Next returns the next character, or -1 if end of file has been reached.
    int read() throws IOException {
        int next = this.peek();
        this.peeked = this.in.read();
        return next;
    }

    // Peek returns the character that will be returned by next(),
    // without advancing in the stream.
    int peek() throws IOException {
        if (this.peeked == UNINITIALIZED) {
            this.peeked = this.in.read();
        }
        return this.peeked;
    }
}


final class Token {
    int type;
    String val;

    static final int INVALID = 0;
    static final int EOF = 1;
    static final int EOL = 2;
    static final int WORD = 3;
    static final int NUMBER = 4;
    static final int LPAREN = 5;
    static final int RPAREN = 6;
    static final String[] typeName = {"INVALID", "EOF", "EOL", "WORD", "NUMBER", "(", ")"};

    Token() {
        this.val = "";
    }

    public String toString() {
        if (this.type >= typeName.length) {
            return "INVALID:" + this.val;
        }
        return typeName[this.type] + " " + this.val;
    }
}
