package day12;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Day12 {
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day12/resources/input.txt"));
		long totalFoldedCount = 0;
		long totalUnfoldedCount = 0;
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			String[] conditionRow = line.split("\\s+");
			String config = conditionRow[0];
			List<Integer> nums = Arrays.stream(conditionRow[1].split(",")).map(a -> Integer.parseInt(a)).toList();
			List<Integer> unfoldedNums = unfoldNums(nums, 5);
			String unfoldedConfig = unfoldConfig(config, 5);
			totalFoldedCount += count(config, nums, new HashMap<Integer, Long>());
			totalUnfoldedCount += count(unfoldedConfig, unfoldedNums, new HashMap<Integer, Long>());
		}
		System.out.println("part1: " + totalFoldedCount);
		System.out.println("part2: " + totalUnfoldedCount);
	}
	
	private static List<Integer> unfoldNums(List<Integer> nums, int count) {
		List<Integer> unfoldedNums = new ArrayList<Integer>();
		for (;count > 0; count--) {
			unfoldedNums.addAll(nums);
		}
		return unfoldedNums;
	}
	
	private static String unfoldConfig(String config, int count) {
		if (count < 1) {
			return "";
		}
		String unfoldedConfig = config;
		count--;
		for (; count > 0; count--) {
			unfoldedConfig += "?" + config;
		}
		return unfoldedConfig;
	}
	
	private static long count(String config, List<Integer> nums, Map<Integer, Long> cache) {
		// if no more config and nums, return 1
		if (config.length() == 0) {
			return nums.size() == 0 ? 1 : 0;
		}
		// if no more nums and config contains no broken parts, return 1
		if (nums.size() == 0) {
			return config.contains("#") ? 0 : 1;
		}
		
		long cnt = 0;
		Key key = new Key(config, nums);
		if (cache.containsKey(key.hashCode())) {
			return cache.get(key.hashCode());
		}
		
		// assume working first
		if (config.charAt(0) == '.' || config.charAt(0) == '?') {
			cnt += count(config.substring(1), nums, cache);
		}
		
		// check for broken
		if (config.charAt(0) == '#' || config.charAt(0) == '?') {
			if (nums.get(0) <= config.length() /* validate length remaining */
					&& !config.substring(0, nums.get(0)).contains(".") /* make sure num[0] will fit config */
					&& (nums.get(0) == config.length() || config.charAt(nums.get(0)) != '#') /* make sure next part is not broken */
					) {
				cnt += count(config.substring(Math.min(nums.get(0) + 1, config.length())), 
						nums.subList(1, nums.size()), 
						cache);
			}
		}
		cache.put(key.hashCode(), cnt);
		return cnt;
	}
}

class Key {
	String config;
	List<Integer> nums;
	
	public Key(String config, List<Integer> nums) {
		this.config = config;
		this.nums = nums;
	}
	
	public String toString() {
		return config + ":" + Arrays.toString(nums.toArray());
	}
	
	@Override
	public int hashCode() {
		return (config + ":" + Arrays.toString(nums.toArray())).hashCode();
	}
}