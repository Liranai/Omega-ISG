package model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import main.OmegaMain;

@Getter
public class Node {

	private Move move;
	private Board board;
	@Setter
	private long value, abvalue;
	private Node parent;
	private ArrayList<Node> children;
	
	public Node(Board board, Move move, Node parent) {
		this.board = board;
		this.move = move;
		this.parent = parent;
		children = new ArrayList<Node>();
	}
	
	public void calculateValue(int player) {
		if(move != null) {
			makeMove();
		}
		long[] scores = board.getScore();
		if(player == 1) {
			value = scores[1] - scores[0];
		} else if (player == 0){
			value = scores[0] - scores[1];
		}
	}
	
	private boolean makeMove() {
		if(board!=null && move!=null) {
			for (int i = 0; i < move.getFields().size(); i++) {
				board.getFields().get(move.getFields().get(i).getXy()).setValue(i + 1);
			}
			return true;
		}
		return false;
	}
	
	public void setChildren(ArrayList<Move> moves) {
		for(Move move : moves) {
			children.add(new Node(board.clone(), move, this));
		}
	}

	public boolean isTerminal() {
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
}
