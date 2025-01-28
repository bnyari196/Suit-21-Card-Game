import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class GameHistoryTest {
    private GameHistory gameHistory;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        gameHistory = new GameHistory();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testRecordAndReplayTurn() {
        // Setup test data
        String playerName = "TestPlayer";
        Card[] initialHand = new Card[]{
                new Card(0, 0), // 2 of Clubs
                new Card(1, 0), // 3 of Clubs
                new Card(2, 0), // 4 of Clubs
                new Card(3, 0), // 5 of Clubs
                new Card(4, 0)  // 6 of Clubs
        };
        Card[] updatedHand = new Card[]{
                new Card(0, 0), // 2 of Clubs
                new Card(1, 0), // 3 of Clubs
                new Card(2, 0), // 4 of Clubs
                new Card(3, 0), // 5 of Clubs
                new Card(12, 0) // Ace of Clubs
        };
        HashMap<String, Integer> suitTotals = new HashMap<>();
        suitTotals.put("Clubs", 20);
        suitTotals.put("Diamonds", 0);
        suitTotals.put("Hearts", 0);
        suitTotals.put("Spades", 0);

        Card droppedCard = new Card(4, 0); // 6 of Clubs
        Card newCard = new Card(12, 0);    // Ace of Clubs

        // Record the turn
        gameHistory.recordTurn(playerName, initialHand, updatedHand, suitTotals,
                "20 of Clubs", droppedCard, newCard, 1);

        // Display the replay
        gameHistory.displayReplay();

        // Get the output as string
        String output = outputStream.toString();

        // Verify all components are present in the replay
        assertTrue(output.contains("TestPlayer"), "Player name should be in replay");
        assertTrue(output.contains("Round 1"), "Round number should be in replay");
        assertTrue(output.contains("6 of Clubs"), "Dropped card should be in replay");
        assertTrue(output.contains("Ace of Clubs"), "New card should be in replay");
        assertTrue(output.contains("Clubs: 20"), "Suit total should be in replay");
        assertTrue(output.contains("20 of Clubs"), "Max score should be in replay");
    }

    @Test
    void testMultipleRoundReplay() {
        // Setup test data for two rounds
        String playerName = "TestPlayer";
        Card[] hand = new Card[]{
                new Card(0, 0), // 2 of Clubs
                new Card(1, 0), // 3 of Clubs
                new Card(2, 0), // 4 of Clubs
                new Card(3, 0), // 5 of Clubs
                new Card(4, 0)  // 6 of Clubs
        };
        HashMap<String, Integer> suitTotals = new HashMap<>();
        suitTotals.put("Clubs", 20);
        suitTotals.put("Diamonds", 0);
        suitTotals.put("Hearts", 0);
        suitTotals.put("Spades", 0);

        // Record two rounds
        gameHistory.recordTurn(playerName, hand, hand, suitTotals,
                "20 of Clubs", null, null, 1);
        gameHistory.recordTurn(playerName, hand, hand, suitTotals,
                "20 of Clubs", null, null, 2);

        // Display the replay
        gameHistory.displayReplay();

        String output = outputStream.toString();

        // Verify both rounds are present
        assertTrue(output.contains("Round 1"), "First round should be in replay");
        assertTrue(output.contains("Round 2"), "Second round should be in replay");
    }

    @Test
    void testEmptyHistoryReplay() {
        gameHistory.displayReplay();
        String output = outputStream.toString();
        assertTrue(output.contains("No game history to display."));
    }

    @Test
    void testClearHistory() {
        // Add a record
        String playerName = "TestPlayer";
        Card[] hand = new Card[]{new Card(0, 0)};
        HashMap<String, Integer> suitTotals = new HashMap<>();
        suitTotals.put("Clubs", 2);

        gameHistory.recordTurn(playerName, hand, hand, suitTotals,
                "2 of Clubs", null, null, 1);

        // Clear history
        gameHistory.clearHistory();

        // Try to display replay
        gameHistory.displayReplay();
        String output = outputStream.toString();

        // Verify it's empty
        assertTrue(output.contains("No game history to display."));
    }

    @Test
    void testReplayWithCardSwap() {
        String playerName = "TestPlayer";
        Card[] initialHand = new Card[]{
                new Card(0, 0), // 2 of Clubs
                new Card(1, 0)  // 3 of Clubs
        };
        Card[] updatedHand = new Card[]{
                new Card(0, 0), // 2 of Clubs
                new Card(12, 0) // Ace of Clubs
        };
        HashMap<String, Integer> suitTotals = new HashMap<>();
        suitTotals.put("Clubs", 13);

        Card droppedCard = initialHand[1]; // 3 of Clubs
        Card newCard = updatedHand[1];     // Ace of Clubs

        gameHistory.recordTurn(playerName, initialHand, updatedHand, suitTotals,
                "13 of Clubs", droppedCard, newCard, 1);

        gameHistory.displayReplay();
        String output = outputStream.toString();

        assertTrue(output.contains("Swapped 3 of Clubs for Ace of Clubs"),
                "Card swap should be shown in replay");
    }
}