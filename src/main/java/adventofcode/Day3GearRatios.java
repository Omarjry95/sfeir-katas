package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3GearRatios {

    public static void main(String[] args) {
        PuzzleReader puzzleReader = new PuzzleReader("gear-ratios.txt");

        String[] linesAsArray = puzzleReader.getLinesAsArray();

        List<SchematicNumber> numbers = new ArrayList<>();
        List<Symbol> symbols = new ArrayList<>();

        for (int i = 0; i < linesAsArray.length; i++) {
            String currentStr = linesAsArray[i];

            Matcher matcher = Pattern.compile("(\\d+|[^.])").matcher(currentStr);

            while (matcher.find()) {
                String found = matcher.group();

                if (found.matches("\\d+"))
                    numbers.add(new SchematicNumber(found, i, matcher.start()));
                else
                    symbols.add(new Symbol(found, i, matcher.start()));
            }
        }

        symbols = symbols.stream()
                .peek(s -> s.setAdjacents(numbers))
                .toList();

        // Part 1
        List<Symbol> finalSymbols = symbols;

        System.out.println(numbers.stream()
                .filter(n -> n.isPartNumber(finalSymbols))
                .map(SchematicNumber::getValue)
                .mapToInt(Integer::parseInt)
                .sum());

        // Part 2
        System.out.println(symbols.stream()
                .filter(Symbol::isGear)
                .map(Symbol::getAdjacents)
                .mapToInt(adjacents -> adjacents.stream()
                        .map(SchematicInfo::getValue)
                        .map(Integer::parseInt)
                        .reduce(1, (a, b) -> a * b))
                .sum());
    }

    public static class SchematicInfo {

        protected String value;

        protected int foundAt;

        protected int startsAt;

        public SchematicInfo(String value, int foundAt, int startsAt) {
            this.value = value;
            this.foundAt = foundAt;
            this.startsAt = startsAt;
        }

        public String getValue() {
            return value;
        }

        public int getFoundAt() {
            return foundAt;
        }

        public int getStartsAt() {
            return startsAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SchematicInfo info = (SchematicInfo) o;
            return foundAt == info.foundAt && startsAt == info.startsAt && Objects.equals(value, info.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, foundAt, startsAt);
        }
    }

    public static class SchematicNumber extends SchematicInfo {
        public SchematicNumber(String value, int foundAt, int startsAt) {
            super(value, foundAt, startsAt);
        }

        public boolean isPartNumber(List<Symbol> symbols) {
            return symbols.stream()
                    .anyMatch(s -> s.getAdjacents().contains(this));
        }
    }

    public static class Symbol extends SchematicInfo {

        List<SchematicNumber> adjacents;

        public Symbol(String value, int foundAt, int startsAt) {
            super(value, foundAt, startsAt);
        }

        public boolean isGear() {
            return adjacents.size() == 2;
        }

        public List<SchematicNumber> getAdjacents() {
            return adjacents;
        }

        public void setAdjacents(List<SchematicNumber> numbers) {
            adjacents = numbers.stream()
                    .filter(n -> foundAt >= n.getFoundAt() - 1 && foundAt <= n.getFoundAt() + 1)
                    .filter(n -> startsAt - 1 <= n.getStartsAt() + n.getValue().length() - 1 && n.getStartsAt() <= startsAt + 1)
                    .toList();
        }
    }
}