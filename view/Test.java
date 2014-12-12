package view;

import javax.swing.JFrame;
import java.awt.Color;

public class Test {

	public static void main(String[] args) {

		JFrame f = new JFrame();

		View v = new View();

		v.polys.add(Poly.zFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.BLUE).flip());
		v.polys.add(	    Poly.zFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.RED));
		v.polys.add(	    Poly.xFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.BLACK).flip());
		v.polys.add(	    Poly.xFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.GRAY));
		v.polys.add(	    Poly.yFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.GREEN).flip());
		v.polys.add(	    Poly.yFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.YELLOW));

		ViewPane vp = new ViewPane(v);

		f.getContentPane().add(vp);
		f.setSize(800, 600);
		f.setVisible(true);

	}
}
