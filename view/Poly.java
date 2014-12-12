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

	Poly(Color c) {
		xpoints = new int[4];
		ypoints = new int[4];
		color = c;
	}

	static Poly zFace(float z, float x1, float y1, float x2, float y2, Color c) {
		Poly p = new Poly(c);

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

		return p;
	}

	static Poly xFace(float x, float y1, float z1, float y2, float z2, Color c) {
		Poly p = new Poly(c);

		p.x1 = x;
		p.y1 = y1;
		p.z1 = z1;

		p.x2 = x;
		p.y2 = y2;
		p.z2 = z1;

		p.x3 = x;
		p.y3 = y2;
		p.z3 = z2;

		p.x4 = x;
		p.y4 = y1;
		p.z4 = z2;

		return p;
	}

	static Poly yFace(float y, float x1, float z1, float x2, float z2, Color c) {
		Poly p = new Poly(c);

		p.x1 = x1;
		p.y1 = y;
		p.z1 = z2;

		p.x2 = x2;
		p.y2 = y;
		p.z2 = z2;

		p.x3 = x2;
		p.y3 = y;
		p.z3 = z1;

		p.x4 = x1;
		p.y4 = y;
		p.z4 = z1;

		return p;
	}

	int orientation() {
		int ax = xpoints[0] - xpoints[1];
		int ay = ypoints[0] - ypoints[1];
		int bx = xpoints[2] - xpoints[1];
		int by = ypoints[2] - ypoints[1];
		return  ax*by - bx*ay;
	}

	public int compareTo(Poly p) {
		if (z < p.z) {
			return -1;
		} else {
			return 1;
		}
	}

	// flip orientation.
	Poly flip() {
		float x1 = this.x1;
		this.x1 = this.x4;
		this.x4 = x1;

		float y1 = this.y1;
		this.y1 = this.y4;
		this.y4 = y1;

		float z1 = this.z1;
		this.z1 = this.z4;
		this.z4 = z1;

		float x2 = this.x2;
		this.x2 = this.x3;
		this.x3 = x2;

		float y2 = this.y2;
		this.y2 = this.y3;
		this.y3 = y2;

		float z2 = this.z2;
		this.z2 = this.z3;
		this.z3 = z2;
		return this;
	}
}
