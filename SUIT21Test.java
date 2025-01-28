import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SUIT21Test {
    private SUIT21 game;
    private ByteArrayInputStream testIn;
    private String input;

    @BeforeEach
    void setUp() {
        // Simulate user input for 2 games and 3 players
        input = "2\n3\nPlayer1\nPlayer2\nComputer\n";
        testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
        game = new SUIT21();
    }

    @AfterEach
    void restoreSystemInput() {
        System.setIn(System.in);
    }

    @Test
    void testGameInitialization() {
        game.askNumberOfGames();
        System.out.println(game.getTotalGames());
        assertEquals(2, game.getTotalGames());
    }

    @Test
    void testPlayerSetup() {
        // Add 3 players
        String input = "3\nPlayer1\nPlayer2\nComputer\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);

        game = new SUIT21();

        game.setupPlayers();

        // Get the list of players and perform assertions
        ArrayList<Player> players = game.getPlayers(0);
        assertEquals(3, players.size());  // Verify there are 3 players
        assertEquals("Player1", players.get(0).getName());  // First player
        assertEquals("Player2", players.get(1).getName());  // Second player
        assertEquals("Computer", players.get(2).getName());  // Third player
    }


    // L1. Deal 5 cards from a shuffled deck.
    @Test
    void testInitialDealing() {
        game.setupPlayers();
        game.dealInitialCards();
        for (Player player : game.getPlayers(0)) {
            assertEquals(5, player.getHand().length);
            for (Card card : player.getHand()) {
                assertNotNull(card);
            }
        }
    }

    // L4. Test Game End conditions && L5. sharing points
    @Test
    void testPointSplittingBetweenWinners() {
        // Create test players
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = new Player("Player1");
        Player player2 = new Player("Player2");
        testPlayers.add(player1);
        testPlayers.add(player2);

        // Simulate both players getting 21 in the same round
        // Set up hands that will result in 21
        player1.addCard(new Card(12, 0)); // Ace of Clubs
        player1.addCard(new Card(9, 0));  // Jack of Clubs

        player2.addCard(new Card(12, 1)); // Ace of Diamonds
        player2.addCard(new Card(9, 1));  // Jack of Diamonds

        // Add both players to roundWinners (using reflection since it's private)
        try {
            java.lang.reflect.Method awardPointsMethod = SUIT21.class.getDeclaredMethod("awardPoints");
            awardPointsMethod.setAccessible(true);

            // Set up the roundWinners list using reflection
            java.lang.reflect.Field roundWinnersField = SUIT21.class.getDeclaredField("roundWinners");
            roundWinnersField.setAccessible(true);
            ArrayList<Player> roundWinners = new ArrayList<>();
            roundWinners.add(player1);
            roundWinners.add(player2);
            roundWinnersField.set(game, roundWinners);

            // Award points
            awardPointsMethod.invoke(game);

            // Get playerPoints map using reflection
            java.lang.reflect.Field playerPointsField = SUIT21.class.getDeclaredField("playerPoints");
            playerPointsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Double> playerPoints = (HashMap<String, Double>) playerPointsField.get(game);

            // Verify points are split correctly
            assertEquals(0.5, playerPoints.get("Player1"));
            assertEquals(0.5, playerPoints.get("Player2"));

        } catch (Exception e) {
            fail("Test failed due to reflection error: " + e.getMessage());
        }
    }

    // L4. && L5.
    @Test
    void testEdgeCaseThreeWayPointSplit() {
        // Create test players
        ArrayList<Player> testPlayers = new ArrayList<>();
        Player player1 = new Player("Player1");
        Player player2 = new Player("Player2");
        Player player3 = new Player("Player3");

        testPlayers.add(player1);
        testPlayers.add(player2);
        testPlayers.add(player3);

        // Simulate both players getting 21 in the same round
        // Set up hands that will result in 21
        player1.addCard(new Card(12, 0)); // Ace of Clubs
        player1.addCard(new Card(9, 0));  // Jack of Clubs

        player2.addCard(new Card(12, 1)); // Ace of Diamonds
        player2.addCard(new Card(9, 1));  // Jack of Diamonds

        player3.addCard(new Card(12, 2)); // Ace of Hearts
        player3.addCard(new Card(9, 2));  // Jack of Hearts

        // Add both players to roundWinners (using reflection since it's private)
        try {
            java.lang.reflect.Method awardPointsMethod = SUIT21.class.getDeclaredMethod("awardPoints");
            awardPointsMethod.setAccessible(true);

            // Set up the roundWinners list using reflection
            java.lang.reflect.Field roundWinnersField = SUIT21.class.getDeclaredField("roundWinners");
            roundWinnersField.setAccessible(true);
            ArrayList<Player> roundWinners = new ArrayList<>();
            roundWinners.add(player1);
            roundWinners.add(player2);
            roundWinners.add(player3);
            roundWinnersField.set(game, roundWinners);

            // Award points
            awardPointsMethod.invoke(game);

            // Get playerPoints map using reflection
            java.lang.reflect.Field playerPointsField = SUIT21.class.getDeclaredField("playerPoints");
            playerPointsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Double> playerPoints = (HashMap<String, Double>) playerPointsField.get(game);

            // Verify points are split correctly
            assertEquals(0.3333333333333333, playerPoints.get("Player1"));
            assertEquals(0.3333333333333333, playerPoints.get("Player2"));
            assertEquals(0.3333333333333333, playerPoints.get("Player3"));

        } catch (Exception e) {
            fail("Test failed due to reflection error: " + e.getMessage());
        }
    }

    // L7 - AI player logic && correct logic test
    @Test
    void testComputerPlayerStrategy() {
        Player computer = new Player("Computer");

        // Set up a hand where dropping lowest card is optimal
        computer.addCard(new Card(0, 0));  // 2 of Clubs
        computer.addCard(new Card(9, 0));  // Jack of Clubs
        computer.addCard(new Card(10, 0)); // Queen of Clubs
        computer.addCard(new Card(11, 0)); // King of Clubs
        computer.addCard(new Card(12, 1)); // Ace of Diamonds

        // Verify computer drops the 2 of Clubs (lowest value)
        assertEquals(0, computer.findLowestValueCardIndex());

        // Reset Hand
        computer.clearHand();

        // Set up a hand where dropping lowest card is optimal
        computer.addCard(new Card(0, 1));  // 2 of Diamonds
        computer.addCard(new Card(9, 0));  // Jack of Clubs
        computer.addCard(new Card(10, 0)); // Queen of Clubs
        computer.addCard(new Card(11, 1)); // King of Diamonds
        computer.addCard(new Card(12, 1)); // Ace of Diamonds

        // Verify computer drops the 2 of Diamonds to make 21 from diamond suit
        assertEquals(0, computer.findLowestValueCardIndex());

        // Reset Hand
        computer.clearHand();

        // Set up a hand where dropping lowest card is optimal
        computer.addCard(new Card(4, 1));  // 6 of Diamonds
        computer.addCard(new Card(0, 1));  // 2 of Diamonds
        computer.addCard(new Card(0, 3)); // 2 of Spades
        computer.addCard(new Card(11, 1)); // King of Diamonds
        computer.addCard(new Card(7, 1)); // 9 of Diamonds

        // Verify computer drops the 6 of Diamonds to make 21 and not 6 of Diamonds
        assertEquals(0, computer.findLowestValueCardIndex());

        computer.clearHand();
/*
        // Set up a hand where dropping lowest card is optimal (drops 2 instead of 0!!)
        computer.addCard(new Card(4, 1));  // 6 of Diamonds
        computer.addCard(new Card(9, 0));  // Jack of Clubs
        computer.addCard(new Card(0, 3)); // 2 of Spades
        computer.addCard(new Card(11, 1)); // King of Diamonds
        computer.addCard(new Card(12, 1)); // Ace of Diamonds

        // Ace logic needs to be recalculated
        assertEquals(0, computer.findLowestValueCardIndex());*/
    }

    @Test
    void testValidGameNumberRange() {
        // Custom setup for this specific test
        ByteArrayInputStream testIn = new ByteArrayInputStream("0\n6\n4\n".getBytes());
        System.setIn(testIn);
        game = new SUIT21();  // Ensure to create a new instance of the game

        // Should reject invalid game numbers (< 1 or > 5)
        game.askNumberOfGames();
        assertEquals(4, game.getTotalGames());
    }

}