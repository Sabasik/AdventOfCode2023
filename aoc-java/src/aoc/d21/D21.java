package aoc.d21;


import aoc.ReadFile;

import java.util.*;
import java.util.stream.Collectors;

public class D21 {

	private static final List<Location> nextTo = List.of(
			new Location(-1, 0),
			new Location(1, 0),
			new Location(0, 1),
			new Location(0, -1)
	);

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d21/input.txt");

		List<List<Character>> garden = parseInput(rows);

		int X = garden.size();
		int n = 26501365 % X;
		int maxFCount = 26501365 / X;
		/*
		long a0 = getSteps(n, garden, false);
		long a1 = getSteps(n + X, garden, false);
		long a2 = getSteps(n + 2 * X, garden, false);
		*/
		//System.out.println(getSteps(26501365, garden, false));
		System.out.println(f(maxFCount));
	}

	private static long f(int n) {
		long a0 = 3734;
		long a1 = 33285;
		long a2 = 92268;
		long b1 = a1 - a0;
		long b2 = a2 - a1;
		return a0 + b1 * n + ((long) n * (n - 1) / 2) * (b2 - b1);
	}

	private static List<List<Character>> parseInput(List<String> rows) {
		List<List<Character>> garden = new ArrayList<>();
		for (String row : rows) {
			garden.add(row.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
		}
		return garden;
	}

	private static List<Location> takeStepInf(List<List<Character>> garden, List<Location> places) {
		List<Location> newPlaces = new ArrayList<>();

		for (Location from : places) {
			for (Location to : nextTo) {
				int row = from.row + to.row;
				int col = from.col + to.col;
				Location reaches = new Location(row, col);

				if (newPlaces.contains(reaches)) continue;

				int posRow = row;
				while (posRow < 0) posRow += garden.size();
				int rowInside = posRow % garden.size();

				int posCol = col;
				while (posCol < 0) posCol += garden.get(rowInside).size();
				int colInside = posCol % garden.get(rowInside).size();

				if (garden.get(rowInside).get(colInside) == '#') continue;

				newPlaces.add(reaches);
			}
		}

		return newPlaces;
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d21/input.txt");

		List<List<Character>> garden = parseInput(rows);
		System.out.println(getSteps(64, garden, true));
	}

	private static long getSteps(int steps, List<List<Character>> garden, boolean part1) {
		List<Location> weAre = new ArrayList<>();
		// get start pos
		for (int row = 0; row < garden.size(); row++) {
			for (int col = 0; col < garden.get(row).size(); col++) {
				if (garden.get(row).get(col) == 'S') {
					weAre.add(new Location(row, col));
					break;
				}
			}
		}

		for (int i = 0; i < steps; i++) {
			if (part1) weAre = takeStep(garden, weAre);
			else weAre = takeStepInf(garden, weAre);
		}
		return weAre.size();
	}

	private record Location(int row, int col) {
		@Override
		public boolean equals(Object obj) {
			Location l = (Location) obj;
			return l.col == col && l.row == row();
		}
	}

	private static List<Location> takeStep(List<List<Character>> garden, List<Location> places) {
		List<Location> newPlaces = new ArrayList<>();

		for (Location from : places) {
			for (Location to : nextTo) {
				int row = from.row + to.row;
				int col = from.col + to.col;
				if (row < 0 || col < 0 || row >= garden.size() || col >= garden.get(row).size()) continue;
				Location reaches = new Location(row, col);

				if (newPlaces.contains(reaches)) continue;
				if (garden.get(row).get(col) == '#') continue;
				newPlaces.add(reaches);
			}
		}

		return newPlaces;
	}
}
