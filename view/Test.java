package view;

import javax.swing.JFrame;

public class Test {

	public static void main(String[] args) {

		JFrame f = new JFrame();
		f.getContentPane().add(new ViewPane());
		f.setSize(800, 600);
		f.setVisible(true);

	}
}
