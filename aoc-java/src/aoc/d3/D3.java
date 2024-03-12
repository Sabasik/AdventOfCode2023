package aoc.d3;

import aoc.ReadFile;

import java.util.Arrays;
import java.util.List;

public class D3 {

    public static void main(String[] args) {
        part2();
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d3/input.txt");

        long sum = 0;

        for (int i = 0; i < rows.size(); i++) {
            int j = 0;
            String[] numbers = rows.get(i).split("\\D+");
            System.out.println(Arrays.toString(numbers));
            for (String number : numbers) {
                if (number.isEmpty()) continue;
                while (!rows.get(i).substring(j).startsWith(number)) j++;
                sum += valueIfNumber(rows, i, j, j + number.length());
            }
        }

        System.out.println(sum);
    }

    private static int valueIfNumber(List<String> rows, int rowNum, int start, int end) {
        System.out.println("got: " + rowNum + ", " + start + ", " + end);
        int value = Integer.parseInt(rows.get(rowNum).substring(start, end));
        for (int i = Math.max(0, rowNum - 1); i < Math.min(rowNum + 2, rows.size()); i++) {
            if (i == rowNum){
                if (start - 1 > 0) {
                    String letter = String.valueOf(rows.get(i).charAt(start - 1));
                    if (!letter.equals(".")) return value;
                }
                if (end < rows.get(i).length()) {
                    String letter = String.valueOf(rows.get(i).charAt(end));
                    if (!letter.equals(".")) return value;
                }
            }
            else {
                for (int j = Math.max(0, start - 1); j < Math.min(end + 1, rows.get(i).length()); j++) {
                    String letter = String.valueOf(rows.get(i).charAt(j));
                    System.out.println("i, j: " + i + ", " + j + " -> " + letter);
                    if (letter.equals(".")) continue;
                    System.out.println(value);
                    return value;
                }
            }
        }
        return 0;
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d3/input.txt");

        long sum = 0;

        for (int i = 0; i < rows.size(); i++) {
            String row = rows.get(i);
            for (int j = 0; j < row.length(); j++) {
                if (row.charAt(j) != '*') continue;
                System.out.println("i, j: " + i + ", " + j);
                int numberCount = 0;
                int multiplication = 1;
                // check if numbers are adjacent
                // previous line
                if (i - 1 >= 0) {
                    String prevRow = rows.get(i - 1);
                    int start = Math.max(0, j - 1);
                    int end = Math.min(j + 2, prevRow.length());
                    String above = prevRow.substring(start, end);
                    if (String.valueOf(above.charAt(0)).matches("\\d")) {
                        int dot = prevRow.substring(0, start).lastIndexOf(".");
                        above = prevRow.substring(dot + 1, start) + above;
                    }
                    if (String.valueOf(above.charAt(above.length() - 1)).matches("\\d")) {
                        int dot;
                        if (end < prevRow.length()) {
                            dot = prevRow.substring(end).indexOf(".");
                            if (dot == -1) dot = prevRow.length();
                            else dot += end;
                        }
                        else dot = prevRow.length();
                        System.out.println(end + ", " + dot);
                        if (dot > end) above += prevRow.substring(end, dot);
                    }
                    System.out.println(above);
                    String[] numbersArray = above.split("\\D+");
                    for (String number : numbersArray) {
                        if (number.isEmpty()) continue;
                        numberCount++;
                        multiplication *= Integer.parseInt(number);
                        System.out.println("above: " + number);
                    }
                }
                // same line
                if (j - 1 >= 0) {
                    if (String.valueOf(row.charAt(j - 1)).matches("\\d")){
                        numberCount++;
                        String before = row.substring(0, j);
                        String numberString = before.substring(before.lastIndexOf(".") + 1);
                        multiplication *= Integer.parseInt(numberString);
                        System.out.println("before: " + numberString);
                    }
                }
                if (j + 1 < row.length()) {
                    if (String.valueOf(row.charAt(j + 1)).matches("\\d")){
                        numberCount++;
                        String after = row.substring(j + 1);
                        String numberString = after.substring(0, after.indexOf("."));
                        multiplication *= Integer.parseInt(numberString);
                        System.out.println("after: " + numberString);
                    }
                }
                // next line
                if (i + 1 < rows.size()) {
                    String nextRow = rows.get(i + 1);
                    int start = Math.max(0, j - 1);
                    int end = Math.min(j + 2, nextRow.length());
                    String below = nextRow.substring(start, end);
                    if (String.valueOf(below.charAt(0)).matches("\\d")) {
                        int dot = nextRow.substring(0, start).lastIndexOf(".");
                        below = nextRow.substring(dot + 1, start) + below;
                    }
                    if (String.valueOf(below.charAt(below.length() - 1)).matches("\\d")) {
                        int dot;
                        if (end < nextRow.length()) {
                            dot = nextRow.substring(end).indexOf(".");
                            if (dot == -1) dot = nextRow.length();
                            else dot += end;
                        }
                        else dot = nextRow.length();
                        System.out.println(end + ", " + dot);
                        if (dot > end) below += nextRow.substring(end, dot);
                    }
                    System.out.println(below);
                    String[] numbersArray = below.split("\\D+");
                    for (String number : numbersArray) {
                        if (number.isEmpty()) continue;
                        numberCount++;
                        multiplication *= Integer.parseInt(number);
                        System.out.println("below: " + number);
                    }
                }
                // if nums == 2 add to sum
                if (numberCount == 2) sum += multiplication;
            }
        }

        System.out.println(sum);
    }
}
