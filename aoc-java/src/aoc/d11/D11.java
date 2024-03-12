package aoc.d11;


import aoc.ReadFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class D11 {

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d11/input.txt");

        ColAndRow colAndRow = getEmpty(rows);
        HashSet<Integer> emptyCols = colAndRow.emptyCols;
        HashSet<Integer> emptyRows = colAndRow.emptyRows;

        // System.out.println(emptyCols);
        // System.out.println(emptyRows);
        List<List<Character>> world = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            world.add(rows.get(i)
                    .chars()
                    .mapToObj(e -> (char)e)
                    .collect(Collectors.toList()));
        }

        long sum = 0;
        List<Pair> galaxies = getGalaxyLocations(world);
        // System.out.println(galaxies);
        for (int i = 0; i < galaxies.size(); i++) {
            Pair p1 = galaxies.get(i);
            for (int j = i + 1; j < galaxies.size(); j++) {
                Pair p2 = galaxies.get(j);
                long dist1 = 0;
                long dist2 = 0;
                for (int k = Math.min(p1.x, p2.x) + 1; k <= Math.max(p1.x, p2.x); k++) {
                    if (emptyRows.contains(k)) dist1 += 1000000;
                    else dist1 += 1;
                }

                for (int k = Math.min(p1.y, p2.y) + 1; k <= Math.max(p1.y, p2.y); k++) {
                    if (emptyCols.contains(k)) dist2 += 1000000;
                    else dist2 += 1;
                }
                sum += dist1 + dist2;
            }
        }
        System.out.println(sum);
    }

    private static ColAndRow getEmpty(List<String> rows) {
        HashSet<Integer> columns = new HashSet<>();
        HashSet<Integer> emptyRows = new HashSet<>();

        for (int i = 0; i < rows.get(0).length(); i++) columns.add(i);

        for (int i = 0; i < rows.size(); i++) {
            String row = rows.get(i);
            boolean galaxyInRow = false;
            for (int j = 0; j < row.length(); j++) {
                if (row.charAt(j) == '#') {
                    galaxyInRow = true;
                    columns.remove(j);
                }
            }

            if (!galaxyInRow) emptyRows.add(i);
        }

        return new ColAndRow(columns, emptyRows);
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d11/input.txt");

        List<List<Character>> world = expand(rows);
        /*
        for (List<Character> characters : world) {
            System.out.println(characters);
        }*/

        int sum = 0;
        List<Pair> galaxies = getGalaxyLocations(world);
        for (int i = 0; i < galaxies.size(); i++) {
            Pair p1 = galaxies.get(i);
            for (int j = i + 1; j < galaxies.size(); j++) {
                Pair p2 = galaxies.get(j);
                sum += Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
            }
        }
        System.out.println(sum);
    }

    private static List<Pair> getGalaxyLocations(List<List<Character>> world) {
        List<Pair> loc = new ArrayList<>();
        for (int i = 0; i < world.size(); i++) {
            for (int j = 0; j < world.get(i).size(); j++) {
                if (world.get(i).get(j) == '#') loc.add(new Pair(i, j));
            }
        }
        return loc;
    }

    private static List<List<Character>> expand(List<String> rows) {
        List<List<Character>> world = new ArrayList<>();
        HashSet<Integer> columns = new HashSet<>();
        for (int i = 0; i < rows.get(0).length(); i++) columns.add(i);

        for (int i = 0; i < rows.size(); i++) {
            String row = rows.get(i);
            boolean galaxyInRow = false;
            List<Character> newRow = new ArrayList<>();
            for (int j = 0; j < row.length(); j++) {
                newRow.add(row.charAt(j));
                if (row.charAt(j) == '#') {
                    galaxyInRow = true;
                    columns.remove(j);
                }
            }
            world.add(newRow);

            if (!galaxyInRow) {
                List<Character> extra = new ArrayList<>();
                for (int j = 0; j < row.length(); j++) extra.add('.');
                world.add(extra);
            }
        }
        // System.out.println(columns);
        // add extra columns
        for (int i = 0; i < world.size(); i++) {
            List<Character> row = world.get(i);
            for (int j = row.size() - 1; j >= 0; j--) {
                if (columns.contains(j)) row.add(j + 1, '.');
            }

        }

        return world;
    }

    private record Pair(int x, int y) {}
    private record ColAndRow(HashSet<Integer> emptyCols, HashSet<Integer> emptyRows) {}
}
