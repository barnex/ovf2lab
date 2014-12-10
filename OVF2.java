import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// OVF2 stores data from a single OOMMF OVF2 file (http://math.nist.gov/oommf)
class OVF2 {

	int nComp;                // number of components (1: scalar, 3:vector)
	int sizeX, sizeY, sizeZ;  // number of grid nodes
	float[][][][] data;       // data index by component, z, y, x

	// Reads OVF data from in.
	static OVF2 read(InputStream in) throws IOException {

		String line = readline(in);

		if (!"# OOMMF OVF 2.0".equals(line)) {
			throw new IOException("invalid header: " + line);
		}

		OVF2 ovf2 = new OVF2();

		return ovf2;
	}

	// Reads a line from in.
	// Files are mixed text+binary, so BufferedReader et al. are problematic
	static String readline(InputStream in) throws IOException {
		StringBuilder str = new StringBuilder();
		int b = in.read();
		while (!isEOL(b)) {
			str.append((char)(b));
			b = in.read();
		}
		return str.toString();
	}

	static boolean isEOL(int c) {
		return (c == -1 || c == '\r' || c == '\n');
	}

	public static void main(String[] args)throws IOException {
		for(String arg: args) {
			read(new FileInputStream(new File(arg)));
		}
	}
}
