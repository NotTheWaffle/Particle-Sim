
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

public class CellularGame extends Game{
	public final int radius = 4;
	

	private byte[][] newGrid;
	private byte[][] oldGrid;
	
	public static final byte AIR = 0;
	public static final byte SAND = 1;
	public static final byte WALL = 2;

	//								 	0 air, 		1 sand,
	private final int[] 	hexCodes =	{0x101010, 	0xed_d3_8c};
	//steams and stuff
	private final Color[] colors = Arrays.stream(hexCodes).mapToObj(Color::new).toArray(Color[]::new);
	
	private int prev = 0;
	
	public CellularGame(int width, int height){
		super(width, height);
		newGrid = new byte[height][width];
		oldGrid = new byte[height][height];
	}
	@Override
	public String name(){
		return "Falling Particle Simulator (Cellular)";
	}
	@Override
	public void tick(){
		byte imdBrush = 1;
		if ((input.mouseDown & Input.MOUSE_RIGHT) > 0) imdBrush = 0;
		
		if (input.mouseDown > 0) fill(input.mouseX, input.mouseY, imdBrush);
		byte[][] temp = newGrid;
		newGrid = oldGrid;
		oldGrid = temp;
		
		
		int checksum = 0;
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				newGrid[y][x] = calculate(x, y);
				checksum += newGrid[y][x];
			}
		}
		if (checksum != prev){
			prev = checksum;
			System.out.println(checksum);
		}
	}


	private byte calculate(int x, int y){
		byte self = oldGrid[y][x];

		if (self == SAND){
			if (getTile(x, y+1) == AIR){
				return AIR;
			}
			if (getTile(x+1, y+1) == AIR && getTile(x+1, y) == AIR && (getTile(x+2, y+1) == AIR || getTile(x+2, y) == AIR)){
				return AIR;
			}
			if (getTile(x-1, y+1) == AIR && getTile(x-1, y) == AIR){
				return AIR;
			}
			return SAND;
			
		}
		if (self == AIR){
			if (getTile(x, y-1) == SAND){
				return SAND;
			}
			if (getTile(x-1, y-1) == SAND && getTile(x-1, y) == SAND){
				return SAND;
			}
			if (getTile(x+1, y-1) == SAND && getTile(x+1, y) == SAND){
				return SAND;
			}
		}
		return AIR;
	}

	private byte getTile(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return WALL;
		}
		return oldGrid[y][x];
	}
	private void setTile(int x, int y, byte value){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return;
		}
		newGrid[y][x] = value;
	}
	
	public void fill(int x, int y, byte value){
		for (int dx = -radius; dx <= radius; dx++){
			for (int dy = -radius; dy <= radius; dy++){
				if (dx * dx + dy * dy > radius * radius) continue;

				int ax = x+dx;
				int ay = y+dy;
				if (ax < 0 || ax >= width || ay < 0 || ay >= height) continue;

				if (value == AIR || value == WALL || newGrid[ay][ax] == AIR){
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
		int value = newGrid[y][x];
		if (value == 0) return;
		if (value >= 0 && value < colors.length){
			g2d.setColor(colors[value]);
		}
		g2d.fillRect(x, y, 1, 1);
	}
}
