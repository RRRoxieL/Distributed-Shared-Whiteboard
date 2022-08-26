package util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import javax.swing.JTextField;

/**
 * MouseListener for detecting mouse action and draw correspondingly.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
class ActionListener extends MouseAdapter {

	private Canvas canvas;
	private int x1, y1, x2, y2;
	private Tool drawTool;
	private JTextField textField;

	public ActionListener(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * Detecting mouse press action to get the start coordination or display
	 * textField for user to paint text.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		this.drawTool = canvas.getTool();

		x1 = e.getX();
		y1 = e.getY();
		canvas.setStartCoordinate(x1, y1);

		if (drawTool == Tool.TEXT) {
			textField = new JTextField();
			textField.setBounds(x1, y1 - 15, 150, 26);
			textField.setBackground(null);
			textField.setBorder(null);
			textField.setOpaque(false);
			textField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == e.VK_ENTER) {
						canvas.setText(textField.getText());
						try {
							canvas.draw();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}

						textField.setVisible(false);
					}
				}
			});
			canvas.add(textField);
			textField.setColumns(10);
		}
	}

	/**
	 * Detecting mouse release action to get the end coordination.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

		this.drawTool = canvas.getTool();

		x2 = e.getX();
		y2 = e.getY();
		canvas.setEndCoordinate(x2, y2);

		if (drawTool != Tool.TEXT) {
			try {
				canvas.draw();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
}
