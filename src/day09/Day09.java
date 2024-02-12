package day09;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day09 {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day09/resources/input.txt"));
		ArrayList<List<Integer>> listOfValues = new ArrayList<List<Integer>>();
		while (reader.hasNextLine()) {
			listOfValues.add(
					Arrays.stream(reader.nextLine().split("\\s+"))
					.map(Integer::parseInt)
					.toList()
			);
		}
		
		int part1 = 0, part2 = 0;
		for (List<Integer> list : listOfValues) {
			part1 += extrapolate(list);
			part2 += extrapolateBackwards(list);
		}
		System.out.println("part1=" + part1);
		System.out.println("part2=" + part2);
	}
	
	private static int extrapolate(List<Integer> values) {
		if (values.stream().allMatch(val -> val == values.get(0))) {
			return values.get(0);
		}
		List<Integer> extrapolatedValues = extrapolateList(values);
		return extrapolate(extrapolatedValues) + values.get(values.size()-1);
	}
	
	private static int extrapolateBackwards(List<Integer> values) {
		if (values.stream().allMatch(val -> val == 0)) {
			return 0;
		}
		List<Integer> extrapolatedValues = extrapolateList(values);
		return values.get(0) - extrapolateBackwards(extrapolatedValues);
	}
	
	private static List<Integer> extrapolateList(List<Integer> values) {
		List<Integer> extrapolatedValues = new ArrayList<Integer>(values.size()-1);
		for (int x=1; x<values.size(); x++) {
			extrapolatedValues.add(values.get(x) - values.get(x-1));
		}
		return extrapolatedValues;
	}
}
