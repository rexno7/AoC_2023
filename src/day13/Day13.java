package day13;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day13 {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day13/resources/input.txt"));
		List<Reflection> reflections = new ArrayList<Reflection>();
		List<String> reflection = new ArrayList<String>();
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			if (line.length() == 0) {
				reflections.add(new Reflection(reflection));
				reflection = new ArrayList<String>();
			} else {
				reflection.add(line);
			}
		}
		reader.close();
		reflections.add(new Reflection(reflection));
		
		// set for part 2
		boolean part2 = false;
		
		int summarize = 0;
		for (Reflection r : reflections) {
			int x = 0;
			x += r.verticalReflection(part2);
			x += 100 * r.horizontalReflection(part2);
//			System.out.println(r + "\n" + x + "\n");
			summarize += x;
		}
		System.out.println("part" + (part2? "2" : "1") + ":" + summarize);
	}
}
 class Reflection {
	 List<String> rows;
	 
	 public Reflection(List<String> rows) {
		 this.rows = rows;
	 }
	 
	 public int horizontalReflection(boolean hasSmudge) {
		 for (int r=1; r<rows.size(); r++) {
			 if (isHorizontalReflection(r - 1, r, hasSmudge)) {
				 return r;
			 }
		 }
		 return 0;
	 }
	 
	 public int verticalReflection(boolean hasSmudge) {
		 for (int c=1; c<rows.get(0).length(); c++) {
			 if (isVerticalReflection(c - 1, c, hasSmudge)) {
				 return c;
			 }
		 }
		 return 0;
	 }
	 
	 /**
	  * Loops through chars in given rows r1 & r2 to first find the horizontal
	  * reflection line. Once one is found, it extends the reflection check in opposite
	  * directions until it reaches a starting or ending row, where it will return true.
	  * If hasSmudge is set, the method requires EXACTLY ONE character difference for 
	  * the entire reflection.
	  * 
	  * @param r1 first row number
	  * @param r2 second row number
	  * @param hasSmudge forces a smudge to be detected to find a reflection
	  * @return row count from 0 to reflection line
	  */
	 private boolean isHorizontalReflection(int r1, int r2, boolean hasSmudge) {
		 if (r1 < 0 || r2 >= rows.size()) {
			 return !hasSmudge;
		 }
		 for (int c=0; c<rows.get(r1).length(); c++) {
			 if (rows.get(r1).charAt(c) != rows.get(r2).charAt(c)) {
				 if (hasSmudge) {
					 hasSmudge = false;
				 } else {
					 return false;
				 }
			 }
		 }
		 return isHorizontalReflection(r1 - 1, r2 + 1, hasSmudge);
	 }
	 
	 /**
	  * Loops through chars in given columns c1 & c2 to first find the vertical 
	  * reflection line. Once one is found, it extends the reflection check in opposite
	  * directions until it reaches a starting or ending column, where it will return true.
	  * If hasSmudge is set, the method requires EXACTLY ONE character difference for 
	  * the entire reflection.
	  * 
	  * @param c1 first column number
	  * @param c2 second column number
	  * @param hasSmudge forces a smudge to be detected to find a reflection
	  * @return column count from 0 to reflection line
	  */
	 private boolean isVerticalReflection(int c1, int c2, boolean hasSmudge) {
		 if (c1 < 0 || c2 >= rows.get(0).length()) {
			 return !hasSmudge;
		 }
		 for (int r=0; r<rows.size(); r++) {
			 if (rows.get(r).charAt(c1) != rows.get(r).charAt(c2)) {
				 if (hasSmudge) {
					 hasSmudge = false;
				 } else {
					 return false;
				 }
			 }
		 }
		 return isVerticalReflection(c1 - 1, c2 + 1, hasSmudge);
	 }
	 
	 @Override
	 public String toString() {
		 String result = "";
		 for (String row : rows) {
			 result += row + "\n";
		 }
		 return result.substring(0,result.length()-1);
	 }
 }