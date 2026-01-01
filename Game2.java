
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Game2 extends Game{

	public final int tileWidth;

	public final int gridWidth;
	public final int gridHeight;

	public final int radius = 8;

	public int checksum = 0;

	public Random random = ThreadLocalRandom.current();

	public int[][] grid;
	public boolean[][] updated;

	public boolean suppress = false;
	
	//								0 air, 		1 sand, 		2 wall, 		3 water, 		4 gas, 			5 stone,		6 wood, 	7 fire,			8 acid,			9 concrete powder, 10 concrete
	public final Color[] colors = {	Color.black,c(0xedd38c),	c(0x63605a),	c(0x4595ff),	c(0xcacbcb),	c(0x8e8383),	c(0x5e4843),c(0xf57d6b),	c(0xd2e500),	c(0x868fa9), Color.lightGray};
	public final int[] 	density = {	0, 			3, 				0, 				2, 				-1, 			4, 				4, 			1,				2,				2,				2};
	public final boolean[] mobile = {true, true, false, true, true, true, false, true, true, true, false};

	private static Color c(int i){
		return new Color(i);
	}

	public Game2(int gridSize, int gameSize){
		super(gameSize,gameSize);
		tileWidth = gameHeight/gridSize;
		
		this.gridWidth = gridSize;
		this.gridHeight = gridSize;
		this.grid = new int[gridHeight][gridWidth];
		this.updated = new boolean[gridHeight][gridWidth];
	}
	
	@Override
	public void tick(){
		//0 air, 1 sand, 2 wall, 3 water, 4 gas, 5 concrete, 6 wood, 7 fire, 8 acid, 9 concrete powder
		for (int y = 0; y < gridHeight; y++) {
			Arrays.fill(updated[y], false);
		}
		for (int x = 0; x < gridWidth; x++){
			for (int y = 0; y < gridHeight; y++){
				if (updated[y][x] || grid[y][x]==0) continue;
				int dir = 1;
				if (random.nextDouble()>.5) dir = -1;
				int self = grid[y][x];
				
				if (self == 1){
					if (attemptLessDenseSwap(x, y, self, x, y+1)){
					} else if (attemptLessDenseSwap(x, y, self, x+dir, y+1)){
					}
				} else if (self == 3){
					if (attemptLessDenseSwap(x, y, self, x, y+1)){
					} else if (attemptLessDenseSwap(x, y, self, x+dir, y)){
					}
				} else if (self == 4){
					if ((attemptMoreDenseSwap(x, y, self, x, y-1))){
					} else if (attemptMoreDenseSwap(x, y, self, x+dir, y)){
					}
				} else if (self == 5){
					if (attemptLessDenseSwap(x, y, self, x, y+1)){
					}
				} else if (self == 6){
					if (attemptLessDenseSwap(x, y, self, x, y)){}
				} else if (self == 7){
					int neighbors = 0;
					for (int dx = -1; dx < 2; dx++){
						for (int dy = -1; dy < 2; dy++){
							if (dx == 0 && dy == 0){
								continue;
							}
							int nb = tileAt(x+dx,y+dy);
							if (tileAt(x+dx, y+dy) == 6 && random.nextDouble() >= .5){
								grid[y+dy][x+dx] = 7;
								updated[y+dy][x+dx] = true;
							}
							if (nb == 7) neighbors++;
						}
					}
					if (neighbors < 5 && random.nextDouble()>=.5){
						if (random.nextDouble() >= .99) {
							grid[y][x] = 4;
						} else {
							grid[y][x] = 0;
						}
					}
				} else if (self == 8){
					if (attemptLessDenseSwap(x, y, self, x, y+1)){
					} else if (attemptLessDenseSwap(x, y, self, x+dir, y)){
					} else {
						for (int dx = -1; dx < 2; dx++){
							for (int dy = -1; dy < 2; dy++){
								if (dx == 0 && dy == 0){
									continue;
								}
								int nb = tileAt(x+dx, y+dy);
								if (nb != 2 && nb != 0 && nb != 8 && !updated[y+dy][x+dx]){
									grid[y+dy][x+dx] = 0;
									updated[y+dy][x+dx] = true;
									grid[y][x] = 0;
								}
							}
						}
					}	
				} else if (self == 9){
					if (attemptLessDenseSwap(x, y, self, x, y+1)){
					} else if (attemptLessDenseSwap(x, y, self, x+dir, y+1)){
					} else {
						for (int dx = -1; dx < 2; dx++){
							for (int dy = -1; dy < 2; dy++){
								if (dx == 0 && dy == 0){
									continue;
								}
								int nb = tileAt(x+dx, y+dy);
								if (nb == 3){
									grid[y][x] = 10;
								}
							}
						}
					}
				}
			}
		}
	}
	public boolean attemptLessDenseSwap(int x1, int y1, int self, int x2, int y2){
		int other = tileAt(x2, y2);
		if (lessDense(other, self)){
			grid[y1][x1] = other;
			grid[y2][x2] = self;
			updated[y2][x2] = true;
			updated[y1][x1] = true;
			return true;
		}
		return false;
	}
	public boolean attemptMoreDenseSwap(int x1, int y1, int self, int x2, int y2){
		int other = tileAt(x2, y2);
		if (moreDense(other, self)){
			grid[y1][x1] = other;
			grid[y2][x2] = self;
			updated[y2][x2] = true;
			updated[y1][x1] = true;
			return true;
		}
		return false;
	}
	public boolean lessDense(int otherValue, int myValue){
		return mobile[otherValue] && density[otherValue] < density[myValue];
	}
	public boolean moreDense(int otherValue, int myValue){
		return mobile[otherValue] && density[otherValue] > density[myValue];
	}
	public int tileAt(int x, int y){
		if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight){
			return 2;
		}
		return grid[y][x];
	}
	
	@Override
	public void fill(int mouseX, int mouseY, int value){
		int x = mouseX/tileWidth;
		int y = mouseY/tileWidth;
		for (int dx = -radius; dx <= radius; dx++){
			for (int dy = -radius; dy <= radius; dy++){
				if (dx * dx + dy * dy >= radius * radius){
					continue;
				}
				int ax = x+dx;
				int ay = y+dy;
				if (ax < 0 || ax >= gridWidth || ay < 0 || ay >= gridHeight){
					continue;
				}
				if (value == 0 || value == 2 || grid[ay][ax] == 0){
					updated[ay][ax] = true;
					grid[ay][ax] = value;
				}
			}
		}
		suppress = true;
	}
	@Override
	public void updateFrame(Graphics2D g2d){
		g2d.setColor(c(0x000010));
		g2d.fillRect(0, 0, g2d.getClipBounds().width, g2d.getClipBounds().height);
		for (int x = 0; x < gridWidth; x++){
			for (int y = 0; y < gridHeight; y++){
				render(x,y,g2d);
			}
		}
		g2d.setColor(Color.white);
		g2d.setFont(new Font("Monospaced", 0, 16));
		double totalTime = (inputHandler.logicTime+inputHandler.renderTime+inputHandler.sleepTime);
		g2d.drawString(String.format("%3.0f",inputHandler.fps),0,g2d.getFont().getSize());
		g2d.drawString(String.format("logic  (us): %3.3f%%",inputHandler.logicTime*100.0/totalTime),0,2*g2d.getFont().getSize());
		g2d.drawString(String.format("render (us): %3.1f%%",inputHandler.renderTime*100.0/totalTime),0,3*g2d.getFont().getSize());
		g2d.drawString(String.format("sleep  (us): %3.1f%%",inputHandler.sleepTime*100.0/totalTime),0,4*g2d.getFont().getSize());
		g2d.drawString(String.format("total  (s):%5.1f",totalTime/1_000_000_000),0 , 5 * g2d.getFont().getSize());
	}
	public void render(int x, int y, Graphics2D g2d){
		int value = grid[y][x];
		if (value == 0) return;
		g2d.setColor(colors[value]);
		g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
	}
}
