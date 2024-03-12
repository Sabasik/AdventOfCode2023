package aoc.d2;

import aoc.ReadFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class D2 {

    private static final List<String> RGB = List.of("red", "green", "blue");

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d2/input2.txt");

        int idPowersSum = 0;

        for (String row : rows) {
            Game game = parseRow(row);

            int[] minimums = new int[3];

            for (int[] round : game.cubes) {
                for (int i = 0; i < 3; i++) {
                    if (round[i] > minimums[i]) {
                        minimums[i] = round[i];
                    }
                }
            }
            System.out.println(Arrays.toString(minimums));

            int power = minimums[0] * minimums[1] * minimums[2];

            idPowersSum += power;
        }

        System.out.println(idPowersSum);
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d2/input2.txt");

        final int[] limits = {12, 13, 14};

        int idSum = 0;

        rows:
        for (String row : rows) {
            Game game = parseRow(row);

            for (int[] round : game.cubes) {
                for (int i = 0; i < 3; i++) {
                    if (round[i] > limits[i]) {
                        continue rows;
                    }
                }
            }

            idSum += game.id;
        }

        System.out.println(idSum);
    }

    private static Game parseRow(String row) {
        String[] idAndRest = row.split(": ");
        int id = Integer.parseInt(idAndRest[0].split(" ")[1]);

        String[] rounds = idAndRest[1].split("; ");

        List<int[]> cubes = new ArrayList<>();
        for (String round : rounds) {
            String[] colorAmounts = round.split(", ");

            int[] parsedColorAmounts = new int[3];
            for (String colorAmount : colorAmounts) {
                int num = Integer.parseInt(colorAmount.split(" ")[0]);
                String color = colorAmount.split(" ")[1];
                parsedColorAmounts[RGB.indexOf(color)] = num;
            }

            cubes.add(parsedColorAmounts);
        }

        return new Game(id, cubes);
    }

    private record Game(int id, List<int[]> cubes) {
    }
}
