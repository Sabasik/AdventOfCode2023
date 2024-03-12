package aoc.d15;


import aoc.ReadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class D15 {

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d15/input.txt");
		String[] sequence = rows.get(0).split(",");
		HashMap<Integer, List<Lens>> boxes = new HashMap<>();
		for (int i = 0; i < 256; i++) boxes.put(i, new ArrayList<>());

		for (String s : sequence) {
			if (s.contains("=")) {
				String[] labelAndValue = s.split("=");
				Lens lens = new Lens(labelAndValue[0], Integer.parseInt(labelAndValue[1]));
				int box = hash(lens.label);
				int isAlready = boxes.get(box).indexOf(lens);
				if (isAlready != -1) boxes.get(box).set(isAlready, lens);
				else boxes.get(box).add(lens);

			} else {
				String label = s.substring(0, s.length() - 1);
				int box = hash(label);
				boxes.get(box).remove(new Lens(label, 0));
			}
		}

		long sum = 0;
		for (Map.Entry<Integer, List<Lens>> entry : boxes.entrySet()) {
			int boxNr = entry.getKey();
			for (int i = 0; i < entry.getValue().size(); i++) {
				int focusPower = boxNr + 1;
				int slotNr = i + 1;
				sum += (long) focusPower * slotNr * entry.getValue().get(i).value;
			}
		}
		System.out.println(sum);
	}

	private record Lens(String label, int value) {
		@Override
		public boolean equals(Object obj) {
			Lens lens = (Lens) obj;
			return lens.label.equals(label);
		}
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d15/input.txt");
		String[] sequence = rows.get(0).split(",");
		// System.out.println(hash("HASH"));
		int sum = 0;
		for (String s : sequence) {
			sum += hash(s);
		}
		System.out.println(sum);
	}

	private static int hash(String s) {
		int value = 0;
		for (char c : s.toCharArray()) {
			value += c;
			value *= 17;
			value = value % 256;
		}
		return value;
	}
}
