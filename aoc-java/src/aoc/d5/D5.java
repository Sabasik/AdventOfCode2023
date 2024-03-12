package aoc.d5;

import aoc.ReadFile;

import java.util.ArrayList;
import java.util.List;

public class D5 {

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d5/input.txt");

        String[] seedStrings = rows.get(0).split(": ")[1].split(" ");
        List<Mapping> mappings = parseInput(rows);
        long lowest = Long.MAX_VALUE;

        for (int i = 0; i + 1 < seedStrings.length; i += 2) {
            System.out.println(i + "/" + seedStrings.length);
            long seedValue = Long.parseLong(seedStrings[i]);
            for (long j = 0; j < Long.parseLong(seedStrings[i + 1]); j++) {
                long value = findValueFromSeed(seedValue + j, mappings);
                if (value < lowest) lowest = value;
                //System.out.println("seed: " + seedValue + " -> " + value);
            }
        }
        System.out.println("lowest: " + lowest);
    }

    private static long findValueFromSeed(long seed, List<Mapping> mappings) {
        long value = seed;
        String current = "seed";
        String end = "location";

        while (!current.equals(end)) {
            for (Mapping mapping : mappings) {
                if (mapping.source.equals(current)) {
                    current = mapping.destination;

                    for (Range range : mapping.ranges) {
                        long min = range.source;
                        long max = min + range.range;
                        if (min <= value && value < max) {
                            long diff = value - min;
                            value = range.destination + diff;
                            //System.out.print(value + " ");
                            break;
                        }
                    }

                }
            }
        }
        return value;
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d5/input.txt");

        List<Long> seeds = new ArrayList<>();
        for (String seedString : rows.get(0).split(": ")[1].split(" ")) {
            seeds.add(Long.parseLong(seedString));
        }

        List<Mapping> mappings = parseInput(rows);

        long lowest = Long.MAX_VALUE;

        for (long seed : seeds) {
            long value = findValueFromSeed(seed, mappings);
            if (value < lowest) lowest = value;
            System.out.println("seed: " + seed + " -> " + value);
        }
        System.out.println("lowest: " + lowest);
    }

    private static List<Mapping> parseInput(List<String> rows) {
        List<Mapping> maps = new ArrayList<>();
        for (int i = 2; i < rows.size(); i++) {
            String[] namesString = rows.get(i).substring(0, rows.get(i).indexOf(" ")).split("-to-");
            Mapping mapping = new Mapping(namesString[0], namesString[1], new ArrayList<>());
            while (i + 1 < rows.size() && !rows.get(i + 1).isEmpty()) {
                i++;
                String[] numberStrings = rows.get(i).split(" ");
                mapping.ranges.add(new Range(
                        Long.parseLong(numberStrings[0]),
                        Long.parseLong(numberStrings[1]),
                        Long.parseLong(numberStrings[2])
                        )
                );
            }
            i++;
            maps.add(mapping);
        }

        return maps;
    }

    private record Range(long destination, long source, long range) {}
    private record Mapping(String source, String destination, List<Range> ranges) {}
}
