
import java.util.concurrent.locks.LockSupport;

public class Main {
	public static void main(String[] args) {
		new Thread(() -> runGame(new ParticleGame(256, 256), 60, 600)).start();
	}
	public static void runGame(final Game game, final double fps, final double tps){
		final Window window = new Window(game);
		final long frameLength = (long) (1_000_000_000/fps);
		final double tpf = tps/fps;

		double tickDeficit = 0;
		while (true){
			long targetTime = System.nanoTime() + frameLength;

			tickDeficit += tpf;
			while (tickDeficit > 1){
				game.tick();
				tickDeficit--;
			}
			window.render();

			long remaining = targetTime - System.nanoTime();
			if (remaining > 200_000) {
				LockSupport.parkNanos(remaining - 100_000);
			}
			while (System.nanoTime() < targetTime) {
				Thread.onSpinWait();
			}
		}
	}
}