package day03;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * I did not write this to only find EXACTLY 2 parts for part 2, but it worked...
 * Need to fix that and make it more efficient eventually if I want to
 */
public class Day03 {

	public static void main(String[] args) throws FileNotFoundException {
		// Read input to from file
		Scanner reader = new Scanner(new File("src/day03/resources/input.txt"));
		
		// Read lines and structure data
		Pattern pattern = Pattern.compile("(\\d+|[^\\d.])");
		int lineCount = 0;
		ArrayList<Part> parts = new ArrayList<Part>();
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		ArrayList<boolean[]> symbolMatrix = new ArrayList<boolean[]>();
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			boolean[] symbolLine = new boolean[line.length()];
			Matcher matcher = pattern.matcher(line);
			while(matcher.find()) {
				String partNo = matcher.group(1);
				try {
					parts.add(new Part(Integer.parseInt(partNo),matcher.start(1), lineCount, matcher.end(1)-matcher.start(1)));
				} catch (NumberFormatException e) {
					symbolLine[matcher.start(1)] = true;
					if (matcher.group(1).equals("*")) {
						symbols.add(new Symbol(matcher.start(1), lineCount));
					}
				}
			}
			symbolMatrix.add(symbolLine);
			lineCount++;
		}
		reader.close();
		
		// Part 1 calculation
		int partNumSum = 0;
		for (Part p : parts) {
			if (p.isValid(symbolMatrix)) {
				partNumSum += p.value;
			}
		}
		System.out.println("sum=" + partNumSum);
		
		// Part 2 calculation
		int gearRatio = 0;
		for (Symbol symbol : symbols) {
			gearRatio += symbol.calculateGearRatio(parts);
		}
		System.out.println("gear ratio: " + gearRatio);
	}
}

class Symbol extends Part {
	public Symbol(int x, int y) {
		super(0, x, y, 1);
	}
	
	public int calculateGearRatio(ArrayList<Part> parts) {
		int gearRatio = 0;
		for (Part part : parts) {
			if (part.isNeighbor(this.x, this.y)) {
				if (gearRatio > 0) {
					gearRatio *= part.value;
					return gearRatio;
				}
				gearRatio = part.value;
			}
		}
		return 0;
	}
}

class Part {
	
	int value;
	int x;
	int y;
	int length;
	
	public Part(int value, int x, int y, int length) {
		this.value = value;
		this.x = x;
		this.y = y;
		this.length = length;
	}
	
	public boolean isNeighbor(int x, int y) {
		if (this.y-1 <= y && y <= this.y+1 && 
				this.x-1 <= x && x <= this.x+this.length) {
			return true;
		}
		return false;
	}
	
	public boolean isValid(ArrayList<boolean[]> symbolMatrix) {
		for (int yTest = y-1; yTest <= y+1; yTest++) {
			if (yTest < 0) {
				continue;
			}
			if (yTest >= symbolMatrix.size()) {
				break;
			}
			for (int xTest = x-1; xTest <= x+length; xTest++) {
				if (xTest < 0) {
					continue;
				}
				if (xTest >= symbolMatrix.get(y).length) {
					break;
				}
				if (symbolMatrix.get(yTest)[xTest]) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String toString() {
		return "{value: " + value + ", x: " + x + ", y: " + y + ", length: " + length + "}";
	}
}