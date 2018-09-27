package logic.ai;

import model.Board;
import model.Move;

public class HumanPlayer extends ArtificialIntelligence{

	public HumanPlayer(int number) {
		super(number);
	}

	@Override
	public String getName() {
		return "HumanPlayer";
	}

	@Override
	public Move getMove(Board board) {
		
		return null;
	}

}
