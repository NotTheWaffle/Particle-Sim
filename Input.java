
public class Input {

	public static final int MOUSE_LEFT = 1;
	public static final int MOUSE_MIDDLE = 2;
	public static final int MOUSE_RIGHT = 4;

	public static final int MOUSE_4 = 8;
	public static final int MOUSE_5 = 16;

	public int mouseX;
	public int mouseY;

	public int mouseDown;

	public double mouseWheel;

	public boolean[] keys;

	public Input(Game game) {
		reset();
	}

	public final void reset(){
		mouseX = -1;
		mouseY = -1;
		mouseDown = 0;
		mouseWheel = 0;
		keys = new boolean[256];
	}
}