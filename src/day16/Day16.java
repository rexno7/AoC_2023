package day16;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day16 {
	
	public final static Vector NORTH = new Vector(0,-1);
	public final static Vector SOUTH= new Vector(0,1);
	public final static Vector EAST = new Vector(1,0);
	public final static Vector WEST = new Vector(-1,0);

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day16/resources/input.txt"));
		List<Tile[]> contraptionGridList = new ArrayList<Tile[]>();
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			ArrayList<Tile> contraptionRow = new ArrayList<Tile>(line.length());
			for (char device : line.toCharArray()) {
				contraptionRow.add(new Tile(device));
			}
			contraptionGridList.add(contraptionRow.toArray(new Tile[line.length()]));
		}
		Contraption contraption = new Contraption(contraptionGridList.toArray(new Tile[contraptionGridList.size()][]));
		contraption.simulateBeamTraversal(0, 0, EAST);
		System.out.println(contraption + "\n");
		System.out.println(contraption.showEnergizedTileDirections());
		long totalEnergizedTiles = 0L;
		for (Tile[] gridRow : contraption.grid) {
			for (Tile tile : gridRow) {
				if (tile.energized) {
					totalEnergizedTiles++;
				}
			}
		}
		System.out.println(totalEnergizedTiles);
		
//		long maxTotalEnergizedTiles = 0L;
//		for (int i=0; i<contraption.grid.length; i++) {
//			maxTotalEnergizedTiles = Math.max(maxTotalEnergizedTiles, 
//					contraption.simulateBeamTraversal(i, 0, south));
//		}
	}
}

class Contraption {
	Tile[][] grid;
	
	public Contraption(Tile[][] grid) {
		this.grid = grid;
	}
	
	public void simulateBeamTraversal(int beamXPos, int beamYPos, Vector beamVector) {
		if (beamXPos < 0 || beamXPos >= grid[0].length 
				|| beamYPos < 0 || beamYPos >= grid.length) {
			return;
		}
		for (Vector vector : grid[beamYPos][beamXPos].energize(beamVector)) {
			simulateBeamTraversal(beamXPos + vector.x, beamYPos + vector.y, vector);
		}
	}
	
	public String showEnergizedTiles() {
		StringBuilder sb = new StringBuilder();
		for (Tile[] tileRow : grid) {
			for (Tile tile : tileRow) {
				sb.append(tile.energized ? "#" : ".");
			}
			sb.append("\n");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	public String showEnergizedTileDirections() {
		StringBuilder sb = new StringBuilder();
		Vector north = new Vector(0,-1);
		Vector south = new Vector(0,1);
		Vector east = new Vector(1,0);
		Vector west = new Vector(-1,0);
		for (Tile[] tileRow : grid) {
			for (Tile tile : tileRow) {
				int tileIncomingVectorCount = tile.incomingVectors.size();
				if (tile.device != '.') { 
					sb.append(tile.device);
				} else if (tileIncomingVectorCount > 1) {
					sb.append(tile.incomingVectors.size());
				} else if (tileIncomingVectorCount == 1){
					Vector incomingVector = tile.incomingVectors.get(0);
					if (incomingVector.equals(north)) {
						sb.append("^");
					} else if (incomingVector.equals(south)) {
						sb.append("v");
					} else if (incomingVector.equals(east)) {
						sb.append(">");
					} else if (incomingVector.equals(west)) {
						sb.append("<");
					}
				} else {
					sb.append(tile.energized ? "#" : ".");
				}
			}
			sb.append("\n");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Tile[] tileRow : grid) {
			for (Tile tile : tileRow) {
				sb.append(tile.device);
			}
			sb.append("\n");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
}

class Vector {
	int x;
	int y;
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector add(Vector other) {
		return new Vector(
				(this.x + other.x) / Math.abs(this.x + other.x), 
				(this.y + other.y) / Math.abs((this.y + other.y)));
	}
	
	@Override
	public boolean equals(Object o) {
		Vector other = (Vector) o;
		return x == other.x && y == other.y;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}

class Tile {
	char device;
	boolean energized;
	List<Vector> incomingVectors;
	
	public Tile(char device) {
		this.device = device;
		energized = false;
		incomingVectors = new ArrayList<Vector>();
	}
	
	// [Empty]
	// '.' => return incomingVector
	// [Mirrors]
	// '\' => (flip x & y) * 1
	// NORTH ( 0, 1) -> EAST  ( 1, 0) => ( y, x)
	// SOUTH ( 0,-1) -> WEST  (-1, 0) => ( y, x)
	// EAST  ( 1, 0) -> NORTH ( 0, 1) => ( y, x)
	// WEST  (-1, 0) -> SOUTH ( 0,-1) => ( y, x)
	// '/' => (flip x & y) * -1
	// NORTH ( 0, 1) -> WEST  (-1, 0) => (-y,-x)
	// SOUTH ( 0,-1) -> EAST  ( 1, 0) => (-y,-x)
	// EAST  ( 1, 0) -> SOUTH ( 0,-1) => (-y,-x)
	// WEST  (-1, 0) -> NORTH ( 0, 1) => (-y,-x)
	// [Splitters]
	// '-' => if y, then [(y,0),(-y,0)], else NO CHANGE
	// NORTH ( 0, 1) -> EAST ( 1, 0), WEST (-1, 0) => ( x, y)
	// SOUTH ( 0,-1) -> EAST ( 1, 0), WEST (-1, 0) => ( x, y)
	// EAST  ( 1, 0) -> EAST  ( 1, 0) => ( x, y)
	// WEST  (-1, 0) -> WEST  (-1, 0) => ( x, y)
	// '|' => if x, then [(0,x),(0,-x)], else NO CHANGE
	// NORTH ( 0, 1) -> NORTH ( 0, 1) => ( x, y)
	// SOUTH ( 0,-1) -> SOUTH ( 0,-1) => ( x, y)
	// EAST  ( 1, 0) -> NORTH ( 0, 1), SOUTH ( 0,-1) => ( 0, y), ( 0,-y)
	// WEST  (-1, 0) -> NORTH ( 0, 1), SOUTH ( 0,-1) => ( 0, y), ( 0,-y)
	public Vector[] energize(Vector incomingVector) {
		if (incomingVectors.contains(incomingVector)) {
			return new Vector[0];
		}
		
		this.energized = true;
		incomingVectors.add(incomingVector);
		if (device == '\\') {
			return new Vector[] { new Vector(incomingVector.y * 1, incomingVector.x * 1) };
		}
		if (device == '/') {
			return new Vector[] { new Vector(incomingVector.y * -1, incomingVector.x * -1) };
		}
		if (device == '-' && incomingVector.y != 0) {
			return new Vector[] { new Vector(1,0), new Vector(-1,0) };
		}
		if (device == '|' && incomingVector.x != 0) {
			return new Vector[] { new Vector(0,1), new Vector(0,-1) };
		}
		return new Vector[] { incomingVector };
	}
	
	@Override
	public String toString() {
		return device + " " + energized;
	}
}