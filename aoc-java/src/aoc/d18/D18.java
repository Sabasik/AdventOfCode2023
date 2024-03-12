package aoc.d18;


import aoc.ReadFile;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class D18 {

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d18/input.txt");

		BigInteger row = new BigInteger("0");
		BigInteger col = new BigInteger("0");
		List<TerrainOrders> terrainOrders = new ArrayList<>();

		for (String s : rows) {
			Instructions orders = translateOrder(getOrders(s));

			terrainOrders.add(new TerrainOrders(
					new Pair(row, col),
					orders
			));
			switch (orders.direction) {
				case 'U': {
					row = row.subtract(BigInteger.valueOf(orders.count));
					break;
				}
				case 'D': {
					row = row.add(BigInteger.valueOf(orders.count));
					break;
				}
				case 'L': {
					col = col.subtract(BigInteger.valueOf(orders.count));
					break;
				}
				case 'R': {
					col = col.add(BigInteger.valueOf(orders.count));
					break;
				}
			}
		}

		System.out.println(countInside(terrainOrders));
	}

	private static BigInteger countInside(List<TerrainOrders> terrainOrders) {
		// arvuta sisemus
		BigInteger left = new BigInteger("0");
		BigInteger right = new BigInteger("0");
		BigInteger boundary = new BigInteger("1");
		BigInteger one = new BigInteger("1");

		Pair previous = terrainOrders.get(0).loc;
		for (int t = 0; t < terrainOrders.size(); t++) {
			TerrainOrders terrainOrder = terrainOrders.get(t);
			for (int i = (t == 0 ? 1 : 0); i < terrainOrder.order.count; i++) {
				boundary = boundary.add(one);
				BigInteger toAdd = new BigInteger(String.valueOf(i));

				BigInteger col = terrainOrder.loc.col;
				BigInteger row = terrainOrder.loc.row;

				switch (terrainOrder.order.direction) {
					case 'U': {
						row = row.subtract(toAdd);
						break;
					}
					case 'D': {
						row = row.add(toAdd);
						break;
					}
					case 'L': {
						col = col.subtract(toAdd);
						break;
					}
					case 'R': {
						col = col.add(toAdd);
						break;
					}
				}

				BigInteger xy = previous.col.multiply(row);
				BigInteger yx = previous.row.multiply(col);
				left = left.add(xy);
				right = right.add(yx);
				previous = new Pair(row, col);
			}
		}
		BigInteger xy = previous.col.multiply(terrainOrders.get(0).loc.row);
		BigInteger yx = previous.row.multiply(terrainOrders.get(0).loc.col);
		left = left.add(xy);
		right = right.add(yx);

		BigInteger result = left.subtract(right).abs().divide(new BigInteger("2"));
		// System.out.println(boundary);
		return result.add(boundary.divide(one.add(one))).add(one);
	}

	private record Pair(BigInteger row, BigInteger col) {
		@Override
		public boolean equals(Object obj) {
			Pair p = (Pair) obj;
			return Objects.equals(p.row, row) && Objects.equals(p.col, col);
		}
	}

	private record TerrainOrders(Pair loc, Instructions order) {}

	private static Instructions translateOrder(Instructions order) {
		String color = order.color.substring(1, order.color.length() - 1);
		char direction = switch (color.charAt(color.length() - 1)){
			case '0' -> 'R';
			case '1' -> 'D';
			case '2' -> 'L';
			case '3' -> 'U';
			default -> throw new IllegalArgumentException("katki");
		};
		String toConvert = color.substring(1, color.length() - 1);

		return new Instructions(direction, Long.parseLong(toConvert, 16), "");
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d18/input.txt");
		List<List<Character>> terrain = new ArrayList<>();
		terrain.add(new ArrayList<>());
		terrain.get(0).add('#');

		int x = 0, y = 0;
		for (String row : rows) {
			Instructions orders = getOrders(row);
			switch (orders.direction) {
				case 'U': {
					for (int i = 0; i < orders.count; i++) {
						if (y == 0) addUp(terrain);
						else y--;
						terrain.get(y).set(x, '#');
					}
					break;
				}
				case 'D': {
					for (int i = 0; i < orders.count; i++) {
						if (y == terrain.size() - 1) addDown(terrain);
						y++;
						terrain.get(y).set(x, '#');
					}
					break;
				}
				case 'L': {
					for (int i = 0; i < orders.count; i++) {
						if (x == 0) addLeft(terrain);
						else x--;
						terrain.get(y).set(x, '#');
					}
					break;
				}
				case 'R': {
					for (int i = 0; i < orders.count; i++) {
						if (x == terrain.get(0).size() - 1) addRight(terrain);
						x++;
						terrain.get(y).set(x, '#');
					}
					break;
				}
			}
		}
		fillOut(terrain);
		printTerrain(terrain);
		System.out.println(countTerrain(terrain));
	}

	private static void fillOut(List<List<Character>> terrain) {
		for (int i = 0; i < terrain.size(); i++) {
			for (int j = 0; j < terrain.get(i).size(); j++) {
				if (terrain.get(i).get(j) == '#') continue;
				if (!canReachOutside(terrain, i, j)) terrain.get(i).set(j, '*');
			}
		}
	}

	private record XY(int row, int col) {
		@Override
		public boolean equals(Object obj) {
			XY p = (XY) obj;
			return p.row == row && p.col == col;
		}
	}

	private static boolean canReachOutside(List<List<Character>> terrain, int row, int col) {
		// if is next to * is inside
		List<XY> nextTo = List.of(
				new XY(0, 1),
				new XY(0, -1),
				new XY(1, 0),
				new XY(-1, 0)
		);

		for (XY next : nextTo) {
			int y = next.row + row;
			int x = next.col + col;
			if (x <= 0 || y <= 0 || y >= terrain.size() || x >= terrain.get(x).size()) continue;
			if (terrain.get(y).get(x) == '*') return false;
		}

		HashSet<XY> seen = new HashSet<>();
		Queue<XY> queue = new LinkedList<>();
		queue.add(new XY(row, col));
		int rowMax = terrain.size() - 1;
		int colMax = terrain.get(0).size() - 1;

		while (!queue.isEmpty()) {
			XY xy = queue.poll();
			if (seen.contains(xy)) continue;
			seen.add(xy);
			if ((xy.row == 0 || xy.col == 0 || xy.row == rowMax || xy.col == colMax)
					&& terrain.get(xy.row).get(xy.col) != '#') return true;
			if (terrain.get(xy.row).get(xy.col) == '#') continue;
			queue.add(new XY(xy.row - 1, xy.col));
			queue.add(new XY(xy.row + 1, xy.col));
			queue.add(new XY(xy.row, xy.col - 1));
			queue.add(new XY(xy.row, xy.col + 1));
		}

		return false;
	}


	private static long countTerrain(List<List<Character>> terrain) {
		long c = 0;
		for (List<Character> characters : terrain) for (char dig : characters) if (dig == '#' || dig == '*') c++;
		return c;
	}

	private static void printTerrain(List<List<Character>> terrain) {
		for (List<Character> characters : terrain) {
			System.out.println(characters.stream().map(String::valueOf)
					.collect(Collectors.joining()));
		}
	}

	private static void addLeft(List<List<Character>> terrain) {
		for (List<Character> characters : terrain) characters.add(0, '.');
	}

	private static void addRight(List<List<Character>> terrain) {
		for (List<Character> characters : terrain) characters.add('.');
	}

	private static void addDown(List<List<Character>> terrain) {
		List<Character> newRow = new ArrayList<>();
		for (int i = 0; i < terrain.get(0).size(); i++) newRow.add('.');
		terrain.add(newRow);
	}

	private static void addUp(List<List<Character>> terrain) {
		List<Character> newRow = new ArrayList<>();
		for (int i = 0; i < terrain.get(0).size(); i++) newRow.add('.');
		terrain.add(0, newRow);
	}

	private static Instructions getOrders(String s) {
		String[] parts = s.split(" ");
		return new Instructions(parts[0].charAt(0), Integer.parseInt(parts[1]), parts[2]);
	}

	private record Instructions(char direction, long count, String color) {
	}
}
