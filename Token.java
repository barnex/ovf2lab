
final class Token {

	String value;  // string representation of the token
	int type;      // token type
	String file;   // file token appeared in
	int line, pos; // positions in file

	// token types
	static final int INVALID = 0;
	static final int EOF = 1;
	static final int EOL = 2;
	static final int IDENT = 3;
	static final int NUMBER = 4;
	static final int STRING = 5;
	static final int CHAR = 6;
	static final int LPAREN = 7;
	static final int RPAREN = 8;
	static final String[] typeName = {"invalid character", "EOF", "EOL", "identifier", "number", "string", "character", "left paren", "right paren"};


	public static String typeName(int type) {
		if (type < 0 || type >= typeName.length) {
			return "UNKNOWN(" + type + ")";
		}
		return typeName[type];
	}

	public String pos() {
		return this.file + ":" + this.line + ":" + this.pos;
	}

	public String toString() {
		return Token.typeName(this.type) + ": " + this.value;
	}
}
