package view;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

class ViewPane extends JPanel {

	View v;
	static final long serialVersionUID = 1L; // sigh

	ViewPane() {
		this.v = new View();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		v.paint((Graphics2D)(g), getWidth(), getHeight());
	}
}
