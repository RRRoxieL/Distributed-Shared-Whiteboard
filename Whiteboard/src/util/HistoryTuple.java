package util;

import java.awt.Color;

/**
 * Self-defined tuple to store drawing history. Used for repainting after being
 * parsed.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public class HistoryTuple implements java.io.Serializable {

	Tool tool;
	int[] param = new int[4];
	Color color;
	String text;

	// Store the data of darwing shapes.
	HistoryTuple(Tool tool, Color color, int[] param) {
		this.tool = tool;
		this.color = color;
		this.param = param;
	}

	// Store the data of drawing text.
	HistoryTuple(Tool tool, Color color, String text, int x1, int y1) {
		this.tool = tool;
		this.color = color;
		this.text = text;
		this.param[0] = x1;
		this.param[1] = y1;
	}
}
