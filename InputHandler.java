import java.awt.event.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputHandler {

	public final Queue<ComponentEvent> unhandled;
	public final Game game;

	public int mouseX;
	public int mouseY;
	public int mouseDown;

	public byte brush = 1;
	public byte imdBrush = brush;

	public double fps;
	public long logicTime;
	public long renderTime;
	public long sleepTime;

	public InputHandler(Game game) {
		this.game = game;
		this.unhandled = new ConcurrentLinkedQueue<>();
	}

	public void handle() {
		ComponentEvent event;

		while ((event = unhandled.poll()) != null) {

			if (game.paused && event.getID() != FocusEvent.FOCUS_GAINED) {
				continue;
			}

			switch (event.getID()) {

				case KeyEvent.KEY_PRESSED -> {
					brush = (byte) (((KeyEvent) event).getKeyChar()-48);
					brush = switch (brush){
						default -> brush;
					};
					if (brush < 0 || brush > 9){
						brush = 0;
					}
					
				}

				case KeyEvent.KEY_RELEASED -> {}

				case MouseEvent.MOUSE_PRESSED -> {
					MouseEvent e = (MouseEvent) event;
					mouseX = e.getX();
					mouseY = e.getY();
					mouseDown = e.getButton();

					imdBrush = brush;
					if (mouseDown == 3){
						imdBrush = 0;
					}
				}

				case MouseEvent.MOUSE_RELEASED -> {
					mouseDown = 0;
				}

				case MouseEvent.MOUSE_MOVED -> {}

				case MouseEvent.MOUSE_ENTERED -> {}

				case MouseEvent.MOUSE_EXITED -> {}

				case MouseEvent.MOUSE_DRAGGED -> {
					MouseEvent e = (MouseEvent) event;
					mouseX = e.getX();
					mouseY = e.getY();
				}

				case MouseEvent.MOUSE_WHEEL -> {}

				case FocusEvent.FOCUS_GAINED -> {
					game.paused = false;
				}

				case FocusEvent.FOCUS_LOST -> {
					game.paused = true;
				}
			}
		}
	}
}