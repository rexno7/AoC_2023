//package day03;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.HashSet;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Day03Efficient {
//
//	public static void main(String[] args) throws FileNotFoundException {
//		Pattern numberPattern = Pattern.compile("\\d+");
//		Pattern symbolPattern = Pattern.compile("[^\\d.]");
//		Scanner reader = new Scanner(new File("src/day03/resources/test1.txt"));
//		
//		int lineNo = 0;
//		Set<Coord> symbols = new HashSet<Coord>();
//		Set<Part> parts = new HashSet<Part>();
//		while(reader.hasNextLine()) {
//			String line = reader.nextLine();
//			Matcher numberMatcher = numberPattern.matcher(line);
//			if (numberMatcher.find()) {
//				parts.add(new Part(
//						numberMatcher.start(), 
//						lineNo, 
//						numberMatcher.end() - numberMatcher.start(), 
//						Integer.parseInt(numberMatcher.group(1))));
//				continue;
//			}
//			Matcher symbolMatcher = symbolPattern.matcher(line);
//			if (symbolMatcher.find()) {
//				symbols.add(new Coord(symbolMatcher.start(), lineNo));
//			}
//		}
//
//	}
//}
//
//class Coord {
//	int x;
//	int y;
//	
//	public Coord(int x, int y) {
//		this.x = x;
//		this.y = y;
//	}
//	
//	public boolean equals(Coord other) {
//		return x == other.x && y == other.y;
//	}
//}
//
//private class Part {
//	Coord coord;
//	int length;
//	int value;
//	
//	public Part(int x, int y, int length, int value) {
//		coord = new Coord(x, y);
//		this.length = length;
//		this.value = value;
//	}
//	
//	public boolean isNeighbor(Coord other) {
//		return coord.x-1 <= other.x && other.x <= coord.x+length && 
//				coord.y-1 <= other.y && other.y <= coord.y+1;
//	}
//}
