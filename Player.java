import java.util.Arrays;
import java.util.HashMap;

public class Player {
    private final String name;
    private final Card[] hand;
    private boolean hasTwentyOne;

    private static final int HAND_SIZE = 5;
    private static final int ACE_HIGH = 11;
    private static final int ACE_LOW = 1;
    private static final int FACE_CARD_VALUE = 10;
    private int cardCount;
    private int highestRank;

    private Card droppedCard;
    private Card swapCard;

    private final HashMap<String, Integer> suitTotals;


    public Player(String name) {
        this.name = name;
        this.hand = new Card[HAND_SIZE];
        this.hasTwentyOne = false;
        this.suitTotals = new HashMap<>();
        this.highestRank  = 0;
    }

    public String getName() {
        return name;
    }

    public Card[] getHand() {
        return Arrays.copyOf(hand, HAND_SIZE);
    }

    public void addCard(Card card) {
        if (cardCount >= HAND_SIZE) {
            throw new IllegalStateException("Hand is full");
        }
        hand[cardCount++] = card;
    }

    public void clearHand() {
        Arrays.fill(hand, null);
        cardCount = 0;
        hasTwentyOne = false;
    }

    public int getCardValue(Card card, int currentSuitTotal) {
        int aceCount = 0;
        String rank = card.getRank();

        // Dynamic Ace Logic
        if (rank.equals("Ace")) {
            return (currentSuitTotal + ACE_HIGH <= 21) ? ACE_HIGH : ACE_LOW; // Default ace value 11
        }
        if (card.isRoyalCard(rank)) return FACE_CARD_VALUE;

        return Integer.parseInt(rank); // 2-10
    }

    public String getMaxSuitValue() {
        updateSuitTotals(); // Ensure suit totals are updated before determining the best score

        // Find the suit with the highest total value
        String highestSuit = null;
        highestRank = 0; // Reset highest rank to ensure it's recalculated

        for (HashMap.Entry<String, Integer> entry : suitTotals.entrySet()) {
            String suit = entry.getKey();
            int total = entry.getValue();

            // If this suit's total is higher than the current highest, update
            if (total > highestRank) {
                highestRank = total;
                highestSuit = suit;
            }
        }

        // Display "21 of [Suit]" if 21 is reached
        if (highestRank == 21) {
            hasTwentyOne = true;
            return "21 of " + highestSuit + " (You win!)";
        }

        // If no 21, return the best score and suit
        return highestRank + " of " + highestSuit;
    }

    public int findLowestValueCardIndex() {
        updateSuitTotals();

        // Step 1: Check if dropping any card results in 21
        for (int i = 0; i < HAND_SIZE; i++) {
            if (hand[i] == null) continue;

            Card cardToRemove = hand[i];
            String suit = cardToRemove.getSuit();
            int suitTotal = suitTotals.getOrDefault(suit, 0);

            // Calculate adjusted suit total dynamically
            int adjustedSuitTotal = calculateAdjustedSuitTotalAfterRemoval(cardToRemove, suitTotal);

            if (adjustedSuitTotal == 21) {
                return i; // Immediately return as this is the optimal move
            }
        }

        // Step 2: Handle suits with totals over 21
        int bestCardIndex = -1;
        int closestTo21 = Integer.MAX_VALUE; // Difference between adjusted total and 21

        for (String suit : suitTotals.keySet()) {
            int suitTotal = suitTotals.get(suit);

            if (suitTotal > 21) {
                for (int i = 0; i < HAND_SIZE; i++) {
                    if (hand[i] == null || !hand[i].getSuit().equals(suit)) continue;

                    Card cardToRemove = hand[i];

                    // Use the adjusted method here as well
                    int adjustedSuitTotal = calculateAdjustedSuitTotalAfterRemoval(cardToRemove, suitTotal);

                    if (adjustedSuitTotal <= 21 && 21 - adjustedSuitTotal < closestTo21) {
                        bestCardIndex = i;
                        closestTo21 = 21 - adjustedSuitTotal;
                    }
                }
            }
        }

        if (bestCardIndex != -1) {
            return bestCardIndex; // Return the best card to drop for suits over 21
        }

        // Step 3: Drop the "least valuable" card based on scoring
        int worstCardIndex = -1;
        double worstCardScore = Double.MAX_VALUE;

        for (int i = 0; i < HAND_SIZE; i++) {
            if (hand[i] == null) continue;

            Card card = hand[i];
            String suit = card.getSuit();
            int suitTotal = suitTotals.getOrDefault(suit, 0);
            boolean isAce = card.getRank().equals("Ace");

            double cardScore = calculateCardValue(card, suitTotal, isAce);

            if (cardScore < worstCardScore) {
                worstCardScore = cardScore;
                worstCardIndex = i;
            }
        }

        return worstCardIndex; // Return the "least valuable" card index
    }

