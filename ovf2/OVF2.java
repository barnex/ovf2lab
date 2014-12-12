package ovf2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

// OVF2 stores data from a single OOMMF OVF2 file (http://math.nist.gov/oommf) generated by mumax.
// There is no guarantee that non-mumax files are accepted.
public final class OVF2 {

	public float[][][][] data;        // data indexed by: component, z, y, x
	public float xBase, yBase, zBase; // cell sizes

	// Reads OVF2, binary 4 format, from in.
	public static OVF2 read(InputStream in) throws IOException {

		String line = readline(in);

		if (!"# OOMMF OVF 2.0".equals(line)) {
			throw new IOException("invalid header: " + line);
		}


		int nComp =0, sizeX =0, sizeY =0, sizeZ = 0;
		OVF2 ovf2 = new OVF2();

		line = readline(in);
		while (!"# End: Header".equals(line)) {
			line = line.substring(2);
			String[]split = line.split(":");
			String key = split[0].trim();
			String val = split[1].trim();
			if ("xnodes".equals(key)) {
				sizeX = Integer.parseInt(val);
			}
			if ("ynodes".equals(key)) {
				sizeY = Integer.parseInt(val);
			}
			if ("znodes".equals(key)) {
				sizeZ = Integer.parseInt(val);
			}
			if ("valuedim".equals(key)) {
				nComp = Integer.parseInt(val);
			}
			if ("xbase".equals(key)) {
				ovf2.xBase = Float.parseFloat(val);
			}
			if ("ybase".equals(key)) {
				ovf2.yBase = Float.parseFloat(val);
			}
			if ("zbase".equals(key)) {
				ovf2.zBase = Float.parseFloat(val);
			}

			line = readline(in);
		}

		line = readline(in);
		if (! "# Begin: Data Binary 4".equals(line)) {
			throw new IOException("invalid data header: " + line);
		}

		// allocate data
		float[][][][] data = new float[nComp][][][];
		for(int c=0; c<data.length; c++) {
			data[c] = new float[sizeZ][][];
			for(int z=0; z<data[c].length; z++) {
				data[c][z] = new float[sizeY][];
				for(int y=0; y<data[c][z].length; y++) {
					data[c][z][y] = new float[sizeX];
				}
			}
		}


		// read data

		// get float from little-endian binary data.
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(3, readByte(in));
		buffer.put(2, readByte(in));
		buffer.put(1, readByte(in));
		buffer.put(0, readByte(in));
		float controlNumber = buffer.getFloat(0);

		if (controlNumber != 1234567.0) {
			throw new IOException("bad ovf2 control number: " + controlNumber);
		}

		for(int z=0; z<data[0].length; z++) {
			for(int y=0; y<data[0][z].length; y++) {
				for(int x=0; x<data[0][z][y].length; x++) {
					for(int c=0; c<data.length; c++) {
						buffer.put(3, readByte(in));
						buffer.put(2, readByte(in));
						buffer.put(1, readByte(in));
						buffer.put(0, readByte(in));
						data[c][z][y][x] = buffer.getFloat(0);
					}
				}
			}
		}

		ovf2.data = data;
		return ovf2;
	}

	// number of cells in X (bound for last index of data)
	public int sizeX() {
		return data[0][0][0].length;
	}

	// number of cells in Y
	public int sizeY() {
		return data[0][0].length;
	}

	// number of cells in Z
	public int sizeZ() {
		return data[0].length;
	}

	// number of components (bound for first index of data)
	public int nComp() {
		return data.length;
	}

	public String toString() {
		return "OVF2 " + nComp() + "x" + sizeX() + "x" + sizeY() + "x" + sizeZ() +
		       "," + xBase + "x" + yBase + "x" + zBase + "m";
	}

	// Reads a line from in.
	// Files are mixed text+binary, so BufferedReader et al. are problematic
	private static String readline(InputStream in) throws IOException {
		StringBuilder str = new StringBuilder();
		byte b = readByte(in);
		while (b != '\n') {
			str.append((char)(b));
			b = readByte(in);
		}
		return str.toString();
	}

	// Reads a byte from in, throws exception on EOF
	private static byte readByte(InputStream in) throws IOException {
		int b = in.read();
		if (b < 0) {
			throw new IOException("unexpected EOF");
		}
		return (byte)(b);
	}

	// main for testing: read files passed as args.
	public static void main(String[] args)throws IOException {
		for(String arg: args) {
			OVF2 data = read(new BufferedInputStream(new FileInputStream(new File(arg))));
			System.out.println(arg + ": " + data);
		}
	}
}
