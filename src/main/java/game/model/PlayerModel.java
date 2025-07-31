package main.java.game.model;

import java.util.ArrayList;

import main.java.storage.Storage;

/**
 * Represents a player in the game, managing their deck, hand, and score.
 */
public class PlayerModel {

    /**
     * The id of the player.
     */
    private final int gameId;

    /**
     * The score of the player.
     */
    private int score;

    /**
     * The deck of 4 card.
     */
    private ArrayList<CardModel> deck;

    /**
     * The card in hand.
     */
    protected CardModel hand;

    /**
     * Username of the player.
     */
    private String username;

    /**
     * The statement of the player.
     */
    protected boolean isReady;

    /**
     * Constructs a PlayerModel with a given ID.
     * Initializes the score to 0 and creates an empty deck.
     *
     * @param gameId The unique identifier of the player.
     */
    public PlayerModel(int gameId) {
        this.gameId = gameId;
        this.score = 0;
        this.deck = new ArrayList<>();
        this.hand = null;
        this.username = "" + gameId;
    }

    /**
     * Adds a card to the player's deck.
     *
     * @param card The card to be added to the deck.
     */
    public void giveCardInDeck(CardModel card) {
        deck.add(card);
    }

    /**
     * Places a card into the player's hand.
     *
     * @param card The card to be placed in hand.
     */
    public void giveCardInHand(CardModel card) {
        hand = card;
    }

    /**
     * Replaces a card in the player's deck with a new card and returns the old
     * card.
     *
     * @param cardPosition The position of the card to be replaced.
     * @param newCard      The new card to be placed in the deck.
     * @return The card that was replaced.
     */
    public CardModel switchCard(int cardPosition, CardModel newCard) {
        CardModel temp = deck.get(cardPosition);
        deck.set(cardPosition, newCard);
        return temp;
    }

    /**
     * Retrieves the card currently in the player's hand.
     *
     * @return The card in the player's hand.
     */
    public CardModel getCardInHand() {
        return hand;
    }

    /**
     * Sets a card at a specific position in the player's deck.
     *
     * @param position  The index in the deck where the card should be placed.
     * @param cardModel The card to place in the deck.
     */
    public void setCardInDeck(int position, CardModel cardModel) {
        this.deck.set(position, cardModel);
    }

    /**
     * Clears the card currently in the player's hand and discards it.
     * The card is passed to the discard method of the CardGameModel.
     *
     * @param cardGameModel The CardGameModel instance used to discard the card.
     */
    public void clearCardInHand(CardGameModel cardGameModel) {
        cardGameModel.discardCard(hand);
        hand = null;
    }

    /**
     * Clears all the cards from the player's deck and discards them.
     * It also clears the player's hand
     * 
     * @param cardGameModel The CardGameModel instance used to discard each card
     *                      from the deck and hand.
     */
    public void clearCard(CardGameModel cardGameModel) {
        deck.forEach(carte -> cardGameModel.discardCard(carte));
        deck.clear();
        clearHand(cardGameModel);
    }

    /**
     * Clears the card in the player's hand and sends it to the discard pile.
     *
     * @param cardGameModel The game model used to access the discard mechanism.
     */
    public void clearHand(CardGameModel cardGameModel) {
        cardGameModel.discardCard(hand);
        hand = null;
    }

    /**
     * Adds a given score to the player's total score.
     *
     * @param newScore The score to be added.
     * @return True if the total score exceeds 50, otherwise false.
     */
    public boolean addScore(int newScore) {
        score += newScore;
        return score > Storage.ENDGAME_SCORE;
    }

    /**
     * Resets the player's score to 0.
     */
    public void resetScore() {
        score = 0;
    }

    /**
     * Retrieves the player's current score.
     *
     * @return The current score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * 
     * @return the list of card in the deck
     */
    public ArrayList<CardModel> getCardInDeck() {
        return deck;
    }

    /**
     * 
     * @param position the position of the wanted card
     * @return the card at `position` in the deck
     */
    public CardModel getCardInDeck(int position) {
        return deck.get(position);
    }

    /**
     * Calculates the total score of the cards in the player's deck.
     *
     * @return The sum of points from all cards in the deck.
     */
    public int getScoreInDeck() {
        int sum = 0;
        for (CardModel card : deck) {
            sum += card.getPoint();
        }
        return sum;
    }

    /**
     * getter for Player's username
     * 
     * @return The username of the player
     */

    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the unique game identifier of the player.
     *
     * @return The unique game ID of the player.
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * getter for isReady
     * 
     * @return boolean to check if the user is ready
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * toggle the isReady boolean.
     */
    public void togglePlayerReady() {
        isReady = !isReady;
    }
}
