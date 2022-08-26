package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import manager.IRemoteManager;

/**
 * Subclass of JPanel, the canvas users draws on.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public class Canvas extends JPanel {
	List<HistoryTuple> history = new ArrayList<>();
	private BasicStroke stroke = new BasicStroke(3f);
	private String text;
	private int x1, y1, x2, y2;
	private Tool tool = Tool.LINE;
	private Color color = Color.BLACK;
	private Font font = new Font("Calibri", Font.BOLD, 22);
	private Graphics2D g;
	private IRemoteManager managerStub;
	private String name;

	public Canvas(String userName) {
		this.setBackground(getBackground());
		this.setPreferredSize(new Dimension(1000, 700));
		this.setBackground(Color.WHITE);
		this.addMouseListener(new ActionListener(this));
		this.addMouseMotionListener(new ActionListener(this));
		this.name = userName;
	}

	/**
	 * get draw history for repaint
	 * 
	 * @return list of draw history
	 */
	public List<HistoryTuple> getHistory() {
		return this.history;
	}

	/**
	 * Update history to sync with other whiteboards
	 * 
	 * @param histroyList
	 */
	public void loadHistory(List<HistoryTuple> list) {
		this.history = list;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool drawTool) {
		this.tool = drawTool;
	}

	public void setDM(IRemoteManager stubM) {
		this.managerStub = stubM;
	}

	public IRemoteManager getDM() {
		return this.managerStub;
	}

	public void setColor(Color drawColor) {
		this.color = drawColor;
	}

	public void setText(String textInserted) {
		this.text = textInserted;
	}

	public void setStartCoordinate(int x1, int y1) {
		this.x1 = x1;
		this.y1 = y1;
	}

	public void setEndCoordinate(int x2, int y2) {
		this.x2 = x2;
		this.y2 = y2;
	}

	public void drawLine() throws RemoteException {
		// g.drawLine(x1, y1, x2, y2);
		int[] paramLine = { x1, y1, x2, y2 };
		HistoryTuple tuple = new HistoryTuple(tool, color, paramLine);

		this.managerStub.updateCanvas(this.name, tuple);
	}

	public void drawCircle() throws RemoteException {
		// g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1
		// - y2));
		int[] paramCircle = { Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2) };
		HistoryTuple tuple = new HistoryTuple(tool, color, paramCircle);

		this.managerStub.updateCanvas(this.name, tuple);
	}

	public void drawTriangle() throws RemoteException {
		// g.drawLine(Math.min(x1, x2), Math.max(y1, y2), (x1 + x2) / 2, Math.min(y1,
		// y2));
//		g.drawLine(Math.min(x2, x1), Math.max(y1, y2), Math.max(x2, x1), Math.max(y1, y2));
//		g.drawLine((x1 + x2) / 2, Math.min(y1, y2), Math.max(x2, x1), Math.max(y1, y2));
		int[] paramLine1 = { Math.min(x1, x2), Math.max(y1, y2), (x1 + x2) / 2, Math.min(y1, y2) };
		int[] paramLine2 = { Math.min(x2, x1), Math.max(y1, y2), Math.max(x2, x1), Math.max(y1, y2) };
		int[] paramLine3 = { (x1 + x2) / 2, Math.min(y1, y2), Math.max(x2, x1), Math.max(y1, y2) };

		List<HistoryTuple> tuples = new ArrayList<HistoryTuple>();
		tuples.add(new HistoryTuple(tool, color, paramLine1));
		tuples.add(new HistoryTuple(tool, color, paramLine2));
		tuples.add(new HistoryTuple(tool, color, paramLine3));

		this.managerStub.updateCanvas(this.name, tuples);
	}

	public void drawPencil() throws RemoteException {
//		g.drawLine(x1, y1, x2, y2);
		int[] paramPencil = { x1, y1, x2, y2 };
		HistoryTuple tuple = new HistoryTuple(tool, color, paramPencil);

		this.managerStub.updateCanvas(this.name, tuple);
	}

	public void drawRectangle() throws RemoteException {
//		g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
		int[] paramRect = { Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2) };
		HistoryTuple tuple = new HistoryTuple(tool, color, paramRect);

		this.managerStub.updateCanvas(this.name, tuple);
	}

	public void drawText() throws RemoteException {
//		g.drawString(text, x1, y1);
		HistoryTuple tuple = new HistoryTuple(tool, color, text, x1, y1);

		this.managerStub.updateCanvas(this.name, tuple);
	}

	public void configGraphics() {
		this.g = (Graphics2D) this.getGraphics();
		this.g.setFont(this.font);
		this.g.setColor(this.color);
		this.g.setStroke(this.stroke);
	}

	public void draw() throws RemoteException {
		configGraphics();
		switch (tool) {
		case LINE:
			drawLine();
			break;
		case CIRCLE:
			drawCircle();
			break;
		case TEXT:
			drawText();
			break;
		case TRIANGLE:
			drawTriangle();
			break;
		case RECTANGLE:
			drawRectangle();
			break;
		default:
			break;
		}
	}

	// Override the paintComponent method to ensure the drawn shapes remains after
	// the window is resized, otherwise, the components will disappear.
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g1 = (Graphics2D) g;
		g1.setFont(this.font);
		g1.setStroke(this.stroke);

		if (history.size() != 0) {
			for (int i = 0; i < history.size(); i++) {
				HistoryTuple historyTuple = history.get(i);

				g1.setColor(historyTuple.color);

				switch (historyTuple.tool) {
				case LINE:
				case TRIANGLE:
					g1.drawLine(historyTuple.param[0], historyTuple.param[1], historyTuple.param[2],
							historyTuple.param[3]);
					break;
				case CIRCLE:
					g1.drawOval(historyTuple.param[0], historyTuple.param[1], historyTuple.param[2],
							historyTuple.param[3]);
					break;
				case RECTANGLE:
					g1.drawRect(historyTuple.param[0], historyTuple.param[1], historyTuple.param[2],
							historyTuple.param[3]);
					break;
				case TEXT:
					g1.drawString(historyTuple.text, historyTuple.param[0], historyTuple.param[1]);
					break;
				}
			}
		}
	}

}
