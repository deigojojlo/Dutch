package main.java.game.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.java.server.ClientHandler;
import main.java.util.Pair;

/**
 * The GameModel class represents the state of the card game, including player
 * management,
 * card handling, and game flow. It manages players, cards, scores, and
 * determines when the
 * game ends.
 */
public class GameModel {
    /** The card game */
    private CardGameModel cardGameModel;
    /** The queue of player */
    private final LinkedList<PlayerModel> playerQueue;

    /** The player who Announced The End */
    private PlayerModel playerWhoAnnouncedTheEnd;
    /** The number of round in the party */
    private int numberOfRound;
    /** The active player */
    private PlayerModel activePlayer;
    private CardModel activeCard;
    private final int numberOfPlayer;
    private boolean gameFinished;

    /**
     * Constructor of GameModel for local mode.
     * 
     * @param numberOfCard   a int which represent total number of cards in game.
     * @param numberOfPlayer a int which represent total number of players in game.
     * @param numberOfAi     a int which represent total number of AI in game.
     * @param difficulty     a int which represent the difficulty of AI players.
     */
    public GameModel(int numberOfCard, int numberOfPlayer, int numberOfAi, Integer difficulty) {
        this.cardGameModel = new CardGameModel(numberOfCard);
        this.numberOfPlayer = numberOfPlayer + numberOfAi;
        playerQueue = new LinkedList<>();
        for (int playerNumber = 0; playerNumber < numberOfPlayer; playerNumber++) {
            playerQueue.add(new PlayerModel(playerNumber));
        }
        for (int aiNumber = 0; aiNumber < numberOfAi; aiNumber++) {
            playerQueue.add(new PlayerComputerModel(numberOfPlayer + aiNumber, difficulty));
        }
    }

    /**
     * Constructor of GameModel for network mode.
     * 
     * @param numberOfCard a int which represent total number of cards in game.
     * @param players      a LinkedList<ClientHandler> which represent the list of
     *                     current player in game.
     * @param numberOfAi   a int which represent total number of AI in game.
     * @param difficulty   a int which represent the difficulty of AI players.
     */
    public GameModel(int numberOfCard, LinkedList<ClientHandler> players, int numberOfAi, Integer difficulty) {
        this.cardGameModel = new CardGameModel(numberOfCard);
        this.numberOfPlayer = players.size() + numberOfAi;
        gameFinished = false;
        playerQueue = new LinkedList<>();
        players.forEach(player -> addPlayer(player.getGameId(), player.getPseudo()));
        for (int aiNumber = 1; aiNumber <= numberOfAi; aiNumber++) {
            playerQueue.add(new PlayerComputerModel(playerQueue.getLast().getGameId() + 1, difficulty));
        }
        nextPlayer();
    }

    /**
     * Adds a new player to the game.
     * 
     * @param gameId   The game ID of the player.
     * @param username The username of the player.
     */
    public void addPlayer(int gameId, String username) {
        playerQueue.addLast(new PlayerModel(gameId));
    }

    /**
     * Gets the current active player.
     * 
     * @return The active player.
     */
    public PlayerModel getActivePlayer() {
        return activePlayer;
    }

    /**
     * Draws a card from the deck and sets it as the active card.
     * 
     * @return The card drawn from the deck.
     */
    public CardModel drawCard() {
        activeCard = cardGameModel.drawCard();
        return activeCard;
    }

    /**
     * Advances to the next player in the queue.
     */
    public void nextPlayer() {
        activePlayer = playerQueue.removeFirst();
        playerQueue.addLast(activePlayer);
    }

    /**
     * Retrieves a specific card from a player's deck.
     * 
     * @param clientId     The game ID of the player.
     * @param cardPosition The index position of the card in the player's deck.
     * @return The card at the specified position in the player's deck, or null if
     *         not found.
     */
    public CardModel getCardOf(byte clientId, byte cardPosition) {
        PlayerModel p = null;
        for (int i = 0; i < playerQueue.size(); i++) {
            if (playerQueue.get(i).getGameId() == clientId)
                p = playerQueue.get(i);
        }
        if (p == null)
            return null;
        return p.getCardInDeck(cardPosition);
    }

