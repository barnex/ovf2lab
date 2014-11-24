import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public final class A2 {
    public static void main(String[] args) {

        for (String f:args) {
            try {
                Reader in = new FileReader(new File(f));
                Scanner scanner = new Scanner(in);
                for(Token t = scanner.next(); t != null; t=scanner.next()) {
                    System.out.println(t);
                }
            } catch(Exception e) {
                fatal(e);
                return;
            }

        }

    }


    public static void fatal(Exception e) {
        System.err.println(e);
        System.exit(1);
    }
}


