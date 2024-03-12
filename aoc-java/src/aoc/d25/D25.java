package aoc.d25;


import aoc.ReadFile;

import java.util.*;

public class D25 {
	private static final Map<String, Set<String>> graph = new HashMap<>();
	/*
	static class Vertice {
		final String name;
		final List<Edge> edges = new ArrayList<>();
		boolean visited = false;


		public Vertice(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Tipp{" + "info='" + name + '\'' + '}';
		}

		@Override
		public boolean equals(Object obj) {
			Vertice vertice = (Vertice) obj;
			return vertice.name.equals(name);
		}
	}

	static class Edge {
		Vertice start;
		Vertice end;

		public Edge(Vertice start, Vertice end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean equals(Object obj) {
			Edge edge = (Edge) obj;
			return start.equals(edge.start) && end.equals(edge.end);
		}
	}*/

	public static void main(String[] args) {
		part1();
	}

	private static void part2() {
		List<String> rows = ReadFile.readLines("src/aoc/d25/testinput.txt");

	}

	private static void part1() {
		List<String> rows = ReadFile.readLines("src/aoc/d25/input.txt");

		HashMap<String, Vertex> vertices = new HashMap<>();
		List<Edge> edges = new ArrayList<>();

		for (String s : rows) {
			String[] parts = s.split(": ");
			Vertex nv = vertices.computeIfAbsent(parts[0], Vertex::new);
			String[] neighbours = parts[1].split(" ");

			for (String nb : neighbours) {
				Vertex nv2 = vertices.computeIfAbsent(nb, Vertex::new);
				Edge edge = new Edge(nv, nv2);
				edges.add(edge);
				nv.addNeighbour(edge);
				nv2.addNeighbour(edge);
			}
		}

		int tries = 3000;
		List<Vertex> vertexList = new ArrayList<>(vertices.values());
		Random r = new Random();
		for (int i = 0; i < tries; i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			Vertex start = vertexList.get(r.nextInt(vertices.size()));
			Vertex end = vertexList.get(r.nextInt(vertices.size()));
			dijkstra(start, end);
		}

		edges.sort((o1, o2) -> o2.counter - o1.counter);
		for (Edge e : edges) {
			System.out.println(e);
		}

		Edge disconnect = edges.get(0);
		Vertex start1 = disconnect.one;
		Vertex start2 = disconnect.two;

		HashSet<Edge> disconnected = new HashSet<>();
		disconnected.add(edges.get(0));
		disconnected.add(edges.get(1));
		disconnected.add(edges.get(2));
		int size1 = flood(start1, vertices, disconnected);
		int size2 = flood(start2, vertices, disconnected);
		System.out.printf("%d = %d + %d\n", vertexList.size(), size1, size2);
		System.out.println(size1 * size2);
	}

	private static int flood(Vertex start, HashMap<String, Vertex> vertices, HashSet<Edge> disconnected) {
		HashSet<Vertex> seen = new HashSet<>();
		LinkedList<Vertex> todo = new LinkedList<>();
		todo.add(start);
		while (!todo.isEmpty()) {
			Vertex cur = todo.removeFirst();
			if (seen.contains(cur)) {
				continue;
			}
			seen.add(cur);
			for (Edge e : cur.outgoing) {
				if (disconnected.contains(e)) {
					continue;
				}
				todo.add(e.other(cur));
			}
		}
		return seen.size();
	}

	record WorkItem(Vertex v, int steps, Edge used, WorkItem prev) {
		@Override
		public String toString() {
			return v.label + " " + steps;
		}
	}

