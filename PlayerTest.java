import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer");
    }

    @Test
    void testAddCardToHand() {
        Card card = new Card(0, 0); // 2 of Clubs
        player.addCard(card);
        Card[] hand = player.getHand();
        assertEquals(card, hand[0]);
    }

    @Test
    void testHandFullException() {
        // Add 5 cards (full hand)
        for (int i = 0; i < 5; i++) {
            player.addCard(new Card(i, 0));
        }
        // Try to add 6th card
        assertThrows(IllegalStateException.class, () -> {
            player.addCard(new Card(5, 0));
        });
    }

    @Test
    void testGetCardValue() {
        Card aceCard = new Card(12, 0); // Ace
        Card kingCard = new Card(11, 0); // King
        Card numberCard = new Card(7, 0); // 9

        // Test Ace high (when total <= 10)
        assertEquals(11, player.getCardValue(aceCard, 10));
        // Test Ace low (when total > 10)
        assertEquals(1, player.getCardValue(aceCard, 11));
        // Test face card
        assertEquals(10, player.getCardValue(kingCard, 0));
        // Test number card
        assertEquals(9, player.getCardValue(numberCard, 0));
    }

    @Test
    void testTwentyOneDetection() {
        // Create a hand that sums to 21 in Hearts
        player.addCard(new Card(12, 2)); // Ace of Hearts
        player.addCard(new Card(9, 2));  // Jack of Hearts
        assertFalse(player.hasTwentyOne());
        assertEquals("21 of Hearts (You win!)", player.getMaxSuitValue());
        assertTrue(player.hasTwentyOne());
    }

    @Test
    void testClearHand() {
        // Add cards to the hand
        player.addCard(new Card(0, 0));  // 2 of Clubs
        player.addCard(new Card(1, 0));  // 3 of Clubs

        // Clear the hand
        player.clearHand();

        // Verify the hand is empty
        assertNull(player.getHand()[0]);
        assertNull(player.getHand()[1]);
        assertFalse(player.hasTwentyOne());
    }

    @Test
    void testNoValidTwentyOne() {
        player.addCard(new Card(8, 0));  // 10 of Clubs
        player.addCard(new Card(7, 0));  // Jack of Clubs

        // Player does not have 21, so this should return false
        assertFalse(player.hasTwentyOne());
    }

    @Test
    void testSwapCard() {
        player.addCard(new Card(0, 0)); // 2 of Clubs
        player.addCard(new Card(1, 0)); // 3 of Clubs

        Card newCard = new Card(2, 0);  // 4 of Clubs
        player.swapCard(0, newCard);    // Swap 2 of Clubs with 4 of Clubs

        // Ensure the card at index 0 has been swapped
        assertEquals(newCard, player.getHand()[0]);
        assertNotEquals(new Card(0, 0), player.getHand()[0]); // 2 of Clubs should not be in hand anymore
    }


}