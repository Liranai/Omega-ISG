package logic;

import java.util.ArrayList;
import java.util.Observable;

import logic.ai.AlphaBetaNegaMax;
import logic.ai.ArtificialIntelligence;
import logic.ai.HumanPlayer;
import logic.ai.RandomMover;
import lombok.Getter;
import main.OmegaMain;
import model.Board;
import model.Field;
import ui.OmegaFrame;

@Getter
public class OmegaLogic2 extends Observable implements Runnable{

	private OmegaFrame frame;
	private ArrayList<ArtificialIntelligence> ais;
	private int turnCount;
	private Board board;

	public OmegaLogic2(Board board) {
		this.board = board;
		turnCount = 0;
		
		ais = new ArrayList<ArtificialIntelligence>();
		AlphaBetaNegaMax abnm = new AlphaBetaNegaMax(0);
		ais.add(abnm);
//		HumanPlayer human = new HumanPlayer(1);
//		ais.add(human);
		
		RandomMover mover = new RandomMover(1);
		ais.add(mover);
	}
	
	public void addFrame(OmegaFrame frame) {
		this.frame = frame;
		for(ArtificialIntelligence ai:ais) {
			if(ai.getClass().equals(HumanPlayer.class)) {
				frame.addMouseListener((HumanPlayer)ai);
				ai.addObserver(frame.getBoardPanel());
			}
		}
	}
	
	@Override
	public void run() {
		
	}
	
	public boolean gameOver(Board board) {
		int emptyFields = board.getFields().size();
		for (Field parent : board.getParents_black().values()) {
			emptyFields -= parent.getGroup_size();
		}
		for (Field parent : board.getParents_white().values()) {
			emptyFields -= parent.getGroup_size();
		}
		if (emptyFields >= (OmegaMain.NUMBER_OF_PLAYERS * OmegaMain.NUMBER_OF_PLAYERS)) {
			return false;
		}
		return true;
	}

}
