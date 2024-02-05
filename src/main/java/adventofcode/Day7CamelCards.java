package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day7CamelCards {

    private static final String cardsOrdered = "AKQJT98765432";

    public static void main(String[] args) {

        PuzzleReader puzzleReader = new PuzzleReader("camel-cards.txt");

        long[] bidsOrdered = puzzleReader.getLines()
                .stream()
                .map(l -> {
                    String[] cardsAndBid = l.split(" ");

                    return new Hand(cardsAndBid[0], Long.parseLong(cardsAndBid[1]));
                })
                .sorted()
                .mapToLong(Hand::getBid)
                .toArray();

        long total = 0;

        for (int i = 0; i < bidsOrdered.length; i++) {
            total += bidsOrdered[i] * (i + 1);
        }

        System.out.println(total);
    }

    private static class Hand implements Comparable {

        private String cards;

        private long bid;

        public Hand(String cards, long bid) {
            this.cards = cards;
            this.bid = bid;
        }

        public String cardsCombinations() {
            return cards.chars()
//                    .mapToObj(c -> (char) c)
                    .boxed()
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
                    .values()
                    .stream()
                    .sorted(Comparator.reverseOrder())
                    .map(String::valueOf)
                    .collect(Collectors.joining());
        }

        private int compareCards(String cards) {
            int diff = 0;
            int index = 0;

            while (diff == 0 && index < 5) {
                char card1 = this.cards.toCharArray()[index];
                char card2 = cards.toCharArray()[index];

                if (card1 != card2) {
                    diff = cardsOrdered.indexOf(card2) - cardsOrdered.indexOf(card1);
                    diff /= Math.abs(diff);
                }

                index++;
            }

            return diff;
        }

        public String getCards() {
            return cards;
        }

        public long getBid() {
            return bid;
        }

        @Override
        public int compareTo(Object o) {
            Hand hand = (Hand) o;

            int compareCombinations = cardsCombinations()
                    .compareTo(hand.cardsCombinations());

            return compareCombinations == 0 ?
                    compareCards(hand.getCards()) :
                    compareCombinations;
        }
    }
}
