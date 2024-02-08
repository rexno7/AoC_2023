package day01;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day01 {

	private static final Map<String,String> NUM_DICT = Map.of("one", "1", "two", "2", "three", "3", "four", "4", "five", "5", "six", "6", "seven", "7", "eight", "8", "nine", "9", "zero", "0");
	
	public static void main(String[] args) throws IOException {
		File day01Input = new File("src/day01/resources/day01_input.dat");
		Scanner reader = new Scanner(day01Input);
		
		int sum = 0;
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			sum += convertWords(line);
		}
		
		reader.close();
		System.out.println("sum: " + sum);
	}
	
	private static int convertWords(String line) {
//		System.out.println("LINE:  " + line);
//		Pattern pattern = Pattern.compile("(\\d)");
		Pattern pattern = Pattern.compile("(?=(one|two|three|four|five|six|seven|eight|nine|\\d))");
		Matcher matcher = pattern.matcher(line);

		matcher.find();
//		System.out.println("group: " + matcher.group(1));
		String firstMatch = matcher.group(1);
		String lastMatch = matcher.group(1);
		while (matcher.find()) {
//			System.out.println("group: " + matcher.group(1));
			lastMatch = matcher.group(1);
		}
		
		String firstInt = firstMatch.length() > 1 ? NUM_DICT.get(firstMatch) : firstMatch;
		String lastInt = lastMatch.length() > 1 ? NUM_DICT.get(lastMatch) : lastMatch;
//		System.out.println("value: " + firstInt + lastInt);
		
		return Integer.parseInt(firstInt + lastInt);
	}
}
