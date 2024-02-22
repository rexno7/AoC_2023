package day16;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day16 {
	
	public static void main(String[] args) throws FileNotFoundException, CloneNotSupportedException {
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
		Contraption contraptionTemplate = new Contraption(contraptionGridList.toArray(new Tile[contraptionGridList.size()][]));
		
		////////////
		// Part 1 //
		////////////
		
		int part1 = Contraption.simulateBeamTraversal(contraptionTemplate, 0, 0, Vector.EAST).energizedTileCount();
		System.out.println("part1: " + part1);
		
		////////////
		// Part 2 //
		////////////
		
		int maxEnergizedCount = 0;
		// rows from EAST
		for (int i=0; i<contraptionTemplate.grid.length; i++) {
			int energizedCount = Contraption.simulateBeamTraversal(contraptionTemplate, 0, i, Vector.EAST).energizedTileCount();
			maxEnergizedCount = Math.max(maxEnergizedCount, energizedCount);
		}
		// rows from WEST
		for (int i=0; i<contraptionTemplate.grid.length; i++) {
			int energizedCount = Contraption.simulateBeamTraversal(contraptionTemplate, contraptionTemplate.grid.length-1, i, Vector.WEST).energizedTileCount();
			maxEnergizedCount = Math.max(maxEnergizedCount, energizedCount);
		}
		// columns from SOUTH
		for (int i=0; i<contraptionTemplate.grid.length; i++) {
			int energizedCount = Contraption.simulateBeamTraversal(contraptionTemplate, i, 0, Vector.SOUTH).energizedTileCount();
			maxEnergizedCount = Math.max(maxEnergizedCount, energizedCount);
		}
		// columns from NORTH
		for (int i=0; i<contraptionTemplate.grid.length; i++) {
			int energizedCount = Contraption.simulateBeamTraversal(contraptionTemplate, i, contraptionTemplate.grid[0].length, Vector.NORTH).energizedTileCount();
			maxEnergizedCount = Math.max(maxEnergizedCount, energizedCount);
		}
		System.out.println("part2: " + maxEnergizedCount);
	}
}

class Contraption {
	Tile[][] grid;
	
	public Contraption(Tile[][] grid) {
		this.grid = grid;
	}
	
	// Copy constructor
	private Contraption(Contraption other) {
		this.grid = new Tile[other.grid.length][];
		for (int row = 0; row < other.grid.length; row++) {
			Tile[] tempTileRow = new Tile[other.grid[row].length];
			for (int col = 0; col < other.grid[row].length; col++) {
				tempTileRow[col] = new Tile(other.grid[row][col].device);
			}
			this.grid[row] = tempTileRow;
		}
	}
	
	public int energizedTileCount() {
		int totalEnergizedTiles = 0;
		for (Tile[] gridRow : this.grid) {
			for (Tile tile : gridRow) {
				if (tile.energized) {
					totalEnergizedTiles++;
				}
			}
		}
		return totalEnergizedTiles;
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
		for (Tile[] tileRow : grid) {
			for (Tile tile : tileRow) {
				int tileIncomingVectorCount = tile.incomingVectors.size();
				if (tile.device != '.') { 
					sb.append(tile.device);
				} else if (tileIncomingVectorCount > 1) {
					sb.append(tile.incomingVectors.size());
				} else if (tileIncomingVectorCount == 1){
					Vector incomingVector = tile.incomingVectors.get(0);
					if (incomingVector.equals(Vector.NORTH)) {
						sb.append("^");
					} else if (incomingVector.equals(Vector.SOUTH)) {
						sb.append("v");
					} else if (incomingVector.equals(Vector.EAST)) {
						sb.append(">");
					} else if (incomingVector.equals(Vector.WEST)) {
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
	
	public static Contraption simulateBeamTraversal(Contraption contraption, int beamXPos, int beamYPos, Vector beamVector) throws CloneNotSupportedException {
		Contraption simulatedContraption = new Contraption(contraption);
		simulatedContraption.simulateBeamTraversal(beamXPos, beamYPos, beamVector);
		return simulatedContraption;
	}
	
	private void simulateBeamTraversal(int beamXPos, int beamYPos, Vector beamVector) {
		if (beamXPos < 0 || beamXPos >= grid[0].length 
				|| beamYPos < 0 || beamYPos >= grid.length) {
			return;
		}
		for (Vector vector : grid[beamYPos][beamXPos].energize(beamVector)) {
			simulateBeamTraversal(beamXPos + vector.x, beamYPos + vector.y, vector);
		}
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
		StringBuffer sb = new StringBuffer(device + " [");
		for (Vector vector : incomingVectors) {
			sb.append(vector + ", ");
		}
		if (incomingVectors.size() > 0) {
			sb.delete(sb.length()-2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}
}

class Vector {
	
	public final static Vector NORTH = new Vector(0,-1);
	public final static Vector SOUTH= new Vector(0,1);
	public final static Vector EAST = new Vector(1,0);
	public final static Vector WEST = new Vector(-1,0);
	
	int x;
	int y;
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
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