package view;

import java.awt.Polygon;

final class View {

	//Camera position.
	float camx, camy , camz=-2;
	float phi, theta;			//0..2pi, -pi/2..pi/2

	// viewport size
	int width, height;
	float scale = 1;
	float persp = 1;

	//transformation matrix
	float m11, m12, m13;
	float m21, m22, m23;
	float m31, m32, m33;

	//One polygon to be re-used many times for drawing each polygon of the 3D object
	final Polygon POLYGON_BUFFER = new Polygon(new int[4], new int[4], 4);
	//final Stroke STROKE = new BasicStroke(1);
	final double PI = Math.PI;

	void updateMatrix() {
		m11 = cos(phi)*scale;
		m12 = 0;
		m13 = -sin(phi)*scale;

		m21 = -sin(phi)*sin(theta)*scale;
		m22 = cos(theta)*scale;
		m23 = -cos(phi)*sin(theta)*scale;

		m31 = sin(phi)*cos(theta)*scale;
		m32 = sin(theta)*scale;
		m33 = cos(phi)*cos(theta)*scale;
	}

	//Arrays.sort(polygons);

	void transform(Poly p) {

		float x1 = (m11 * p.x1 + m12 * p.y1 + m13 * p.z1) - camx;
		float y1 = (m21 * p.x1 + m22 * p.y1 + m23 * p.z1) - camy;
		float z1 = (m31 * p.x1 + m32 * p.y1 + m33 * p.z1) - camz;

		p.xpoints[0] = (int)(x1);
		p.ypoints[0] = (int)(y1);
		p.z = z1;
	}

	void rotateCam(double dPhi, double dTheta) {
		phi += dPhi;
		phi %= 2*PI;
		theta += dTheta;
		setCamDir(phi, theta);
	}

	void setCamDir(float phi, float theta) {
		this.phi = phi;
		this.theta = theta;
	}

	void moveCam(float dx, float dy, float dz) {
		setCamPos(camx + dx, camy + dy, camz + dz);
	}

	void setCamPos(float x, float y, float z) {
		camx = x;
		camy = y;
		camz = z;
	}

	float sin(float x) {
		return (float)(Math.sin(x));
	}
	float cos(float x) {
		return (float)(Math.cos(x));
	}
}
