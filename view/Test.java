package view;

import java.io.File;
import java.io.FileInputStream;
import javax.swing.JFrame;
import ovf2.OVF2;

public class Test {

	public static void main(String[] args) throws Throwable {

		JFrame f = new JFrame();

		View v = new View();

		OVF2 data = OVF2.read(new FileInputStream(new File(args[0])));

		v.render(data);

		//v.polys.add(Poly.zFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.BLUE).flip());
		//v.polys.add(	    Poly.zFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.RED));
		//v.polys.add(	    Poly.xFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.BLACK).flip());
		//v.polys.add(	    Poly.xFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.GRAY));
		//v.polys.add(	    Poly.yFace(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.GREEN).flip());
		//v.polys.add(	    Poly.yFace(0.25f, -0.25f, -0.25f, 0.25f, 0.25f, Color.YELLOW));

		ViewPane vp = new ViewPane(v);

		f.getContentPane().add(vp);
		f.setSize(800, 600);
		f.setVisible(true);

	}
}
