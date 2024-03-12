package aoc.d17;


import aoc.ReadFile;

import java.util.*;

public class D17 {

	private static int[][] memory;
	private static List<Pair> directions = List.of(
			new Pair(1, 0),
			new Pair(0, 1),
			new Pair(-1, 0),
			new Pair(0, -1)
	);

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d17/input.txt");
		List<List<Integer>> map = new ArrayList<>();
		for (String row : rows) {
			List<Integer> newNumList = new ArrayList<>();
			for (char c : row.toCharArray()) newNumList.add(Integer.parseInt(String.valueOf(c)));
			map.add(newNumList);
		}
		System.out.println(traverse(map, new Pair(0, 0), new Pair(map.get(0).size() - 1, map.size() - 1), 4, 10));

	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d17/input.txt");

		List<List<Integer>> map = new ArrayList<>();
		for (String row : rows) {
			List<Integer> newNumList = new ArrayList<>();
			for (char c : row.toCharArray()) newNumList.add(Integer.parseInt(String.valueOf(c)));
			map.add(newNumList);
		}
		/*
		memory = new int[map.size()][map.get(0).size()];
		for (int i = 0; i < memory.length; i++) {
			for (int j = 0; j < memory[i].length; j++) {
				if (i == 0 && j == 0) continue;
				memory[i][j] = Integer.MAX_VALUE;
			}
		}*/
		System.out.println(traverse(map, new Pair(0, 0), new Pair(map.get(0).size() - 1, map.size() - 1), 1, 3));
		/*
		for (int[] arr : memory) {
			System.out.println(Arrays.toString(arr));
		}*/
	}

	private record Pair(int x, int y) {
		@Override
		public boolean equals(Object obj) {
			Pair p = (Pair) obj;
			return p.x == x && p.y == y;
		}
	}
	private record WithHeat(int heat, Pair start, Pair xy) {
		@Override
		public boolean equals(Object obj) {
			WithHeat wh = (WithHeat) obj;
			return heat == wh.heat && start.equals(wh.start) && xy.equals(wh.xy);
		}
	}
	private record WithoutHeat(Pair start, Pair xy) {
		@Override
		public boolean equals(Object obj) {
			WithoutHeat wh = (WithoutHeat) obj;
			return start.equals(wh.start) && xy.equals(wh.xy);
		}
	}
	private static int traverse(List<List<Integer>> map, Pair start, Pair end, int least, int most) {
		Queue<WithHeat> queue = new PriorityQueue<>(Comparator.comparing(a -> a.heat));
		HashSet<WithoutHeat> seen = new HashSet<>();
		queue.add(new WithHeat(0, start, new Pair(0, 0)));

		while (!queue.isEmpty()) {
			WithHeat things = queue.poll();
			WithoutHeat forCheck = new WithoutHeat(things.start, things.xy);
			if (things.start.equals(end)) return things.heat;
			if (seen.contains(forCheck)) continue;
			seen.add(forCheck);
			for(Pair pair : directions) {
				if (pair.equals(things.xy)) continue;
				if (pair.equals(new Pair(-things.xy.x, -things.xy.y))) continue;
				int a = things.start.x;
				int b = things.start.y;
				int h = things.heat;
				for (int i = 1; i < most + 1; i++) {
					a += pair.x;
					b += pair.y;
					if (a < 0 || b < 0 || a >= map.get(0).size() || b >= map.size()) break;
					h += map.get(b).get(a);
					if (i >= least) queue.add(new WithHeat(h, new Pair(a, b), new Pair(pair.x, pair.y)));
				}
			}
		}

		/*
		boolean changed;
		do {
			changed = false;

			for (int row = 0; row < map.size(); row++) {
				for (int col = 0; col < map.get(row).size(); col++) {
					int thisPreviously = memory[row][col];
					int toAdd = map.get(row).get(col);
					// check if is better if came from up, left, right, down
					if (row != 0 && memory[row - 1][col] != Integer.MAX_VALUE && memory[row - 1][col] + toAdd < memory[row][col]) memory[row][col] = memory[row - 1][col] + toAdd;
					if (col != 0 && memory[row][col - 1] != Integer.MAX_VALUE && memory[row][col - 1] + toAdd < memory[row][col]) memory[row][col] = memory[row][col - 1] + toAdd;
					if (row + 1 < map.size() && memory[row + 1][col] != Integer.MAX_VALUE && memory[row + 1][col] + toAdd < memory[row][col]) memory[row][col] = memory[row + 1][col] + toAdd;
					if (col + 1 < map.get(row).size() && memory[row][col + 1] != Integer.MAX_VALUE && memory[row][col + 1] + toAdd < memory[row][col]) memory[row][col] = memory[row][col + 1] + toAdd;
					if (memory[row][col] < thisPreviously) changed = true;
				}
			}
		} while (changed);
		*/
		return -1;
	}
}
