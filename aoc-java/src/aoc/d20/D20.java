package aoc.d20;


import aoc.ReadFile;

import java.util.*;
import java.util.stream.Collectors;

public class D20 {

	private static List<Module> modules;

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d20/input.txt");

		modules = new ArrayList<>();
		doModules(rows);

		List<String> accept = List.of("fv", "kk", "vt", "xr");
		// 3863, 3931, 3797, 3769
		List<Long> counts = List.of(3863, 3931, 3797, 3769).stream().map(Long::valueOf).collect(Collectors.toList());
		String name = accept.get(0);
		for (int i = 1; ; i++) {
			if (pushButtonRX(name)) {
				System.out.println(i);
				break;
			}
		}
		System.out.println(lcm(counts));
	}

	static long lcm(List<Long> numbers) {
		return numbers.stream().reduce(1L, (x, y) -> (x * y) / gcd(x, y));
	}

	static long gcd(long a, long b) {
		if (b == 0) return a;
		return gcd(b, a % b);
	}

	private static boolean pushButtonRX(String nameGoal) {
		Queue<Pulse> queue = new LinkedList<>();
		queue.add(new Pulse(false, getModuleByName("broadcaster"), null));

		while (!queue.isEmpty()) {
			Pulse pulse = queue.poll();
			if (pulse.receiver == null) continue;

			String name;
			if (pulse.sender instanceof Conjunction) {
				name = ((Conjunction) pulse.sender).name;
			} else if (pulse.sender instanceof FlipFlop) {
				name = ((FlipFlop) pulse.sender).name;
			} else name = "broadcaster";
			// sq -> rx; sq <- fv, kk, vt, xr
			if (name.equals(nameGoal) && pulse.high) return true;

			queue.addAll(pulse.receiver.nextPulses(pulse));
		}
		return false;
	}

	private static void doModules(List<String> rows) {
		for (String row : rows) modules.add(parseRow(row));

		// add input modules for conjunction modules
		for (Module sender : modules) {
			if (sender instanceof Conjunction) {
				for (String destination : ((Conjunction) sender).destinations) {
					Module dest = getModuleByName(destination);
					if (!(dest instanceof Conjunction)) continue;
					((Conjunction) dest).addSender(sender);
				}
			}
			;
			if (sender instanceof FlipFlop) {
				for (String destination : ((FlipFlop) sender).destinations) {
					Module dest = getModuleByName(destination);
					if (!(dest instanceof Conjunction)) continue;
					((Conjunction) dest).addSender(sender);
				}
			}
			;
		}
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d20/input.txt");

		modules = new ArrayList<>();
		doModules(rows);

		//List<String> moduleHistory = new ArrayList<>();
		List<Pair> lowHighHistory = new ArrayList<>();

		int i, cycleBeginning = -1;
		for (i = 0; i < 1000; i++) {
			Pair pair = pushButton();

			// check if has been in the same state before
			//String moduleCopy = getModuleStatesAsString();
			//cycleBeginning = moduleHistory.indexOf(moduleCopy);
			//if (cycleBeginning != -1) break;
			lowHighHistory.add(pair);
			//System.out.println(pair);
			//moduleHistory.add(moduleCopy);
			// System.out.println("-------");
		}

		long low = 0, high = 0;
		/*
		if (cycleBeginning != -1) {
			i++;
			int leftToDO = 1000 - i;
			int cycleLength = moduleHistory.size() - cycleBeginning;
			int finalFieldIndex = cycleBeginning + (leftToDO % cycleLength);
			int count = (1000 - cycleBeginning) / cycleLength;

			// cycle
			long cycleHigh = 0, cycleLow = 0;
			for (int j = cycleBeginning; j < lowHighHistory.size(); j++) {
				Pair pair = lowHighHistory.get(j);
				cycleLow += pair.low;
				cycleHigh += pair.high;
			}

			// end (half) cycle
			long cycleAHigh = 0, cycleALow = 0;
			if ((1000 - cycleBeginning) % count != 0) {
				for (int j = cycleBeginning; j < finalFieldIndex; j++) {
					Pair pair = lowHighHistory.get(j);
					cycleALow += pair.low;
					cycleAHigh += pair.high;
				}
			}

			// before cycle
			long cycleBHigh = 0, cycleBLow = 0;
			for (int j = 0; j < cycleBeginning; j++) {
				Pair pair = lowHighHistory.get(j);
				cycleBLow += pair.low;
				cycleBHigh += pair.high;
			}

			low = cycleBLow + cycleLow * count + cycleALow;
			high = cycleBHigh + cycleHigh * count + cycleAHigh;

		} else {

		}*/
		for (Pair pair : lowHighHistory) {
			low += pair.low;
			high += pair.high;
		}
		System.out.println(low * high);
	}

	private static Pair pushButton() {
		Queue<Pulse> queue = new LinkedList<>();
		queue.add(new Pulse(false, getModuleByName("broadcaster"), null));

		long low = 0, high = 0;
		while (!queue.isEmpty()) {
			//System.out.println(queue);
			Pulse pulse = queue.poll();
			//System.out.println(pulse);

			if (pulse.high) high++;
			else low++;

			if (pulse.receiver == null) continue;
			queue.addAll(pulse.receiver.nextPulses(pulse));
		}
		return new Pair(low, high);
	}

	private static String getModuleStatesAsString() {
		StringBuilder s = new StringBuilder();
		for (Module module : modules) s.append(module.toString()).append(";");
		return s.toString();
	}

	private record Pair(long low, long high) {
	}

	private static Module parseRow(String row) {
		String[] nameAndReceivers = row.split(" -> ");
		String nameString = nameAndReceivers[0];
		List<String> receivers = new ArrayList<>(Arrays.asList(nameAndReceivers[1].split(", ")));
		return switch (nameString.charAt(0)) {
			case '%' -> new FlipFlop(nameString.substring(1), receivers);
			case '&' -> new Conjunction(nameString.substring(1), receivers);
			default -> new Module(receivers);
		};
	}

	private static Module getModuleByName(String name) {
		for (Module module : modules) {
			if (module instanceof FlipFlop) {
				if (((FlipFlop) module).name.equals(name)) return module;
			}
			if (module instanceof Conjunction) {
				if (((Conjunction) module).name.equals(name)) return module;
			}
			if (module.name.equals(name)) return module;
		}
		return null;
	}

	private record Pulse(boolean high, Module receiver, Module sender) {
	}

	private static class Module {
		String name;
		List<String> destinations;

		public Module() {
			this.name = "";
			this.destinations = new ArrayList<>();
		}

		// broadcaster
		public Module(List<String> destinations) {
			this.name = "broadcaster";
			this.destinations = destinations;
		}

		public List<Pulse> nextPulses(Pulse pulse) {
			List<Pulse> pulses = new LinkedList<>();
			for (String destination : destinations) {
				pulses.add(new Pulse(
						pulse.high, getModuleByName(destination), this
				));
			}
			return pulses;
		}

		@Override
		public String toString() {
			return name + "->" + String.join(",", destinations);
		}
	}

	private static class FlipFlop extends Module {
		String name;
		boolean onOff;
		List<String> destinations;

		public FlipFlop(String name, List<String> destinations) {
			this.name = name;
			this.onOff = false;
			this.destinations = destinations;
		}

		public List<Pulse> nextPulses(Pulse pulse) {
			List<Pulse> pulses = new LinkedList<>();
			if (pulse.high) return pulses;
			this.onOff = !this.onOff;
			for (String destination : destinations) {
				pulses.add(new Pulse(
						this.onOff,
						getModuleByName(destination),
						this
				));
			}
			return pulses;
		}

		@Override
		public String toString() {
			return name + "->" + String.join(",", destinations); // + "->" + onOff;
		}
	}

	private static class Conjunction extends Module {
		String name;
		HashMap<String, Boolean> memory;
		List<String> destinations;

		public Conjunction(String name, List<String> destinations) {
			this.name = name;
			this.memory = new HashMap<>();
			this.destinations = destinations;
		}

		public void addSender(Module module) {
			String name;
			if (module instanceof Conjunction) {
				name = ((Conjunction) module).name;
			} else if (module instanceof FlipFlop) {
				name = ((FlipFlop) module).name;
			} else name = "broadcaster";
			if (!memory.containsKey(name)) memory.put(name, false);
		}

		public List<Pulse> nextPulses(Pulse pulse) {
			List<Pulse> pulses = new LinkedList<>();
			String name;
			if (pulse.sender instanceof Conjunction) {
				name = ((Conjunction) pulse.sender).name;
			} else if (pulse.sender instanceof FlipFlop) {
				name = ((FlipFlop) pulse.sender).name;
			} else name = "broadcaster";
			memory.put(name, pulse.high);

			boolean high = false;
			for (Boolean value : memory.values()) {
				if (!value) {
					high = true;
					break;
				}
			}

			//System.out.println(memory);
			//System.out.println("-->" + high);
			for (String destination : destinations) {
				pulses.add(new Pulse(
						high,
						getModuleByName(destination),
						this
				));
			}
			return pulses;
		}

		@Override
		public String toString() {
			/*
			StringBuilder pulses = new StringBuilder();
			for (Map.Entry<String, Boolean> entry : memory.entrySet()) {
				pulses.append(entry.getKey()).append(":").append(entry.getValue());
			}*/
			return name + "->" + String.join(",", destinations); //  + "->" + pulses;
		}
	}
}
