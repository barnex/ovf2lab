import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// OVF2 stores data from a single OOMMF OVF2 file (http://math.nist.gov/oommf) generated by mumax.
// There is no guarantee that non-mumax files are accepted.
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

		line = readline(in);
		while (!"# End: Header".equals(line)) {
			line = line.substring(2);
			String[]split = line.split(":");
			String key = split[0].trim();
			String val = split[1].trim();
			if ("xnodes".equals(key)) {
				ovf2.sizeX = Integer.parseInt(val);
			}
			if ("ynodes".equals(key)) {
				ovf2.sizeY = Integer.parseInt(val);
			}
			if ("znodes".equals(key)) {
				ovf2.sizeZ = Integer.parseInt(val);
			}
			if ("valuedim".equals(key)) {
				ovf2.nComp = Integer.parseInt(val);
			}

			line = readline(in);
		}

		return ovf2;
	}

	public String toString() {
		return "OVF2 " + nComp + "x" + sizeX + "x" + sizeY + "x" + sizeZ;
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
			OVF2 data = read(new FileInputStream(new File(arg)));
			System.out.println(data);
		}
	}
}
