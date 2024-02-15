package day11;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day11 {

	public static void main(String[] args) throws FileNotFoundException {
		// Read in lines and save galaxy Coords with max row and colLength
		Scanner reader = new Scanner(new File("src/day11/resources/input.txt"));
		List<Coord> galaxies = new LinkedList<Coord>();
		long row = 0L;
		long colLength = 0L;
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			colLength = line.length();
			long chopped = 0;
			while (line.length() > 0) {
				int idx = line.indexOf('#');
				if (idx == -1) {
					break;
				}
				galaxies.add(new Coord(row, idx + chopped));
				line = line.substring(idx+1);
				chopped += idx + 1;
			}
			row++;
		}
		
		// Identify expanded rows and cols
		List<Long> galaxyRows = galaxies.stream().map((coord) -> coord.row).distinct().toList();
		List<Long> galaxyCols = galaxies.stream().map((coord) -> coord.col).distinct().toList();
		List<Long> expandedRows = new ArrayList<Long>();
		List<Long> expandedCols = new ArrayList<Long>();
		for (long r=0; r<row; r++) {
			if (!galaxyRows.contains(r)) {
				expandedRows.add(r);
			}
		}
		for (long c=0; c<colLength; c++) {
			if (!galaxyCols.contains(c)) {
				expandedCols.add(c);
			}
		}
		
		// Create combinations of galaxy Coord pairs
//		galaxies.stream().forEach(System.out::println);
		Map<CoordPair, Long> pairDistsP1 = new HashMap<CoordPair, Long>();
		Map<CoordPair, Long> pairDistsP2 = new HashMap<CoordPair, Long>();
		for (Coord c1 : galaxies) {
			for (Coord c2 : galaxies) {
				if (c1.equals(c2)) {
					continue;
				}
				pairDistsP1.put(new CoordPair(c1, c2), c1.calcDist(c2, expandedRows, expandedCols, 2));
				pairDistsP2.put(new CoordPair(c1, c2), c1.calcDist(c2, expandedRows, expandedCols, 1000000));
			}
		}
		
		System.out.println("part1: " + pairDistsP1.values().stream().reduce(0L, (a,b) -> a + b));
		System.out.println("part2: " + pairDistsP2.values().stream().reduce(0L, (a,b) -> a + b));
	}
}

class Coord implements Comparable<Coord> {
	long row;
	long col;
	
	public Coord(long row, long col) {
		this.row = row;
		this.col = col;
	}
	
	public long calcDist(Coord other) {
		return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
	}
	
	public long calcDist(Coord o, List<Long> rowExpanse, List<Long> colExpanse, long multiplier) {
		long expanse = 0;
		for (Long r : rowExpanse) {
			if ((this.row < r && r < o.row) || (o.row < r && r < this.row)) {
				expanse += (multiplier - 1);
			}
		}
		for (Long c : colExpanse) {
			if ((this.col < c && c < o.col) || (o.col < c && c < this.col)) {
				expanse += (multiplier - 1);
			}
		}
		return calcDist(o) + expanse;
	}
	
	@Override
	public boolean equals(Object other) {
		Coord o = (Coord) other;
		return row == o.row && col == o.col;
	}
	
	@Override
	public String toString() {
		return "{" + row + "," + col + "}";
	}

	@Override
	public int compareTo(Coord o) {
		if (this.row < o.row) { return 1; }
		else if (this.row > o.row) { return -1; }
		else if (this.col < o.col) { return 1; }
		else if (this.col > o.col) { return -1; }
		return 0;
	}
}

class CoordPair {
	Coord c1, c2;
	long distance;
	
	public CoordPair(Coord c1, Coord c2) {
		if (c1.compareTo(c2) > 0) {
			this.c1 = c1;
			this.c2 = c2;
		} else {
			this.c1 = c2;
			this.c2 = c1;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		CoordPair o = (CoordPair) other;
		return (this.c1.equals(o.c1) && this.c2.equals(o.c2)) 
				|| (this.c1.equals(o.c2) && this.c2.equals(o.c1));
	}
	
	@Override
	public int hashCode() {
		return ("" + c1.row + c1.col + c2.row + c2.col).hashCode();
	}
	
	@Override
	public String toString() {
		return c1.toString() + "->" + c2.toString();
	}
}