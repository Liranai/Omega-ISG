package logic.ai;

import java.util.ArrayList;
import java.util.Random;

import main.OmegaMain;
import model.Board;
import model.Field;
import model.Move;

public class RandomMover extends ArtificialIntelligence{

	Random rand;
	public RandomMover(int number) {
		super(number);
		rand = new Random(System.currentTimeMillis());
	}

	@Override
	public String getName() {
		return "RandomMover";
	}

	@Override
	public Move getMove(Board board) {
		Field[] fields = new Field[OmegaMain.NUMBER_OF_PLAYERS];

		for (int i = 0; i < OmegaMain.NUMBER_OF_PLAYERS; i++) {
			ArrayList<Field> emptyFields = new ArrayList<Field>();
			empty: for (Field field : board.getFields().values()) {
				if (field.getValue() == 0) {
					for(int j = 0; j < fields.length; j++) {
						if(fields[j] != null && fields[j].getXy() == field.getXy())
							continue empty;
					}
					emptyFields.add(field);
				}
			}
			Field f = emptyFields.get(rand.nextInt(emptyFields.size() - 1));
			fields[i] = f;
		}
		return new Move(fields);
	}
}
