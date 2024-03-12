package aoc.d13;


import aoc.ReadFile;

import java.util.*;

public class D13 {

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d13/input.txt");

        List<Valley> valleys = parseInput(rows);

        int sum = 0;
        for (Valley valley : valleys) sum += fixSmudge(valley);

        System.out.println(sum);
    }

    private static int fixSmudge(Valley valley) {
        List<String> pattern = valley.pattern;

        for (int i = 0; i < pattern.size(); i++) {
            for (int j = 0; j < pattern.get(i).length(); j++) {
                String row = pattern.get(i);
                char value = row.charAt(j);
                String withDot = row.substring(0, j) + "." + row.substring(j + 1);
                String withHashtag = row.substring(0, j) + "#" + row.substring(j + 1);

                Pair pair = value == '.' ?
                        checkIsMirror(pattern, i, row, withHashtag, withDot)
                        : checkIsMirror(pattern, i, row, withDot, withHashtag);
                // System.out.println(pattern);

                // if (pair.isMirror) System.out.println(pair);
                if (pair.isMirror) return pair.value;
            }
        }
        return 0;
    }

    private record Pair(boolean isMirror, int value) {
    }

    private static Pair checkIsMirror(List<String> pattern, int i, String row, String changed, String original) {
        List<String> originalPattern = new ArrayList<>();
        originalPattern.addAll(pattern);

        pattern.set(i, changed);
        for (int k = 0; k < row.length(); k++) {
            if (isVerticalMirror(pattern, k) && !isVerticalMirror(originalPattern, k)) {
                pattern.set(i, original);
                return new Pair(true, k + 1);
            }
        }
        for (int k = 0; k < pattern.size(); k++) {
            if (isHorizontalMirror(pattern, k) && !isHorizontalMirror(originalPattern, k)) {
                pattern.set(i, original);
                return new Pair(true, (k + 1) * 100);
            }
        }
        pattern.set(i, original);
        return new Pair(false, 0);
    }

    private static boolean isVerticalMirror(List<String> pattern, int i) {
        boolean inLoop = false;
        for (String row : pattern) {
            for (int j = 0; i - j >= 0 && i + 1 + j < row.length(); j++) {
                inLoop = true;
                if (row.charAt(i - j) != row.charAt(i + 1 + j)) return false;
            }
        }
        return inLoop;
    }

    private static boolean isHorizontalMirror(List<String> pattern, int i) {
        boolean inLoop = false;
        for (int j = 0; i - j >= 0 && i + 1 + j < pattern.size(); j++) {
            inLoop = true;
            if (!pattern.get(i - j).equals(pattern.get(i + 1 + j))) return false;
        }
        return inLoop;
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d13/input.txt");

        List<Valley> valleys = parseInput(rows);

        int sum = 0;
        valley:
        for (Valley valley : valleys) {
            List<String> pattern = valley.pattern;

            for (int i = 0; i < pattern.get(0).length(); i++) {
                if (isVerticalMirror(pattern, i)) {
                    sum += i + 1;
                    continue valley;
                }
            }

            for (int i = 0; i < pattern.size(); i++) {
                if (isHorizontalMirror(pattern, i)) {
                    sum += (i + 1) * 100;
                    continue valley;
                }
            }
        }
        System.out.println(sum);
    }

    private static List<Valley> parseInput(List<String> rows) {
        List<Valley> valleys = new ArrayList<>();
        List<String> newValley = new ArrayList<>();
        for (String row : rows) {
            if (row.isEmpty()) {
                valleys.add(new Valley(newValley));
                newValley = new ArrayList<>();
            } else {
                newValley.add(row);
            }
        }
        if (!newValley.isEmpty()) valleys.add(new Valley(newValley));
        return valleys;
    }

    private record Valley(List<String> pattern) {
    }
}
