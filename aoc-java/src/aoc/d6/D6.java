package aoc.d6;

import aoc.ReadFile;

import java.util.Arrays;
import java.util.List;

public class D6 {

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d6/input.txt");

        String timeString = rows.get(0).replaceAll("\\s+", "").split(":")[1];
        String distanceString = rows.get(1).replaceAll("\\s+", "").split(":")[1];
        System.out.println(timeString);
        System.out.println(distanceString);

        long result = findPossibilities(Long.parseLong(timeString), Long.parseLong(distanceString));

        System.out.println(result);
    }

    private static long findPossibilities(long time, long distance) {
        long count = 0;
        for (long i = 0; i < (time + 1) / 2; i++) {
            if (i * (time - i) > distance) count += 2;
        }
        if (time % 2 == 0) {
            long i = time / 2 + 1;
            if (i * (time - i) > distance) count++;
        }
        return count;
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d6/input.txt");

        String[] timeStrings = rows.get(0).split("\\s+");
        String[] distanceStrings = rows.get(1).split("\\s+");
        System.out.println(Arrays.toString(timeStrings));
        System.out.println(Arrays.toString(distanceStrings));

        int result = 1;
        for (int i = 1; i < timeStrings.length; i++) {
            int time = Integer.parseInt(timeStrings[i]);
            int distance = Integer.parseInt(distanceStrings[i]);
            result *= findPossibilities(time, distance);
            System.out.println(time + ", " + distance + " -> " + findPossibilities(time, distance));
        }
        System.out.println(result);
    }

    private static int findPossibilities(int time, int distance) {
        int count = 0;
        for (int i = 0; i < (time + 1) / 2; i++) {
            if (i * (time - i) > distance) count += 2;
        }
        if (time % 2 == 0) {
            int i = time / 2 + 1;
            if (i * (time - i) > distance) count++;
        }
        return count;
    }
}
