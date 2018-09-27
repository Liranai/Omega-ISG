package logic.ai;

import java.util.Observable;

import lombok.Getter;
import model.Board;
import model.Move;

@Getter
public abstract class ArtificialIntelligence extends Observable {
	
	protected int playerNumber;
	
	public ArtificialIntelligence(int number) {
		playerNumber = number;
	}

	public abstract String getName();
	
	public abstract Move getMove(Board board);
}
