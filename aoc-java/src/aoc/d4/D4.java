package aoc.d4;

import aoc.ReadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class D4 {

    public static void main(String[] args) {
        part2();
    }

    private static void part2() {
        List<String> rows = ReadFile.readLines("src/aoc/d4/input.txt");

        long sum = 0;

        List<Card> cards = new ArrayList<>();
        for (String row : rows) {
            Card card = parseRow(row);
            cards.add(card);
        }

        HashMap<Integer, Integer> cardCounts = new HashMap<>();
        for (Card card : cards) cardCounts.put(card.id, 1);

        for (Card card : cards) {
            // every same number
            for (int i = 0; i < card.sameNumbers; i++) {
                // every card instance
                for (int j = 0; j < cardCounts.get(card.id); j++) {
                    int changeId = card.id + 1 + i;
                    cardCounts.put(changeId, cardCounts.get(changeId) + 1);
                }
            }
        }

        for (Integer count : cardCounts.values()) {
            sum += count;
        }

        System.out.println(sum);
    }

    private static void part1() {
        List<String> rows = ReadFile.readLines("src/aoc/d4/input.txt");

        long sum = 0;

        for (String row : rows) {
            Card card = parseRow(row);
            int winPoints = 0;
            for (int i = 0; i < card.sameNumbers; i++) {
                if (i == 0) winPoints = 1;
                else winPoints *= 2;
            }
            sum += winPoints;
        }
        System.out.println(sum);
    }

    private static List<Integer> sameNum(List<Integer> winNum, List<Integer> yourNum) {
        List<Integer> same = new ArrayList<>();
        for (Integer num : yourNum) {
            if (winNum.contains(num)) same.add(num);
        }
        return same;
    }

    private static Card parseRow(String row) {
        String[] idAndRest = row.split(": ");
        String[] rest = idAndRest[0].split(" ");
        int id = Integer.parseInt(rest[rest.length - 1]);

        String[] numbersSeparated = idAndRest[1].split(" \\| ");
        String[] winNumStrings = numbersSeparated[0].split(" ");
        List<Integer> winNum = new ArrayList<>();
        for (String winNumString : winNumStrings) {
            if (winNumString.isEmpty()) continue;
            winNum.add(Integer.parseInt(winNumString));
        }
        String[] yourNumStrings = numbersSeparated[1].split(" ");
        List<Integer> yourNum = new ArrayList<>();
        for (String yourNumString : yourNumStrings) {
            if (yourNumString.isEmpty()) continue;
            yourNum.add(Integer.parseInt(yourNumString));
        }

        List<Integer> same = sameNum(winNum, yourNum);
        int sameCount = same.size();

        return new Card(id, winNum, yourNum, sameCount);
    }

    private record Card(int id, List<Integer> winningNumbers, List<Integer> numbersYouHave, int sameNumbers) {
    }
}
