package model;

import java.awt.Point;
import java.awt.Polygon;

import lombok.Getter;
import lombok.Setter;
import ui.OmegaGamePanel;

@Getter
public class Field {
	
	public enum Neighbour{
		TopLeft,
		TopRight,
		Right,
		BottomRight,
		BottomLeft,
		Left;
	}
	
	private Polygon hex;
	private Point xy;
	private Field[] neighbours; // List of all neighbours going in order: TL, TR, R, BR, BL, L
	@Setter
	private int value = 0; // 0 is empty, every number corresponds to the player number;
	private Piece piece; // Piece placed on the field. If piece == null, field is empty
	
	public Field(int x, int y) {
		xy = new Point(x, y);
		neighbours = new Field[6];
		piece = null;
	}
	
	public Field(Point p) {
	 	xy = p;
	 	neighbours = new Field[6];
	 	createHex();
	 	piece = null;
	}
	
	private Field(Point p, int value, Polygon hex) {
		this.xy = p;
		this.value = value;
		this.hex = hex;
		neighbours = new Field[6];
	}
	
	public void setNeighbour(Field neighbour, Neighbour pos) {
		neighbours[pos.ordinal()] = neighbour;
		createHex();
	}
	
	public void createHex() {
		int x = (int) (OmegaGamePanel.OFFSET + (OmegaGamePanel.SQUARESIZE * xy.x) +  (xy.y * 0.5 * OmegaGamePanel.SQUARESIZE)) + OmegaGamePanel.CENTER_OFFSET_X;
		int y = (int) (OmegaGamePanel.OFFSET + (xy.y * (3* (OmegaGamePanel.SQUARESIZE/4) + 2))) + OmegaGamePanel.CENTER_OFFSET_Y;
		
		int[] xPoints = new int[6];
		int[] yPoints = new int[6];

		xPoints[0] = x + 1;
		xPoints[1] = x + OmegaGamePanel.SQUARESIZE / 2;
		xPoints[2] = x + OmegaGamePanel.SQUARESIZE - 1;
		xPoints[3] = xPoints[2];
		xPoints[4] = xPoints[1];
		xPoints[5] = xPoints[0];

		yPoints[0] = y + OmegaGamePanel.SQUARESIZE / 4 + 1;
		yPoints[1] = y;
		yPoints[2] = yPoints[0];
		yPoints[3] = y + 3 * (OmegaGamePanel.SQUARESIZE / 4);
		yPoints[4] = y + OmegaGamePanel.SQUARESIZE - 1;
		yPoints[5] = yPoints[3];
		
		hex = new Polygon(xPoints, yPoints, 6);
	}
	
	@Override
	public Field clone() {
		return new Field(new Point(xy.x, xy.y), value, new Polygon(hex.xpoints, hex.ypoints, 6));
	}
	
	@Override
	public int hashCode() {
		return xy.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return xy.equals(((Field) obj).getXy());
	}
}
