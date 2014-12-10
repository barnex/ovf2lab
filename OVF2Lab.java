import a2.Interpreter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class OVF2Lab {

	public static void main(String[] args) throws IOException{
		Interpreter w = new Interpreter();

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		for (;;){
			String line = stdin.readLine();
			w.exec(line);
		}
	
	}



}
