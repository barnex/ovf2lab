package view;

import javax.swing.JFrame;
import java.awt.Color;

public class Test {

	public static void main(String[] args) {

		JFrame f = new JFrame();

		View v = new View();

		v.polys = new Poly[] {
		    Poly.zFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.BLUE).flip(),
		    Poly.zFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.RED),
		    Poly.xFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.BLACK).flip(),
		    Poly.xFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.WHITE),
		    Poly.yFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.GREEN).flip(),
		    Poly.yFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.YELLOW),

		};

		ViewPane vp = new ViewPane(v);

		f.getContentPane().add(vp);
		f.setSize(800, 600);
		f.setVisible(true);

	}
}
