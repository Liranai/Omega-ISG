package logic.ai;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;

public class HumanPlayer extends ArtificialIntelligence implements MouseListener{
	
	private Field selection;
	private Board currentBoard;

	public HumanPlayer(int number) {
		super(number);
	}

	@Override
	public String getName() {
		return "HumanPlayer";
	}

	@Override
	public Move getMove(Board board) {
		currentBoard = board;
		selection = null;
		ArrayList<Field> move = new ArrayList<Field>();
		while(move.size() < OmegaMain.NUMBER_OF_PLAYERS) {
			if(selection != null) {
				move.add(selection);
				selection = null;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Field[] array = new Field[OmegaMain.NUMBER_OF_PLAYERS];
		for(int i = 0; i < move.size(); i++)
			array[i] = move.get(i);
		return new Move(array);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (currentBoard != null) {
			for (Field field : currentBoard.getFields().values()) {
				if (field.getHex().contains(e.getPoint())) {
					selection = field;
				}
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
