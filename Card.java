import java.util.Random;

public class Card {

    private int rank;
    private int suit;

    private static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10",
            "Jack", "Queen", "King", "Ace"};

    private static String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};

    public Card() {
        Random random = new Random();
        this.rank = random.nextInt(Card.ranks.length);
        this.suit = random.nextInt(Card.suits.length);
    }

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return Card.ranks[this.rank];
    }

    public int getRankValue() {
        return this.rank;
    }

    public String getSuit() {
        return Card.suits[this.suit];
    }

    public boolean isBiggerThan(Card anotherCard) {
        return this.rank > anotherCard.rank;
    }

    public boolean isRoyalCard(String rank) {
        return rank.equals("King") || rank.equals("Queen") || rank.equals("Jack");
    }

    public String toString() {
        return getRank() + " of " + getSuit();
    }

    public static String getSuitSymbol(String suitName) {
        switch (suitName) {
            case "Spades":
                return "♠";
            case "Hearts":
                return "♥";
            case "Diamonds":
                return "♦";
            case "Clubs":
                return "♣";
            default:
                return ""; // Default case, in case of an invalid suit
        }
    }
}
