package logic.ai;

import java.util.ArrayList;
import java.util.Random;

import logic.OmegaLogic;
import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;
import model.Node;

public class AlphaBetaNegaMax extends ArtificialIntelligence {

	private static int MAX_DEPTH = 2;
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

//	@Override
//	public Move getMove(Board board) {
//		turn_counter++;
//		if(turn_counter < 2 * board.boardSize) {
//			Random rand = new Random(System.nanoTime());
//			ArrayList<Move> moves = OmegaLogic.getLegalMoves(board);
//			for(Move move: moves) {
//				for(Field field : move.getFields().get(playerNumber).getNeighbours()) {
//					if(field != null && field.getValue() == playerNumber)
//						return move;
//				}
//			}
//			
//			return moves.get(rand.nextInt(moves.size())); 
//		}
//		
//
//		Node maxNode = null;
//		for (Move move : OmegaLogic.getLegalMoves(board)) {
//			Node root = new Node(board.clone(), move, null);
//			root.setAbvalue(alphaBeta(root, 0, Long.MIN_VALUE, Long.MAX_VALUE, true));
//			if (maxNode == null) {
//				maxNode = root;
//			} else if (root.getAbvalue() > maxNode.getAbvalue()) {
//				maxNode = root;
//			}
//		}
//
//		return maxNode.getMove();
//	}
	
	public Move getMove(Board board) {
		AINode root = new AINode(board.clone(),null,null);
		best = root;
		bestscore = 0;
		long value = alphaBetaNega(root, MAX_DEPTH, Long.MIN_VALUE, Long.MAX_VALUE, 0, OmegaMain.NUMBER_OF_PLAYERS);
		System.out.println("BEST VALUE " + value);
		
		AINode maxNode = best;
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

	private long alphaBetaNega(AINode state, int depth, long alpha, long beta, int step_number, int total_steps) {
//		System.out.println("abNega " + depth + " s:" + step_number + " max:" + total_steps);
		if(state.isTerminal() || depth == 0) {
//			System.out.println("abNega " + depth + " s:" + step_number + " max:" + total_steps);
			return state.evaluate(this.playerNumber);
		} else {
			long score = Long.MIN_VALUE;
			for(AIMove childMove : getMoves(state.getBoard(), step_number + 1)) {
				long value = 0;
				if(step_number < total_steps) {
					value = alphaBetaNega(new AINode(state.getBoard().clone(), childMove, state), depth, alpha, beta, step_number + 1, total_steps);
				} else {
					value = -alphaBetaNega(new AINode(state.getBoard().clone(), childMove, state), depth - 1, -beta, -alpha, 0, total_steps);
				}
				if(value > score) score = value;
				if(score > alpha) alpha = score;
				if(score >= beta) break;
				if(score > bestscore) {
					bestscore = score;
					best = new AINode(state.getBoard().clone(), childMove, state);
				}
			}
			return score;
		}
		
	}
	
//	public long alphaBeta(Node node, int depth, long alpha, long beta, boolean min_max) {
//		// System.out.println(depth);
//		// viewer.drawState(node.getState());
//		node.calculateValue(super.playerNumber);
//		if (depth == MAX_DEPTH) {
//			return node.getValue();
//		}
//		ArrayList<Move> moves = OmegaLogic.getLegalMoves(node.getBoard());
//		node.setChildren(moves);
//		if (min_max) {
//			long value = Long.MIN_VALUE;
//			for (Node child : node.getChildren()) {
//				value = Math.max(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//				alpha = Math.max(alpha, value);
//				if (beta <= alpha)
//					break;
//			}
//			node.getChildren().clear();
//			return value;
//		} else {
//			long value = Long.MAX_VALUE;
//			for (Node child : node.getChildren()) {
//				value = Math.min(value, alphaBeta(child, depth + 1, alpha, beta, !min_max));
//				beta = Math.min(beta, value);
//				if (beta <= alpha)
//					break;
//			}
//			node.getChildren().clear();
//			return value;
//		}
//	}

}
