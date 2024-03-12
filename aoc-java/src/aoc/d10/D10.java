package aoc.d10;

import aoc.ReadFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class D10 {

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d10/input.txt");
        int x = 0, y = 0;
        for (String row : rows) {
            if (row.contains("S")) {
                x = row.indexOf("S");
                break;
            }
            y++;
        }
        Location next = null;
        if (x + 1 < rows.get(0).length()) {
            char right = rows.get(y).charAt(x + 1);
            if (right == 'J' || right == '-' || right == '7') {
                next = new Location(x + 1, y);
            }
        }
        if (next == null && x > 0) {
            char left = rows.get(y).charAt(x - 1);
            if (left == 'L' || left == '-' || left == 'F') {
                next = new Location(x - 1, y);
            }
        }
        if (next == null && y > 0) {
            char up = rows.get(y - 1).charAt(x);
            if (up == '|' || up == '7' || up == 'F') {
                next = new Location(x, y - 1);
            }
        }

        HashSet<Location> loop = traverse2(next, new Location(x, y), rows);
        List<List<Character>> filtered = doNew(rows, loop);

        /*
        for (List<Character> characters : filtered) {
            System.out.println(characters.stream()
                    .map(e->e.toString())
                    .reduce((acc, e) -> acc  + e)
                    .get());
        }*/
        System.out.println(countInside(filtered));
    }

    public static int countInside(List<List<Character>> field) {
        String pipeParts = "|J7FLS";
        int count = 0;
        for (int i = 0; i < field.size(); i++) {
            List<Character> row = field.get(i);
            boolean in = false;
            char last = '.';
            for (int j = 0; j < row.size(); j++) {
                char pipe = row.get(j);
                if (in && pipe == '.') {
                    count++;
                    // System.out.println(i + "; " + j);
                }
                String next = "";
                in = switch (pipe) {
                    case '|', 'L', 'J' -> !in;
                    default -> in;
                };
            }
        }

        return count;
    }

    public static List<List<Character>> doNew(List<String> rows, HashSet<Location> loop) {
        List<List<Character>> newLoop = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<Character> smallNew = new ArrayList<>();
            for (int j = 0; j < rows.get(i).length(); j++) {
                if (inLoop(loop, j, i)) smallNew.add(rows.get(i).charAt(j));
                else smallNew.add('.');
            }
            newLoop.add(smallNew);
        }
        return newLoop;
    }
    
    private static boolean inLoop(HashSet<Location> loop, int x, int y) {
        for (Location location : loop) {
            if (location.x == x && location.y == y) return true;
        }
        return false;
    }

    private static HashSet<Location> traverse2(Location location, Location last, List<String> rows) {
        HashSet<Location> loop = new HashSet<>();
        while (true) {
            char pipe = rows.get(location.y).charAt(location.x);
            boolean checkUp = location.x == last.x && location.y - 1 == last.y;
            boolean checkRight = location.x + 1 == last.x && location.y == last.y;
            boolean checkLeft = location.x - 1 == last.x && location.y == last.y;
            Location help = location;
            loop.add(location);
            switch (pipe) {
                case 'S':
                    return loop;
                case '|': {
                    if (checkUp) location = new Location(location.x, location.y + 1);
                    else location = new Location(location.x, location.y - 1);
                    break;
                }
                case '-':
                    if (checkRight) location = new Location(location.x - 1, location.y);
                    else location = new Location(location.x + 1, location.y);
                    break;
                case 'L':
                    if (checkUp) location = new Location(location.x + 1, location.y);
                    else location = new Location(location.x, location.y - 1);
                    break;
                case 'J':
                    if (checkUp) location = new Location(location.x - 1, location.y);
                    else location = new Location(location.x, location.y - 1);
                    break;
                case '7':
                    if (checkLeft) location = new Location(location.x, location.y + 1);
                    else location = new Location(location.x - 1, location.y);
                    break;
                case 'F':
                    if (checkRight) location = new Location(location.x, location.y + 1);
                    else location = new Location(location.x + 1, location.y);
                    break;
                default:
                    throw new RuntimeException("i am broken");
            }
            last = help;
        }
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d10/input.txt");

        int x = 0, y = 0;
        for (String row : rows) {
            if (row.contains("S")) {
                x = row.indexOf("S");
                break;
            }
            y++;
        }
        Location next = null;
        if (x + 1 < rows.get(0).length()) {
            char right = rows.get(y).charAt(x + 1);
            if (right == 'J' || right == '-' || right == '7') next = new Location(x + 1, y);
        }
        if (next == null && x > 0) {
            char left = rows.get(y).charAt(x - 1);
            if (left == 'L' || left == '-' || left == 'F') next = new Location(x - 1, y);
        }
        if (next == null && y > 0) {
            char up = rows.get(y - 1).charAt(x);
            if (up == '|' || up == '7' || up == 'F') next = new Location(x, y - 1);
        }

        int steps = traverse(next, new Location(x, y), rows);
        System.out.println(steps);
        System.out.println(steps / 2);
    }

    private static int traverse(Location location, Location last, List<String> rows) {
        int steps = 0;
        while (true) {
            steps++;
            char pipe = rows.get(location.y).charAt(location.x);
            // System.out.println(pipe + ": " + location.x + ", " + location.y);
            // if (steps > 10) return 0;
            boolean checkUp = location.x == last.x && location.y - 1 == last.y;
            boolean checkRight = location.x + 1 == last.x && location.y == last.y;
            boolean checkLeft = location.x - 1 == last.x && location.y == last.y;
            // System.out.println(checkLeft + ", " + checkUp + ", " + checkRight);
            Location help = location;
            switch (pipe) {
                case 'S':
                    return steps;
                case '|': {
                    if (checkUp) location = new Location(location.x, location.y + 1);
                    else location = new Location(location.x, location.y - 1);
                    break;
                }
                case '-':
                    if (checkRight) location = new Location(location.x - 1, location.y);
                    else location = new Location(location.x + 1, location.y);
                    break;
                case 'L':
                    if (checkUp) location = new Location(location.x + 1, location.y);
                    else location = new Location(location.x, location.y - 1);
                    break;
                case 'J':
                    if (checkUp) location = new Location(location.x - 1, location.y);
                    else location = new Location(location.x, location.y - 1);
                    break;
                case '7':
                    if (checkLeft) location = new Location(location.x, location.y + 1);
                    else location = new Location(location.x - 1, location.y);
                    break;
                case 'F':
                    if (checkRight) location = new Location(location.x, location.y + 1);
                    else location = new Location(location.x + 1, location.y);
                    break;
                default:
                    throw new RuntimeException("i am broken");
            }
            last = help;
        }
    }

    private record Location(int x, int y) {
    }

}
