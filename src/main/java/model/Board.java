package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import lombok.Getter;
import main.OmegaMain;
import model.Field.Neighbour;
import ui.OmegaGamePanel;

@Getter
public class Board {

	public static final int[][] DIRECTIONS = { {0, -1}, {1, -1}, {1, 0}, {0,1}, {-1, 1}, {-1, 0}}; 
	public int boardSize;
	public HashMap<Point, Field> fields;
	public HashMap<Field, Integer> parents_white, parents_black;
	public long[] hashedFields;
	public long hash;
	
	public static long[] hashedEmpty, hashedWhites, hashedBlacks;
	
	public Board (int n) {
		boardSize = n;
		hash = 0;
		OmegaGamePanel.CENTER_OFFSET_X = (n -1) * OmegaGamePanel.SQUARESIZE;
		OmegaGamePanel.CENTER_OFFSET_Y = (int)(Math.ceil((n - 1) * 0.75 * OmegaGamePanel.SQUARESIZE + 5));
		
		hashedFields = new long[ ((n*n) + (n * (n-1)) + ((n-1) * (n-1)))];
		
		hashedEmpty = new long[hashedFields.length];
		hashedWhites = new long[hashedFields.length];
		hashedBlacks = new long[hashedFields.length];
		HashSet<Long> uniqueHashes = new HashSet<Long>();
		
		Random rand = new Random(System.currentTimeMillis());
		
		for(int i = 0; i< hashedFields.length; i++) {
			long rand_num = rand.nextInt(Integer.MAX_VALUE);
			if(uniqueHashes.contains(rand_num)) {
				i--; System.out.println("Type-1 Error when generating hashes"); continue; 
			}
			uniqueHashes.add(rand_num);
			hashedEmpty[i] = rand_num;
			hash = hash ^ rand_num;
		}
		for(int i = 0; i< hashedFields.length; i++) {
			long rand_num = rand.nextInt(Integer.MAX_VALUE);
			if(uniqueHashes.contains(rand_num)) {
				i--; System.out.println("Type-1 Error when generating hashes"); continue; 
			}
			uniqueHashes.add(rand_num);
			hashedWhites[i] = rand_num;
		}
		for(int i = 0; i< hashedFields.length; i++) {
			long rand_num = rand.nextInt(Integer.MAX_VALUE);
			if(uniqueHashes.contains(rand_num)) {
				i--; System.out.println("Type-1 Error when generating hashes"); continue; 
			}
			uniqueHashes.add(rand_num);
			hashedBlacks[i] = rand_num;
		}
		if(OmegaMain.DEBUG >= 2)
			for(int i = 0; i< hashedFields.length; i++) {
	//			System.out.println(hashedEmpty[i] + " " + hashedWhites[i] + " " + hashedvalueBlacks[i]);
//				System.out.println(hashedWhites[i] ^ hashedBlacks[i]);
			}
		
		parents_white = new HashMap<Field, Integer>();
		parents_black = new HashMap<Field, Integer>();
		fields = new HashMap<Point, Field>();
		
		for(int i = 1-n; i < n; i++) {
			for(int j = 1-n; j<n; j++) {
				if(j + i >= n || j + i <= -n)
					continue;
				Field tField = new Field(i,j);
				fields.put(new Point(i,j),tField);
			}
		}
		createLinking();
	}
	
	private Board (HashMap<Point, Field> fields, int boardSize, long[] hashedFields, HashMap<Field, Integer> parents_white, HashMap<Field, Integer> parents_black) {
		this.fields = fields;
		this.hashedFields = hashedFields;
		createLinking();
		this.boardSize = boardSize;
		this.parents_white = parents_white;
		this.parents_black = parents_black;
	}
	
//	public void undoMove(Move move) {
//		for (int i = 0; i < move.getFields().size(); i++) {
////			board.placeStone(move.getFields().get(i).getXy(), i + 1);
//			Field target = move.getFields().get(i);
//		}
//	}
	
	/**
	 * Make sure colour is 1-2, does not check for errors.
	 *
	 * @param p location of field
	 * @param colour player number ranging from 1 to 4
	 */
	public void placeStone(Point p, int colour) {
//		System.out.println("Placing Stone: " + p);
		Field target = fields.get(p);
		target.setParent(target.getXy());
		target.setValue(colour);
		int group_size = 1;
		
		int index  = pointToIndex(p);
		hash = hash ^ (hashedFields[index]);
		hash = hash ^ (colour == 1 ? hashedWhites[index] :hashedBlacks[index]);
		
		HashSet<Field> neighbour_parents = new HashSet<Field>();
		for(Field neighbour : target.getNeighbours()) {
			if(neighbour != null && neighbour.getParent() != null) {
//				System.out.println("Searching Neighbour: " + neighbour.getXy());
				if(neighbour.getValue() == target.getValue()) {
					Field parent = fields.get(neighbour.getParent());
//					System.out.println(target.getXy() + " " + neighbour.getXy() + " " + neighbour.getParent());
					do {
						parent = fields.get(parent.getParent());
					}while(!parent.getXy().equals(parent.getParent()));
					neighbour_parents.add(parent);
				}
			}
		}
		for(Field parent : neighbour_parents) {
//			group_size += parent.getGroup_size();
			parent.setParent(target.getXy());
			if(colour == 1) {
				group_size += parents_white.remove(parent);
			}else {
				group_size += parents_black.remove(parent);
			}
		}
		if(colour == 1) {
			parents_white.put(target, group_size);
		}else {
			parents_black.put(target, group_size);
		}
//		target.setGroup_size(group_size);
		
		if(OmegaMain.DEBUG > 3) {
		System.out.println("Parents: ");
			if(neighbour_parents.size() != 0) {
				for(Field field : neighbour_parents) {
					System.out.print("\t" + field.getXy());
				}
			}
			System.out.println();
		}
	}
	
