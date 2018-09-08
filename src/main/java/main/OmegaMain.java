package main;
import logic.OmegaLogic;
import model.Board;
import ui.OmegaFrame;

public class OmegaMain {
	
	public static final int DEBUG = 1;
	public static final int NUMBER_OF_PLAYERS = 4;
	public static final int BOARDSIZE = 8;
	
	public enum players {
		White,
		Black,
		Red,
		Blue;
	}

    public static void main (String[] args){
        System.out.println("Hello World");
        Board b = new Board(BOARDSIZE);	
        OmegaLogic logic = new OmegaLogic(b);
        OmegaFrame frame = new OmegaFrame(b, logic);
        logic.addFrame(frame);
        Thread t = new Thread(logic);
        t.start();
    }
}
