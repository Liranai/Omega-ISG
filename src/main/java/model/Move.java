package model;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class Move {

	private ArrayList<Field> fields;
	
	public Move(Field... fields) {
		this.fields = new ArrayList<Field>();
		for (int i = 0; i < fields.length; i++) {
			this.fields.add(fields[i]);
		}
	}
}