	private static void dijkstra(Vertex start, Vertex end) {
		PriorityQueue<WorkItem> todo = new PriorityQueue<>(Comparator.comparingInt(o -> o.steps));
		HashMap<Vertex, Integer> shortest = new HashMap<>();
		todo.add(new WorkItem(start, 0, null, null));
		while (!todo.isEmpty()) {
			WorkItem wi = todo.remove();
			int way = shortest.computeIfAbsent(wi.v, v -> Integer.MAX_VALUE);
			if (way <= wi.steps) {
				continue;
			}
			if (end == wi.v) {
				WorkItem cur = wi;
				while (cur.used != null) {
					cur.used.counter++;
					cur = cur.prev;
				}
				return;
			}
			shortest.put(wi.v, wi.steps);
			for (Edge e : wi.v.outgoing) {
				Vertex cand = e.other(wi.v);
				int cur = shortest.computeIfAbsent(cand, v -> Integer.MAX_VALUE);
				if (cur > wi.steps + 1) {
					todo.add(new WorkItem(cand, wi.steps + 1, e, wi));
				}
			}
		}
	}

	private static void dfs(Vertex start, int n) {
		Set<Vertex> visited = new HashSet<>();
		LinkedList<Vertex> todo = new LinkedList<>();
		LinkedList<Edge> todo2 = new LinkedList<>();
		for (Edge e : start.outgoing) {
			todo2.addFirst(e);
			todo.addFirst(e.other(start));
		}
		while (visited.size() < n) {
			Vertex vertex = todo.removeLast();
			Edge edge = todo2.removeLast();
			if (!visited.contains(vertex)) {
				visited.add(vertex);
				edge.counter++;
				for (Edge e : vertex.outgoing) {
					todo2.addFirst(e);
					todo.addFirst(e.other(vertex));
				}
			}
		}
	}

	static class Vertex {
		List<Edge> outgoing = new ArrayList<>();
		String label;

		public Vertex(String label) {
			this.label = label;
		}

		public void addNeighbour(Edge edge) {
			outgoing.add(edge);
		}
	}

	static class Edge {
		Vertex one, two;
		int counter;

		public Edge(Vertex one, Vertex two) {
			this.one = one;
			this.two = two;
		}

		public Vertex other(Vertex v) {
			return v == one ? two : one;
		}

