package day05;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day05 {
	
	public enum Factor {
		SEED,
		SOIL,
		FERTILIZER,
		WATER,
		LIGHT,
		TEMPERATURE,
		HUMIDITY,
		LOCATION
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		// Patterns
		Pattern seedPattern = Pattern.compile("(\\d+)");
		Pattern seedPattern2 = Pattern.compile("(\\d+)\s+(\\d+)");
		Pattern mapPattern = Pattern.compile("(\\w+)-to-(\\w+) map:");
		Pattern rangePattern = Pattern.compile("(\\d+) (\\d+) (\\d+)");
		
		// Read Seed values
		ArrayList<Long> seeds = new ArrayList<Long>();
		HashMap<Long, Long> seedsAvailable = new HashMap<Long, Long>();
		Scanner reader = new Scanner(new File("src/day05/resources/test.txt"));
		if (reader.hasNextLine()) {
			String line = reader.nextLine();
			Matcher seedMatcher = seedPattern.matcher(line.split(":")[1]);
			Matcher seedMatcher2 = seedPattern2.matcher(line.split(":")[1]);
			while (seedMatcher.find()) {
				seeds.add(Long.parseLong(seedMatcher.group(1)));
			}
			while (seedMatcher2.find()) {
				seedsAvailable.put(
						Long.parseLong(seedMatcher2.group(1)), 
						Long.parseLong(seedMatcher2.group(2)));
			}
		}
		
		// Read map values
		HashMap<FactorMap, ArrayList<RangeMap>> maps = new HashMap<FactorMap, ArrayList<RangeMap>>();
		FactorMap currentMapName = null;
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			Matcher mapMatcher = mapPattern.matcher(line);
			Matcher rangeMatcher = rangePattern.matcher(line);
			if (mapMatcher.find()) {
				currentMapName = new FactorMap(
						Factor.valueOf(mapMatcher.group(1).toUpperCase()), 
						Factor.valueOf(mapMatcher.group(2).toUpperCase()));
			} else if (rangeMatcher.find()) {
				if (!maps.containsKey(currentMapName)) {
					maps.put(currentMapName, new ArrayList<RangeMap>());
				}
				maps.get(currentMapName).add(new RangeMap(
						Long.parseLong(rangeMatcher.group(1)), 
						Long.parseLong(rangeMatcher.group(2)),
						Long.parseLong(rangeMatcher.group(3))));
			}
		}
		reader.close();
		
		// Order Factor Maps
		int i = 0;
//		Factor x = Factor.SEED; /* part 1 */
		Factor x = Factor.LOCATION; /* part 2 */
		FactorMap[] sortedFactorMaps = new FactorMap[maps.keySet().size()];
		while (x != null) {
			for (FactorMap map : maps.keySet()) {
//				if (map.src == x) { /* part 1 */
				if (map.dest == x) { /* part 2 */
					sortedFactorMaps[i] = map;
//					x = map.dest; /* part 1 */
					x = map.src; /* part 2 */
					i++;
					if (i >= sortedFactorMaps.length) {
						x = null;
					}
					break;
				}
			}
		}
		
		// sort all FactorMaps in ascending order to find the lowest location-seed pair first
		for (FactorMap factorMap : maps.keySet()) {
			Collections.sort(maps.get(factorMap), new Comparator<RangeMap>() {
				public int compare(RangeMap r1, RangeMap r2) {
					if (r1.destVal == r2.destVal) {
						return 0;
					}
					return r1.destVal < r2.destVal ? -1 : 1;
				}
			});
		}
		
		// Part 2 Loop
		// Loop through locations from [0,) and traverse from LOCATION to SEED and return first found
		Long lowestLocation = 0L;
		while (true) {
				
				// Traverse through the factor map:
				//   LOCATION->HUMIDITY->TEMPERATURE->LIGHT->WATER->FERTILIZER->SOIL->SEED
				// Stop when you can no longer traverse through.
				Long factorValue = lowestLocation;
				System.out.print("Testing Location " + factorValue + "...");
				for (FactorMap factorMap : sortedFactorMaps) {
					
					// Loop through ranges of RangeMaps for the current FactorMap
					// until you find a match
					for (RangeMap frm : maps.get(factorMap)) {
						if (Range.inRange(factorValue, frm.destVal, frm.range)) {
							factorValue += frm.srcVal - frm.destVal;
							break; /* if factorValue fits in the destVal range */
						}
					}
				}
				for (Long seedValStart : seedsAvailable.keySet()) {
					if (Range.inRange(factorValue, seedValStart, seedsAvailable.get(seedValStart))) {
						System.out.println("seed " + factorValue + " found!");
						return;
					}
				}
				System.out.println("No seed " + factorValue + " found");
				lowestLocation++;
		}
		
		// Part 1 loop
//		Long closestSeed = -1L;
//		Long closestLocation = 0L;
//		for (Long seed : seeds) {
//			Long factorValue = seed;
//			for (FactorMap factorMap : sortedFactorMaps) {
//				for (RangeMap rangeMap : maps.get(factorMap)) {
//					if (rangeMap.srcVal <= factorValue && 
//							factorValue <= rangeMap.srcVal+rangeMap.range) {
//						factorValue = factorValue + (rangeMap.destVal - rangeMap.srcVal);
//						break;
//					}
//				}
//			}
//			if (closestLocation < 0 || factorValue < closestLocation) {
//				closestLocation = factorValue;
//				closestSeed = seed;
//			}
//		}
//		System.out.println("place seed " + closestSeed + " at location " + closestLocation);
	}
}

class FactorMap {
	Day05.Factor src;
	Day05.Factor dest;
	
	public FactorMap(Day05.Factor src, Day05.Factor dest) {
		this.src = src;
		this.dest = dest;
	}
	
	public String toString() {
		return src + "-to-" + dest;
	}
}

class RangeMap {
	Long srcVal;
	Long destVal;
	Long range;
	
	public RangeMap(Long destVal, Long srcVal, Long range) {
		this.srcVal = srcVal;
		this.destVal = destVal;
		this.range = range;
	}
	
	public String toString() {
		return "{" + destVal + " " + srcVal + " " + range + "}";
	}
}


class Range {
	public static boolean inRange(Long testNum, Long startVal, Long rangeLen) {
		return startVal <= testNum && testNum < startVal + rangeLen;
	}
}