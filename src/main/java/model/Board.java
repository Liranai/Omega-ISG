package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
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
	public long[] hashedFields;
	
	public static long[] hashedEmpty, hashedWhites, hashedBlacks;
	
	public Board (int n) {
		boardSize = n;
		OmegaGamePanel.CENTER_OFFSET_X = (n -1) * OmegaGamePanel.SQUARESIZE;
		OmegaGamePanel.CENTER_OFFSET_Y = (int)(Math.ceil((n - 1) * 0.75 * OmegaGamePanel.SQUARESIZE + 5));
		
		hashedFields = new long[ ((n*n) + (n * (n-1)) + ((n-1) * (n-1)))];
		
		hashedEmpty = new long[hashedFields.length];
		hashedWhites = new long[hashedFields.length];
		hashedBlacks = new long[hashedFields.length];
		
		Random rand = new Random(System.currentTimeMillis());
		for(int i = 0; i< hashedFields.length; i++) {
			hashedEmpty[i] = rand.nextLong();
			hashedWhites[i] = rand.nextLong();
			hashedBlacks[i] = rand.nextLong();
		}
		
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
	
	private Board (HashMap<Point, Field> fields, int boardSize, long[] hashedFields) {
		this.fields = fields;
		this.hashedFields = hashedFields;
		createLinking();
		this.boardSize = boardSize;
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
		Board tempBoard = this.clone();
		
		ArrayList<ArrayList<Integer>> playerGroups = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i< OmegaMain.NUMBER_OF_PLAYERS; i++) {
			ArrayList<Integer> groups = new ArrayList<Integer>();
			playerGroups.add(groups);
		}
		
		for(Field f : tempBoard.getFields().values()) {
			if(f.getValue() != 0) {
				int value = f.getValue();
				int n = countGroup(tempBoard, f, 0, value);
				playerGroups.get(value - 1).add(n);
			}
		}
		
		long[] scores = new long[OmegaMain.NUMBER_OF_PLAYERS];
		for(int i = 0; i< OmegaMain.NUMBER_OF_PLAYERS; i++) {
			long score = 1;
			for(int j = 0; j < playerGroups.get(i).size(); j++) {
				if(playerGroups.get(i).get(j) != 0)
					score *= playerGroups.get(i).get(j);
			}
			scores[i] = score;
		}
		return scores;
	}
	
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
		return new Board(clonedFields, boardSize, hashedFields);
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
