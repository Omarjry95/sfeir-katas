package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.*;
import java.util.stream.Collectors;

public class Day1Trebuchet {

    public static void main(String[] args) {

        PuzzleReader puzzleReader = new PuzzleReader("trebuchet.txt");

        // Part 1
        // Procedural Approach
        System.out.println(puzzleReader.getLines()
                .stream()
                .map(l -> l.chars()
                        .mapToObj(c -> (char) c)
                        .filter(Character::isDigit)
                        .map(String::valueOf)
                        .collect(Collectors.joining()))
                .map(s -> String.valueOf(s.charAt(0)) + s.charAt(s.length() - 1))
                .mapToInt(Integer::parseInt)
                .sum());

        // OOP Approach
        System.out.println(puzzleReader.getLines()
                .stream()
                .map(CalibrationLine::new)
                .mapToInt(cl -> cl.getCalibrationValue(false))
                .sum());

        // Part 2
        // OOP Approach
        System.out.println(puzzleReader.getLines()
                .stream()
                .map(CalibrationLine::new)
                .mapToInt(cl -> cl.getCalibrationValue(true))
                .sum());
    }

    private static class CalibrationLine {

        private String value;

        private final static Map<String, String> validDigits = new HashMap<>();

        static {
            validDigits.put("one", "1");
            validDigits.put("two", "2");
            validDigits.put("three", "3");
            validDigits.put("four", "4");
            validDigits.put("five", "5");
            validDigits.put("six", "6");
            validDigits.put("seven", "7");
            validDigits.put("eight", "8");
            validDigits.put("nine", "9");
        }

        public CalibrationLine(String value) {
            this.value = value;
        }

        public int getCalibrationValue(boolean joinSpelled) {
            Map<Integer, String> digitsByIndexes = new TreeMap<>();

            validDigits.forEach((k, v) -> {
                        extract(v, digitsByIndexes);

                        if (joinSpelled)
                            extract(k, digitsByIndexes);
                    });

            List<String> foundDigits = digitsByIndexes.values()
                    .stream()
                    .map(i -> validDigits.getOrDefault(i, i))
                    .toList();

            return Integer.parseInt(foundDigits.get(0) + foundDigits.get(foundDigits.size() - 1));
        }

        private void extract(String digit, Map<Integer, String> digitsByIndexes) {
            if (value.contains(digit)) {
                digitsByIndexes.putIfAbsent(value.indexOf(digit), digit);
                digitsByIndexes.putIfAbsent(value.lastIndexOf(digit), digit);
            }
        }
    }
}
