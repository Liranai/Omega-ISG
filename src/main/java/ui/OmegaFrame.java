package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Board;
import model.Field;

public class OmegaFrame extends JFrame{

    private static final long serialVersionUID = -2399200433217264450L;
    private JPanel boardPanel;
    public static int SQUARESIZE = 55;

    public static BufferedImage Foot_steps;
    public static BufferedImage Queen_White;
    public static BufferedImage Queen_Black;
    public static BufferedImage Arrow;

    public OmegaFrame(Board board, MouseListener listener) {
        super("MainFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // TODO: Correct screensize
        // setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setUndecorated(true);

//        try {
//            // TODO: load transparent files
//            Foot_steps = ImageIO.read(new File("footsteps3.gif"));
//            Queen_White = ImageIO.read(new File("white_queen.gif"));
//            Queen_Black = ImageIO.read(new File("black_queen.gif"));
//            Arrow = ImageIO.read(new File("arrowCCW.gif"));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        setLayout(new BorderLayout());
        boardPanel = new OmegaGamePanel(board);
        boardPanel.addMouseListener(listener);
        add(boardPanel, BorderLayout.CENTER);
        setMinimumSize(new Dimension(boardPanel.getMinimumSize()));
        
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
    }
}
