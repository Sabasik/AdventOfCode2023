package aoc.d23;


import aoc.ReadFile;

import java.util.ArrayList;
import java.util.List;

public class D23 {

	private static final List<Pair> nextSteps = List.of(
			new Pair(1, 0),
			new Pair(-1, 0),
			new Pair(0, 1),
			new Pair(0, -1)
	);

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d23/input.txt");

		List<String> map = new ArrayList<>(rows);
		System.out.println(maxSteps(map, 0, new Pair(0, 1)));
	}

	private static int maxSteps(List<String> map, int steps, Pair currentLocation) {
		if (currentLocation.row == map.size() - 1
				&& currentLocation.col == map.get(currentLocation.row).length() - 2) {
			return steps;
		}

		int longest = 0;

		char tile = map.get(currentLocation.row).charAt(currentLocation.col);
		if (tile == '#' || tile == 'O') return 0;

		String thisRow = map.get(currentLocation.row);
		String newThisRow = thisRow.substring(0, currentLocation.col) + "O" + thisRow.substring(currentLocation.col + 1);
		map.set(currentLocation.row, newThisRow);

		for (Pair step : nextSteps) {
			int newRow = currentLocation.row + step.row;
			int newCol = currentLocation.col + step.col;
			Pair next = new Pair(newRow, newCol);
			if (newRow < 0 || newCol < 0 || newRow >= map.size() || newCol >= map.get(newRow).length()) continue;
			if (map.get(newRow).charAt(newCol) == '#') continue;
			int stepCount = maxSteps(map, steps + 1, next);
			if (stepCount > longest) longest = stepCount;
		}
		map.set(currentLocation.row, thisRow);

		return longest;
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d23/input.txt");

		System.out.println(maxSteps(rows, new ArrayList<>(List.of(new Pair(0, 1)))));

	}

	private static int maxSteps(List<String> map, List<Pair> trail) {
		Pair currentLocation = trail.get(trail.size() - 1);
		if (currentLocation.row == map.size() - 1
				&& currentLocation.col == map.get(currentLocation.row).length() - 2) {
			return trail.size() - 1;
		}

		int longest = 0;

		char tile = map.get(currentLocation.row).charAt(currentLocation.col);
		if (tile == '#') return 0;

		if (tile == '.') {
			for (Pair step : nextSteps) {
				int newRow = currentLocation.row + step.row;
				int newCol = currentLocation.col + step.col;
				Pair next = new Pair(newRow, newCol);
				if (trail.contains(next)) continue;
				if (canGo(map, newRow, newCol)) {
					int stepCount = maxSteps(map, addNew(trail, next));
					if (stepCount > longest) longest = stepCount;
				}
			}
		} else {
			int newRow, newCol;
			switch (tile) {
				case '>': {
					newRow = currentLocation.row;
					newCol = currentLocation.col + 1;
					break;
				}
				case '<': {
					newRow = currentLocation.row;
					newCol = currentLocation.col - 1;
					break;
				}
				case '^': {
					newRow = currentLocation.row - 1;
					newCol = currentLocation.col;
					break;
				}
				case 'v': {
					newRow = currentLocation.row + 1;
					newCol = currentLocation.col;
					break;
				}
				default:
					throw new IllegalArgumentException("katki");
			}
			Pair next = new Pair(newRow, newCol);
			if (!trail.contains(next) && canGo(map, newRow, newCol)) {
				longest = maxSteps(map, addNew(trail, next));
			}
		}

		return longest;
	}

	private static List<Pair> addNew(List<Pair> trail, Pair newPair) {
		List<Pair> newList = new ArrayList<>(trail);
		newList.add(newPair);
		return newList;
	}

	private static boolean canGo(List<String> map, int row, int col) {
		if (row < 0 || col < 0 || row >= map.size() || col >= map.get(row).length()) return false;
		return map.get(row).charAt(col) != '#';
	}

	private record Pair(int row, int col) {
		@Override
		public boolean equals(Object obj) {
			Pair pair = (Pair) obj;
			return row == pair.row && col == pair.col;
		}
	}
}
