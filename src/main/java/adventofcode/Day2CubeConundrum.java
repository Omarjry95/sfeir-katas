package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.*;

public class Day2CubeConundrum {

    public static void main(String[] args) {
        PuzzleReader puzzleReader = new PuzzleReader("cube-conundrum.txt");

        // Part 1
        // OOP Approach
        System.out.println(puzzleReader.getLines()
                .stream()
                .map(CubeGame::new)
                .filter(CubeGame::isPossible)
                .mapToInt(CubeGame::getId)
                .sum()
        );

        // Part 2
        // OOP Approach
        System.out.println(puzzleReader.getLines()
                .stream()
                .map(CubeGame::new)
                .mapToInt(CubeGame::getPower)
                .sum()
        );
    }

    private static class CubeGame {

        private int id;

        private List<String> subsets;

        private static Map<String, Integer> limits = new HashMap<>();

        static {
            limits.put("red", 12);
            limits.put("green", 13);
            limits.put("blue", 14);
        }

        public CubeGame(String input) {

            String[] titleAndSubsets = input.split(": ");

            id = Integer.parseInt(titleAndSubsets[0].replace("Game ", ""));

            subsets = Arrays.stream(titleAndSubsets[1]
                            .replace(";", ",")
                            .split(", "))
                    .toList();
        }

        public boolean isPossible() {
            return subsets.stream()
                    .noneMatch(s -> {
                        String[] numberAndColor = s.split(" ");

                        return limits.get(numberAndColor[1]) < Integer.parseInt(numberAndColor[0]);
                    });
        }

        public int getPower() {
            return limits.keySet()
                    .stream()
                    .map(k -> subsets.stream()
                            .filter(s -> s.split(" ")[1].equals(k))
                            .mapToInt(s -> Integer.parseInt(s.split(" ")[0]))
                            .max()
                            .orElse(0))
                    .reduce(1, (a, b) -> a * b);
        }

        public int getId() {
            return id;
        }
    }
}
