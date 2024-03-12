package aoc.d19;


import aoc.ReadFile;

import java.math.BigInteger;
import java.util.*;

public class D19 {

	public static void main(String[] args) {
		part2();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d19/input.txt");

		HashMap<String, List<Rule>> flows = new HashMap<>();

		for (String row : rows) {
			if (row.isEmpty()) break;
			Workflow flow = parseFlow(row);
			flows.put(flow.name, flow.rules);
		}

		List<String> rulePaths = new ArrayList<>();
		BigInteger count = findAcceptPaths(flows, rulePaths);
		System.out.println(count);

		/*
		List<List<Integer>> xmasConditions = new ArrayList<>();
		for (String rulePath : rulePaths) {
			xmasConditions.add(constraints(rulePath.split(";")));
		}*/

	}

	private static List<Integer> constraints(String[] rules) {
		// min - inclusive, max exclusive
		int maxX = 4001, minX = 1;
		int maxM = 4001, minM = 1;
		int maxA = 4001, minA = 1;
		int maxS = 4001, minS = 1;

		for (String rule : rules) {
			if (rule.isEmpty()) continue;

			if (rule.contains(">=")) {
				String[] nameAndValue = rule.split(">=");
				switch (nameAndValue[0]) {
					case "x": {
						minX = Math.max(minX, Integer.parseInt(nameAndValue[1]));
						break;
					}
					case "m": {
						minM = Math.max(minM, Integer.parseInt(nameAndValue[1]));
						break;
					}
					case "a": {
						minA = Math.max(minA, Integer.parseInt(nameAndValue[1]));
						break;
					}
					case "s": {
						minS = Math.max(minS, Integer.parseInt(nameAndValue[1]));
						break;
					}
				}
			} else if (rule.contains("<=")) {
				String[] nameAndValue = rule.split("<=");
				switch (nameAndValue[0]) {
					case "x": {
						maxX = Math.min(maxX, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
					case "m": {
						maxM =  Math.min(maxM, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
					case "a": {
						maxA =  Math.min(maxA, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
					case "s": {
						maxS =  Math.min(maxS, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
				}
			} else if (rule.contains(">")) {
				String[] nameAndValue = rule.split(">");
				switch (nameAndValue[0]) {
					case "x": {
						minX = Math.max(minX, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
					case "m": {
						minM = Math.max(minM, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
					case "a": {
						minA = Math.max(minA, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
					case "s": {
						minS = Math.max(minS, Integer.parseInt(nameAndValue[1]) + 1);
						break;
					}
				}
			} else if (rule.contains("<")) {
				String[] nameAndValue = rule.split("<");
				switch (nameAndValue[0]) {
					case "x": {
						maxX =  Math.min(maxX, Integer.parseInt(nameAndValue[1]));
						break;
					}
					case "m": {
						maxM =  Math.min(maxM, Integer.parseInt(nameAndValue[1]));
						break;
					}
					case "a": {
						maxA =  Math.min(maxA, Integer.parseInt(nameAndValue[1]));
						break;
					}
					case "s": {
						maxS =  Math.min(maxS, Integer.parseInt(nameAndValue[1]));
						break;
					}
				}
			}
		}
		return List.of(minX, maxX, minM, maxM, minA, maxA, minS, maxS);
	}

	private static BigInteger findAcceptPaths(HashMap<String, List<Rule>> flows, List<String> rulePaths) {
		return findAcceptPaths(flows, rulePaths, "", "in");
	}

	private static BigInteger findAcceptPaths(HashMap<String, List<Rule>> flows, List<String> rulePaths, String path, String ruleName) {
		if (ruleName.equals("A")) {
			rulePaths.add(path);
			List<Integer> conditions = constraints(path.split(";"));
			BigInteger xRange = new BigInteger(String.valueOf(conditions.get(1) - conditions.get(0)));
			BigInteger mRange = new BigInteger(String.valueOf(conditions.get(3) - conditions.get(2)));
			BigInteger aRange = new BigInteger(String.valueOf(conditions.get(5) - conditions.get(4)));
			BigInteger sRange = new BigInteger(String.valueOf(conditions.get(7) - conditions.get(6)));
			return xRange.multiply(mRange).multiply(aRange).multiply(sRange);
		}
		if (ruleName.equals("R")) return new BigInteger("0");
		List<Rule> ruleList = flows.get(ruleName);
		BigInteger count = new BigInteger("0");
		for (Rule rule : ruleList) {
			if (rule.condition.isEmpty()) count = count.add(findAcceptPaths(flows, rulePaths, path, rule.result));
			else {
				// läbime reegli, uus nimi
				count = count.add(findAcceptPaths(flows, rulePaths, path + ";" + rule.condition, rule.result));
				// ei läbi reeglit, jätkame
				path += ";" + reverseCondition(rule.condition);
			}
		}
		return count;
	}

	private static String reverseCondition(String condition) {
		if (condition.isEmpty()) return "";
		if (condition.contains("<")) {
			int split = condition.indexOf("<");
			return condition.substring(0, split) + ">=" + condition.substring(split + 1);
		}
		int split = condition.indexOf(">");
		return condition.substring(0, split) + "<=" + condition.substring(split + 1);
	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d19/input.txt");

		List<Workflow> workflows = new ArrayList<>();
		List<Part> parts = new ArrayList<>();
		HashMap<String, List<Rule>> flows = new HashMap<>();

		boolean first = true;
		for (String row : rows) {
			if (row.isEmpty()) {
				first = false;
				continue;
			}
			if (first) workflows.add(parseFlow(row));
			else parts.add(parsePart(row));
		}
		for (Workflow workflow : workflows) flows.put(workflow.name, workflow.rules);

		int sum = 0;
		for (Part part : parts) {
			if (isAccepted(flows, part)) {
				sum += part.x + part.m + part.a + part.s;
			}
		}
		System.out.println(sum);
	}

	private static boolean isAccepted(HashMap<String, List<Rule>> flows, Part part) {
		String start = "in";
		String accepted = "A";
		String rejected = "R";

		String current = start;

		newFlow:
		while (!current.equals(rejected)) {
			if (current.equals(accepted)) return true;
			List<Rule> rules = flows.get(current);

			for (Rule rule : rules) {
				if (fillsCondition(rule, part)) {
					current = rule.result;
					continue newFlow;
				}
			}

		}

		return false;
	}

	private static boolean fillsCondition(Rule rule, Part part) {
		if (rule.condition.isEmpty()) return true;
		int partValue = switch (rule.condition.charAt(0)) {
			case 'x' -> part.x;
			case 'm' -> part.m;
			case 'a' -> part.a;
			case 's' -> part.s;
			default -> throw new IllegalArgumentException("katki");
		};
		int conditionValue = Integer.parseInt(rule.condition.substring(2));
		return switch (rule.condition.charAt(1)) {
			case '<' -> partValue < conditionValue;
			case '>' -> partValue > conditionValue;
			default -> throw new IllegalArgumentException("katki");
		};
	}

	private static Workflow parseFlow(String row) {
		String[] nameAndRest = row.substring(0, row.length() - 1).split("\\{");
		String name = nameAndRest[0];
		List<Rule> rules = new ArrayList<>();
		for (String s : nameAndRest[1].split(",")) {
			if (s.contains(":")) {
				String[] conditionAndResult = s.split(":");
				rules.add(new Rule(conditionAndResult[0], conditionAndResult[1]));
			} else rules.add(new Rule("", s));
		}

		return new Workflow(name, rules);
	}

	private static Part parsePart(String row) {
		String[] things = row.substring(1, row.length() - 1).split(",");
		int x = Integer.parseInt(things[0].substring(2));
		int m = Integer.parseInt(things[1].substring(2));
		int a = Integer.parseInt(things[2].substring(2));
		int s = Integer.parseInt(things[3].substring(2));
		return new Part(x, m, a, s);
	}

	private static record Part(int x, int m, int a, int s) {
		@Override
		public boolean equals(Object obj) {
			Part part = (Part) obj;
			return x == part.x && m == part.m && a == part.a && s == part.s;
		}
	}

	private static record Rule(String condition, String result) {
	}

	private static record Workflow(String name, List<Rule> rules) {
	}
}
