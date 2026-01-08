import java.awt.Graphics2D;

public class Game {
	public final Input input;

	public final int width;
	public final int height;

	protected Game(int width, int height){
		input = new Input(this);
		this.width = width;
		this.height = height;
	}
	public String name(){
		return "Default";
	}
	public void tick(){
		
	}
	public void updateFrame(Graphics2D g2d){
		
	}
}