    /**
     * Sets the player who announced the end of the game.
     * 
     * @param player The player who announced the end.
     */
    public void setPlayerWhoAnnoncedTheEnd(PlayerModel player) {
        playerWhoAnnouncedTheEnd = player;
    }

    /**
     * Distribute 4 card at each player one by one
     * 
     */
    public void distribute() {
        for (int cardNumber = 1; cardNumber <= 4; cardNumber++) {
            for (PlayerModel player : playerQueue) {
                player.giveCardInDeck(cardGameModel.drawCard());
            }
        }
    }

    /**
     * Remove the player card in hand and deck
     * 
     */
    public void clearPlayerCard() {
        for (PlayerModel player : playerQueue) {
            player.clearCard(cardGameModel);
            if (player instanceof PlayerComputerModel pc) {
                pc.clearMemory();
            }
        }
    }

    /**
     * Add score to each player and check if the game is finished
     * 
     * @return true if this game is finish, false otherwise.
     */
    public boolean addScore() {
        applyPenaltyIfNeeded();

        boolean gameIsEnd = addScoresToPlayers();

        this.gameFinished = gameIsEnd;
        return gameIsEnd;
    }

    /**
     * Applies a penalty if the player who announced the end does not have the
     * lowest score in their deck.
     */
    private void applyPenaltyIfNeeded() {
        if (playerWhoAnnouncedTheEnd != null) {
            int playerWhoAnnouncedTheEndScore = playerWhoAnnouncedTheEnd.getScoreInDeck();
            int minScore = getMinScoreInDeck();

            if (playerWhoAnnouncedTheEndScore < minScore) {
                playerWhoAnnouncedTheEnd.addScore(10);
            }
        }
    }

    /**
     * Gets the minimum score among all players in the queue.
     *
     * @return the minimum score in the deck.
     */
    private int getMinScoreInDeck() {
        int minScore = Integer.MAX_VALUE;
        for (PlayerModel player : playerQueue) {
            int playerScore = player.getScoreInDeck();
            minScore = Math.min(minScore, playerScore);
        }
        return minScore;
    }

    /**
     * Adds score to each player and returns whether the game is finished.
     *
     * @return true if any player’s score exceeds 50, indicating the game has ended,
     *         false otherwise.
     */
    private boolean addScoresToPlayers() {
        boolean gameIsEnd = false;
        for (PlayerModel player : playerQueue) {
            gameIsEnd |= player.addScore(player.getScoreInDeck());
        }
        return gameIsEnd;
    }

    /**
     * Give the scoreboard
     */
    public List<Pair<Integer, Integer>> getScoreboard() {
        List<Pair<Integer, Integer>> scoreboard = new ArrayList<Pair<Integer, Integer>>();
        int index = 0;
        for (PlayerModel player : playerQueue) {
            scoreboard.add(index, new Pair<>(player.getGameId(), player.getScore()));
            index++;
        }
        return scoreboard;
    }

    /**
     * Gets the current decks of all players.
     * 
     * @return A LinkedList of pairs containing player IDs and their respective
     *         decks of cards.
     */
    public LinkedList<Pair<Integer, ArrayList<CardModel>>> getDecks() {
        LinkedList<Pair<Integer, ArrayList<CardModel>>> res = new LinkedList<>();
        playerQueue.forEach(player -> res.add(new Pair<>(player.getGameId(),
                player.getCardInDeck())));
        return res;
    }

    /**
     * Displays the key of a card from a player's deck based on the given index.
     *
     * @param player          The player whose deck is being accessed.
     * @param indexCardPlayer The index of the card to display.
     */
    public void seeCard(PlayerModel player, int indexCardPlayer) {
        System.out.println(player.getCardInDeck().get(indexCardPlayer));
    }

    /**
     * Swaps two cards between two players' decks at the specified indices.
     *
     * @param player1    The first player involved in the swap.
     * @param indexCard1 The index of the card in player1's deck to be swapped.
     * @param player2    The second player involved in the swap.
     * @param indexCard2 The index of the card in player2's deck to be swapped.
     */
    public void swapCard(PlayerModel player1, int indexCard1, PlayerModel player2, int indexCard2) {
        CardModel temp = player1.getCardInDeck().get(indexCard1);
        player1.getCardInDeck().set(indexCard1, player2.getCardInDeck().get(indexCard2));
        player2.getCardInDeck().set(indexCard2, temp);
    }

