
import java.awt.Color;
import java.awt.Graphics2D;

public class Game1 extends Game{

	public final int tileWidth;

	public final int gridWidth;
	public final int gridHeight;

	public final int radius = 8;

	public int[][] grid;
	public int[][] oldGrid;
	public boolean[][] claim;

	public int brushIndex = 0;
	public int firstDir;
	

	public Game1(int width, int height){
		super(1024,1024);
		tileWidth = 1024/width;
		
		this.gridWidth = width;
		this.gridHeight = height;
		this.grid = new int[gridHeight][gridWidth];
		firstDir = 1;
	}

	public void tick(){
		oldGrid = grid;
		grid = new int[grid.length][grid[0].length];
		claim = new boolean[grid.length][grid[0].length];
		for (int x = 0; x < gridWidth; x++){
			for (int y = 0; y < gridHeight; y++){
				if (oldGrid[y][x] == 0){
					continue;
				}
				switch (oldGrid[y][x]) {
					case 1 -> {
						if (tileAt(x,y+1)<oldGrid[y][x] &&
							attemptClaim(x, y+1, 1)){
						} else if (tileAt(x,y+1)>=oldGrid[y][x] && tileAt(x-firstDir,y+1)<oldGrid[y][x] &&
							attemptClaim(x-firstDir, y+1, 1)){

						} else if (tileAt(x,y+1)>=oldGrid[y][x] && tileAt(x+firstDir,y+1)<oldGrid[y][x] &&
							attemptClaim(x+firstDir, y+1, 1)){
								
						} else if (tileAt(x+firstDir,y)<oldGrid[y][x] &&
							attemptClaim(x+firstDir, y, 1)){

						} else if (tileAt(x-firstDir,y)<oldGrid[y][x] &&
							attemptClaim(x-firstDir, y, 1)){

						} else {
							if (!attemptClaim(x, y, 1)){
								attemptClaim(x, y-1, 1);
							}
						}
					}
					case 2 -> {
						if (tileAt(x,y+1)<oldGrid[y][x] &&
							attemptClaim(x, y+1, 2)){
						} else if (tileAt(x,y+1)>=oldGrid[y][x] && tileAt(x-firstDir,y+1)<oldGrid[y][x] &&
							attemptClaim(x-firstDir, y+1, 2)){
						} else if (tileAt(x,y+1)>=oldGrid[y][x] && tileAt(x+firstDir,y+1)<oldGrid[y][x] &&
							attemptClaim(x+firstDir, y+1, 2)){
						} else {
							attemptClaim(x, y, 2);
						}
					}
					case 3 -> grid[y][x] = 3;
					case -1 -> {
						if (tileAt(x,y-1)>oldGrid[y][x] &&
							attemptClaim(x, y-1, -1)){

						} else if (tileAt(x,y-1)<=oldGrid[y][x] && tileAt(x-firstDir,y-1)>oldGrid[y][x] &&
							attemptClaim(x-firstDir, y-1, -1)){

						} else if (tileAt(x,y-1)<=oldGrid[y][x] && tileAt(x+firstDir,y-1)>oldGrid[y][x] &&
							attemptClaim(x+firstDir, y-1, -1)){

						} else if (tileAt(x-firstDir,y)>oldGrid[y][x] &&
							attemptClaim(x-firstDir, y, -1)){

						} else if (tileAt(x+firstDir,y)>oldGrid[y][x] &&
							attemptClaim(x+firstDir, y, -1)){
						} else {
							if (!attemptClaim(x, y, -1)){
								attemptClaim(x, y+1, -1);
							}
						}
					}
					default -> {}
				}
			}
		}
	}
	public void fill(int mouseX, int mouseY, byte value, int radius){
		int x = mouseX/tileWidth;
		int y = mouseY/tileWidth;
		value = switch(value){
			case 0 -> 0;
			case 1 -> 2; // water
			case 2 -> 3; // sand
			case 3 -> 1; // wall
			case 4 -> -1; // gas
			default -> 0;
		};
		for (int dx = Math.max(x-radius,0); dx<Math.min(x+radius,gridWidth); dx++){
			for (int dy = Math.max(y-radius,0); dy<Math.min(y+radius,gridHeight); dy++){
				if (value == 0 || value == 2 || grid[dy][dx]==0) grid[dy][dx] = value;
			}
		}
	}


	public int tileAt(int x, int y){
		if (x < 0 || x >= gridWidth){
			return 3;
		}
		if (y < 0 || y >= gridHeight){
			return 3;
		}
		return oldGrid[y][x];
	}
	public void setAt(int x, int y, int value){
		if (x < 0 || x >= gridWidth){
			return;
		}
		if (y < 0 || y >= gridHeight){
			return;
		}
		grid[y][x] = value;
	}
	public boolean attemptClaim(int x, int y, int value){
		if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight || claim[y][x]){
			return false;
		}
		grid[y][x] = value;
		claim[y][x] = true;
		return true;
	}
	public void updateFrame(Graphics2D g2d){
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, g2d.getClipBounds().width, g2d.getClipBounds().height);
		for (int x = 0; x < gridWidth; x++){
			for (int y = 0; y < gridHeight; y++){
				render(x,y,g2d);
			}
		}
	}
	public void render(int x, int y, Graphics2D g2d){
		int value = grid[y][x];
		if (value == 0){
			return;
		}
		g2d.setColor(Color.orange);
		if (value == 1){
			g2d.setColor(Color.blue);
		}
		if (value == 2){
			g2d.setColor(Color.yellow);
		}
		if (value == 3){
			g2d.setColor(Color.gray);
		}
		if (value == -1){
			g2d.setColor(Color.magenta);
		}
		g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
	}
}
