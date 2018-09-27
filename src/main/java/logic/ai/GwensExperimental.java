package logic.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import model.Board;
import model.Field;
import model.Move;
import model.Node;
import model.util.Pair;

public class GwensExperimental extends ArtificialIntelligence{

	public static int MAX_DEPTH = 1;
	public static final int POMISING_FIELDS_TO_CONSIDER = 10;
	
	private int turn_counter;
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
		turn_counter++;
		if(turn_counter % 5 == 0) {
			MAX_DEPTH++;
		}
		
		heatMap = generateHeatMap(board);
		
		
		ArrayList<Move> moves = generateMoves(heatMap, board, POMISING_FIELDS_TO_CONSIDER);
		
		setChanged();
		notifyObservers(heatMap);
		
		Node maxNode = null;
		for (Move move : moves) {
			Node root = new Node(board.clone(), move, null);
			root.setAbvalue(alphaBeta(root, 0, Long.MIN_VALUE, Long.MAX_VALUE, true));
			if (maxNode == null) {
				maxNode = root;
			} else if (root.getAbvalue() > maxNode.getAbvalue()) {
				maxNode = root;
			}
		}
		
		System.out.println("MOVE " + super.playerNumber + ": W: " + maxNode.getMove().getFields().get(0).getXy().x + "," + maxNode.getMove().getFields().get(0).getXy().y + " B:" +maxNode.getMove().getFields().get(1).getXy().x + "," + maxNode.getMove().getFields().get(1).getXy().y);
		
		return maxNode.getMove();
	}		
	
	private ArrayList<Move> generateMoves(HashMap<Point, Pair<Integer,Integer>> map, Board board, int n){
		ArrayList<Move> moves = new ArrayList<Move>();
		
		HashMap<Point, Integer> flatmap = new HashMap<Point, Integer>();
		for(Point p : map.keySet()) {
			flatmap.put(p, map.get(p).getFirst() - map.get(p).getSecond());
		}
		
		Point[] jericho = new Point[n];
		
		int minValue = Integer.MAX_VALUE;
		int index = 0;
		for(Point p : flatmap.keySet()) {
			if(index < n) {
				jericho[index] = p;
				index++;
				if(flatmap.get(p) < minValue) {
					minValue = flatmap.get(p);
				}
			}
			if(flatmap.get(p) > minValue) {
				for(int i = 0; i < jericho.length; i++) {
					if(flatmap.get(jericho[i]) == minValue) {
						jericho[i] = p;
						minValue = flatmap.get(p);
						break;
					}
				}
				for(int i = 0; i < jericho.length; i++) {
					if(jericho[i] != null && flatmap.get(jericho[i]) < minValue)
						minValue = flatmap.get(jericho[i]);
				}
			}
		}
		
		for(int i = 0; i < jericho.length; i++) {
			for (int j = 0; j < jericho.length; j++) {
				if(i == j) continue;
				moves.add(new Move(board.getFields().get(jericho[i]), board.getFields().get(jericho[j])));
			}
		}
		
//		
//		for(Point p1 : map.keySet()) {
//			if(board.getFields().get(p1).getValue() != 0)
//				continue;
//			for(Point p2 : map.keySet()) {
//				if(p1.equals(p2))
//					continue;
//				if(board.getFields().get(p2).getValue() != 0)
//					continue;
//				moves.add(new Move(board.getFields().get(p1), board.getFields().get(p2)));
//			}
//		}
		
		return moves;
	}
	
	public long alphaBeta(Node node, int depth, long alpha, long beta, boolean min_max) {
		// viewer.drawState(node.getState());
		node.calculateValue(super.playerNumber);
		if (depth == MAX_DEPTH) {
			return node.getValue();
		}
		ArrayList<Move> moves = generateMoves(generateHeatMap(node.getBoard()), node.getBoard(), POMISING_FIELDS_TO_CONSIDER);
		if(depth < 2)
//			System.out.println("Depth: " + depth);
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
		
		for(Pair<Integer, ArrayList<Point>> group : groups.getFirst()) {
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
		for(Pair<Integer, ArrayList<Point>> group : groups.getSecond()) {
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
