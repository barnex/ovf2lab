
public final class A2 {
	public static void main(String[] args) throws Throwable {
		for (String f:args) {
			Parser p = new Parser();
			p.parseFile(f);
			if (p.errors.size() > 0) {
				p.printErrors(System.out);
			} else if (p.ast != null) {
				p.ast.print(System.out);
			}
		}
	}

	public static void fatal(Exception e) {
		System.err.println(e);
		System.exit(1);
	}
}


