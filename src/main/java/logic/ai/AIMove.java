package logic.ai;

import lombok.Getter;
import model.Field;

@Getter
public class AIMove {

	private Field field;
	private int colour;
	
	public AIMove(Field field, int colour) {
		this.field = field;
		this.colour = colour;
	}
}
