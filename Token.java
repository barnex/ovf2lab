final class Token {

	String value; // string representation of the token
	int type;     // token type
	String file;  // file token appeared in
	int line, pos;      // positions in file

	// token types
	static final int INVALID = 0;
	static final int EOF = 1;
	static final int EOL = 2;
	static final int WORD = 3;
	static final int NUMBER = 4;
	static final int LPAREN = 5;
	static final int RPAREN = 6;
	static final String[] typeName = {"INVALID", "EOF", "EOL", "WORD", "NUMBER", "(", ")"};


	String typeName() {
		if (this.type < 0 || this.type >= typeName.length) {
			return "UNKNOWN(" + this.type + ")";
		}
		return typeName[this.type];
	}

	public String toString() {
		return this.file + ":" + this.line + ":" + this.pos + ": " + this.typeName() + ":" + this.value;
	}
}
