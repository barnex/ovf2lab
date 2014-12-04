// Token position in source file
final class Pos {

	String file;   // file token appeared in
	int line, pos; // positions in file

	public Pos(String file, int line, int pos) {
		this.file = file;
		this.line = line;
		this.pos = pos;
	}

	public String toString() {
		if (file == null) {
			return "" + line + ":" + pos;
		} else {
			return file + ":" + line + ":" + pos;
		}
	}
}
