
import java.util.concurrent.locks.LockSupport;

public class Main {
	public static void main(String[] args) {
		int size = 1024;
		new Thread(()->{runGame(new ThreadedParticleGame(size, size), 60, 240);}).start();
		runGame(new ParticleGame(size, size), 60, 240);
	}
	public static void runGame(final Game game, final double fps, final double tps){
		final Window window = new Window(game);
		final long frameLength = (long) (1_000_000_000/fps);
		final double tpf = tps/fps;

		double tickDeficit = 0;
		while (true){
			final long targetTime = System.nanoTime() + frameLength;
			
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