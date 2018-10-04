package logic.ai;

import java.util.ArrayList;
import java.util.Random;

import logic.OmegaLogic;
import model.Board;
import model.Field;
import model.Move;
import model.Node;

public class AlphaBetaNegaMax extends ArtificialIntelligence {

	private static int MAX_DEPTH = 1;
	private int turn_counter;

	public AlphaBetaNegaMax(int number) {
		super(number);
		turn_counter = 0;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "AlphaBetaNegaMax";
	}

	@Override
	public Move getMove(Board board) {
		turn_counter++;
		if(turn_counter < 2 * board.boardSize) {
			Random rand = new Random(System.nanoTime());
			ArrayList<Move> moves = OmegaLogic.getLegalMoves(board);
			for(Move move: moves) {
				for(Field field : move.getFields().get(playerNumber).getNeighbours()) {
					if(field != null && field.getValue() == playerNumber)
						return move;
				}
			}
			
			return moves.get(rand.nextInt(moves.size())); 
		}
		

		Node maxNode = null;
		for (Move move : OmegaLogic.getLegalMoves(board)) {
			Node root = new Node(board.clone(), move, null);
			root.setAbvalue(alphaBeta(root, 0, Long.MIN_VALUE, Long.MAX_VALUE, true));
			if (maxNode == null) {
				maxNode = root;
			} else if (root.getAbvalue() > maxNode.getAbvalue()) {
				maxNode = root;
			}
		}

		return maxNode.getMove();
	}

	public long alphaBeta(Node node, int depth, long alpha, long beta, boolean min_max) {
		// System.out.println(depth);
		// viewer.drawState(node.getState());
		node.calculateValue(super.playerNumber);
		if (depth == MAX_DEPTH) {
			return node.getValue();
		}
		ArrayList<Move> moves = OmegaLogic.getLegalMoves(node.getBoard());
		node.setChildren(moves);
		if (min_max) {
			long value = Long.MIN_VALUE;
			for (Node child : node.getChildren()) {
				value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
				alpha = Math.max(alpha, value);
				if (beta <= alpha)
					break;
			}
			node.getChildren().clear();
			return value;
		} else {
			long value = Long.MAX_VALUE;
			for (Node child : node.getChildren()) {
				value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
				beta = Math.min(beta, value);
				if (beta <= alpha)
					break;
			}
			node.getChildren().clear();
			return value;
		}
	}

}