    /**
     * Swaps two cards between two players by their game IDs.
     * 
     * @param player1    The game ID of the first player.
     * @param indexCard1 The index of the card in player1's deck to be swapped.
     * @param player2    The game ID of the second player.
     * @param indexCard2 The index of the card in player2's deck to be swapped.
     */
    public void swapCard(int player1, int indexCard1, int player2, int indexCard2) {
        swapCard(intToPlayerModel(player1), indexCard1, intToPlayerModel(player2), indexCard2);
    }

    /**
     * Converts a player ID to a PlayerModel instance.
     * 
     * @param playerid The ID of the player.
     * @return The corresponding PlayerModel instance.
     */
    private PlayerModel intToPlayerModel(int playerid) {
        if (playerid <= getPlayerQueue().size()) {
            return getPlayerQueue()
                    .get((activePlayer.getGameId() - playerid + getPlayerQueue().size()) % getPlayerQueue().size());
        }
        return null;

    }

    /**
     * Restarts the game, resetting all game state.
     */
    public void restart() {
        playerWhoAnnouncedTheEnd = null;
        gameFinished = false;

        if (activeCard != null) {
            cardGameModel.addDrawStack(activeCard);
        }
        activeCard = null;

        clearPlayerCard();

        cardGameModel = new CardGameModel(104);

        numberOfRound = 0;
    }

    /**
     * Gets the card game model managing the deck and the cards.
     * 
     * @return The card game model.
     */
    public CardGameModel getCardGameModel() {
        return this.cardGameModel;
    }

    /**
     * Sets the card game model.
     * 
     * @param cardGameModel The {@link CardGameModel} to set.
     */
    public void setCardGameModel(CardGameModel cardGameModel) {
        this.cardGameModel = cardGameModel;
    }

    /**
     * Gets the player queue.
     * 
     * @return The queue of players.
     */
    public LinkedList<PlayerModel> getPlayerQueue() {
        return this.playerQueue;
    }

    /**
     * Gets the player who announced the end of the game.
     * 
     * @return The player who announced the end.
     */
    public PlayerModel getPlayerWhoAnnouncedTheEnd() {
        return this.playerWhoAnnouncedTheEnd;
    }

    /**
     * Sets the player who announced the end of the game.
     * 
     * @param playerWhoAnnouncedTheEnd The player to set.
     */
    public void setPlayerWhoAnnouncedTheEnd(PlayerModel playerWhoAnnouncedTheEnd) {
        this.playerWhoAnnouncedTheEnd = playerWhoAnnouncedTheEnd;
    }

    /**
     * Gets the current round number.
     * 
     * @return The round number.
     */
    public int getNumberOfRound() {
        return this.numberOfRound;
    }

    /**
     * Sets the current round number.
     * 
     * @param numberOfRound The round number to set.
     */
    public void setNumberOfRound(int numberOfRound) {
        this.numberOfRound = numberOfRound;
    }

    /**
     * Gets the card at the top of the discard pile.
     * 
     * @return The top discard card.
     */
    public CardModel getOnDiscard() {
        return cardGameModel.getTopDiscard();
    }

    /**
     * Gets the second card from the discard pile.
     * 
     * @return The second card from the discard pile.
     */
    public CardModel get2ndOnDiscard() {
        return cardGameModel.get2ndTopDiscard();
    }

    /**
     * Gets the total number of players in the game.
     * 
     * @return The total number of players.
     */
    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    /**
     * Checks if the game is finished.
     * 
     * @return true if the game is finished, false otherwise.
     */
    public boolean isGameFinished() {
        return gameFinished;
    }

    /**
     * 
     * @return the id of winner
     */
    public int getWinner() {
        List<Pair<Integer, Integer>> scoreboard = getScoreboard();
        int minScore = Integer.MAX_VALUE;
        int winnerId = -1;
        for (Pair<Integer, Integer> playerScore : scoreboard) {
            if (playerScore.getValue() < minScore) {
                minScore = playerScore.getValue();
                winnerId = playerScore.getKey();
            }
        }
        return winnerId;
    }
}