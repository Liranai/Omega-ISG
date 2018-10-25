package logic.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;
import model.Node;
import model.util.Pair;

public class GwensExperimental extends ArtificialIntelligence{

	public static int MAX_DEPTH = 2;
	public static final int PROMISING_FIELDS_TO_CONSIDER = 10;
	
	private int turn_counter;
	private static int nodes = 0;
	private HashMap<Point, Pair<Integer, Integer>> heatMap;
	
	public GwensExperimental(int number) {
		super(number);
		turn_counter = 0;
	}

	@Override
	public String getName() {
		return "Gwen's Experimental AI";
	}

	@Override
	public Move getMove(Board board) {
		nodes = 0;
		turn_counter++;
		if(turn_counter > 5)
			MAX_DEPTH = 3;
		if(turn_counter > 8)
			MAX_DEPTH = 4;
		long time = System.currentTimeMillis();
//		turn_counter++;
		
//		if(turn_counter % 5 == 0) {
//			MAX_DEPTH++;
//		}
		
		heatMap = generateHeatMap(board);
		
		setChanged();
		notifyObservers(heatMap);
		
		AINode root = new AINode(board.clone(),null,null);
		Pair<Long, AINode> result = alphaBetaNega(root, 0, Long.MIN_VALUE, Long.MAX_VALUE, 0, OmegaMain.NUMBER_OF_PLAYERS);
		System.out.println("BEST VALUE " + result.getFirst());
		
		AINode maxNode = result.getSecond();
		AINode maxNode2 = null;
		while(maxNode.getParent() != root) {
			maxNode2 = maxNode;
			maxNode = maxNode.getParent();
		}
		
		Move move = new Move(maxNode.getMove().getField(), maxNode2.getMove().getField());
		System.out.println("NODES EXPLORED: " + nodes + " in " + (System.currentTimeMillis() - time));
		return move;
	}		
	
	private ArrayList<AIMove> getMoves(HashMap<Point, Pair<Integer,Integer>> map, Board board, int number_of_nodes, int colour){
			ArrayList<AIMove> availableMoves = new ArrayList<AIMove>();
			
			
			
			for(Field field : board.getFields().values()) {
				if(field.getValue() == 0) {
					availableMoves.add(new AIMove(field, colour));
				}
			}
			
			return availableMoves;
	}
	
	private Pair<Long, AINode> alphaBetaNega(AINode state, int depth, long alpha, long beta, int step_number, int total_steps) {
		nodes++;
//		System.out.println("abNega " + depth + " s:" + step_number + " max:" + total_steps);
		if(state.isTerminal() || depth == MAX_DEPTH) {
			return new Pair<Long, AINode>(state.evaluate((depth + this.playerNumber) % 2), state);
		} else {
			long score = Long.MIN_VALUE;
			AINode select = null;
			ArrayList<AIMove> moves = getMoves(generateHeatMap(state.getBoard()), state.getBoard(), PROMISING_FIELDS_TO_CONSIDER, step_number + 1);
			for(AIMove childMove : moves) {
				long value = 0;
				Pair<Long, AINode> result;
				if(step_number < total_steps - 1) {
					result = alphaBetaNega(new AINode(state.getBoard().clone(), childMove, state), depth, alpha, beta, step_number + 1, total_steps);
					value = result.getFirst();
				} else {
					result = alphaBetaNega(new AINode(state.getBoard().clone(), childMove, state), depth + 1, -beta, -alpha, 0, total_steps);
					value = -result.getFirst();
				}
				if(value > score) {score = value; select = result.getSecond();}
				if(score > alpha) alpha = score;
				if(score >= beta) break;
			}
			return new Pair<Long, AINode>(score, select);
		}
	}
	
//	public long alphaBeta(Node node, int depth, long alpha, long beta, boolean min_max) {
//		// viewer.drawState(node.getState());
//		node.calculateValue(super.playerNumber);
//		if (depth == MAX_DEPTH) {
//			return node.getValue();
//		}
//		ArrayList<Move> moves = generateMoves(generateHeatMap(node.getBoard()), node.getBoard(), POMISING_FIELDS_TO_CONSIDER);
//		if(depth < 2)
////			System.out.println("Depth: " + depth);
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
	
	private HashMap<Point, Pair<Integer, Integer>> generateHeatMap(Board board) {
		Pair<HashSet<Pair<Integer, ArrayList<Point>>>, HashSet<Pair<Integer, ArrayList<Point>>>> groups = getGroups(board.clone(), super.playerNumber);
		HashMap<Point, Pair<Integer, Integer>> localHeatMap = new HashMap<Point, Pair<Integer, Integer>>();
		
		for(Field field : board.getFields().values()) {
			if(field.getValue() != 0) {
				for(Field neighbour : field.getNeighbours())
					if(neighbour != null)
					localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(0,0));
			} else {
				if(!localHeatMap.containsKey(field.getXy())) {
					localHeatMap.put(field.getXy(), new Pair<Integer,Integer>((int)( (field.getXy().distance(0,0) / 18.0) * 255), (int)((field.getXy().distance(0,0) / 18.0) * 255)));
				}
			}
		}
		
