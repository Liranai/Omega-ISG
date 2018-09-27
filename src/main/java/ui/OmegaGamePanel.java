package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import main.OmegaMain;
import model.Board;
import model.Field;
import model.util.Pair;

public class OmegaGamePanel extends JPanel implements Observer{

	private static final long serialVersionUID = 3853160688027671266L;
	public static int SQUARESIZE = 50;
	public static final int OFFSET = 5;
	public static int CENTER_OFFSET_X;
	public static int CENTER_OFFSET_Y;
	
	private Board board;
	private Field[] fields;
	private HashMap<Point, Pair<Integer, Integer>> heatMap;

	public OmegaGamePanel(Board board) {
		this.board = board;
		this.setMinimumSize(new Dimension((board.boardSize * 2) * SQUARESIZE - (int)Math.floor(0.75*SQUARESIZE), (int)(Math.ceil(board.boardSize * 1.5 * SQUARESIZE))));
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.gray);
		g2.fill(getVisibleRect());
		
		if(OmegaMain.DEBUG >= 2) {
			if(heatMap != null) {
				int maxB = 0, minB = 255;
				int maxR = 0, minR = 255;
				for(Point key : heatMap.keySet()) {
					if(heatMap.get(key).getFirst() > maxR) {
						maxR = heatMap.get(key).getFirst();
					}
					if(heatMap.get(key).getFirst() < minR) {
						minR = heatMap.get(key).getFirst();
					}
					if(Math.abs(heatMap.get(key).getSecond()) > maxB) {
						maxB = Math.abs(heatMap.get(key).getSecond());
					}
					if(Math.abs(heatMap.get(key).getSecond()) < minB) {
						minB = Math.abs(heatMap.get(key).getSecond());
					}
				}
				maxB++; maxR++;
				
				for(Point key : heatMap.keySet()) {
					g2.setColor(new Color( (255*(heatMap.get(key).getFirst() - minR))/(maxR-minR), 120, 0, 250));
					g2.fill(board.getFields().get(key).getHex());
					g2.setColor(new Color(0, 120, (255*(Math.abs(heatMap.get(key).getSecond()) - minB))/(maxB-minB), 250));
					g2.fill(new Polygon(Arrays.copyOfRange(board.getFields().get(key).getHex().xpoints, 1, 5), Arrays.copyOfRange(board.getFields().get(key).getHex().ypoints, 1, 5), 4));
				}
			}
		}

		for (Field f : board.getFields().values()) {
			g2.setStroke(new BasicStroke(2l));
			g2.setColor(new Color(131, 56, 148));
//			if (x % 2 == 0)
//				g2.setColor(new Color(95, 32, 40));
			g2.draw(f.getHex());
			if (f.getValue() > 0) {
				switch (f.getValue()) {
				case 1:
					g2.setColor(new Color(235, 235, 235));
					break;
				case 2:
					g2.setColor(new Color(24, 24, 24));
					break;
				case 3:
					g2.setColor(Color.red);
					break;
				case 4:
					g2.setColor(Color.blue);
					break;
				default:
					break;
				}
				g2.fill(new Ellipse2D.Double(f.getHex().xpoints[0] + OFFSET / 2,
						f.getHex().ypoints[0] - (SQUARESIZE / 4.0) + OFFSET, SQUARESIZE - OFFSET,
						SQUARESIZE - OFFSET * 2));
			}
						
			if(OmegaMain.DEBUG >= 1) {
				int x = (int) (OFFSET + (SQUARESIZE * f.getXy().x) +  (f.getXy().y * 0.5 * SQUARESIZE)) + CENTER_OFFSET_X;
				int y = (int) (OFFSET + (f.getXy().y * (3* (SQUARESIZE/4) + 2))) + CENTER_OFFSET_Y;
				g2.setColor(new Color(50,180,50));
				g2.setFont(new Font("Arial", Font.BOLD, 8));
				if(OmegaMain.DEBUG == 1)
					g2.drawString(x + "," + y, x+ 7, y+ 35);
				g2.drawString(f.getXy().x +"," +f.getXy().y, x + 10, y+20);
				if(OmegaMain.DEBUG >= 2) {
					g2.setColor(Color.BLUE);
					g2.drawString("" + board.pointToIndex(f.getXy()), x + 7, y + 35);
					
					g2.setColor(Color.RED);
					g2.drawString("" + board.indexToPoint(board.pointToIndex(f.getXy())).x + "," + board.indexToPoint(board.pointToIndex(f.getXy())).y, x + 27, y + 30);
				}
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		heatMap = (HashMap<Point, Pair<Integer, Integer>>)arg;
	}
}
