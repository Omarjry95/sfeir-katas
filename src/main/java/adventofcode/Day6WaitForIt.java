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

        // Part 1
        List<List<Long>> lines = puzzleReader.getLines()
                .stream()
                .map(l -> l.replaceAll("\\D+:\\s+", "")
                        .split("\\s+"))
                .map(a -> Arrays.stream(a)
                        .mapToLong(Long::parseLong)
                        .boxed()
                        .toList())
                .toList();

        Map<Integer, List<List<Long>>> linesBySize = lines.stream()
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
                .reduce(1L, (a, b) -> a * b));

        // Part 2
        List<Long> linesPart2 = puzzleReader.getLines()
                .stream()
                .map(l -> l.replaceAll("\\D+:|\\s+", ""))
                .mapToLong(Long::parseLong)
                .boxed()
                .toList();

        Race race = new Race(linesPart2.get(0), linesPart2.get(1));

        Optional<RecordBreaker> recordBreaker = Optional.ofNullable(race.getRecordBreaker());

        recordBreaker.ifPresent(breaker -> System.out.println(breaker.numberOfWaysToBreakRecord()));
    }

    private static class Race {

        private long recordTime;

        private long distance;

        private Race(long recordTime, long distance) {
            this.recordTime = recordTime;
            this.distance = distance;
        }

        public RecordBreaker getRecordBreaker() {
            long holdTime = recordTime;
            long travelTime = 0;

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

            private long recordTime;

            private long distance;

            public Builder recordTime(long recordTime) {
                this.recordTime = recordTime;

                return this;
            }

            public Builder distance(long distance) {
                this.distance = distance;

                return this;
            }

            public Race build() {
                return new Race(recordTime, distance);
            }
        }
    }

    public static class RecordBreaker {

        private long holdFor;

        private long travelFor;

        private RecordBreaker(long holdFor, long travelFor) {
            this.holdFor = holdFor;
            this.travelFor = travelFor;
        }

        public long numberOfWaysToBreakRecord() {
            return holdFor - travelFor + 1;
        }

        public static Builder builder() {
            return new Builder();
        }

        private static class Builder {

            private long holdFor;

            private long travelFor;

            public Builder holdFor(long holdFor) {
                this.holdFor = holdFor;

                return this;
            }

            public Builder travelFor(long travelFor) {
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