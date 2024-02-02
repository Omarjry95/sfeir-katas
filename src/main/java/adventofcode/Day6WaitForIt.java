package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day6WaitForIt {

    public static void main(String[] args) {

        PuzzleReader puzzleReader = new PuzzleReader("wait-for-it.txt");

        List<List<Integer>> lines = puzzleReader.getLines()
                .stream()
                .map(l -> l.replaceAll("\\D+:\\s+", "")
                        .split("\\s+"))
                .map(a -> Arrays.stream(a)
                        .mapToInt(Integer::parseInt)
                        .boxed()
                        .toList())
                .toList();

        Map<Integer, List<List<Integer>>> linesBySize = lines.stream()
                .collect(Collectors.groupingBy(List::size));

        if (linesBySize.size() != 1)
            throw new CorruptDataException();

        List<Race> races = IntStream.range(0, linesBySize.keySet()
                        .stream()
                        .findFirst()
                        .orElse(0))
                .mapToObj(i -> Race.builder()
                        .recordTime(lines.get(0).get(i))
                        .distance(lines.get(1).get(i))
                        .build())
                .toList();

        System.out.println(races.stream()
                .map(Race::getRecordBreaker)
                .filter(r -> Optional.ofNullable(r).isPresent())
                .map(RecordBreaker::numberOfWaysToBreakRecord)
                .reduce(1, (a, b) -> a * b));
    }

    private static class Race {

        private int recordTime;

        private int distance;

        private Race(int recordTime, int distance) {
            this.recordTime = recordTime;
            this.distance = distance;
        }

        public RecordBreaker getRecordBreaker() {
            int holdTime = recordTime;
            int travelTime = 0;

            while (holdTime > travelTime) {
                if (holdTime * travelTime > distance)
                    break;

                holdTime--;
                travelTime++;
            }

            return holdTime < travelTime ? null : RecordBreaker.builder()
                    .holdFor(holdTime)
                    .travelFor(travelTime)
                    .build();
        }

        public static Builder builder() {
            return new Builder();
        }

        private static class Builder {

            private int recordTime;

            private int distance;

            public Builder recordTime(int recordTime) {
                this.recordTime = recordTime;

                return this;
            }

            public Builder distance(int distance) {
                this.distance = distance;

                return this;
            }

            public Race build() {
                return new Race(recordTime, distance);
            }
        }
    }

    public static class RecordBreaker {

        private int holdFor;

        private int travelFor;

        private RecordBreaker(int holdFor, int travelFor) {
            this.holdFor = holdFor;
            this.travelFor = travelFor;
        }

        public int numberOfWaysToBreakRecord() {
            return holdFor - travelFor + 1;
        }

        public static Builder builder() {
            return new Builder();
        }

        private static class Builder {

            private int holdFor;

            private int travelFor;

            public Builder holdFor(int holdFor) {
                this.holdFor = holdFor;

                return this;
            }

            public Builder travelFor(int travelFor) {
                this.travelFor = travelFor;

                return this;
            }

            public RecordBreaker build() {
                return new RecordBreaker(holdFor, travelFor);
            }
        }
    }

    private static class CorruptDataException extends RuntimeException {

        public CorruptDataException() {
            super("Puzzle input is corrupt !");
        }
    }
}