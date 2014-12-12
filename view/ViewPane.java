package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

class ViewPane extends JPanel implements MouseListener, MouseMotionListener {

	View v;
	boolean mouseDown;
	int lastMouseX, lastMouseY;
	float speedx = 0.005f;
	float speedy = 0.005f;

	static final long serialVersionUID = 1L; // sigh

	ViewPane(View v) {
		this.v = v;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBackground(Color.WHITE);
	}


	public void mousePressed(MouseEvent e) {
		mouseDown = true;;
		lastMouseX = e.getX();
		lastMouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
	}

	public void mouseDragged(MouseEvent e) {
		if (mouseDown) {
			int dx = e.getX() - lastMouseX;
			int dy = e.getY() - lastMouseY;
			v.rotCam(-dx*speedx, dy*speedy);
			lastMouseX = e.getX();
			lastMouseY = e.getY();
			repaint();
		}
	}
	public void mouseExited(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		v.paint((Graphics2D)(g), getWidth(), getHeight());
	}


}
