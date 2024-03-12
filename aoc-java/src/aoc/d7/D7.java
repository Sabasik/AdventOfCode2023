package aoc.d7;

import aoc.ReadFile;

import java.util.ArrayList;
import java.util.List;

public class D7 {

    public static final List<Character> cardTypes1 = List.of('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2');
    public static final List<Character> cardTypes2 = List.of('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J');

    public static boolean firstPart;

    public static void main(String[] args) {
        firstPart = false;
        part2();
    }

    public static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d7/input.txt");


        List<HandAndType> hands = new ArrayList<>();
        for (String row : rows) {
            String[] handAndBid = row.split(" ");
            hands.add(
                    new HandAndType(
                            handAndBid[0],
                            Integer.parseInt(handAndBid[1]),
                            handTypeJ(handAndBid[0])
                    )
            );
        }
        hands.sort(null);

        System.out.println(hands);
        int winnings = 0;
        int rank = hands.size();
        for (int i = 0; i < hands.size(); i++) {
            winnings += rank * hands.get(i).bid;
            rank--;
        }
        System.out.println(winnings);
    }

    public static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d7/input.txt");


        List<HandAndType> hands = new ArrayList<>();
        for (String row : rows) {
            String[] handAndBid = row.split(" ");
            hands.add(
                    new HandAndType(
                            handAndBid[0],
                            Integer.parseInt(handAndBid[1]),
                            handType(handAndBid[0])
                    )
            );
        }
        hands.sort(null);

        // System.out.println(hands);
        int winnings = 0;
        int rank = hands.size();
        for (int i = 0; i < hands.size(); i++) {
            winnings += rank * hands.get(i).bid;
            rank--;
        }
        System.out.println(winnings);
    }

    public record HandAndType(String hand, Integer bid, Integer type)
            implements Comparable<HandAndType> {
        @Override
        public int compareTo(HandAndType handAndType) {
            int result = type.compareTo(handAndType.type);
            if (result != 0) return result;
            return firstIsStronger(hand, handAndType.hand) ? -1 : 1;
        }
    }

    public static boolean firstIsStronger(String hand1, String hand2) {
        List<Character> cardTypes = firstPart ? cardTypes1 : cardTypes2;

        for (int i = 0; i < hand1.length(); i++) {
            int first = cardTypes.indexOf(hand1.charAt(i));
            int second = cardTypes.indexOf(hand2.charAt(i));
            if (first < second) return true;
            if (first > second) return false;
        }
        throw new IllegalArgumentException("cannot have equal hands");
    }

    public static int handType(String hand) {
        List<Integer> counts = findCounts(hand);
        // 5 of a kind
        if (counts.contains(5)) return 0;
        // 4 of a kind
        if (counts.contains(4)) return 1;
        // full house
        if (counts.contains(3) && counts.contains(2)) return 2;
        // 3 of a kind
        if (counts.contains(3)) return 3;
        // two pair
        int first2 = counts.indexOf(2);
        if (first2 != -1 && counts.lastIndexOf(2) != first2) return 4;
        // one pair
        if (counts.contains(2)) return 5;
        // high card
        return 6;
    }

    public static int handTypeJ(String hand) {
        CountsAndValues cav = findCountsAndValues(hand);
        int n = cav.counts.size();
        // 5 of a kind
        if (cav.counts.contains(5)) return 0;
        int jLoc = cav.values.indexOf('J');
        boolean checkJ = jLoc != -1;
        if (checkJ) {
            for (int i = 0; i < n; i++) {
                if (i == jLoc) continue;
                if (cav.counts.get(i) + cav.counts.get(jLoc) == 5) return 0;
            }
        }
        // 4 of a kind
        if (cav.counts.contains(4)) return 1;
        if (checkJ) {
            for (int i = 0; i < n; i++) {
                if (i == jLoc) continue;
                if (cav.counts.get(i) + cav.counts.get(jLoc) == 4) return 1;
            }
        }
        // full house
        if (cav.counts.contains(3) && cav.counts.contains(2)) return 2;
        if (checkJ) {
            for (int i = 0; i < n; i++) {
                if (i == jLoc) continue;
                int cardI = cav.counts.get(i);
                int jCount = cav.counts.get(jLoc);
                for (int j = 0; j < n; j++) {
                    if (j == jLoc || i == j) continue;
                    if (cardI + cav.counts.get(j) + jCount == 5) return 2;
                }
            }
        }
        // 3 of a kind
        if (cav.counts.contains(3)) return 3;
        if (checkJ) {
            for (int i = 0; i < n; i++) {
                if (i == jLoc) continue;
                if (cav.counts.get(i) + cav.counts.get(jLoc) == 3) return 3;
            }
        }
        // two pair
        int first2 = cav.counts.indexOf(2);
        if (first2 != -1 && cav.counts.lastIndexOf(2) != first2) return 4;
        if (checkJ) if (first2 != -1) return 4;
        // one pair
        if (cav.counts.contains(2)) return 5;
        if (checkJ) if (cav.counts.get(jLoc) >= 1) return 5;
        // high card
        return 6;
    }

    public static List<Integer> findCounts(String hand) {
        List<Integer> counts = new ArrayList<>();
        List<Character> values = new ArrayList<>();
        for (char c : hand.toCharArray()) {
            int location = values.indexOf(c);
            if (location != -1) counts.set(location, counts.get(location) + 1);
            else {
                values.add(c);
                counts.add(1);
            }
        }

        return counts;
    }

    public static CountsAndValues findCountsAndValues(String hand) {
        List<Integer> counts = new ArrayList<>();
        List<Character> values = new ArrayList<>();
        for (char c : hand.toCharArray()) {
            int location = values.indexOf(c);
            if (location != -1) counts.set(location, counts.get(location) + 1);
            else {
                values.add(c);
                counts.add(1);
            }
        }

        return new CountsAndValues(counts, values);
    }

    public record CountsAndValues(List<Integer> counts, List<Character> values) {
    }
}
