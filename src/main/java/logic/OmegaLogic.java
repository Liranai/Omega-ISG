package logic;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;

import logic.ai.AlphaBetaNegaMax;
import logic.ai.ArtificialIntelligence;
import logic.ai.GwensExperimental;
import logic.ai.HumanPlayer;
import logic.ai.RandomMover;
import lombok.Getter;
import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;
import ui.OmegaFrame;

@Getter
public class OmegaLogic extends Observable implements MouseListener, Runnable{

	private Board board;
	private OmegaFrame frame;
	private int turnCount;
	private ArrayList<ArtificialIntelligence> ais;
	
	public OmegaLogic(Board board) {
		this.board = board;
		turnCount = 1;
		
		ais = new ArrayList<ArtificialIntelligence>();
		GwensExperimental gw = new GwensExperimental(0);
		ais.add(gw);
		
		AlphaBetaNegaMax abnm = new AlphaBetaNegaMax(1);
		ais.add(abnm);
		
		
//		GwensExperimental gw2 = new GwensExperimental(1);
//		ais.add(gw2);
//		HumanPlayer human = new HumanPlayer(1);
//		ais.add(human);
//		for (int i = 1; i < OmegaMain.NUMBER_OF_PLAYERS; i++) {
//			RandomMover mover = new RandomMover(i);
//			ais.add(mover);
//		}
	}
	
	public void addFrame(OmegaFrame frame) {
		this.frame = frame;
		for(ArtificialIntelligence ai : ais) {
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
		while(!gameOver()) {
			frame.repaint();
			
			Move move = ais.get(turnCount - 1).getMove(board.clone());
			
			System.out.println("AI: " + ais.get(turnCount - 1).getName() + " move: W:"
					+ move.getFields().get(0).getXy().x + "," + move.getFields().get(0).getXy().y + " |B:"
					+ move.getFields().get(1).getXy().x + "," + move.getFields().get(1).getXy().y);
			
			if(validMove(move)) {
				for (int i = 0; i < move.getFields().size(); i++) {
					board.getFields().get(move.getFields().get(i).getXy()).setValue(i + 1);
				}
				turnCount %= OmegaMain.NUMBER_OF_PLAYERS;
				turnCount++;
				setChanged();
				notifyObservers();
			} else {
				System.out.println("INVALID MOVE MADE BY " + ais.get(turnCount - 1).getName());
			}
		}
		System.out.println("GAME OVER");
		long[] score = board.getScore();
		System.out.print("SCORE");
		for(int i = 0; i < score.length; i++) {
			System.out.print(" " + OmegaMain.players.values()[i] + ": " + score[i]);
		}
		
	}
	
	private boolean validMove(Move move) {
		for(Field field : move.getFields()) {
			if(field.getValue() != 0)
				return false;
		}
		return true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
//		for(Field field : board.getFields().values()) {
//			if(field.getHex().contains(e.getPoint())) {
//				field.setValue(turnCount);
//				frame.repaint();
//				
//				turnCount %= OmegaMain.NUMBER_OF_PLAYERS;
//				turnCount++;
//				if(OmegaMain.DEBUG >=2) {
//					System.out.println("x: " + field.getXy().x + " ,y: " + field.getXy().y);
//					System.out.println(field.getValue());
//				}
//			}
//		}
	}
	
	public boolean gameOver() {
		int emptyFields = 0;
		for(Field field : board.fields.values()) {
			if (field.getValue() == 0) {
				emptyFields++;
				if(emptyFields >= (OmegaMain.NUMBER_OF_PLAYERS * OmegaMain.NUMBER_OF_PLAYERS)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static ArrayList<Move> getLegalMoves(Board board){
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		Field[] fieldlist = new Field[OmegaMain.NUMBER_OF_PLAYERS];
		ArrayList<Field> emptyFields = new ArrayList<Field>();

		for (Field queryfield : board.getFields().values()) {
			if(queryfield.getValue() == 0)
				emptyFields.add(queryfield);
			}
		
		if(OmegaMain.NUMBER_OF_PLAYERS > 0) {
			for( Field fieldQ1 : emptyFields) {
				fieldlist[0] = fieldQ1;
				if(OmegaMain.NUMBER_OF_PLAYERS > 1) {
					for(Field fieldQ2 : emptyFields) {
						if(fieldlist[0] == fieldQ2)
							continue;
						fieldlist[1] = fieldQ2;
						if(OmegaMain.NUMBER_OF_PLAYERS > 2) {
							for(Field fieldQ3 : emptyFields) {
								if(fieldlist[0] == fieldQ3 || fieldlist[1] == fieldQ3)
									continue;
								fieldlist[2] = fieldQ3;
								if(OmegaMain.NUMBER_OF_PLAYERS > 3) {
									for(Field fieldQ4 : emptyFields) {
										if(fieldlist[0] == fieldQ4 || fieldlist[1] == fieldQ4 || fieldlist[2] == fieldQ4)
											continue;
										fieldlist[3] = fieldQ4;
										legalMoves.add(new Move(fieldlist));
									}
								} else {
									legalMoves.add(new Move(fieldlist));
								}
							}
						} else {
							legalMoves.add(new Move(fieldlist));
						}
					}
				} else {
					legalMoves.add(new Move(fieldlist));
				}
			}
		}
		
		if(OmegaMain.DEBUG > 1) {
			System.out.println("NUMBER OF MOVES FOUND: " + legalMoves.size());
		}
		return legalMoves;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
