import java.util.concurrent.locks.LockSupport;

public class Main {
	public static void main(String[] args) {
		int gridSize = 1024;
		int gameSize = 1024;
		
		for (int i = 0; i < 1; i++){
			new GameRunner(new Game2(gridSize,gameSize), 60, 60).start();
		}
	}
	public static class GameRunner extends Thread{
		private final Game game;
		private final double fps;
		private final double tps;
		public GameRunner(Game game, double fps, double tps){
			this.game = game;
			if (fps > tps){
				this.fps = tps;
			} else {
				this.fps = fps;
			}
			this.tps = tps;
		}

		@Override
		public void run(){
			double tpf = tps/fps;
			System.out.println(game.getClass()+" running on thread "+Thread.currentThread().getName()+" with "+fps+"FPS and "+tps+" TPS ("+tpf+"TPF)");
			Window window = new Window(game);
			double tickDeficit = 0;

			long frameLength = (int) (1_000_000_000/fps);

			
			long start = System.nanoTime();
			long frames = 0;

			long sleepStart = System.nanoTime();

			while (true){
				long frameStart = System.nanoTime();

				// frame start
				game.inputHandler.sleepTime = System.nanoTime() - sleepStart;

				long logicstart = System.nanoTime();
				tickDeficit += tpf;
				while (tickDeficit > 1){
					game.inputHandler.handle();
					game.tick();
					tickDeficit--;
				}
				game.inputHandler.logicTime = System.nanoTime()-logicstart;
				long renderstart = System.nanoTime();
				window.render();
				game.inputHandler.renderTime = System.nanoTime()-renderstart;
				
				frames++;
				long elapsed = System.nanoTime() - start;
				game.inputHandler.fps = frames/(elapsed / 1_000_000_000.0);

				sleepStart = System.nanoTime();
				// frame end

				
				long now = System.nanoTime();

				long targetTime = frameStart + frameLength;

				long remaining = targetTime - now;
				if (remaining > 200_000) {
					LockSupport.parkNanos(remaining - 100_000);
				}
				while (System.nanoTime() < targetTime) {
					Thread.onSpinWait();
				}
			}
		}
	}
}