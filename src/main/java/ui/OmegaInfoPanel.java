package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import logic.OmegaLogic;
import logic.ai.ArtificialIntelligence;
import lombok.Getter;
import main.OmegaMain;
import model.util.Pair;

public class OmegaInfoPanel extends JPanel implements Observer {

	private static final long serialVersionUID = -4462408537612403651L;

	private Info info;

	public OmegaInfoPanel() {
		this.setMinimumSize(new Dimension(400, 100));
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.lightGray);
		g2.fill(getVisibleRect());

		g2.setColor(new Color(95, 32, 40));
		g2.draw(new Line2D.Double(0, 0, 0, getVisibleRect().height));
		g2.draw(new Line2D.Double(1, 0, 1, getVisibleRect().height));
		if (info != null) {
			for (int i = 0; i < info.getPlayers().size(); i++) {
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("Arial", Font.PLAIN, 12));
				g2.drawString("Player: " + info.getPlayers().get(i), 7, (i * 50) + 50);
				g2.drawString("Current score: " + info.getScores().get(i), 7, (i * 50) + 64);
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		ArrayList<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
		for (int i = 0; i < OmegaMain.NUMBER_OF_PLAYERS; i++) {
			pairs.add(new Pair<String, String>(((OmegaLogic) arg0).getAis().get(i).getName(), "" + ((OmegaLogic) arg0).getBoard().getScore()[i]));
		}

		info = new Info(pairs);
		repaint();
	}

	@Getter
	private class Info {

		ArrayList<String> players;
		ArrayList<String> scores;

		public Info(ArrayList<Pair<String, String>> pairs) {
			players = new ArrayList<String>();
			scores = new ArrayList<String>();

			for (int i = 0; i < pairs.size(); i++) {
				players.add(pairs.get(i).getFirst());
				scores.add(pairs.get(i).getSecond());
			}

		}
	}
}
