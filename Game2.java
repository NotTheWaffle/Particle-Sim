
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
	
	public Random random = ThreadLocalRandom.current();

	public int[][] grid;
	public boolean[][] updated;
	
	public static final int AIR = 0;
	public static final int SAND = 1;
	public static final int WATER = 2;
	public static final int WALL = 3;
	public static final int SMOKE = 4;
	public static final int STONE = 5;
	public static final int WOOD = 6;
	public static final int FIRE = 7;
	public static final int ACID = 8;
	public static final int POWDER = 9;
	public static final int CONCRETE = 10;


	//								 0 air, 	1 sand, 	2 water, 	3 wall, 	4 smooke,	5 stone,	6 wood, 	7 fire,		8 acid,		9 powder, 	10 concrete
	public final int[] hexCodes =	{0x101010,	0xedd38c,	0x4595ff,	0x63605a, 	0xcacbcb,	0x8e8383,	0x5e4843,	0xf57d6b,	0xd2e500,	0x868fa9,	0xd3d3d3,	};
	public final int[] density = 	{0, 		3, 			2, 			0, 			-1, 		4, 			0, 			0,			2,			2,			0, 			};
	public final boolean[] mobile = {true, 		true, 		true, 		false,		true, 		true, 		false, 		false, 		true, 		true, 		false,		};
	//steams and stuff :sob:
	public final Color[] colors = Arrays.stream(hexCodes).mapToObj(Color::new).toArray(Color[]::new);
	
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
		for (int y = 0; y < gridHeight; y++) {
			Arrays.fill(updated[y], false);
		}
		for (int x = 0; x < gridWidth; x++){
			for (int y = 0; y < gridHeight; y++){
				int self = grid[y][x];
				if (updated[y][x] || self == AIR || self == WOOD || self == WALL || self == CONCRETE) continue;
				
				int dir = random.nextBoolean() ? 1 : -1;

				switch (self) {
					case SAND -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+dir, y+1)){}
					}
					case WATER -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+dir, y)){}
					}
					case WALL -> {}
					case SMOKE -> {
						if ((attemptMoreDenseSwap(x, y, self, x, y-1))){}
						else if (attemptMoreDenseSwap(x, y, self, x+dir, y)){}
					}
					case STONE -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
					}
					case WOOD -> {}
					case FIRE -> {
						int neighbors = 0;
						for (int dx = -1; dx < 2; dx++){
							for (int dy = -1; dy < 2; dy++){
								if (dx == 0 && dy == 0){
									continue;
								}
								int neighbor = tileAt(x+dx,y+dy);
								if (neighbor == WOOD && chance(.5)){
									setTile(x+dx, y+dy, FIRE);
								}
								if (neighbor == FIRE) neighbors++;
							}
						}
						if (neighbors < 5 && chance(.5)){
							if (chance(.01)) {
								setTile(x, y, SMOKE);
							} else {
								setTile(x, y, AIR);
							}
						}
					}
					case ACID -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+dir, y)){}
						else {
							for (int dx = -1; dx < 2; dx++){
								for (int dy = -1; dy < 2; dy++){
									if (dx == 0 && dy == 0){
										continue;
									}
									int nb = tileAt(x+dx, y+dy);
									if (nb != WALL && nb != AIR && nb != ACID && !updated[y+dy][x+dx]){
										setTile(x+dx, y+dy, AIR);
										setTile(x, y, AIR);
									}
								}
							}
						}
					}
					case POWDER -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+dir, y+1)){}
						else {
							for (int dx = -1; dx < 2; dx++){
								for (int dy = -1; dy < 2; dy++){
									if (dx == 0 && dy == 0){
										continue;
									}
									int nb = tileAt(x+dx, y+dy);
									if (nb == WATER){
										setTile(x, y, CONCRETE);
									}
								}
							}
						}
					}
					case CONCRETE -> {}
					default -> {}
				}
			}
		}
	}

	public boolean chance(double c){
		return random.nextDouble() < c;
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
			return WALL;
		}
		return grid[y][x];
	}
	public void setTile(int x, int y, int value){
		grid[y][x] = value;
		updated[y][x] = true;
	}
	public void trySetTile(int x, int y, int value){
		if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight || grid[y][x] == WALL){
			return;
		}
		grid[y][x] = value;
		updated[y][x] = true;
	}
	@Override
	public void fill(int mouseX, int mouseY, int value){
		int radius = this.radius;
		if (value == 11){
			radius = 1;
		}
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
				if (value == AIR || value == WALL || grid[ay][ax] == 0){
					setTile(ax, ay, value);
				}
			}
		}
	}
	@Override
	public void updateFrame(Graphics2D g2d){
		g2d.setColor(colors[AIR]);
		g2d.fillRect(0, 0, g2d.getClipBounds().width, g2d.getClipBounds().height);
		for (int x = 0; x < gridWidth; x++){
			for (int y = 0; y < gridHeight; y++){
				render(x, y, g2d);
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
