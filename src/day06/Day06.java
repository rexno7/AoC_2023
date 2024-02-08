package day06;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day06 {

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<Race> races = readFile(1);
		
		int permutations = 1;
		for (Race r : races) {
			permutations *= r.recordBreakingWindow();
		}
		System.out.println("permutations: " + permutations);
	}
	
	private static ArrayList<Race> readFile(int part) throws FileNotFoundException {
		Pattern pattern = Pattern.compile("(\\d+)");
		ArrayList<Race> races = new ArrayList<Race>();
		Scanner reader = new Scanner(new File("src/day06/resources/input.txt"));
		
		// Time
		if (part == 1) {
			Matcher matcher = pattern.matcher(reader.nextLine().split(":")[1]);
			while (matcher.find()) {
				races.add(new Race(Integer.parseInt(matcher.group(1))));
			}
		} else { /* part 2 */
			races.add(new Race(Long.parseLong(
					reader.nextLine().split(":")[1].replaceAll("\\s+", ""))));
		}
		
		// Distance
		if (part == 1) {
			Matcher matcher = pattern.matcher(reader.nextLine().split(":")[1]);
			for (int i=0; matcher.find(); i++) {
				races.get(i).setRecordDistance(Integer.parseInt(matcher.group(1)));
			}
		} else { /* part 2 */
			races.get(0).setRecordDistance(Long.parseLong(
					reader.nextLine().split(":")[1].replaceAll("\\s+", "")));
		}
		
		reader.close();
		return races;
	}
	
	private static class Race {
		private long time;
		private long recordDistance;
		
		public Race(long time) {
			this.time = time;
		}
		
		public void setRecordDistance(long recordDistance) {
			this.recordDistance = recordDistance;
		}
		
		/**
		 * Binary search through from 0..time/2 and find 
		 * the earliest time you can release the button in
		 * order to break the distance record. Makes sure 
		 * the earliestRelease > recordDistance. Returns the
		 * count of the range from earliestRelease to 
		 * time+1 - earliestRelease to give the full window. 
		 */
		public long recordBreakingWindow() {
			long earliestRelease = time/2;
			long lo = 0;
			long hi = earliestRelease;
			while (lo <= hi) {
				long mid = lo + ((hi - lo) / 2);
				long midDist = (time-mid) * mid;
				if (midDist < recordDistance) {
					earliestRelease = mid + 1;
					lo = mid + 1;
				} else if (midDist > recordDistance) {
					earliestRelease = mid;
					hi = mid - 1;
				} else {
					earliestRelease = mid + 1;
					break;
				}
			}
			return time+1-earliestRelease*2;
		}
		
		public String toString() {
			return "{ time: " + time + ", recordDistance: " + recordDistance + " }";
		}
	}
}
