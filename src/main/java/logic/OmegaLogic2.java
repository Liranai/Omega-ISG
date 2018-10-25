package logic;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Observable;

import logic.ai.AlphaBetaNegaMax;
import logic.ai.ArtificialIntelligence;
import logic.ai.GwensExperimental;
import logic.ai.HumanPlayer;
import logic.ai.RandomMover;
import lombok.Getter;
import lombok.Setter;
import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;
import ui.OmegaFrame;

@Getter
public class OmegaLogic2 extends Observable implements Runnable{

	private OmegaFrame frame;
	private ArrayList<ArtificialIntelligence> ais;
	private int turnCount;
	@Setter
	private Board board;
//	private ArrayList<Board> board_history;

	public OmegaLogic2(Board board) {
		this.board = board;
		turnCount = 1;
		
		ais = new ArrayList<ArtificialIntelligence>();
		AlphaBetaNegaMax abnm = new AlphaBetaNegaMax(0);
		ais.add(abnm);
		GwensExperimental gw2 = new GwensExperimental(1);
		ais.add(gw2);
//		AlphaBetaNegaMax abnm2 = new AlphaBetaNegaMax(1);
//		ais.add(abnm2);
//		HumanPlayer human = new HumanPlayer(1);
//		ais.add(human);
		
//		RandomMover mover = new RandomMover(1);
//		ais.add(mover);
//		board_history = new ArrayList<Board>();
	}
	
	public void addFrame(OmegaFrame frame) {
		this.frame = frame;
		for(ArtificialIntelligence ai:ais) {
			if(ai.getClass().equals(GwensExperimental.class)) {
				ai.addObserver(frame.getBoardPanel());
			} else if(ai.getClass().equals(HumanPlayer.class)) {
				frame.addMouseListener((HumanPlayer)ai);
				ai.addObserver(frame.getBoardPanel());
			}
		}
	}
	
	@Override
	public void run() {
		while(!gameOver(board)) {
			frame.repaint();
//			board_history.add(board.clone());
			
//			System.out.println("Searching move : " + ais.get(turnCount - 1).getName());
			Move move = ais.get(turnCount - 1).getMove(board.clone());
			
			System.out.println("AI: " + ais.get(turnCount - 1).getName() + " move: W:"
					+ move.getFields().get(0).getXy().x + "," + move.getFields().get(0).getXy().y + " |B:"
					+ move.getFields().get(1).getXy().x + "," + move.getFields().get(1).getXy().y);
			
			if(isValidMove(move)) {
				for (int i = 0; i < move.getFields().size(); i++) {
					board.placeStone(move.getFields().get(i).getXy(), i + 1);
				}
				System.out.println(board.getHash());
				
				turnCount %= OmegaMain.NUMBER_OF_PLAYERS;
				turnCount++;
				setChanged();
				notifyObservers();
			} else {
				System.out.println("Invalid MOVE by " + ais.get(turnCount - 1).getName());
			}
		}
	}
	
	public boolean gameOver(Board board) {
		int emptyFields = board.getFields().size();
		for (Integer parent : board.getParents_black().values()) {
			emptyFields -= parent;
		}
		for (Integer parent : board.getParents_white().values()) {
			emptyFields -= parent;
		}
		if (emptyFields >= (OmegaMain.NUMBER_OF_PLAYERS * OmegaMain.NUMBER_OF_PLAYERS)) {
			return false;
		}
		return true;
	}

	public boolean isValidMove(Move move) {
		for(Field field : move.getFields()) {
			if(field.getValue() != 0)
				return false;
		}
		return true;
	}
}
