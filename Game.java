
import java.awt.Graphics2D;

public abstract class Game {
	public final InputHandler inputHandler;

	public boolean paused;
	
	public final int gameWidth;
	public final int gameHeight;

	protected Game(int width, int height){
		inputHandler = new InputHandler(this);
		this.gameWidth = width;
		this.gameHeight = height;
	}
	public abstract void tick();
	public abstract void fill(int x, int y, int type);
	public abstract void updateFrame(Graphics2D g2d);
}
