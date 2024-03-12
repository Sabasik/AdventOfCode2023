package aoc.d24;


import aoc.ReadFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class D24 {

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d24/input.txt");

		List<Hailstone> hailstones = new ArrayList<>();
		for (String row : rows) hailstones.add(parseRow(row));

		Hailstone result = findPosition(hailstones);
		System.out.println(result);
		System.out.println(result.position.x + result.position.y + result.position.z);
	}

	private static Hailstone findPosition(List<Hailstone> hailstones) {
		Hailstone h1 = hailstones.get(0);
		Hailstone h2 = hailstones.get(1);

		int range = 500;
		for (int vx = -range; vx <= range; vx++) {
			for (int vy = -range; vy <= range; vy++) {
				for (int vz = -range; vz <= range; vz++) {
					if (vx == 0 || vy == 0 || vz == 0) continue;
					/*
					Find starting point for rock that will intercept
					first two hailstones (x,y) on this trajectory
 					simultaneous linear equation (from part 1):
                    H1:  x = A + a*t   y = B + b*t
                    H2:  x = C + c*u   y = D + d*u

                    t = [ d ( C - A ) - c ( D - B ) ] / ( a * d - b * c )

                    Solve for origin of rock intercepting both hailstones in x,y:
                    x = A + a*t - vx*t   y = B + b*t - vy*t
                    x = C + c*u - vx*u   y = D + d*u - vy*u
					*/

					long A = (long) h1.position.x, a = (long) h1.velocity.x - vx;
					long B = (long) h1.position.y, b = (long) h1.velocity.y - vy;
					long C = (long) h2.position.x, c = (long) h2.velocity.x - vx;
					long D = (long) h2.position.y, d = (long) h2.velocity.y - vy;

					// skip if division by 0
					if (c == 0 || (a * d) - (b * c) == 0) continue;
					// Rock intercepts H1 at time t
					long t = (d * (C - A) - c * (D - B)) / ((a * d) - (b * c));

					// Calculate starting position of rock from intercept point
					long x = (long) h1.position.x + (long) h1.velocity.x * t - vx * t;
					long y = (long) h1.position.y + (long) h1.velocity.y * t - vy * t;
					long z = (long) h1.position.z + (long) h1.velocity.z * t - vz * t;

					// check if this rock throw will hit all hailstones
					boolean hitall = true;
					for (int i = 0; i < hailstones.size(); i++) {
						Hailstone h = hailstones.get(i);
						long u;
						if ((long) h.velocity.x != vx) {
							u = (x - (long) h.position.x) / ((long) h.velocity.x - vx);
						} else if ((long) h.velocity.y != vy) {
							u = (y - (long) h.position.y) / ((long) h.velocity.y - vy);
						} else if ((long) h.velocity.z != vz) {
							u = (z - (long) h.position.z) / ((long) h.velocity.z - vz);
						} else throw new RuntimeException("olen katki");

						if ((x + u * vx != (long) h.position.x + u * (long) h.velocity.x)
								|| (y + u * vy != (long) h.position.y + u * (long) h.velocity.y)
								|| ( z + u * vz != (long) h.position.z + u * (long) h.velocity.z)) {
							hitall = false;
							break;
						}
					}
					if (hitall) {
						System.out.println(x);
						System.out.println(y);
						System.out.println(z);
						System.out.println(x + y + z);
						return new Hailstone(
								new Three(x, y, z),
								new Three(vx, vy, vz)
						);
					}
				}
			}
		}

		return new Hailstone(
				new Three(0, 0, 0),
				new Three(0, 0, 0)
		);
	}

	private static Hailstone moveXNanoseconds(Hailstone hailstone, int seconds) {
		Three currentPos = hailstone.position;
		Three velocity = hailstone.velocity;
		Three newPos = new Three(
				currentPos.x + velocity.x * seconds,
				currentPos.y + velocity.y * seconds,
				currentPos.z + velocity.y * seconds
		);
		return new Hailstone(newPos, velocity);
	}


	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d24/input.txt");

		List<Hailstone> hailstones = new ArrayList<>();
		for (String row : rows) hailstones.add(parseRow(row));

		double posMin = 200000000000000.0;
		double posMax = 400000000000000.0;
		 /*
		double posMin = 7.0;
		double posMax = 27.0;
		*/
		long count = 0;
		for (int i = 0; i < hailstones.size(); i++) {
			for (int j = i + 1; j < hailstones.size(); j++) {
				if (willIntersect(hailstones.get(i), hailstones.get(j), posMin, posMax)){
					count++;
				}
			}
		}
		System.out.println(count);
	}

	private static boolean willIntersect(Hailstone h1, Hailstone h2, double posMin, double posMax) {
		double a1 = h1.velocity.y / h1.velocity.x;
		double a2 = h2.velocity.y / h2.velocity.x;
		double b1 = h1.position.y - (h1.position.x * h1.velocity.y) / h1.velocity.x;
		double b2 = h2.position.y - (h2.position.x * h2.velocity.y) / h2.velocity.x;

		double x = (b2 - b1) / (a1 - a2);
		double y = a1 * x + b1;

		if (x < posMin || y < posMin || x > posMax || y > posMax) return false;
		if (wasInPast(h1, x, y)) return false;
		if (wasInPast(h2, x, y)) return false;
		/*
		System.out.println(h1);
		System.out.println(h2);
		System.out.println(x + ", " + y);
		 */
		return true;
	}

	private static boolean wasInPast(Hailstone h, double x, double y) {
		if (x < h.position.x && h.velocity.x > 0) return true;
		if (x > h.position.x && h.velocity.x < 0) return true;
		if (y < h.position.y && h.velocity.y > 0) return true;
		return y > h.position.y && h.velocity.y < 0;
	}

	private static Hailstone parseRow(String row) {
		String[] posAndVel = row.split(" @ ");
		String[] pos = posAndVel[0].split(", ");
		String[] vel = posAndVel[1].split(", ");
		return new Hailstone(
				new Three(Double.parseDouble(pos[0].trim()), Double.parseDouble(pos[1].trim()), Double.parseDouble(pos[2].trim())),
				new Three(Double.parseDouble(vel[0].trim()), Double.parseDouble(vel[1].trim()), Double.parseDouble(vel[2].trim()))
		);
	}

	private record Three(double x, double y, double z) {}

	private record Hailstone(Three position, Three velocity) {}
}
