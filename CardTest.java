import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card(12, 3); // Ace of Spades
    }

    @Test
    void testCardCreation() {
        assertEquals("Ace", card.getRank());
        assertEquals("Spades", card.getSuit());
    }

    @Test
    void testIsRoyalCard() {
        assertTrue(card.isRoyalCard("King"));
        assertTrue(card.isRoyalCard("Queen"));
        assertTrue(card.isRoyalCard("Jack"));
        assertFalse(card.isRoyalCard("Ace"));
        assertFalse(card.isRoyalCard("10"));
    }

    @Test
    void testGetSuitSymbol() {
        assertEquals("♠", Card.getSuitSymbol("Spades"));
        assertEquals("♥", Card.getSuitSymbol("Hearts"));
        assertEquals("♦", Card.getSuitSymbol("Diamonds"));
        assertEquals("♣", Card.getSuitSymbol("Clubs"));
        assertEquals("", Card.getSuitSymbol("Invalid"));
    }
}





