package logic.ai;

import lombok.Getter;
import main.OmegaMain;
import model.Board;
import model.Field;

@Getter
public class AINode {

	private AIMove move;
	private Board board;
	private AINode parent;
	
	public AINode(Board board, AIMove move, AINode parent) {
		this.board = board;
		this.move = move;
		this.parent = parent;
		makeMove();
	}
	
	private void makeMove() {
		if(move != null)
		board.getFields().get(move.getField().getXy()).setValue(move.getColour());
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
	
	public long evaluate(int playerNumber) {
		long[] scores = board.getScore();
		long score = 0;
		for(int i = 0; i < scores.length; i++) {
			if(i == playerNumber) {
				score += scores[i];
			} else {
				score -= scores[i];
			}
		}
		return score;
	}
	
}
