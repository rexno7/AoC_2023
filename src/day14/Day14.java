package day14;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Day14 {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day14/resources/input.txt"));
		LinkedList<String> data = new LinkedList<String>();
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			data.addFirst(line);
		}
		RockPlatform platform = new RockPlatform(data);
		System.out.println(platform.toString());
		long cycles = 1000;
		for (long i=0; i<cycles; i++) {
			platform.tilt(RockPlatform.Direction.NORTH);
			platform.tilt(RockPlatform.Direction.WEST);
			platform.tilt(RockPlatform.Direction.SOUTH);
			platform.tilt(RockPlatform.Direction.EAST);
//			System.out.println("cycle " + (i + 1));
//			System.out.print(platform.toString());
			System.out.println((i+1) + ": " + platform.calculateLoad());
		}
		// found a repeating pattern of 22 numbers and just did mod math on the
		// repeating set to figure out the billionth cycle load of 108404
		System.out.println((1000000000-115) % 22);
	}
}

class RockPlatform {
	
	enum Direction {
		NORTH,
		SOUTH,
		EAST,
		WEST
	}
	
	char[][] platform;
	
	public RockPlatform(List<String> data) {
		platform = new char[data.size()][];
		for (int r=0; r < data.size(); r++) {
			platform[r] = data.get(r).toCharArray();
		}
	}
	
	public RockPlatform(char[][] platform) {
		this.platform = platform;
	}
	
	public void tilt(Direction dir) {
		// Fill a new platform 2D char array
		char[][] newPlatform = new char[platform.length][platform[0].length];
		for (int i=0; i<platform.length; i++) {
			Arrays.fill(newPlatform[i], '.');
		}
		
		if (dir == Direction.NORTH) { 
			tiltNorth(newPlatform); 
		} else if (dir == Direction.SOUTH) {
			tiltSouth(newPlatform);
		} else if (dir == Direction.EAST) {
			tiltEast(newPlatform);
		} else if (dir == Direction.WEST) {
			tiltWest(newPlatform);
		}
	}
	
	private void tiltNorth(char[][] newPlatform) {
		for (int c=0; c<platform[0].length; c++) {
			LinkedList<Integer> openRows = new LinkedList<Integer>();
			for (int r=platform.length-1; r >= 0; r--) {
				processCellVertical(r, c, newPlatform, openRows);
			}
		}
		platform = newPlatform;
	}
	
	private void tiltSouth(char[][] newPlatform) {
		for (int c=0; c<platform[0].length; c++) {
			LinkedList<Integer> openRows = new LinkedList<Integer>();
			for (int r=0; r < platform.length; r++) {
				processCellVertical(r, c, newPlatform, openRows);
			}
		}
		platform = newPlatform;
	}
	
	private void tiltEast(char[][] newPlatform) {
		for (int r=0; r<platform.length; r++) {
			LinkedList<Integer> openCols = new LinkedList<Integer>();
			for (int c=platform[r].length-1; c >= 0; c--) {
				processCellHorizontal(r, c, newPlatform, openCols);
			}
		}
		platform = newPlatform;
	}
	
	private void tiltWest(char[][] newPlatform) {
		for (int r=0; r<platform.length; r++) {
			LinkedList<Integer> openCols = new LinkedList<Integer>();
			for (int c=0; c < platform[r].length; c++) {
				processCellHorizontal(r, c, newPlatform, openCols);
			}
		}
		platform = newPlatform;
	}
	
	private void processCellVertical(int r, int c, 
			char[][] newPlatform, LinkedList<Integer> openRows) {
		if (platform[r][c] == 'O') {
			Integer poppedRow = openRows.poll();
			if (poppedRow == null) {
				newPlatform[r][c] = 'O';
			} else {
				newPlatform[poppedRow][c] = 'O';
				openRows.add(r);
			}
		}
		else if (platform[r][c] == '.') {
			openRows.add(r);
		} else if (platform[r][c] == '#') {
			newPlatform[r][c] = '#';
			openRows.clear();
		}
	}
	
	private void processCellHorizontal(int r, int c, 
			char[][] newPlatform, LinkedList<Integer> openCols) {
		if (platform[r][c] == 'O') {
			Integer poppedCol = openCols.poll();
			if (poppedCol == null) {
				newPlatform[r][c] = 'O';
			} else {
				newPlatform[r][poppedCol] = 'O';
				openCols.add(c);
			}
		}
		else if (platform[r][c] == '.') {
			openCols.add(c);
		} else if (platform[r][c] == '#') {
			newPlatform[r][c] = '#';
			openCols.clear();
		}
	}
	
	public long calculateLoad() {
		long load = 0;
		for (int r=0; r < platform.length; r++) {
			for (int c=0; c<platform[r].length; c++) {
				if (platform[r][c] == 'O') {
					load += r + 1;
				}
			}
		}
		return load;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r=platform.length-1; r >= 0; r--) {
			sb.append((r+1) + ((r+1 > 99) ? " " : (r+1 > 9) ? "  " : "   "));
			for (int c=0; c<platform[r].length; c++) {
				sb.append(platform[r][c]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
