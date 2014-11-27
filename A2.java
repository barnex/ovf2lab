import java.io.File;
import java.io.FileReader;
import java.io.Reader;

// a2 main command.
// a2 scan files: outputs tokens
// a2 parse files: outputs ast
public final class A2 {

	public static void main(String[] args) throws Throwable {

		if (args.length < 2) {
			badUsage();
		}

		String cmd = args[0];
		if (cmd.equals("scan")) {
			mainScan(args);
		} else if(cmd.equals("parse")) {
			mainParse(args);
		} else {
			badUsage();
		}
	}

	// main for scanning files
	static void mainScan(String[] args) throws Throwable {
		for (int i=1; i<args.length; i++) {
			String f = args[i];
			Reader r = new FileReader(new File(f));
			Scanner s = new Scanner(f, r);
			for (Token t = s.next(); t.type != Token.EOF; t = s.next()) {
				System.out.println(t.pos() + "\t" + Token.typeName(t.type) + ":\t" +  t.value);
				s.next();
			}
			Token t = s.next();
			System.out.println(t.pos() + "\t" + Token.typeName(t.type) + ":\t" +  t.value);
		}
	}

	// main for parsing files
	static void mainParse(String[] args) throws Throwable {
		int status = 0;
		for (int i=1; i<args.length; i++) {
			String f = args[i];
			Parser p = new Parser();
			p.parseFile(f);
			if (p.errors.size() > 0) {
				p.printErrors(System.out);
				status = 1;
			} else if (p.ast != null) {
				p.ast.print(System.out);
			}
		}
		System.exit(status);
	}

	static void badUsage() {
		System.err.println("Usage: a2 scan|parse <file>");
		System.exit(1);
	}

	static void fatal(Exception e) {
		System.err.println(e);
		System.exit(1);
	}
}