		for(Pair<Integer, ArrayList<Point>> group : (this.playerNumber == 1 ? groups.getFirst() : groups.getSecond())) {
			switch(group.getFirst()){
				case 1:
					for(Field neighbour : board.getFields().get(group.getSecond().get(0)).getNeighbours()) {
						if(neighbour != null)
							localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(localHeatMap.get(neighbour.getXy()).getFirst() + 100, localHeatMap.get(neighbour.getXy()).getSecond() - 100));
					}
					break;
				case 2:
					for(Point p : group.getSecond()) {
						for(Field neighbour : board.getFields().get(p).getNeighbours()) {
							if(neighbour != null && neighbour.getValue() == 0)
								localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(localHeatMap.get(neighbour.getXy()).getFirst() + 50, localHeatMap.get(neighbour.getXy()).getSecond()));
						}
					}
					break;
				default:
					for(Point p : group.getSecond()) {
						for(Field neighbour : board.getFields().get(p).getNeighbours()) {
							if(neighbour != null)
								localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(localHeatMap.get(neighbour.getXy()).getFirst() - 50, localHeatMap.get(neighbour.getXy()).getSecond() + 80));
						}
					}
					break;
			}
		}
		for(Pair<Integer, ArrayList<Point>> group : (this.playerNumber == 1 ? groups.getSecond() : groups.getFirst())) {
			switch(group.getFirst()){
				case 1:
					for(Field neighbour : board.getFields().get(group.getSecond().get(0)).getNeighbours()) {
						if(neighbour != null)
						localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(localHeatMap.get(neighbour.getXy()).getFirst() + 100, localHeatMap.get(neighbour.getXy()).getSecond() - 100));
					}
					break;
				case 2:
					for(Point p : group.getSecond()) {
						for(Field neighbour : board.getFields().get(p).getNeighbours()) {
							if(neighbour != null)
								localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(localHeatMap.get(neighbour.getXy()).getFirst() - 20, localHeatMap.get(neighbour.getXy()).getSecond()));
						}		
					}
					break;
				default:
					for(Point p : group.getSecond()) {
						for(Field neighbour : board.getFields().get(p).getNeighbours()) {
							if(neighbour != null)
								localHeatMap.put(neighbour.getXy(), new Pair<Integer, Integer>(localHeatMap.get(neighbour.getXy()).getFirst() - 50, localHeatMap.get(neighbour.getXy()).getSecond() + 20));
							}
					}
					break;
			}
		}
		return localHeatMap;
	}
	
	private Pair<HashSet<Pair<Integer, ArrayList<Point>>>, HashSet<Pair<Integer, ArrayList<Point>>>> getGroups(Board board, int player){
		HashSet<Pair<Integer, ArrayList<Point>>> playerSet = new HashSet<Pair<Integer, ArrayList<Point>>>();
		HashSet<Pair<Integer, ArrayList<Point>>> opponentSet = new HashSet<Pair<Integer, ArrayList<Point>>>();
		
		for(Field f : board.fields.values()) {
			if(f.getValue() == player + 1) {
				playerSet.add(countGroup(board, f, new Pair<Integer, ArrayList<Point>>(0, new ArrayList<Point>()), player + 1));
			} else if(f.getValue() != 0) {
				opponentSet.add(countGroup(board, f, new Pair<Integer, ArrayList<Point>>(0, new ArrayList<Point>()), ((player + 1) % 2) + 1 ));
			}
		}
		
		Pair<HashSet<Pair<Integer, ArrayList<Point>>>, HashSet<Pair<Integer, ArrayList<Point>>>> groups = new Pair<HashSet<Pair<Integer, ArrayList<Point>>>, HashSet<Pair<Integer, ArrayList<Point>>>>(playerSet, opponentSet);
		return groups;
	}
	
	private Pair<Integer, ArrayList<Point>> countGroup(Board board, Field f, Pair<Integer, ArrayList<Point>> list, int originalValue) {
		if(f.getValue() != originalValue) {
			return list;
		} else {
			list.setFirst(list.getFirst() + 1);
			list.getSecond().add(f.getXy());
			f.setValue(0);
			for(Field neighbour : f.getNeighbours()) {
				if(neighbour != null) {
					countGroup(board, neighbour, list, originalValue);
				}
			}
		}
		return list;
	}
}