	public Point indexToPoint(int index) {
		int y = 0;
		int x = 0;
		int count = index;
		if (index < (hashedFields.length/2 + boardSize)) {
			for (int i = 0; i < boardSize; i++) {
				if (count < (boardSize + i)) {
					x = count;
				} else {
					count -= (boardSize + i);
					y++;
				}
			}

			y -= boardSize - 1;
			x -= ((boardSize - 1) + y);
		} else {
			count -= hashedFields.length/2 + (boardSize);
			for(int i = 1; i < boardSize; i++) {
				if(count < (2*boardSize - (i + 1))) {
					x = count;
					y = i;
					break;
				} else {
					count -= (2 * boardSize - (i + 1));
				}
			}
			x -= boardSize - 1;
			
		}
		return new Point(x, y);
	}
	
	/**
	 * Given a point in the board, return it's index in a linear collection of fields.
	 * Disgusting method, but works.
	 * @param point
	 * @return
	 */
	public int pointToIndex(Point point) {
		int index = 0;
		int x = point.x + (boardSize-1);
		int y = point.y + (boardSize-1);
		
		if(y <= boardSize -1) {
			index = y * (boardSize);
			for(int i = 0; i < y; i++) {
				index += i;
			}
			index += x - (boardSize-1 - y);
		} else {
			index = y * (boardSize);
			for(int i = boardSize - 1; i > y - (boardSize-1); i--) {
				index += i;
			}
			index += (y - boardSize) * (boardSize);
			index += x + 1;
		}
		return index;
	}
	
	public long[] getScore() {
		long[] scores = new long[OmegaMain.NUMBER_OF_PLAYERS];
		long score = 1;
		for(Integer inter : parents_white.values()) {
			score *= inter;
		}
		scores[0] = score;
		score = 1;
		for(Integer inter : parents_black.values()) {
			score *= inter;
		}
		scores[1] = score;
		return scores;
	}
	
//	public long[] getScore() {
//		Board tempBoard = this.clone();
//		
//		ArrayList<ArrayList<Integer>> playerGroups = new ArrayList<ArrayList<Integer>>();
//		for(int i = 0; i< OmegaMain.NUMBER_OF_PLAYERS; i++) {
//			ArrayList<Integer> groups = new ArrayList<Integer>();
//			playerGroups.add(groups);
//		}
//		
//		for(Field f : tempBoard.getFields().values()) {
//			if(f.getValue() != 0) {
//				int value = f.getValue();
//				int n = countGroup(tempBoard, f, 0, value);
//				playerGroups.get(value - 1).add(n);
//			}
//		}
//		
//		long[] scores = new long[OmegaMain.NUMBER_OF_PLAYERS];
//		for(int i = 0; i< OmegaMain.NUMBER_OF_PLAYERS; i++) {
//			long score = 1;
//			for(int j = 0; j < playerGroups.get(i).size(); j++) {
//				if(playerGroups.get(i).get(j) != 0)
//					score *= playerGroups.get(i).get(j);
//			}
//			scores[i] = score;
//		}
//		return scores;
//	}
	
	public ArrayList<Field> getFieldRange(int low, int high) {
		ArrayList<Field> tempFields = new ArrayList<Field>();
		if(high > hashedFields.length)
			high = hashedFields.length;
		for( int i = low; i < high; i++) {
			tempFields.add(fields.get(indexToPoint(i)));
		}
		return tempFields;
	}
	
	private int countGroup(Board board, Field f, int number, int originalValue) {
		if(f.getValue() != originalValue) {
			return 0;
		} else {
			number++;
			f.setValue(0);
			for(Field neighbour : f.getNeighbours()) {
				if(neighbour != null) {
					number += countGroup(board, neighbour, 0, originalValue);
				}
			}
		}
		return number;
	}
	
	@Override
	public Board clone() {
		HashMap<Point, Field> clonedFields = new HashMap<Point, Field>();
		for(Point point : fields.keySet()) {
			clonedFields.put(new Point(point.x, point.y), fields.get(point).clone());
		}
		HashMap<Field, Integer> clonedWhiteParents = new HashMap<Field, Integer>();
		for(Field point : parents_white.keySet()) {
			clonedWhiteParents.put(point.clone(), parents_white.get(point));
		}
		HashMap<Field, Integer> clonedBlackParents = new HashMap<Field, Integer>();
		for(Field point : parents_black.keySet()) {
			clonedBlackParents.put(point.clone(), parents_black.get(point));
		}
		return new Board(clonedFields, boardSize, hashedFields, clonedWhiteParents, clonedBlackParents);
	}
	
	private void createLinking() {
		for(Field field : fields.values()) {
			Point position = field.getXy();
			for(int i = 0; i< DIRECTIONS.length; i++) {
				if(fields.containsKey(new Point(position.x + DIRECTIONS[i][0], position.y + DIRECTIONS[i][1]))){
					field.setNeighbour(fields.get(new Point(position.x + DIRECTIONS[i][0], position.y + DIRECTIONS[i][1])), Neighbour.values()[i]);
					if(OmegaMain.DEBUG >= 3)
						System.out.println("SET " + new Point(position.x + DIRECTIONS[i][0], position.y + DIRECTIONS[i][1]) + " to " + Neighbour.values()[i] + " for \t" + field.getXy());
				}
			}
		}
	}
}
