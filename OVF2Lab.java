import a2.Error;
import a2.Node;
import a2.Parser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

class OVF2Lab {
	static final String PROMPT = ">";

	public static void main(String[] args) throws IOException {

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		for (;;) {
			System.out.print(PROMPT);
			String line = stdin.readLine();
			if (line == null) {
				System.out.println();
				break;
			}
			try {
				Node ast = Parser.parseLine(line);
				ast.print(new PrintStream(System.out), 0);
				System.out.println();
			} catch(Error e) {
				System.out.print("error: ");
				System.out.println(e);
			}
		}

	}



}
