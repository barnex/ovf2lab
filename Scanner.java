import java.io.Reader;
//import java.io.IOException;

final class Scanner {

    Scanner(Reader in) {

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
