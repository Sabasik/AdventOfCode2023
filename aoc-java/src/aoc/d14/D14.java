package aoc.d14;


import aoc.ReadFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class D14 {

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d14/input.txt");

		List<List<List<Character>>> fieldHistory = new ArrayList<>();

		List<List<Character>> field = new ArrayList<>();
		for (String row : rows) {
			field.add(row.chars()
					.mapToObj(e -> (char) e)
					.collect(Collectors.toList()));
		}

		int i, cycleBeginning = -1;
		for (i = 0; i < 1000000000; i++) {
			spinCycle(field);
			// System.out.println(fieldHistory.size());

			List<List<Character>> fieldCopy = new ArrayList<>();
			for (List<Character> characters : field) fieldCopy.add(new ArrayList<>(characters));
			cycleBeginning = fieldHistory.indexOf(fieldCopy);
			if (cycleBeginning != -1) break;
			fieldHistory.add(fieldCopy);
		}
		if (cycleBeginning != -1) {
			i++; // break siis i ei suurenenud kuigi sai tehtud
			int leftToDO = 1000000000 - i;
			int cycleLength = fieldHistory.size() - cycleBeginning;
			int finalFieldIndex = cycleBeginning + (leftToDO % cycleLength);
			System.out.println(countLoad(fieldHistory.get(finalFieldIndex)));
		} else System.out.println(countLoad(field));

	}

	private static void spinCycle(List<List<Character>> field) {
		tiltNorth(field);
		tiltWest(field);
		tiltSouth(field);
		tiltEast(field);
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d14/input.txt");

		List<List<Character>> field = new ArrayList<>();
		for (String row : rows) {
			field.add(row.chars()
					.mapToObj(e -> (char) e)
					.collect(Collectors.toList()));
		}

		tiltNorth(field);
		System.out.println(countLoad(field));
	}

	private static void tiltNorth(List<List<Character>> field) {
		boolean changed = false;
		for (int row = 1; row < field.size(); row++) {
			for (int column = 0; column < field.get(row).size(); column++) {
				if (field.get(row).get(column) == 'O' && field.get(row - 1).get(column) == '.') {
					field.get(row - 1).set(column, 'O');
					field.get(row).set(column, '.');
					changed = true;
				}
			}
		}
		if (changed) tiltNorth(field);
	}

	private static void tiltWest(List<List<Character>> field) {
		boolean changed = false;
		for (int column = 1; column < field.get(0).size(); column++) {
			for (int row = 0; row < field.size(); row++) {
				if (field.get(row).get(column) == 'O' && field.get(row).get(column - 1) == '.') {
					field.get(row).set(column - 1, 'O');
					field.get(row).set(column, '.');
					changed = true;
				}
			}
		}

		if (changed) tiltWest(field);
	}

	private static void tiltSouth(List<List<Character>> field) {
		boolean changed = false;
		for (int row = field.size() - 2; row >= 0; row--) {
			for (int column = 0; column < field.get(row).size(); column++) {
				if (field.get(row).get(column) == 'O' && field.get(row + 1).get(column) == '.') {
					field.get(row + 1).set(column, 'O');
					field.get(row).set(column, '.');
					changed = true;
				}
			}
		}
		if (changed) tiltSouth(field);
	}

	private static void tiltEast(List<List<Character>> field) {
		boolean changed = false;
		for (int column = field.get(0).size() - 2; column >= 0; column--) {
			for (int row = 0; row < field.size(); row++) {
				if (field.get(row).get(column) == 'O' && field.get(row).get(column + 1) == '.') {
					field.get(row).set(column + 1, 'O');
					field.get(row).set(column, '.');
					changed = true;
				}
			}
		}
		if (changed) tiltEast(field);
	}

	private static int countLoad(List<List<Character>> field) {
		int sum = 0;
		int rockLoad = 1;
		for (int row = field.size() - 1; row >= 0; row--, rockLoad++) {
			for (Character c : field.get(row)) if (c == 'O') sum += rockLoad;
		}
		return sum;
	}

}
