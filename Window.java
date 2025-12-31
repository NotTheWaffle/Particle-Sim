

import javax.swing.*;

public class Window {
	private final JFrame frame;

	private final GamePanel gamePanel;


	public Window(Game game){
		int width  = 8  + game.gameWidth  + 8;
		int height = 31 + game.gameHeight + 8;

		this.frame = new JFrame("Particle Sim running "+game.getClass());
		this.frame.setBounds(0, 0, width, height);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true); 
		this.frame.setLayout(null);
		this.frame.setResizable(false);
		

		this.gamePanel = new GamePanel(game.gameWidth, game.gameHeight, this, game);
		this.gamePanel.setBounds(0, 0, game.gameWidth, game.gameHeight);
		this.frame.add(gamePanel);
		
		gamePanel.addMouseListener(gamePanel);
		gamePanel.addMouseMotionListener(gamePanel);
		gamePanel.addMouseWheelListener(gamePanel);
		gamePanel.addKeyListener(gamePanel);
	 	gamePanel.addFocusListener(gamePanel);

		gamePanel.requestFocus();
	}

	public void render(){
		gamePanel.repaint();
	}
}