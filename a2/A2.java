package a2;

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
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
		} else if (cmd.equals("simplify")) {
			mainSimplify(args);
		} else if (cmd.equals("compile")) {
			mainCompile(args);
		}
		else {
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
				System.out.println(s.pos() + "\t" + typeName(t.type) + ":\t" +  t.value);
			}
			Token t = s.next();
			System.out.println(s.pos() + "\t" + typeName(t.type) + ":\t" +  t.value);
		}
	}

	static String typeName(int tokenType) {
		String s = Token.typeName(tokenType);
		while (s.length() < 20) {
			s = s + " ";
		}
		return s;
	}

	// main for parsing files
	static void mainParse(String[] args) throws Throwable {
		for (int i=1; i<args.length; i++) {
			String f = args[i];
			Node ast = Parser.parse(f, new FileInputStream(new File(f)));
			ast.print(System.out, 0);
		}
	}

	//
	static void mainSimplify(String[] args) throws Throwable {
		for (int i=1; i<args.length; i++) {
			String f = args[i];
			Node ast = Parser.parse(f, new FileInputStream(new File(f)));
			ast = Compiler.simplify(ast);
			ast.print(System.out, 0);
		}
	}

	static void mainCompile(String[] args) throws Throwable {
		for (int i=1; i<args.length; i++) {
			String f = args[i];
			StmtList ast = Parser.parse(f, new FileInputStream(new File(f)));
			Scope scope = new Scope();
			Compiler.resolve(ast, scope);
			ast.print(System.out, 0);
		}
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


