package aoc.d8;

import aoc.ReadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class D8 {
    public static void main(String[] args) {
        part2();
    }

    public static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d8/input.txt");

        Network network = getNetwork(rows);
        List<String> location = network.location;
        List<String> left = network.left;
        List<String> right = network.right;

        char[] directions = rows.get(0).toCharArray();
        List<String> currents = new ArrayList<>();
        for (int i = 0; i < location.size(); i++) {
            if (location.get(i).endsWith("A")) currents.add(location.get(i));
        }
        int[] forZ = new int[currents.size()];
        int found = 0;
        int i;
        for (i = 0; ; i++) {
            int index = i % directions.length;

            for (int j = 0; j < currents.size(); j++) {
                int whichRow = location.indexOf(currents.get(j));

                switch (directions[index]) {
                    case 'L' -> currents.set(j, left.get(whichRow));
                    case 'R' -> currents.set(j, right.get(whichRow));
                }
                if (currents.get(j).endsWith("Z")) {
                    forZ[j] = i + 1;
                    found++;
                }
            }

            if (found == currents.size()) break;
        }

        System.out.println(lcmOfArray(forZ));
    }

    public static int gcd(int a, int b) {
        if (a < b) return gcd(b, a);
        if (a % b == 0) return b;
        else return gcd(b, a % b);
    }

    public static long lcmOfArray(int[] elementArray)
    {
        long lcmOfArrayElements = 1;
        int divisor = 2;

        while (true) {
            int counter = 0;
            boolean divisible = false;

            for (int i = 0; i < elementArray.length; i++) {
                if (elementArray[i] == 0) return 0;
                else if (elementArray[i] < 0) elementArray[i] = elementArray[i] * (-1);
                if (elementArray[i] == 1) counter++;

                if (elementArray[i] % divisor == 0) {
                    divisible = true;
                    elementArray[i] = elementArray[i] / divisor;
                }
            }

            if (divisible) lcmOfArrayElements = lcmOfArrayElements * divisor;
            else divisor++;

            if (counter == elementArray.length) return lcmOfArrayElements;
        }
    }

    public static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d8/input.txt");

        Network network = getNetwork(rows);
        List<String> location = network.location;
        List<String> left = network.left;
        List<String> right = network.right;

        char[] directions = rows.get(0).toCharArray();

        String current = "AAA";
        int i;
        for (i = 0; ; i++) {
            int index = i % directions.length;
            int whichRow = location.indexOf(current);

            switch (directions[index]) {
                case 'L' -> current = left.get(whichRow);
                case 'R' -> current = right.get(whichRow);
            }

            if (current.equals("ZZZ")) break;
        }

        System.out.println(i + 1);
    }

    private static Network getNetwork(List<String> rows) {
        List<String> location = new ArrayList<>();
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();

        for (int i = 2; i < rows.size(); i++) {
            String[] locAndRest = rows.get(i).split(" = ");
            location.add(locAndRest[0]);

            String rest = locAndRest[1].replace("(", "").replace(")", "");
            String[] whereTo = rest.split(", ");
            left.add(whereTo[0]);
            right.add(whereTo[1]);
        }

        return new Network(location, left, right);
    }

    private record Network(List<String> location, List<String> left, List<String> right) {
    }
}
