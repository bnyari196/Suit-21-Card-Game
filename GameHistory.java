import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class GameHistory {

    private static class TurnRecord {
        final String playerName;
        final Card[] initialHand;
        final Card[] hand;
        final HashMap<String, Integer> suitTotals;
        final String maxScore;
        final Card droppedCard;
        final Card newCard;
        final int roundNumber;

        TurnRecord(String playerName,Card[] initialHand,
                   Card[] hand, HashMap<String, Integer> suitTotals,
                   String maxScore, Card droppedCard, Card newCard, int roundNumber) {
            this.playerName = playerName;
            this.initialHand = Arrays.copyOf(initialHand, initialHand.length);
            this.hand = Arrays.copyOf(hand, hand.length);
            this.suitTotals = new HashMap<>(suitTotals);
            this.maxScore = maxScore;
            this.droppedCard = droppedCard;
            this.newCard = newCard;
            this.roundNumber = roundNumber;
        }
    }

    private final ArrayList<TurnRecord> gameRecords;

    public GameHistory() {
        this.gameRecords = new ArrayList<>();
    }

    public void recordTurn(String playerName,Card[] initialHand,
                           Card[] hand, HashMap<String, Integer> suitTotals,
                           String maxScore, Card droppedCard, Card newCard, int roundNumber) {
        gameRecords.add(new TurnRecord(playerName, initialHand, hand, suitTotals, maxScore,
                droppedCard, newCard, roundNumber));
    }

    public void displayReplay() {
        if (gameRecords.isEmpty()) {
            System.out.println("No game history to display.");
            return;
        }

        System.out.println("\n\n*********************\n*** Game Replay!! ***\n*********************\n");

        int currentRound = 1;
        for (TurnRecord record : gameRecords) {
            if (record.roundNumber > currentRound) {
                System.out.printf("\n=== Round %d ===\n", record.roundNumber);
                currentRound = record.roundNumber;
            }

            System.out.printf("\n%s's turn (Round %d)\n\n",
                    record.playerName, record.roundNumber);
            for (int i = 0; i < record.initialHand.length; i++) {
                System.out.println(record.initialHand[i]);
            }

            if (record.droppedCard != null && record.newCard != null) {
                System.out.printf("\nSwapped %s for %s\n\n", record.droppedCard, record.newCard);
            }

            for (int i = 0; i < record.hand.length; i++) {
                System.out.println(record.hand[i]);
            }
            System.out.println("\nSuit scores:");
            record.suitTotals.forEach((suit, total) -> System.out.printf("%s: %d, ", suit, total));
            System.out.println("\nBest Score: " + record.maxScore);

            System.out.println("\n-------------------------------------------");
        }
    }

    public void clearHistory() {
        gameRecords.clear();
    }
}
