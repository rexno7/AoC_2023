package day02;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day02 {

	public static void main(String[] args) throws FileNotFoundException {
		// Read in file
		Scanner reader = new Scanner(new File("src/day02/resources/input.txt"));
		
		// Object construction
		LinkedList<Game> games = new LinkedList<Game>();
		while(reader.hasNextLine()) {
			games.add(new Game(reader.nextLine()));
		}
		reader.close();
		
		// Part 1 solution
		int sum = 0;
		Grab maxGrabVals = new Grab(12, 14, 13);
		for (Game game : games) {
			if (game.isPossible(maxGrabVals)) {
				sum += game.id;
			}
		}
		System.out.println("sum of IDs: " + sum);
		
		// Part 2 solution
		int sumOfPower = 0;
		for (Game game : games) {
			Grab minBagVals = game.getMinBagVals();
			sumOfPower += minBagVals.red * minBagVals.blue * minBagVals.green;
		}
		System.out.println("sum of power: " + sumOfPower);
	}
}

class Game {
	int id;
	List<Grab> grabs;
	
	public Game(String rawGame) {
		parseGame(rawGame);
	}
	
	public boolean isPossible(Grab maxGrabVals) {
		for (Grab grab : grabs) {
			if (!grab.isPossible(maxGrabVals)) {
				return false;
			}
		}
		return true;
	}
	
	public Grab getMinBagVals() {
		int maxRed = 0, maxBlue = 0, maxGreen = 0;
		for (Grab grab : grabs) {
			maxRed = grab.red > maxRed ? grab.red : maxRed;
			maxBlue = grab.blue > maxBlue ? grab.blue : maxBlue;
			maxGreen = grab.green > maxGreen ? grab.green : maxGreen;
		}
		return new Grab(maxRed, maxBlue, maxGreen);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Game " + id + ":");
		for (Grab grab : grabs) {
			sb.append(" " + grab.toString() + ";");
		}
		return sb.deleteCharAt(sb.length()-1).toString();
	}
	
	private void parseGame(String rawGame) {
		// Split game string
		Matcher gameMatcher = Pattern.compile("Game (\\d+): (.*)").matcher(rawGame);
		String rawGrabs = "";
		if (gameMatcher.find()) {
			id = Integer.parseInt(gameMatcher.group(1));
			rawGrabs = gameMatcher.group(2);
		}
		
		// Split grab string
		String[] rawGrabList = rawGrabs.split(";");
		
		// Populate grab list and validate
		grabs = new LinkedList<Grab>();
		for (String rawGrab : rawGrabList) {
			Grab g = new Grab(rawGrab.strip());
			grabs.add(g);
		}
	}
}

class Grab {
	int red = 0;
	int blue = 0;
	int green = 0;
	
	public Grab(String rawGrab) {
		parseRawGrab(rawGrab.toLowerCase());
	}
	
	public Grab(int red, int blue, int green) {
		this.red = red;
		this.blue = blue;
		this.green = green;
	}
	
	public boolean isPossible(Grab maxGrabVals) {
		return this.red <= maxGrabVals.red && this.blue <= maxGrabVals.blue && 
				this.green <= maxGrabVals.green;
	}
	
	public String toString() {
		return red + " red, " + blue + " blue, " + green + " green";
	}
	
	private void parseRawGrab(String rawGrab) {
		Matcher grabMatcher = Pattern.compile("(\\d+) (\\w+)").matcher(rawGrab);
		while (grabMatcher.find()) {
			switch (grabMatcher.group(2)) {
			case "red":
				red = Integer.parseInt(grabMatcher.group(1));
				break;
			case "blue":
				blue = Integer.parseInt(grabMatcher.group(1));
				break;
			case "green":
				green = Integer.parseInt(grabMatcher.group(1));
				break;
			default:
				System.err.println("Bad match: " + rawGrab);
			}
		}
	}
}

