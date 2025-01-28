import java.util.*;

public class SUIT21 {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    private static final int HAND_SIZE = 5;
    private static final double POINT = 1.0;

    private final ArrayList<Player> players;
    private final ArrayList<Player> roundWinners;
    private final HashMap<String, Double> playerPoints;
    private final GameHistory gameHistory;
    private Deck deck;

    private final Scanner scanner;

    private int roundCount;
    private int totalGames;


    public SUIT21() {
        // Instance variables
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.scanner = new Scanner(System.in);
        this.roundWinners = new ArrayList<>();
        this.roundCount = 0;
        this.playerPoints = new HashMap<>();
        this.gameHistory = new GameHistory();
    }

    public void startGame() {
        askNumberOfGames();
        setupPlayers();

        for (int gameNumber = 1; gameNumber <= totalGames; gameNumber++) {
            System.out.printf("\n*******************\n*** Game %d of %d ***\n*******************\n",
                    gameNumber, totalGames);
            setupGame();
            dealInitialCards();
            playGame();
            displayLeaderBoard();
        }

    }


    private void setupGame() {
        // Reset game state for the new game, only keep the scoreboard
        deck = new Deck();
        roundCount = 0;
        roundWinners.clear();
        gameHistory.clearHistory();

        // Reset player
        if (!players.isEmpty()) {
            for (Player player : players) {
                player.clearHand();
            }
        }
    }

    protected void askNumberOfGames() {
        while (true) {
            try {
                System.out.print("How many games? (1-5) >>> ");
                totalGames = Integer.parseInt(scanner.nextLine());

                // Check if the number of games is within the valid range
                if (totalGames < 1 || totalGames > 5) {
                    System.out.println("Please enter a number between 1 and 5.");
                } else {
                    break;  // Valid input, exit the loop
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    protected void setupPlayers() {
        while (true) {
            try {
                System.out.print("How many players? (2-6) >>> ");
                int numPlayers = Integer.parseInt(scanner.nextLine());

                // Check if the number of players is within the valid range
                if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
                    System.out.println("Please enter a number between 2 and 6.");
                } else {
                    // Proceed to add players
                    for (int i = 1; i <= numPlayers; i++) {
                        System.out.printf("Player %d name (or 'Computer' for AI player) >>> ", i);
                        String playerName = scanner.nextLine().trim();

                        // Check if the player name is not empty
                        if (playerName.isEmpty()) {
                            System.out.println("Player name cannot be empty. Please enter a valid name.");
                            i--;  // Retry this iteration for the current player
                            continue;
                        }

                        // If valid name, create player and add to the list
                        Player player = new Player(playerName);
                        players.add(player);
                        playerPoints.put(playerName, 0.0);
                    }
                    break;  // Exit the loop if input is valid
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number of players.");
            }
        }
    }

    protected void dealInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < HAND_SIZE; i++) {
                player.addCard(deck.deal());
            }
        }
    }

    private void playGame() {
        boolean twentyOneScored = false;
        roundWinners.clear();

        while (!deck.isEmpty() && !twentyOneScored) {
            roundCount++;
            //displayAllHands();

            for (Player player : players) {
                if (!deck.isEmpty()) {
                    processPlayerTurn(player);
                    waitForPlayerToContinue();

                    if (player.hasTwentyOne()) {
                        twentyOneScored = true;
                        roundWinners.add(player);
                    }

                }
            }
            if (twentyOneScored) {
                awardPoints();
            }
        }
        offerReplay();
    }

    private void awardPoints() {
        if (!roundWinners.isEmpty()) {
            // Split one point among all winners

            double pointShare = POINT / roundWinners.size();

            System.out.println("\n==== Points Awarded ====");
            for (Player winner : roundWinners) {
                double currentPoints = playerPoints.getOrDefault(winner.getName(), 0.0); // Defaults to 0.0 if not present
                playerPoints.put(winner.getName(), currentPoints + pointShare);

                System.out.printf("%s awarded %.2f point(s)%n", winner.getName(), pointShare);
            }
        }
    }

    private void displayLeaderBoard() {
        System.out.println("\n==== Total Scores ====");
        playerPoints.entrySet()
                .stream()
                .sorted(HashMap.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(entry -> System.out.printf("%-15s: %.2f points%n",
                        entry.getKey(), entry.getValue()));
    }

    private void processPlayerTurn(Player player) {
        Card[] initialHand = player.getHand(); // This creates a copy of the hand array

        displayPlayerTurn(player);

        if (player.getName().equalsIgnoreCase("Computer")) {
            processComputerTurn(player);
        } else {
            processHumanTurn(player);
        }

        System.out.println("\nUpdated Hand and Suit totals:");
        displayPlayerHandAndScores(player);

        System.out.println("\nTurn is over!\n");

        // Record the turn in history
        gameHistory.recordTurn(
                player.getName(),
                initialHand,
                player.getHand(),
                player.getSuitTotals(),
                player.getMaxSuitValue(),
                player.getDroppedCard(),
                player.getSwapCard(),
                roundCount
        );

    }

    private void processHumanTurn(Player player) {
        try {
            System.out.print("\nNominate a Card to swap (0-4): ");
            String input = scanner.nextLine().trim();  // Remove leading/trailing spaces
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a valid card index.");
                return;  // Skip turn if input is empty
            }

            int swapIndex = Integer.parseInt(input);

            // Check if the swap index is within the valid range
            if (swapIndex < 0 || swapIndex >= HAND_SIZE) {
                System.out.println("Invalid index. Please enter a number between 0 and 4.");
                return;  // Skip turn if the index is out of range
            }

            // Proceed with swapping the card
            player.swapCard(swapIndex, deck.deal());
            System.out.printf("\n%s replaced by %s\n", player.getDroppedCard(), player.getSwapCard());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number between 0 and 4.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred. Turn skipped.");
        }
    }

    private void processComputerTurn(Player player) {
        int lowestValueIndex = player.findLowestValueCardIndex();
        Card newCard = deck.deal();

        player.swapCard(lowestValueIndex, newCard);
        System.out.printf("\nCOMPUTER'S CHOICE -- %s replaced by %s\n", player.getDroppedCard(), player.getSwapCard());
    }

    private void displayPlayerTurn(Player player) {
        System.out.printf("\n\n==========================\n%s's turn (Round %d)\n==========================\n\n",
                player.getName(), roundCount);
        displayPlayerHandAndScores(player);
    }

    private void displayPlayerHandAndScores(Player player) {
        System.out.println("Hand: ");
        int cardIndex = 0;
        for (Card card : player.getHand()) {
            System.out.printf("%d: %s of %s\n", cardIndex, card.getRank(), Card.getSuitSymbol(card.getSuit())); //TODO: remove this if changed mind
            cardIndex++;
        }
        player.printSuitTotals();
        System.out.println("\nBest Score: " + player.getMaxSuitValue());
    }

    private void waitForPlayerToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void offerReplay() {
        System.out.print("\nView replay? y/n >>> ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y")) {
            gameHistory.displayReplay();
        }
    }

    protected ArrayList<Player> getPlayers(int index) {
        return players;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public static void main(String[] args) {
        SUIT21 game = new SUIT21();
        game.startGame();
    }
}
