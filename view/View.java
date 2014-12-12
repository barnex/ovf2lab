package view;

import java.awt.Polygon;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.ArrayList;
import ovf2.OVF2;

final class View {

	//Camera position.
	float camx, camy , camz=-2;
	float phi, theta;			//0..2pi, -pi/2..pi/2

	// viewport size
	int width, height;
	float persp = 1, scale = 1;

	//transformation matrix
	float m11, m12, m13;
	float m21, m22, m23;
	float m31, m32, m33;

	ArrayList<Poly> polys;

	//One polygon to be re-used many times for drawing each polygon of the 3D object
	final Polygon POLYGON_BUFFER = new Polygon(new int[4], new int[4], 4);
	//final Stroke STROKE = new BasicStroke(1);
	final double PI = Math.PI;

	View() {
		polys = new ArrayList<Poly>();
	}

	void render(OVF2 data) {
		for(int iz=0; iz<data.sizeZ(); iz++) {
			for(int iy=0; iy<data.sizeY(); iy++) {
				for(int ix=0; ix<data.sizeX(); ix++) {

				}
			}
		}
	}

	void paint(Graphics2D g, int w, int h) {
		this.width = w;
		this.height = h;
		this.scale = Math.min(width, height);

		updateMatrix();
		for(Poly p: polys) {
			transform(p);
		}
		Collections.sort(polys);

		for(Poly p: polys) {
			// cull faces pointing backward
			if (p.z <= 0 || p.orientation() < 0) {
				continue;
			}
			g.setColor(p.color);
			g.fillPolygon(p.xpoints, p.ypoints, p.xpoints.length);
		}

	}

	void updateMatrix() {
		m11 = cos(phi);
		m12 = 0;
		m13 = -sin(phi);

		m21 = -sin(phi)*sin(theta);
		m22 = cos(theta);
		m23 = -cos(phi)*sin(theta);

		m31 = -sin(phi)*cos(theta);
		m32 = -sin(theta);
		m33 = -cos(phi)*cos(theta);
	}


	void transform(Poly p) {

		float x = (m11 * p.x1 + m12 * p.y1 + m13 * p.z1) - camx;
		float y = (m21 * p.x1 + m22 * p.y1 + m23 * p.z1) - camy;
		float z = (m31 * p.x1 + m32 * p.y1 + m33 * p.z1) - camz;
		x/=z;
		y/=z;
		p.xpoints[0] = (int)(scale*x+width/2);
		p.ypoints[0] = (int)(height/2-scale*y);
		p.z = z;

		x = (m11 * p.x2 + m12 * p.y2 + m13 * p.z2) - camx;
		y = (m21 * p.x2 + m22 * p.y2 + m23 * p.z2) - camy;
		z = (m31 * p.x2 + m32 * p.y2 + m33 * p.z2) - camz;
		x/=z;
		y/=z;
		p.xpoints[1] = (int)(scale*x+width/2);
		p.ypoints[1] = (int)(height/2-scale*y);
		p.z = min(p.z, p.z+z); // z is average, for sorting

		x = (m11 * p.x3 + m12 * p.y3 + m13 * p.z3) - camx;
		y = (m21 * p.x3 + m22 * p.y3 + m23 * p.z3) - camy;
		z = (m31 * p.x3 + m32 * p.y3 + m33 * p.z3) - camz;
		x/=z;
		y/=z;
		p.xpoints[2] = (int)(scale*x+width/2);
		p.ypoints[2] = (int)(height/2-scale*y);
		p.z = min(p.z, p.z+z);

		x = (m11 * p.x4 + m12 * p.y4 + m13 * p.z4) - camx;
		y = (m21 * p.x4 + m22 * p.y4 + m23 * p.z4) - camy;
		z = (m31 * p.x4 + m32 * p.y4 + m33 * p.z4) - camz;
		x/=z;
		y/=z;
		p.xpoints[3] = (int)(scale*x+width/2);
		p.ypoints[3] = (int)(height/2-scale*y);
		p.z = min(p.z, p.z+z);

	}

	void rotCam(double dPhi, double dTheta) {
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
	float min(float x, float y) {
		if (x<y) {
			return x;
		}
		else {
			return y;
		}
	}
}
