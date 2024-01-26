package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day4Scratchcards {

    public static void main(String[] args) {

        PuzzleReader puzzleReader = new PuzzleReader("scratchcards.txt");

        List<String> lines = puzzleReader.getLines();

        // Part 1
        System.out.println(lines
                .stream()
                .map(ScratchCard::new)
                .mapToInt(ScratchCard::points)
                .sum());

        // Part 2
        Set<ScratchCard> scratchCards = lines
                .stream()
                .map(ScratchCard::new)
                .collect(Collectors.toCollection(TreeSet::new));

        System.out.println(lines.stream()
                .map(ScratchCard::new)
                .mapToInt(s -> {
                    Queue<ScratchCard> scratchCardsCopies = scratchCards.stream()
                            .filter(c -> c.getId() == s.getId() || c.isWon(s.getId(), s.getWinners()))
                            .collect(Collectors.toCollection(PriorityQueue::new));

                    Optional<ScratchCard> current = Optional.ofNullable(scratchCardsCopies.poll());

                    scratchCards.removeAll(scratchCardsCopies);

                    int copies = current.orElseThrow(() -> new NullPointerException("Card Not Found !")).getCopies();

                    scratchCards.addAll(scratchCardsCopies.stream()
                            .peek(c -> c.addCopies(copies))
                            .collect(Collectors.toSet()));

                    return copies;
                })
                .sum());
    }

    private static class ScratchCard implements Comparable {

        private int id;

        private List<Integer> winning;

        private List<Integer> possessed;

        private int winners;

        private int copies = 1;

        public ScratchCard(String input) {
            String[] cardAndNumbers = input.split(":\\s+");

            String[] winningAndPossessed = cardAndNumbers[1].split("\\s+\\|\\s+");

            id = Integer.parseInt(cardAndNumbers[0].replaceAll("Card\\s+", ""));
            winning = getListOfNumbers(winningAndPossessed, 0);
            possessed = getListOfNumbers(winningAndPossessed, 1);
            winners = possessed.stream()
                    .mapToInt(p -> winning.contains(p) ? 1 : 0)
                    .sum();
        }

        private List<Integer> getListOfNumbers(String[] winningAndPossessed, int side) {
            return Arrays.stream(winningAndPossessed[side].split("\\s+"))
                    .filter(Predicate.not(String::isBlank))
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .toList();
        }

        public int points() {
            return winners > 0 ? (int) Math.pow(2, winners - 1) : 0;
        }

        public boolean isWon(int id, int winners) {
            return this.id > id && this.id <= id + winners;
        }

        public void addCopies(int n) {
            this.copies += n;
        };

        public int getId() {
            return id;
        }

        public int getWinners() {
            return winners;
        }

        public int getCopies() {
            return copies;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScratchCard that = (ScratchCard) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public int compareTo(Object o) {
            ScratchCard s = (ScratchCard) o;

            return Integer.compare(id, s.getId());
        }
    }
}