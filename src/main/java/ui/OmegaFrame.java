package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.Getter;
import model.Board;
import model.Field;

@Getter
public class OmegaFrame extends JFrame{

    private static final long serialVersionUID = -2399200433217264450L;
    private OmegaGamePanel boardPanel;
    private OmegaInfoPanel infoPanel;

    public OmegaFrame(Board board, MouseListener listener) {
        super("MainFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setUndecorated(true);

        setLayout(new BorderLayout());
        boardPanel = new OmegaGamePanel(board);

        boardPanel.addMouseListener(listener);
        infoPanel = new OmegaInfoPanel();
        
        System.out.println("BPx: " + boardPanel.getMinimumSize().width + " BPy: " + boardPanel.getMinimumSize().height);
        System.out.println("BIx: " + infoPanel.getMinimumSize().width + " BIy: " + infoPanel.getMinimumSize().height);
        
//        setMinimumSize(new Dimension((boardPanel.getMinimumSize().width + infoPanel.getMinimumSize().width), boardPanel.getMinimumSize().height));

        boardPanel.setPreferredSize(boardPanel.getMinimumSize());
        infoPanel.setPreferredSize(infoPanel.getMinimumSize());
        
        
        add(boardPanel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.EAST);
        
        addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component) evt.getSource();

                int width = c.getSize().width;
                int height = c.getSize().height;

                OmegaGamePanel.SQUARESIZE = (int) Math.min(width / ((board.boardSize* 2) - 0.75), height / (board.boardSize * 1.5));
                OmegaGamePanel.CENTER_OFFSET_X = (board.boardSize -1) * OmegaGamePanel.SQUARESIZE;
        		OmegaGamePanel.CENTER_OFFSET_Y = (int)(Math.ceil((board.boardSize - 1) * 0.75 * OmegaGamePanel.SQUARESIZE + 5));
        		for(Field f : board.fields.values())
        			f.createHex();
            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(ComponentEvent evt) {
            }
            
            @Override
            public void componentShown(ComponentEvent arg0) {
            }
        });
        

        // addMenuBar(this);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        System.out.println(this.getSize());
    }
    
    @Override
    public synchronized void addMouseListener(MouseListener l) {
    	super.addMouseListener(l);
    	boardPanel.addMouseListener(l);
    }
}
