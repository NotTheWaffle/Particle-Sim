
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleGame extends Game{
	public final int radius = 4;
	
	private final Random random = ThreadLocalRandom.current();

	private final byte[][] grid;
	private final boolean[][] updated;
	
	public static final byte AIR = 0;
	public static final byte SAND = 1;
	public static final byte WATER = 2;
	public static final byte WALL = 3;
	public static final byte SMOKE = 4;
	public static final byte STONE = 5;
	public static final byte WOOD = 6;
	public static final byte FIRE = 7;
	public static final byte ACID = 8;
	public static final byte POWDER = 9;
	public static final byte CONCRETE = 10;

	//								 	0 air, 		1 sand, 	2 water, 	3 wall, 	4 smoke, 	5 stone, 	6 sawdust, 	7 fire, 	8 acid, 	9 powder, 	10 concrete
	private final int[] 	hexCodes =	{0x101010, 	0xedd38c, 	0x4595ff, 	0x63605a, 	0xcacbcb, 	0x8e8383, 	0xc69354, 	0xf57d6b, 	0xd2e500, 	0x868fa9, 	0xd3d3d3};
	private final int[] 	density = 	{0, 		3, 			2, 			0, 			-1, 		3, 			1, 			-1, 		1, 			2, 			0 		};
	private final boolean[] mobile =	{true, 		true, 		true, 		false, 		true, 		true, 		true, 		true, 		true, 		true, 		false	};
	//steams and stuff
	private final Color[] colors = Arrays.stream(hexCodes).mapToObj(Color::new).toArray(Color[]::new);
	
	private byte brush;
	
	public ParticleGame(int width, int height){
		super(width, height);
		this.grid = new byte[height][width];
		this.updated = new boolean[height][width];
		brush = 1;
	}
	@Override
	public String name(){
		return "Falling Particle Simulator";
	}
	@Override
	public void tick(){
		for (boolean[] row : updated) {
			Arrays.fill(row, false);
		}
		for (char i = '0'; i <= '9'; i++){
			if (input.keys[i]){
				brush = (byte)(i-'0');
			}
		}
		byte imdBrush = brush;
		if ((input.mouseDown & Input.MOUSE_RIGHT) > 0) imdBrush = 0;
		
		if (input.mouseDown > 0) fill(input.mouseX, input.mouseY, imdBrush);
		
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				byte self = grid[y][x];
				if (updated[y][x] || self == AIR || self == WALL || self == CONCRETE) continue;

				int xDir = random.nextBoolean() ? 1 : -1;
				
				switch (self) {
					case SAND -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+xDir, y+1)){}
					}
					case WATER -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+xDir, y)){}
					}
					case WALL -> {}
					case SMOKE -> {
						if ((attemptMoreDenseSwap(x, y, self, x+xDir, y-1))){}
						else if (attemptMoreDenseSwap(x, y, self, x, y-1)){}
						else if (attemptMoreDenseSwap(x, y, self, x+xDir, y)){}
					}
					case STONE -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if ((lessDense(tileAt(x+xDir, y), self) && lessDense(tileAt(x+xDir, y+1), self) && lessDense(tileAt(x+xDir, y+2), self)) && attemptLessDenseSwap(x, y, self, x+xDir, y+3)){}
					}
					case WOOD -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (tileAt(x+xDir, y) == AIR && attemptLessDenseSwap(x, y, self, x+2*xDir, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+xDir, y+1)){}
					}
					case FIRE -> {
						for (int dx = -1; dx < 2; dx++){
							for (int dy = -1; dy < 2; dy++){
								if (dx == 0 && dy == 0){
									continue;
								}
								byte neighbor = tileAt(x+dx, y+dy);
								if (neighbor == WOOD && chance(.5)){
									setTile(x+dx, y+dy, FIRE);
								}
							}
						}
						if (chance(.1)){
							if (chance(.01)) {
								setTile(x, y, SMOKE);
							} else {
								setTile(x, y, AIR);
							}
						}
					}
					case ACID -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+xDir, y)){}
						else {
							outer:
							for (int dx = -1; dx < 2; dx++){
								for (int dy = -1; dy < 2; dy++){
									if (dx == 0 && dy == 0){
										continue;
									}
									byte neighbor = tileAt(x+dx, y+dy);
									if (neighbor != WALL && neighbor != AIR && neighbor != ACID && !updated[y+dy][x+dx]){
										setTile(x+dx, y+dy, AIR);
										setTile(x, y, AIR);
										break outer;
									}
								}
							}
						}
					}
					case POWDER -> {
						if (attemptLessDenseSwap(x, y, self, x, y+1)){}
						else if (attemptLessDenseSwap(x, y, self, x+xDir, y+1)){}
						else {
							for (int dx = -1; dx < 2; dx++){
								for (int dy = -1; dy < 2; dy++){
									if (dx == 0 && dy == 0){
										continue;
									}
									byte neighbor = tileAt(x+dx, y+dy);
									if (neighbor == WATER){
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

	private boolean chance(double c){
		return random.nextDouble() < c;
	}

	private boolean attemptLessDenseSwap(int x1, int y1, byte self, int x2, int y2){
		byte other = tileAt(x2, y2);
		if (lessDense(other, self)){
			grid[y1][x1] = other;
			grid[y2][x2] = self;
			updated[y2][x2] = true;
			updated[y1][x1] = true;
			return true;
		}
		return false;
	}

	private boolean attemptMoreDenseSwap(int x1, int y1, byte self, int x2, int y2){
		byte other = tileAt(x2, y2);
		if (moreDense(other, self)){
			grid[y1][x1] = other;
			grid[y2][x2] = self;
			updated[y2][x2] = true;
			updated[y1][x1] = true;
			return true;
		}
		return false;
	}
	private boolean lessDense(byte otherValue, byte myValue){
		return mobile[otherValue] && density[otherValue] < density[myValue];
	}
	private boolean moreDense(byte otherValue, byte myValue){
		return mobile[otherValue] && density[otherValue] > density[myValue];
	}
	private byte tileAt(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return WALL;
		}
		return grid[y][x];
	}
	private void setTile(int x, int y, byte value){
		grid[y][x] = value;
		updated[y][x] = true;
	}
	
	public void fill(int x, int y, byte value){
		for (int dx = -radius; dx <= radius; dx++){
			for (int dy = -radius; dy <= radius; dy++){
				if (dx * dx + dy * dy > radius * radius) continue;

				int ax = x+dx;
				int ay = y+dy;
				if (ax < 0 || ax >= width || ay < 0 || ay >= height) continue;

				if (value == AIR || value == WALL || grid[ay][ax] == AIR){
					setTile(ax, ay, value);
				}
			}
		}
	}
	@Override
	public void updateFrame(Graphics2D g2d){
		g2d.setColor(colors[AIR]);
		g2d.fillRect(0, 0, width, height);
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				render(x, y, g2d);
			}
		}
	}
	public void render(int x, int y, Graphics2D g2d){
		int value = grid[y][x];
		if (value == 0) return;
		if (value >= 0 && value < colors.length){
			g2d.setColor(colors[value]);
		}
		g2d.fillRect(x, y, 1, 1);
	}
}
