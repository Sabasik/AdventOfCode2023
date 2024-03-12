package aoc.d9;

import aoc.ReadFile;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class D9 {
    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d9/input.txt");

        int sum = 0;
        for (int i = 0; i < rows.size(); i++) {
            List<Integer> numbers = new ArrayList<>();
            for (String numString : rows.get(i).split(" ")) {
                numbers.add(Integer.parseInt(numString));
            }
            sum += findPrevious(numbers);
        }

        System.out.println(sum);
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d9/input.txt");

        int sum = 0;
        for (int i = 0; i < rows.size(); i++) {
            List<Integer> numbers = new ArrayList<>();
            for (String numString : rows.get(i).split(" ")) {
                numbers.add(Integer.parseInt(numString));
            }
            sum += findNext(numbers);
        }

        System.out.println(sum);
    }

    private static int findNext(List<Integer> sequence) {
        List<List<Integer>> values = new ArrayList<>();
        values.add(sequence);
        while (!values.get(values.size() - 1).stream().allMatch(i -> i == 0)) {
            List<Integer> betweenValues = new ArrayList<>();
            List<Integer> lastValues = values.get(values.size() - 1);
            for (int i = 1; i < lastValues.size(); i++) {
                betweenValues.add(lastValues.get(i) - lastValues.get(i - 1));
            }
            values.add(betweenValues);
        }
        values.get(values.size() - 1).add(0);
        for (int i = values.size() - 2; i >= 0; i--) {
            List<Integer> whereToAdd = values.get(i);
            List<Integer> fromWhere = values.get(i + 1);
            whereToAdd.add(fromWhere.get(fromWhere.size() - 1) + whereToAdd.get(whereToAdd.size() - 1));
        }

        return values.get(0).get(values.get(0).size() - 1);
    }

    private static int findPrevious(List<Integer> sequence) {
        List<List<Integer>> values = new ArrayList<>();
        values.add(sequence);
        while (!values.get(values.size() - 1).stream().allMatch(i -> i == 0)) {
            List<Integer> betweenValues = new ArrayList<>();
            List<Integer> lastValues = values.get(values.size() - 1);
            for (int i = 1; i < lastValues.size(); i++) {
                betweenValues.add(lastValues.get(i) - lastValues.get(i - 1));
            }
            values.add(betweenValues);
        }
        values.get(values.size() - 1).add(0);
        for (int i = values.size() - 2; i >= 0; i--) {
            List<Integer> whereToAdd = values.get(i);
            List<Integer> fromWhere = values.get(i + 1);
            whereToAdd.add(0, whereToAdd.get(0) - fromWhere.get(0));
        }

        return values.get(0).get(0);
    }
}
