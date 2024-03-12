package aoc.d22;


import aoc.ReadFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D22 {

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d22/input.txt");
		List<Brick> bricks = new ArrayList<>();
		for (String row : rows) bricks.add(parseRow(row));

		finishFalling(bricks);

		int sum = 0;
		for (int i = 0; i < bricks.size(); i++) {
			sum += countChainReaction(bricks, i);
		}
		System.out.println(sum);
	}

	private static int countChainReaction(List<Brick> bricks, int brickIndex) {
		List<Integer> disintegrated = new ArrayList<>(List.of(brickIndex));
		boolean changed = true;

		while (changed) {
			changed = false;
			for (int i = 0; i < bricks.size(); i++) {
				if (disintegrated.contains(i)) continue;
				// check if other bricks will fall
				if (canMoveDown(bricks, i, disintegrated)) {
					changed = true;
					disintegrated.add(i);
				}
			}
		}

		return disintegrated.size() - 1;
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d22/input.txt");

		List<Brick> bricks = new ArrayList<>();
		for (String row : rows) bricks.add(parseRow(row));

		finishFalling(bricks);

		long count = 0;
		for (int i = 0; i < bricks.size(); i++) {
			if (canDisintegrate(bricks, i)) count++;
		}
		System.out.println(count);
	}

	private static boolean canDisintegrate(List<Brick> bricks, int brickIndex) {
		for (int i = 0; i < bricks.size(); i++) {
			if (i == brickIndex) continue;
			// check if other bricks will fall
			if (canMoveDown(bricks, i, List.of(brickIndex))) return false;
		}
		/*
		// check if completely surrounded
		Brick brick = bricks.get(brickIndex);

		// z - 1, z + 1
		for (int x = brick.start.x; x <= brick.end.x; x++) {
			for (int y = brick.start.y; y <= brick.end.y; y++) {
				XYZ down = new XYZ(x, y, brick.start.z - 1);
				XYZ up = new XYZ(x, y, brick.end.z + 1);

				Brick downBrick = new Brick(down, down);
				Brick upBrick = new Brick(up, up);
				boolean collidesUp = false;
				boolean collidesDown = false;
				for (int i = 0; i < bricks.size(); i++) {
					if (i == brickIndex) continue;
					if (!collidesDown && collision(bricks.get(i), downBrick)) collidesDown = true;
					if (!collidesUp && collision(bricks.get(i), upBrick)) collidesUp = true;
					if (collidesDown && collidesUp) break;
				}
				if (!collidesUp || !collidesDown) return false;
			}
		}

		// y - 1, y + 1
		for (int x = brick.start.x; x <= brick.end.x; x++) {
			for (int z = brick.start.z; z <= brick.end.z; z++) {
				XYZ down = new XYZ(x, brick.start.y - 1, z);
				XYZ up = new XYZ(x, brick.end.y + 1, z);

				Brick downBrick = new Brick(down, down);
				Brick upBrick = new Brick(up, up);
				boolean collidesUp = false;
				boolean collidesDown = false;
				for (int i = 0; i < bricks.size(); i++) {
					if (i == brickIndex) continue;
					if (!collidesDown && collision(bricks.get(i), downBrick)) collidesDown = true;
					if (!collidesUp && collision(bricks.get(i), upBrick)) collidesUp = true;
					if (collidesDown && collidesUp) break;
				}
				if (!collidesUp || !collidesDown) return false;
			}
		}

		// x - 1, x + 1
		for (int z = brick.start.z; z <= brick.end.z; z++) {
			for (int y = brick.start.y; y <= brick.end.y; y++) {
				XYZ down = new XYZ(brick.start.x - 1, y, z);
				XYZ up = new XYZ(brick.end.x + 1, y, z);

				Brick downBrick = new Brick(down, down);
				Brick upBrick = new Brick(up, up);
				boolean collidesUp = false;
				boolean collidesDown = false;
				for (int i = 0; i < bricks.size(); i++) {
					if (i == brickIndex) continue;
					if (!collidesDown && collision(bricks.get(i), downBrick)) collidesDown = true;
					if (!collidesUp && collision(bricks.get(i), upBrick)) collidesUp = true;
					if (collidesDown && collidesUp) break;
				}
				if (!collidesUp || !collidesDown) return false;
			}
		}
		*/
		return true;
	}

	private static void finishFalling(List<Brick> bricks) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < bricks.size(); i++) {
				if (canMoveDown(bricks, i, List.of(-1))) {
					bricks.set(i, moveDown(bricks.get(i)));
					changed = true;
				}
			}
		}
	}

	private static Brick moveDown(Brick brick) {
		XYZ start = new XYZ(brick.start.x, brick.start.y, brick.start.z - 1);
		XYZ end = new XYZ(brick.end.x, brick.end.y, brick.end.z - 1);
		return new Brick(start, end);
	}

	private static boolean canMoveDown(List<Brick> bricks, int brickIndex, List<Integer> disintegrated) {
		if (bricks.get(brickIndex).start.z == 1) return false;
		Brick whereTo = moveDown(bricks.get(brickIndex));
		for (int i = 0; i < bricks.size(); i++) {
			if (i == brickIndex) continue;
			if (disintegrated.contains(i)) continue;
			Brick b = bricks.get(i);
			if (collision(b, whereTo)) return false;
		}
		return true;
	}

	private static boolean collision(Brick b1, Brick b2) {
		boolean xCol = false, yCol = false, zCol = false;
		// x
		int minEndX = Math.min(b1.end.x, b2.end.x);
		int maxStartX = Math.max(b1.start.x, b2.start.x);
		if (maxStartX <= minEndX) xCol = true;
		// y
		int minEndY = Math.min(b1.end.y, b2.end.y);
		int maxStartY = Math.max(b1.start.y, b2.start.y);
		if (maxStartY <= minEndY) yCol = true;
		// z
		int minEndZ = Math.min(b1.end.z, b2.end.z);
		int maxStartZ = Math.max(b1.start.z, b2.start.z);
		if (maxStartZ <= minEndZ) zCol = true;

		return xCol && yCol && zCol;
	}

	private static Brick parseRow(String row) {
		String[] startAndEnd = row.split("~");
		String[] startString = startAndEnd[0].split(",");
		String[] endString = startAndEnd[1].split(",");
		return new Brick(
				new XYZ(
						Integer.parseInt(startString[0]),
						Integer.parseInt(startString[1]),
						Integer.parseInt(startString[2])
				),
				new XYZ(
						Integer.parseInt(endString[0]),
						Integer.parseInt(endString[1]),
						Integer.parseInt(endString[2])
				)
		);
	}

	private record XYZ(int x, int y, int z) {
		@Override
		public boolean equals(Object obj) {
			XYZ xyz = (XYZ) obj;
			return x == xyz.x && y == xyz.y && x == xyz.z;
		}
	}

	private record Brick(XYZ start, XYZ end) {
	}
}
