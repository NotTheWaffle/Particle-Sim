
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, FocusListener{

	private final Game game;
	private final InputHandler input;
	
	public GamePanel(int width, int height, Window window, Game game){
		this.game = game;
		this.input = game.inputHandler;
		
		this.setFocusTraversalKeysEnabled(false);
		this.setFocusable(true);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		game.updateFrame(g2d);
	}

	@Override public void keyPressed	(KeyEvent e) {input.unhandled.add(e);}
	@Override public void keyReleased	(KeyEvent e) {input.unhandled.add(e);}
	@Override public void keyTyped		(KeyEvent e) {}

	@Override public void mouseReleased	(MouseEvent e) {input.unhandled.add(e);}
	@Override public void mousePressed	(MouseEvent e) {input.unhandled.add(e);}
	@Override public void mouseClicked	(MouseEvent e) {}

	@Override public void mouseMoved	(MouseEvent e) {input.unhandled.add(e);}
	@Override public void mouseEntered	(MouseEvent e) {input.unhandled.add(e);}
	@Override public void mouseExited	(MouseEvent e) {input.unhandled.add(e);}
	@Override public void mouseDragged	(MouseEvent e) {input.unhandled.add(e);}

	@Override public void mouseWheelMoved(MouseWheelEvent e){input.unhandled.add(e);}
	
	@Override public void focusGained(FocusEvent e) {input.unhandled.add(e);}
	@Override public void focusLost(FocusEvent e)   {input.unhandled.add(e);}
}