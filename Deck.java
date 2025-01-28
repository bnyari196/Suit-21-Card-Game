import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> deck = new ArrayList<Card>();

    public Deck() {
        for (int i = 0; i < 52; i++) {
            this.deck.add(new Card( i % 13, i / 13 ) );
        }
        Collections.shuffle(this.deck);
    }

    public Card deal() {
        if (this.deck.size() > 0){
            Card card = this.deck.get(0);
            this.deck.remove(0);
            return card;
        } else return null;
    }

    // Used to end the game whenever deck is empty, no points awarded
    public boolean isEmpty() {
        return this.deck.isEmpty();
    }

    public String toString() {
        String resultStr = "\n";
        for (Card card : deck) {
            resultStr += card + "\n";
        }
        return resultStr;
    }
}