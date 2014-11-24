import java.io.Reader;
import java.io.FileReader;
import java.io.File;

public final class A2 {
    public static void main(String[] args) {

        for (String f:args) {
            Reader in;
            try {
                in = new FileReader(new File(f));
            } catch(Exception e) {
                fatal(e);
                return;
            }
            tokenize(in);
        }

    }


    public static void tokenize(Reader in) {

    }

    public static void fatal(Exception e) {
        System.err.println(e);
        System.exit(1);
    }
}
