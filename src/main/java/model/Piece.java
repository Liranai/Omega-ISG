package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Piece {

	private int size, colour;
	private Piece parent;
	
	public Piece(int colour) {
		this.colour = colour;
		size = 1;
		parent = null;
	}
	
	public Piece(int colour, int size, Piece parent) {
		this.colour = colour;
		this.size = size;
		this.parent = parent;
	}
	
	public Piece clone() {
		return new Piece(colour, size, parent);
	}
}