		@Override
		public String toString() {
			return String.format("%s -> %s : %d", one.label, two.label, counter);
		}
	}
/*
	private static List<Edge> getEdges(List<Vertice> graph) {
		List<Edge> edges = new ArrayList<>();
		for (Vertice vertice : graph) {
			for (Edge edge : vertice.edges) {
				if (!edges.contains(new Edge(edge.end, edge.start))) edges.add(edge);
			}
		}
		return edges;
	}

	private static Map<String, Set<String>> karger(List<Vertice> graph, List<Edge> edges) {
		int E = edges.size();
		int V = graph.size();

		final Random rand = new Random();
		final Map<String, Set<String>> verticesGroups = new HashMap<>();
		graph.forEach((v) -> {
			verticesGroups.put(v.name, new HashSet<>(List.of(v.name)));
		});
		while (verticesGroups.size() > 2) {
			final Edge edge = edges.get(rand.nextInt(edges.size()));
			final Vertice from = edge.start;
			final Vertice to = edge.end;

			final Set<String> fromGroup = verticesGroups.get(from.name);
			final Set<String> toGroup = verticesGroups.get(to.name);
			fromGroup.addAll(toGroup);

			verticesGroups.remove(to.name);

			edges.removeIf((e) -> (e.start.equals(to) && e.end.equals(from)) || (e.start.equals(from) && e.end.equals(to)));

			edges
					.forEach((e) -> {
						if (e.start.equals(to)) {
							e.start = from;
						}

						if (e.end.equals(to)) {
							e.end = from;
						}
					});
		}
		return verticesGroups;
	}

	private static int solve(List<Vertice> graph) {
		for (Vertice start1 : graph) {
			List<Edge> edgeList1 = new ArrayList<>(start1.edges);
			for (Edge edge1 : edgeList1) {
				Vertice end1 = edge1.end;
				// have edge1 between start1 and end1, remove it
				removeEdge(start1, end1);

				for (Vertice start2 : graph) {
					List<Edge> edgeList2 = new ArrayList<>(start2.edges);
					for (Edge edge2 : edgeList2) {
						if (edge1.equals(edge2)) continue;
						Vertice end2 = edge2.end;
						// have edge2 between start2 and end2, remove it
						removeEdge(start2, end2);

						for (Vertice start3 : graph) {
							List<Edge> edgeList3 = new ArrayList<>(start3.edges);
							for (Edge edge3 : edgeList3) {
								if (edge3.equals(edge1) || edge3.equals(edge2)) continue;
								Vertice end3 = edge3.end;
								// have edge3 between start3 and end3, remove it
								removeEdge(start3, end3);

								ArrayList<ArrayList<Integer>> components = new ArrayList<>();
								connectedComponents(graph, components);
								if (components.size() == 2) return components.get(0).size() * components.get(1).size();

								// put edge3 back
								addEdge(start3, end3);
							}
						}

						// put edge2 back
						addEdge(start2, end2);
					}
				}

				// put edge1 back
				addEdge(start1, end1);
			}
		}
		throw new RuntimeException("did not find solution");
	}

	private static void connectedComponents(List<Vertice> graph, int v, boolean[] visited, ArrayList<Integer> al) {
		visited[v] = true;
		al.add(v);
		Vertice vertice = graph.get(v);
		for (Edge edge : vertice.edges) {
			Vertice next = edge.end;
			int n = graph.indexOf(next);
			if (!visited[n]) connectedComponents(graph, n, visited, al);
		}
	}

	private static void connectedComponents(List<Vertice> graph, ArrayList<ArrayList<Integer>> components) {
		int V = graph.size();
		boolean[] visited = new boolean[V];

		for (int i = 0; i < V; i++) {
			ArrayList<Integer> al = new ArrayList<>();
			if (!visited[i]) {
				connectedComponents(graph, i, visited, al);
				components.add(al);
			}
		}
	}

	private static void addEdge(Vertice one, Vertice two) {
		Edge edge1 = new Edge(one, two);
		Edge edge2 = new Edge(two, one);
		if (!one.edges.contains(edge1)) one.edges.add(edge1);
		if (!two.edges.contains(edge2)) two.edges.add(edge2);
	}

	private static void removeEdge(Vertice one, Vertice two) {
		Edge edge1 = new Edge(one, two);
		Edge edge2 = new Edge(two, one);
		one.edges.remove(edge1);
		two.edges.remove(edge2);
	}

	private static Boolean isCyclic(List<Vertice> graph, int v, Boolean[] visited, int parent) {
		visited[v] = true;
		Vertice vertice = graph.get(v);

		for (Edge edge : vertice.edges) {
			Vertice next = edge.end;
			int i = graph.indexOf(next);
			if (!visited[i]) if (isCyclic(graph, i, visited, v)) return true;
			else if (i != parent) return true;
		}
		return false;
	}

	private static Boolean isCyclic(List<Vertice> graph) {
		int V = graph.size();
		Boolean[] visited = new Boolean[V];
		Arrays.fill(visited, false);

		for (int u = 0; u < V; u++) {
			if (!visited[u]) if (isCyclic(graph, u, visited, -1)) return true;
		}

		return false;
	}

	private static Vertice getByName(String name, List<Vertice> vertices) {
		for (Vertice vertice : vertices) if (vertice.name.equals(name)) return vertice;
		throw new RuntimeException("no vertice");
	}

	private static List<Vertice> parseInput(List<String> rows) {
		List<Vertice> graph = new ArrayList<>();
		List<String> added = new ArrayList<>();

		// add all vertices
		for (String row : rows) {
			String[] nameAndCons = row.split(": ");
			if (!added.contains(nameAndCons[0])) {
				graph.add(new Vertice(nameAndCons[0]));
				added.add(nameAndCons[0]);
			}
			for (String name : nameAndCons[1].split(" ")) {
				if (!added.contains(name)) {
					graph.add(new Vertice(name));
					added.add(name);
				}
			}
		}

		// add all connections
		for (String row : rows) {
			String[] nameAndCons = row.split(": ");
			Vertice one = getByName(nameAndCons[0], graph);
			for (String name : nameAndCons[1].split(" ")) {
				Vertice two = getByName(name, graph);
				addEdge(one, two);
			}
		}
		return graph;
	}
*/

}
