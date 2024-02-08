package day04;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04 {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day04/resources/input.txt"));
		Pattern pattern = Pattern.compile("(\\d+)");
		
		ArrayList<ScratchCard> scratchCards = new ArrayList<ScratchCard>();
		
		while(reader.hasNextLine()) {
			// Read next line
			String line = reader.nextLine();
			
			// Prep line for processing numbers
			String[] numSets = line.split(":")[1].split("\\|");
			Matcher winningMatcher = pattern.matcher(numSets[0]);
			Matcher yourMatcher = pattern.matcher(numSets[1]);
			
			// Process winning numbers
			HashSet<String> winningNums = new HashSet<String>();
			while(winningMatcher.find()) {
				winningNums.add(winningMatcher.group(1));
			}
			
			// Process your numbers
			HashSet<String> yourNums = new HashSet<String>();
			while(yourMatcher.find()) {
				yourNums.add(yourMatcher.group(1));
			}
			
			// Create a ScratchCard to the list
			scratchCards.add(new ScratchCard(winningNums, yourNums));
		}
		reader.close();
		
		// Part 1 solution
		int points = 0;
		for (ScratchCard scratchCard : scratchCards) {
			HashSet<String> winningNums = new HashSet<String>(scratchCard.winningNums);
			winningNums.retainAll(scratchCard.yourNums);
			scratchCard.winCount = winningNums.size();
			points += (int) (winningNums.size() == 0 ? 0 : Math.pow(2, winningNums.size()-1));
		}
		System.out.println("points: " + points);
		
		// Part 2 solution
		int totalCardCount = 0;
		for (int i=0; i<scratchCards.size(); i++) {
			// add ScratchCard and its copies to the total count
			totalCardCount += 1 + scratchCards.get(i).copies;
			
			// for each ScratchCard and its copies
			int winCount = scratchCards.get(i).winCount;
			for (int c=0; c < 1+scratchCards.get(i).copies; c++) {
				// for each winning number add a copy to the next card(s)
				for (int j=i+1; j < i+1+winCount && j < scratchCards.size(); j++) {
					scratchCards.get(j).copies++;
				}
			}
		}
		System.out.println("total card count: " + totalCardCount);
	}

}


class ScratchCard {
	int copies;
	int winCount;
	HashSet<String> winningNums;
	HashSet<String> yourNums;
	
	public ScratchCard(HashSet<String> winningNums, HashSet<String> yourNums) {
		copies = 0;
		winCount = 0;
		this.winningNums = winningNums;
		this.yourNums = yourNums;
	}
	
	public String toString() {
		String s = "";
		for (Object num : winningNums.toArray()) {
			s += num + " ";
		}
		s += "| ";
		for (Object num : yourNums.toArray()) {
			s += num + " ";
		}
		s += " winCount=" + winCount + " copies=" + copies;
		return s;
	}
}
