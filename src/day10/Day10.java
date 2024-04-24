package day10;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Day10 {

	public static void main(String[] args) throws Exception {
		// Read in input and generate PipeMap
		Scanner reader = new Scanner(new File("src/day10/resources/input.txt"));
		List<char[]> charList = new ArrayList<char[]>();
		while (reader.hasNextLine()) {
			char[] line = reader.nextLine().toCharArray();
			charList.add(line);
		}
		reader.close();
		TileMap tileMap = new TileMap(charList.toArray(new char[charList.size()][charList.get(0).length]));
		System.out.println("Part 1: " + tileMap.loop.size() / 2);
		System.out.println("Part 2: " + tileMap.findEnclosureCount());
	}
}

class Coord {
	private final static int NORTH = 0;
	private final static int SOUTH = 1;
	private final static int EAST = 2;
	private final static int WEST = 3;
	int x;
	int y;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Coord add(Coord c, int x, int y) {
		return new Coord(c.x + x, c.y + y);
	}
	
	public void add(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	public Coord minus(Coord other) {
		return new Coord(this.x - other.x, this.y - other.y);
	}
	
	public void move(int direction) {
		if (direction == NORTH) { add(0, -1); }
		if (direction == SOUTH) { add(0, 1); }
		if (direction == EAST) { add(1, 0); }
		if (direction == WEST) { add(-1, 0); }
	}
	
	public Coord clone() {
		return new Coord(x, y);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) { return false; }
		return this.x == ((Coord)other).x && this.y == ((Coord)other).y;
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}

class TileMap {
	private final static int NORTH = 0;
	private final static int SOUTH = 1;
	private final static int EAST = 2;
	private final static int WEST = 3;
	public static final Map<Character, boolean[]> types;
	static {
		Map<Character, boolean[]> t = new HashMap<Character, boolean[]>(8);
		t.put('S', new boolean[] {true, true, true, true});
		t.put('|', new boolean[] {true, true, false, false});
		t.put('-', new boolean[] {false, false, true, true});
		t.put('L', new boolean[] {true, false, true, false});
		t.put('F', new boolean[] {false, true, true, false});
		t.put('J', new boolean[] {true, false, false, true});
		t.put('7', new boolean[] {false, true, false, true});
		t.put('.', new boolean[] {false, false, false, false});
		types = t;
	};
	char[][] map;
	LinkedList<Coord> loop;
	
	public TileMap(char[][] map) throws Exception {
		this.map = map;
		loop = findLoop(getStartCoord());
		updateStartInMap();
	}
	
	public char getTile(Coord c) {
		return map[c.y][c.x];
	}
	
	public LinkedList<Coord> findLoop(Coord start) {
		for (int direction=NORTH; direction<=WEST; direction++) {
			Coord ptr = start.clone();
			ptr.move(direction);
			LinkedList<Coord> loop = new LinkedList<Coord>();
			loop.add(start);
			loop.add(ptr.clone());
			int reverse = getReverse(direction);
			while(!start.equals(ptr)) {
				boolean[] tileDirections;
				try {
					tileDirections = types.get(getTile(ptr));
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (!tileDirections[reverse]) {
					break;
				}
				for(int d=NORTH; d<=WEST; d++) {
					if (tileDirections[d] && d != reverse) {
						ptr.move(d);
						reverse = getReverse(d);
						loop.addLast(ptr.clone());
						break;
					}
				}
			}
			if (start.equals(ptr)) {
				return loop;
			}
		}
		return new LinkedList<Coord>();
	}
	
	// Use ray casting algorithm and intersect at any of the following:
	//   |
	//   L---7
	//   F---J
	public int findEnclosureCount() {
		List<Character> indicators = Arrays.asList(new Character[] {'|', 'F', 'L'});
		int insideCnt = 0;
		for (int row=0; row<map.length; row++) {
			int intersections = 0;
			Coord ptr = new Coord(0, row);
			for (int col=0; col<map[row].length; col++) {
				boolean onLoop = loop.contains(ptr);
				boolean isIndicator = indicators.contains(map[row][col]);
				if (onLoop && isIndicator) {
					if (map[row][col] == '|') {
						intersections++;
					} else if (map[row][col] == 'L') {
						while (col < map[row].length) {
							col++;
							ptr.add(1, 0);
							if (map[row][col] == '7') {
								intersections++;
								break;
							} else if (map[row][col] == 'J') {
								break;
							}
						}
					} else if (map[row][col] == 'F') {
						while (col < map[row].length) {
							col++;
							ptr.add(1, 0);
							if (map[row][col] == 'J') {
								intersections++;
								break;
							} else if (map[row][col] == '7') {
								break;
							}
						}
					}
				} else if (!onLoop && intersections % 2 == 1) {
					insideCnt++;
				}
				ptr.add(1, 0);
			}
		}
		return insideCnt;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (char[] row : map) {
			sb.append(Arrays.toString(row) + "\n");
		}
		return sb.toString();
	}
	
	private int getReverse(int direction) {
		if (direction == NORTH) { return SOUTH; }
		if (direction == SOUTH) { return NORTH; }
		if (direction == EAST) { return WEST; }
		if (direction == WEST) { return EAST; }
		return -1;
	}
	
	private Coord getStartCoord() throws Exception {
		for (int y=0; y<map.length; y++) {
			for (int x=0; x<map[y].length; x++) {
				if (map[y][x] == 'S') {
					return new Coord(x, y);
				}
			}
		}
		throw new Exception("No start position found in map");
	}
	
	private void updateStartInMap() {
		boolean[] openEdges = new boolean[4];
		Coord start = loop.getFirst();
		Coord first = loop.get(1);
		Coord last = loop.get(loop.size()-2);
		Coord firstDiff = first.minus(start);
		Coord lastDiff = last.minus(start);
		for (Coord coord : new Coord[]{firstDiff, lastDiff}) {
			if (coord.x > 0) {
				openEdges[EAST] = true;
			} else if (coord.x < 0) {
				openEdges[WEST] = true;
			} else if (coord.y > 0) {
				openEdges[SOUTH] = true;
			} else if (coord.y < 0) {
				openEdges[NORTH] = true;
			}
		}
		for (Character c : types.keySet()) {
			if (Arrays.equals(types.get(c), openEdges)) {
				map[start.y][start.x] = c;
				break;
			}
		}
	}
}