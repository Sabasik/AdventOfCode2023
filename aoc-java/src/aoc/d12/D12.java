package aoc.d12;


import aoc.ReadFile;

import java.util.*;

public class D12 {

    private static Map<String, Long> memory = new HashMap<>();

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d12/input.txt");

        long sum = 0;
        int rCount = 1;
        for (String row : rows) {
            String[] halves = row.split(" ");
            String parts = halves[0];
            parts = (parts + '?').repeat(5);
            parts = parts.substring(0, parts.length() - 1);
            List<Integer> criteria = new ArrayList<>();
            String[] numStrings = halves[1].split(",");
            for (int i = 0; i < 5; i++) for (String num : numStrings) criteria.add(Integer.parseInt(num));

            int[] minimalGroups = new int[criteria.size() * 2 + 1];
            for (int i = 1; i < minimalGroups.length - 1; i++) {
                minimalGroups[i] = (i % 2 == 0) ? 1 : criteria.get(i / 2);
            }
            int emptySpotsToFill = parts.length() - Arrays.stream(minimalGroups).sum();
            long res = findDP(parts, emptySpotsToFill, 0, minimalGroups);
            memory.clear();

            sum += res;
            System.out.println(rCount++ + "/" + rows.size() + " -> " + res);
        }
        System.out.println(sum);
    }

    private static long findDP(String parts, int emptySpotsToFill, int groupId, int[] groups) {
        if (emptySpotsToFill == 0) return isValidUpTo(groups.length, groups, parts) ? 1 : 0;
        if (groupId > groups.length) return 0;

        long solutions = 0;

        for (int amountFilled = 0; amountFilled <= emptySpotsToFill; amountFilled++) {
            groups[groupId] += amountFilled;

            if (isValidUpTo(groupId + 1, groups, parts)) {
                String key = (groupId + 2) + " " + (emptySpotsToFill - amountFilled);
                if (!memory.containsKey(key)) {
                    memory.put(key, findDP(parts, emptySpotsToFill - amountFilled, groupId + 2, groups));
                }
                solutions += memory.get(key);
            }

            groups[groupId] -= amountFilled;
        }
        return solutions;
    }

    private static boolean isValidUpTo(final int groupPtr, final int[] groups, final String target) {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < groupPtr; x++) {
            result.append(((x % 2 == 0) ? "." : "#").repeat(groups[x]));
        }
        for (int x = 0; x < result.length(); x++) {
            if (target.charAt(x) != '?' && result.charAt(x) != target.charAt(x)) {
                return false;
            }
        }
        return true;
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d12/input.txt");

        int sum = 0, sum2 = 0;
        for (String row : rows) {
            String[] halves = row.split(" ");
            String parts = halves[0];
            List<Integer> criteria = new ArrayList<>();
            for (String num : halves[1].split(",")) {
                criteria.add(Integer.parseInt(num));
            }
            sum += possibleArrangements(parts, criteria, 0, "");
            // sum2 += findDP();
        }
        System.out.println(sum);
        System.out.println(sum2);

    }

    private static int possibleArrangements(String parts, List<Integer> criteria, int i, String newParts) {
        if (i >= parts.length()) return isCorrect(newParts, criteria) ? 1 : 0;
        char part = parts.charAt(i);
        if (part == '?') {
            return possibleArrangements(parts, criteria, i + 1, newParts + '.')
                    + possibleArrangements(parts, criteria, i + 1, newParts + '#');
        }
        return possibleArrangements(parts, criteria, i + 1, newParts + part);
    }

    private static boolean isCorrect(String parts, List<Integer> criteria) {
        int partI = 0;
        int critI;
        // points in the beginning
        while (partI < parts.length() && parts.charAt(partI) == '.') partI++;
        // numbers
        for (critI = 0; critI < criteria.size(); critI++) {
            int count = criteria.get(critI);
            for (int i = 0; i < count; i++, partI++) {
                if (partI >= parts.length()) return false;
                if (parts.charAt(partI) != '#') return false;
            }
            // at least one . if not last
            if (critI + 1 != criteria.size()) {
                if (partI >= parts.length()) return false;
                if (parts.charAt(partI) != '.') return false;
            }
            while (partI < parts.length() && parts.charAt(partI) == '.') partI++;
        }

        return partI == parts.length() && critI == criteria.size();
    }
}
