import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public final class A2 {
	public static void main(String[] args) {
		try {
			mainUnsafe(args);
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static void mainUnsafe(String[] args) {
		for (String f:args) {
			try {
				Reader in = new FileReader(new File(f));
				Scanner scanner = new Scanner(f, in);
				for(scanner.next(); scanner.currentToken != null; scanner.next()) {
					System.out.println(scanner.currentToken);
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


