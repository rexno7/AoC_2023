package day08;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day08 {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner reader = new Scanner(new File("src/day08/resources/input.txt"));
		Pattern pattern = Pattern.compile("(\\w{3}) = \\((.*)\\)");
		
		// Create string mapping of network
		HashMap<String, String[]> network = new HashMap<String, String[]>();
		String instructions = reader.nextLine();
		while (reader.hasNextLine()) {
			Matcher matcher = pattern.matcher(reader.nextLine());
			if (matcher.find()) {
				network.put(
						matcher.group(1), 
						matcher.group(2).replace(" ", "").split(","));
			}
		}
		reader.close();

		// Part 1
		MapNetwork mapNetwork = new MapNetwork(instructions, network, "AAA", "ZZZ");
		System.out.println("part1: " + mapNetwork.navigateNetwork());
		
		// Part 2
		MapNetwork mapNetwork2 = new MapNetwork(instructions, network, "\\w{2}A", "\\w{2}Z");
		System.out.println("part2: " + mapNetwork2.navigateNetwork());
	}

}

class MapNetwork {

	private final String instructions;
	private HashMap<String, String[]> network;
	private List<Node> startNodes;

	public MapNetwork(String instructions, HashMap<String, String[]> network, String startRegex, String endRegex) {
		this.instructions = instructions;
		this.network = network;
		this.startNodes = new ArrayList<Node>();
		createNodeNetwork(startRegex, endRegex);
	}

	public long navigateNetwork() {
		long steps = 0;
		List<Node> currentNodes = new ArrayList<Node>(this.startNodes);
		HashMap<Integer, Long> firstInstances = new HashMap<Integer, Long>(this.startNodes.size());
		while (firstInstances.size() < this.startNodes.size()) {
			for (Node n : currentNodes.stream().filter(n -> n.endPoint == true).toList()) {
				firstInstances.put(currentNodes.indexOf(n), steps);
			}
			for (int i = 0; i < currentNodes.size(); i++) {
				Node currentNode = currentNodes.get(i);
				currentNodes.set(i, instructions.charAt((int) (steps % instructions.length())) == 'L' ? currentNode.left
						: currentNode.right);
			}
			steps++;
		}
		
		return lcm(firstInstances.entrySet().stream().map(e -> e.getValue()).toList(), 0);
	}
	
	private long lcm(List<Long> list, int index) {
		if (index == list.size()-1) {
			return list.get(index);
		}
		long a = list.get(index);
		long b = lcm(list, index+1);
		return (a*b / gcd(a,b));
	}
	
	private long gcd(long a, long b) {
		if (b == 0) {
			return a;
		}
		return gcd(b, a % b);
	}

	private void createNodeNetwork(String startRegex, String endRegex) {
		// Create Nodes
		HashMap<String, Node> stringToNode = new HashMap<String, Node>(network.keySet().size());
		for (String nodeName : network.keySet()) {
			Node node = new Node(nodeName, nodeName.matches(endRegex));
			stringToNode.put(nodeName, node);
			if (nodeName.matches(startRegex)) {
				startNodes.add(node);
			}
		}

		// Connect Nodes
		for (Node node : stringToNode.values()) {
			node.left = stringToNode.get(network.get(node.name)[0]);
			node.right = stringToNode.get(network.get(node.name)[1]);
		}
	}

	private class Node {
		Node left;
		Node right;
		final String name;
		boolean endPoint;

		public Node(String name, boolean endPoint) {
			this.name = name;
			this.endPoint = endPoint;
		}

		public String toString() {
			return name + ": [" + left.name + ", " + right.name + "] " + endPoint;
		}
	}
 }