    private double calculateCardValue(Card card, int suitTotal, boolean isAce) {
        double score = 100.0; // Base score

        int cardValueCheck = getCardValue(card, suitTotal);
        int adjustedSuitTotal = suitTotal - cardValueCheck;

        // If removing this card results in a suit total of exactly 21, make it the highest priority
        if (adjustedSuitTotal == 21) {
            return Double.NEGATIVE_INFINITY;
        }

        // Determine the card's value (important for face cards and numbered cards)
        int cardValue = 0;
        String rank = card.getRank();

        // Handle face cards (Jack, Queen, King)
        if (card.isRoyalCard(rank)) {
            cardValue = FACE_CARD_VALUE;  // Assign value 10 for Royal Cards
        }
        // Handle Aces
        else if (rank.equals("Ace")) {
            cardValue = ACE_HIGH; // Default Ace value is 11
        }
        // For numbered cards, parse the number
        else {
            cardValue = Integer.parseInt(rank);
        }

        // Aces are valuable due to flexibility
        if (isAce) {
            score += 50;
            if (suitTotal <= 10) {
                score += 25; // Aces more useful in suits far from 21
            }

        }

        // Favour cards in suits close to 21
        if (suitTotal >= 16 && suitTotal < 21) {
            score += 40; // These cards are critical for achieving 21
        }

        // Assign higher penalty to cards in the suit with the highest total
        if (suitTotal == highestRank) {
            score += 10;

        }

        // Cards in suits with very low totals are less valuable
        if (suitTotal < 10) {
            score -= 20; // Favour dropping these cards

        }

        // Penalise cards in suits with totals above 21
        if (suitTotal > 21) {
            score -= 70;

        }

        // Add card value as a factor for scoring
        score += cardValue;

        // debugging print statement
        //System.out.println("Final score for " + card + ": " + score);

        return score;
    }

    private void updateSuitTotals() {
        suitTotals.clear(); // Reset totals
        // Initialize all 4 suits
        suitTotals.put("Hearts", 0);
        suitTotals.put("Diamonds", 0);
        suitTotals.put("Clubs", 0);
        suitTotals.put("Spades", 0);

        // First pass: calculate non-Ace values
        for (Card card : hand) {
            if (card != null && !card.getRank().equals("Ace")) {
                String suit = card.getSuit();
                suitTotals.merge(suit, getCardValue(card, 0), Integer::sum);
            }
        }

        // Second pass: add Aces with dynamic values
        for (Card card : hand) {
            if (card != null && card.getRank().equals("Ace")) {
                String suit = card.getSuit();
                int currentTotal = suitTotals.getOrDefault(suit, 0);

                // Use the helper method for Ace value adjustment
                int aceValue = getAdjustedAceValue(currentTotal);
                suitTotals.put(suit, currentTotal + aceValue);
            }
        }
    }

    private int getAdjustedAceValue(int currentTotal) {
        return (currentTotal + ACE_HIGH <= 21) ? ACE_HIGH : ACE_LOW;
    }

    private int calculateAdjustedSuitTotalAfterRemoval(Card cardToRemove, int suitTotal) {
        int cardValue = getCardValue(cardToRemove, suitTotal);
        int adjustedTotal = suitTotal - cardValue;

        // Reapply Ace logic to dynamically adjust the suit total
        if (adjustedTotal + ACE_HIGH <= 21 && cardToRemove.getRank().equals("Ace")) {
            adjustedTotal += ACE_HIGH - ACE_LOW; // Reassign the Ace as high
        } else if (adjustedTotal <= 21 && suitTotal > 21) {
            // Check if reducing the suit total allows another Ace to become high
            adjustedTotal += 10; // Account for other Aces becoming high
        }

        return adjustedTotal;
    }

    public void printSuitTotals() {
        updateSuitTotals(); // Ensure totals are up to date
        System.out.println("\nYour suit scores:");
        for (HashMap.Entry<String, Integer> entry : suitTotals.entrySet()) {
            System.out.printf("%s: %d, ", entry.getKey(), entry.getValue());
        }
    }

    public void swapCard(int handIndex, Card newCard) {
        droppedCard = hand[handIndex]; // Store the dropped card
        swapCard = newCard;           // Store the new card
        hand[handIndex] = newCard;    // Replace the card in the hand
    }

    public boolean hasTwentyOne(){
        return hasTwentyOne;
    }

    public Card getDroppedCard() {
        return droppedCard;
    }

    public Card getSwapCard() {
        return swapCard;
    }

    public HashMap<String, Integer> getSuitTotals() {
        return suitTotals;
    }

    public String toString() {
        // TODO: turn this into a stringbuilder - more efficient
        return name + "'s hand: " + hand.toString(); // Display player name, hand, dropped Card
    }
}

