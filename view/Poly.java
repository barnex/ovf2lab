package view;

import java.awt.Color;

class Poly implements Comparable<Poly> {

	float x1, y1, z1;
	float x2, y2, z2;
	float x3, y3, z3;
	float x4, y4, z4;
	int[] xpoints, ypoints;
	float z;
	Color color;

	Poly() {
		xpoints = new int[4];
		ypoints = new int[4];
	}

	static Poly zFace(float z, float x1, float y1, float x2, float y2, Color c) {
		Poly p = new Poly();

		p.x1 = x1;
		p.y1 = y1;
		p.z1 = z;

		p.x2 = x2;
		p.y2 = y1;
		p.z2 = z;

		p.x3 = x2;
		p.y3 = y2;
		p.z3 = z;

		p.x4 = x1;
		p.y4 = y2;
		p.z4 = z;

		p.color = c;

		return p;
	}

	public int compareTo(Poly p) {
		if (z < p.z) {
			return -1;
		} else {
			return 1;
		}
	}
}
