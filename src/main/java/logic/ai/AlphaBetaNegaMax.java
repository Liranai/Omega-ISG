package logic.ai;

import java.util.ArrayList;

import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;
import model.util.Pair;

public class AlphaBetaNegaMax extends ArtificialIntelligence {

	private static int MAX_DEPTH = 3;
	private int turn_counter;
	private static AINode best;
	private static long bestscore;

	public AlphaBetaNegaMax(int number) {
		super(number);
		turn_counter = 0;
	}

	@Override
	public String getName() {
		return "AlphaBetaNegaMax";
	}

	
	public Move getMove(Board board) {
		AINode root = new AINode(board.clone(),null,null);
		best = root;
		bestscore = 0;
		Pair<Long, AINode> result = alphaBetaNega(root, 0, Long.MIN_VALUE, Long.MAX_VALUE, 0, OmegaMain.NUMBER_OF_PLAYERS);
		System.out.println("BEST VALUE " + result.getFirst());
		
		AINode maxNode = result.getSecond();
		AINode maxNode2 = null;
		while(maxNode.getParent() != root) {
			maxNode2 = maxNode;
			maxNode = maxNode.getParent();
		}
		
		Move move = new Move(maxNode.getMove().getField(), maxNode2.getMove().getField());
		return move;
	}
	
	private ArrayList<AIMove> getMoves(Board board, int colour){
		ArrayList<AIMove> availableMoves = new ArrayList<AIMove>();
		
		for(Field field : board.getFields().values()) {
			if(field.getValue() == 0) {
				availableMoves.add(new AIMove(field, colour));
			}
		}
		
		return availableMoves;
	}

	private Pair<Long, AINode> alphaBetaNega(AINode state, int depth, long alpha, long beta, int step_number, int total_steps) {
//		System.out.println("abNega " + depth + " s:" + step_number + " max:" + total_steps);
		if(state.isTerminal() || depth == MAX_DEPTH) {
			return new Pair<Long, AINode>(state.evaluate((depth + this.playerNumber) % 2), state);
		} else {
			long score = Long.MIN_VALUE;
			AINode select = null;
			for(AIMove childMove : getMoves(state.getBoard(), step_number + 1)) {
				long value = 0;
				Pair<Long, AINode> result;
				if(step_number < total_steps - 1) {
					result = alphaBetaNega(new AINode(state.getBoard().clone(), childMove, state), depth, alpha, beta, step_number + 1, total_steps);
					value = result.getFirst();
				} else {
					result = alphaBetaNega(new AINode(state.getBoard().clone(), childMove, state), depth + 1, -beta, -alpha, 0, total_steps);
					value = -result.getFirst();
				}
				if(value > score) {score = value; select = result.getSecond(); System.out.println("abNega " + depth + " s:" + step_number + " max:" + total_steps);}
				if(score > alpha) alpha = score;
				if(score >= beta) break;
			}
			return new Pair<Long, AINode>(score, select);
		}
		
	}
}
