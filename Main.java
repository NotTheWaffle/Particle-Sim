
public class Main {
	public static void main(String[] args) {
		int size = 512;
		Thread t2 = new GameRunner(new Game2(size,size));
		t2.start();
	}
	public static class GameRunner extends Thread{
		private final Game game;
		public GameRunner(Game game){
			this.game = game;
		}

		@Override
		public void run(){
			System.out.println(game.getClass()+" running on thread "+Thread.currentThread().getName());
			Window window = new Window(game);
			double tps = 120;
			double fps = 60;
			double tickDeficit = 0;
			double tpf = tps/fps;
			double sleepTime = 1_000.0/fps;
			long time;
			while (true){
				time = System.currentTimeMillis();
				game.inputHandler.handle();
				if (true){
					tickDeficit += tpf;
					while (tickDeficit > 1){
						game.tick();
						tickDeficit--;
					}
				}
				window.render();
				int takenMillis = (int)(System.currentTimeMillis()-time);
				int millis = (int) (sleepTime-takenMillis);
				if (millis < 0){
					System.out.println("lagging");
					continue;
				}
				try {
					Thread.sleep((int)(millis));
				} catch (Exception e){}
			}
		}
	}
}