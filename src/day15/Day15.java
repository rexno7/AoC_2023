package day15;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Day15 {
	
	public final static int MAX_BOXES = 256;

	public static void main(String[] args) throws FileNotFoundException {
		// Read in the initialization sequence steps 
		Scanner reader = new Scanner(new File("src/day15/resources/input.txt"));
		String initializationSequence = reader.nextLine();
		reader.close();
		String[] stepsInInitSequence = initializationSequence.split(",");
		
		// Create 256 boxes labeled 0-255
		ArrayList<Box> boxes = new ArrayList<Box>(MAX_BOXES);
		for (int i=0; i<MAX_BOXES; i++) {
			boxes.add(new Box(i));
		}

		// Part 1: Get sum of HASHes
		// Part 2: Follow steps of HASHMAP for each step
		int sumOfHashes = 0;
		for (String step : stepsInInitSequence) {
			sumOfHashes += holidayAsciiStringHelper(step);
			holidayAsciiStringHelperManualArrangementProcedure(boxes, step);
		}
		System.out.println("part1: " + sumOfHashes);
		
		// Calculate focusing power of all lenses in all boxes
		long totalFocusingPower = 0;
		for (Box box : boxes) {
			totalFocusingPower += box.totalFocusingPower();
		}
		System.out.println("part2: " + totalFocusingPower);
		
	}
	
	////////////
	// PART 1 //
	////////////
	
	private static int holidayAsciiStringHelper(String stringToBeHashed) {
		int currentValue = 0;
		for (char character : stringToBeHashed.toCharArray()) {
			currentValue += asciiCode(character);
			currentValue *= 17;
			currentValue %= 256;
		}
		return currentValue;
	}
	
	private static int asciiCode(char character) {
		return (int) character;
	}
	
	////////////
	// PART 2 //
	////////////

	private static void holidayAsciiStringHelperManualArrangementProcedure(
			ArrayList<Box> boxes, String step) {
		String[] stepPieces = step.split("=|-");
		String lensLabel = stepPieces[0];
		int boxNumber = holidayAsciiStringHelper(lensLabel);
		if (step.contains("-")) {
			boxes.get(boxNumber).removeLens(lensLabel);
		} else if (step.contains("=")) {
			String focalLength = stepPieces[1];
			boxes.get(boxNumber).addLens(
					new Lens(lensLabel, Integer.parseInt(focalLength)));
		}
	}
}

/////////////
// Classes //
/////////////

class Box {
	int number;
	LinkedList<Lens> lenses;
	
	public Box(int number) {
		this.number = number;
		this.lenses = new LinkedList<Lens>();
	}
	
	// focalLength of lens that needs to go into boxNumber
	// mark lens with lensLabel
	// Possible scenarios:
	//   1) if duplicate label in box, replace the old lens with new one
	//   2) place lens at the back of the box (if empty it just goes in front)
	public void addLens(Lens newLens) {
		for (int i=0; i<lenses.size(); i++) {
			if (lenses.get(i).label.equals(newLens.label)) {
				lenses.set(i, newLens);
				return;
			}
		}
		lenses.add(newLens);
	}
	
	// 1) get lens with matching label from box
	// 2) move lenses in order to front of box
	// * if no lens in box has a matching label, nothing happens
	public boolean removeLens(String label) {
		return lenses.removeIf(lens -> lens.label.equals(label));
	}
	
	// boxFocusingPower = boxNumber+1 * lensSlot+1 * lensFocalLength
	public long totalFocusingPower() {
		long boxFocusingPower = 0;
		for (int slot=0; slot<lenses.size(); slot++) {
			long lensFocusingPower = 
					(number + 1) * (slot + 1) * lenses.get(slot).focalLength;
			boxFocusingPower += lensFocusingPower;
		}
		return boxFocusingPower;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Box " + number + ":");
		for (Lens lens : lenses) {
			sb.append(" [" + lens.label + " " + lens.focalLength + "]");
		}
		return sb.toString();
	}
}

class Lens {
	
	String label;
	int focalLength;
	
	public Lens(String label, int focalLength) {
		this.label = label;
		this.focalLength = focalLength;
	}
	
	@Override
	public String toString() {
		return label + " " + focalLength;
	}
}