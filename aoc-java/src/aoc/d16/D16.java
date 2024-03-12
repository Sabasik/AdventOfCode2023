package aoc.d16;


import aoc.ReadFile;

import java.util.*;

public class D16 {
	private record Tile(int row, int col, char dir) {
		@Override
		public boolean equals(Object obj) {
			Tile t = (Tile) obj;
			return row == t.row && col == t.col && dir == t.dir;
		}
	}

	private static HashSet<Tile> seen = new HashSet<>();

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d16/input.txt");

		int maxCount = 0;
		List<Tile> starts = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			starts.add(new Tile(i, 0, 'W'));
			starts.add(new Tile(i, rows.get(0).length() - 1, 'E'));
		}
		for (int i = 0; i < rows.get(0).length(); i++) {
			starts.add(new Tile(0, i, 'N'));
			starts.add(new Tile(rows.size() - 1, 0, 'S'));
		}

		for (Tile start : starts) {
			seen.clear();
			boolean[][] tilesEnergized = new boolean[rows.size()][rows.get(0).length()];
			followBeams(tilesEnergized, rows, start.row, start.col, start.dir);
			int count = 0;
			for (int k = 0; k < tilesEnergized.length; k++) {
				// System.out.println(Arrays.toString(tilesEnergized[i]));
				for (int l = 0; l < tilesEnergized[k].length; l++) {
					if (tilesEnergized[k][l]) count++;
				}
			}
			if (count > maxCount) maxCount = count;
		}
		System.out.println(maxCount);
	}
	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d16/input.txt");
		boolean[][] tilesEnergized = new boolean[rows.size()][rows.get(0).length()];

		followBeams(tilesEnergized, rows, 0, 0, 'W');

		int count = 0;
		for (int i = 0; i < tilesEnergized.length; i++) {
			// System.out.println(Arrays.toString(tilesEnergized[i]));
			for (int j = 0; j < tilesEnergized[i].length; j++) {
				if (tilesEnergized[i][j]) count++;
			}
		}
		System.out.println(count);
	}

	private static void followBeams(boolean[][] tilesEnergized, List<String> rows, int row, int col, Character cameFrom) {
		if (row < 0 || col < 0 || row >= rows.size() || col >= rows.get(0).length()) return;
		Tile newTile = new Tile(row, col, cameFrom);
		if (seen.contains(newTile)) return;
		seen.add(newTile);

		char tile = rows.get(row).charAt(col);
		tilesEnergized[row][col] = true;
		// System.out.println(row + ", " + col + "->" + tile);

		switch (tile) {
			case '.': {
				switch (cameFrom){
					case 'N':
						followBeams(tilesEnergized, rows, row + 1, col, cameFrom);
						break;
					case 'S':
						followBeams(tilesEnergized, rows, row - 1, col, cameFrom);
						break;
					case 'W':
						followBeams(tilesEnergized, rows, row, col + 1, cameFrom);
						break;
					case 'E':
						followBeams(tilesEnergized, rows, row, col - 1, cameFrom);
						break;
				}
				break;
			}
			case '/': {
				switch (cameFrom){
					case 'N':
						followBeams(tilesEnergized, rows, row, col - 1, 'E');
						break;
					case 'S':
						followBeams(tilesEnergized, rows, row, col + 1, 'W');
						break;
					case 'W':
						followBeams(tilesEnergized, rows, row - 1, col, 'S');
						break;
					case 'E':
						followBeams(tilesEnergized, rows, row + 1, col, 'N');
						break;
				}
				break;
			}
			case '\\': {
				switch (cameFrom){
					case 'N':
						followBeams(tilesEnergized, rows, row, col + 1, 'W');
						break;
					case 'S':
						followBeams(tilesEnergized, rows, row, col - 1, 'E');
						break;
					case 'W':
						followBeams(tilesEnergized, rows, row + 1, col, 'N');
						break;
					case 'E':
						followBeams(tilesEnergized, rows, row - 1, col, 'S');
						break;
				}
				break;
			}
			case '-': {
				switch (cameFrom){
					case 'N':
					case 'S':
						followBeams(tilesEnergized, rows, row, col - 1, 'E');
						followBeams(tilesEnergized, rows, row, col + 1, 'W');
						break;
					case 'W':
						followBeams(tilesEnergized, rows, row, col + 1, cameFrom);
						break;
					case 'E':
						followBeams(tilesEnergized, rows, row, col - 1, cameFrom);
						break;
				}
				break;
			}
			case '|': {
				switch (cameFrom){
					case 'N':
						followBeams(tilesEnergized, rows, row + 1, col, cameFrom);
						break;
					case 'S':
						followBeams(tilesEnergized, rows, row - 1, col, cameFrom);
						break;
					case 'W':
					case 'E':
						followBeams(tilesEnergized, rows, row + 1, col, 'N');
						followBeams(tilesEnergized, rows, row - 1, col, 'S');
						break;
				}
				break;
			}
		}

	}
}
