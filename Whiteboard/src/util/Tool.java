package util;

import java.awt.Color;

/***
 * 
 * Tools for picking shapes and colors.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public enum Tool {

	LINE, CIRCLE, TRIANGLE, RECTANGLE, TEXT;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	// image array for configuring the shape panel
	public static final String[] getTools() {
		String[] tools = { "/image/circle.png", "/image/triangle.png", "/image/text.png", "/image/line.png",
				"/image/rectangle.png" };
		return tools;
	}

	// color array for configuring the color panel
	public static final Color[] getPalette() {
		Color[] palette = { Color.BLACK, Color.RED, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW,
				Color.LIGHT_GRAY, Color.BLUE, Color.CYAN, new Color(255, 250, 205), new Color(0, 139, 139),
				new Color(147, 112, 219), new Color(173, 255, 47), new Color(176, 196, 222), new Color(255, 20, 147),
				new Color(148, 0, 211), new Color(240, 255, 240), new Color(210, 105, 30) };
		return palette;
	}

}
