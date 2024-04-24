package day07;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day07 {
	
	enum HandType {
		HIGH_CARD,
		ONE_PAIR,
		TWO_PAIR,
		THREE_OF_A_KIND,
		STRAIGHT,
		FLUSH,
		FULL_HOUSE,
		FOUR_OF_A_KIND,
		STRAIGHT_FLUSH,
		FIVE_OF_A_KIND
	};
	
	enum Card {
		JOKER,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		JACK,
		QUEEN,
		KING,
		ACE;
		
		public static Card convertToValue(String sVal) throws NumberFormatException{
			try {
				return Card.values()[Integer.parseInt(sVal)-1];
			} catch (NumberFormatException e) {
				if ("T".equals(sVal)) {
					return Card.TEN;
				} else if ("J".equals(sVal)) {
					return Card.JACK; /* part 1 */
					//return Card.JOKER; /* part 2 */
				} else if ("Q".equals(sVal)) {
					return Card.QUEEN;
				} else if ("K".equals(sVal)) {
					return Card.KING;
				} else if ("A".equals(sVal)) {
					return Card.ACE;
				} else {
					throw new NumberFormatException("Unexpected value: " + sVal);
				}
			}
		}
	}
	
	private static class Hand {
		
		List<Card> cards;
		int bid;
		HandType handType;
		
		public Hand(String handString, int bid) {
			cards = Arrays.asList(handString.split("")).stream().map(sVal -> Card.convertToValue(sVal)).toList();
			this.bid = bid;
			handType = calculateHandType();
		}
		
		public int compareTo(Hand other) {
			// HandType comparison
			int handTypeComparison = handType.compareTo(other.handType);
			if (handTypeComparison != 0) {
				return handTypeComparison;
			}
			
			// tie-breaker comparison
			for (int i=0; i<cards.size(); i++) {
				int comparison = cards.get(i).compareTo(other.cards.get(i));
				if (comparison == 0) {
					continue;
				}
				return comparison;
			}
			return 0;
		}
		
		public String toString() {
			return handType.name() + " " + Arrays.toString(cards.toArray()) + " " + bid;
		}
		
		private HandType calculateHandType() {
			// create a map with the key->value for Card->count
			Map<Card, Long> groups = cards.stream().collect(
					Collectors.groupingBy(
							Function.identity(), 
							Collectors.counting()));
			long jokerCnt = 
					groups.containsKey(Card.JOKER) ? groups.get(Card.JOKER) : 0;
			
			// Logic with and without Joker for Hand HandType
			switch (groups.size()) {
				case 1:
					return HandType.FIVE_OF_A_KIND;
				case 2:
					if (jokerCnt > 0) {
						return HandType.FIVE_OF_A_KIND;
					}
					if (groups.containsValue(4L)) {
						return HandType.FOUR_OF_A_KIND;
					}
					return HandType.FULL_HOUSE;
				case 3:
					if (jokerCnt > 1) {
						return HandType.FOUR_OF_A_KIND;
					}
					if (groups.containsValue(2L)) {
						if (jokerCnt == 1) {
							return HandType.FULL_HOUSE;
						}
						return HandType.TWO_PAIR;
					}
					if (jokerCnt == 1) {
						return HandType.FOUR_OF_A_KIND;
					}
					return HandType.THREE_OF_A_KIND;
				case 4:
					if (jokerCnt > 0) {
						return HandType.THREE_OF_A_KIND;
					}
					return HandType.ONE_PAIR;
				default:
					if (jokerCnt > 0) {
						return HandType.ONE_PAIR;
					}
					return HandType.HIGH_CARD;
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		// Read in file and create a list of Hands
		Scanner reader = new Scanner(new File("src/day07/resources/input.txt"));
		ArrayList<Hand> hands = new ArrayList<Hand>();
		while (reader.hasNextLine()) {
			String[] values = reader.nextLine().split("\\s+");
			hands.add(new Hand(values[0], Integer.parseInt(values[1])));
		}
		reader.close();
		
		// Sort the hands
		List<Hand> sortedHands = hands.stream().sorted((h1, h2) -> h1.compareTo(h2)).toList();
		
		// Calculate the winnings
		long winnings = 0L;
		for (int i=0; i<sortedHands.size(); i++) {
			winnings += (i+1) * sortedHands.get(i).bid;
		}
		System.out.println("winnings: " + winnings);
	}

}
