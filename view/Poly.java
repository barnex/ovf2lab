package view;

class Poly implements Comparable<Poly> {
	float x1, y1, z1;
	float x2, y2, z2;
	float x3, y3, z3;
	float x4, y4, z4;

	int[] xpoints, ypoints;
	float z;

	public int compareTo(Poly p) {
		if (z < p.z) {
			return -1;
		} else {
			return 1;
		}
	}
}
