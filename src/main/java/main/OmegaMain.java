package main;
import logic.OmegaLogic;
import model.Board;
import ui.OmegaFrame;

public class OmegaMain {
	
	public static final int DEBUG = 2;
	public static final int NUMBER_OF_PLAYERS = 2;
	public static final int BOARDSIZE = 6;
	
	public enum players {
		White,
		Black,
		Red,
		Blue;
	}

    public static void main (String[] args){
        Board b = new Board(BOARDSIZE);	
        OmegaLogic logic = new OmegaLogic(b);
        OmegaFrame frame = new OmegaFrame(b, logic);
        
        logic.addObserver(frame.getInfoPanel());
        
        logic.addFrame(frame);
        Thread t = new Thread(logic);
        t.start();
    }
    
    //TODO: Multithread move generation??
    //TODO: Zorbist hash the field/board
    //TODO: convert the board to primitive types
    //TODO: UNDO MOVE!!!!!
    //TODO: Split current model to it's most primitive form and move excess to gui layer
    //TODO: No MiniMax in the first couple moves, instead evaluate board positions. For example
    // The outer rims are move valuable since the chances of 'killing' a group are slimmer. Additionally,
    // you need to protect a group once it only had 1 spot between two groups. On the other hand, using this
    // you can kill groups of the opponent like this. Therefor, try to always have 2 fields distance between two groups.
    // ADDITIONALLY: groups of size 1 are USELESS. Make them 2 or bigger AT LEAST
    // consequently, try to put he opponent's stones in groups of 1.
    // Change the moveordering to use two individual values instead of just a combined one.
}