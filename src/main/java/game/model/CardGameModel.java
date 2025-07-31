package main.java.game.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * The model class for the card game.
 */
public class CardGameModel {

    /**
     * The draw stack of the game.
     */
    private ArrayList<CardModel> drawStack;

    /**
     * The discard stack of the game.
     */
    private LinkedList<CardModel> discardStack;

    /**
     * The random number generator for drawing cards.
     */
    private static final Random rand = new Random();
    private int size = 0;

    /**
     * Constructor for the card game model.
     * 
     * @param drawSize The size of the draw stack.
     */
    public CardGameModel(int drawSize) {
        if (drawSize < 1 && drawSize % 32 != 0 && drawSize % 52 != 0) {
            throw new IllegalArgumentException("Draw size must be a multiple of 32 or 52.");
        }
        size = drawSize;
        initDrawStack(drawSize);
        discardStack = new LinkedList<>();
    }

    /**
     * Check if the card is in a 32-card deck.
     * 
     * @param value The value of the card.
     * @return True if the card is in a 32-card deck, false otherwise.
     */
    private boolean isIn32Deck(CardModel.CardValue value) {
        return value.ordinal() == 0 || (value.ordinal() >= 6 && value.ordinal() <= 12);
    }

    /**
     * Initializes the draw stack of the game.
     * 
     * @param drawSize The size of the draw stack.
     */
    public void initDrawStack(int drawSize) {
        drawStack = new ArrayList<>();
        int numberOfDeck = (drawSize % 32 == 0) ? drawSize / 32 : drawSize / 52;
        for (int i = 0; i < numberOfDeck; i++) {
            for (CardModel.CardColor color : CardModel.CardColor.values()) {
                for (CardModel.CardValue value : CardModel.CardValue.values()) {
                    if (drawSize % 52 == 0 || (drawSize % 32 == 0 && isIn32Deck(value))) {
                        drawStack.add(new CardModel(color, value));
                    }
                }
            }
        }
    }

    /**
     * Draw a random card from the draw stack.
     * If only one card remains, moves all cards from discard to draw stack except
     * the top one.
     * 
     * @return The card drawn.
     */
    public CardModel drawCard() {
        if (drawStack.isEmpty()) {
            throw new IllegalStateException("Draw stack is empty.");
        }

        // If only one card remains, move cards from discard to draw
        if (drawStack.size() == 1) {
            CardModel lastCard = drawStack.removeFirst();

            CardModel discardTopCard = popDiscard();

            while (!discardStack.isEmpty()) {
                drawStack.add(discardStack.remove());
            }
            if (discardTopCard != null)
                discardStack.add(discardTopCard);

            return lastCard;
        }

        int index = rand.nextInt(drawStack.size());
        return drawStack.remove(index);
    }

    /**
     * Discard a card to the discard stack.
     * 
     * @param card The card to discard.
     */
    public void discardCard(CardModel card) {
        if (card != null) {
            card.setIsDrawedInDiscard(true);
            discardStack.add(card);
        }
    }

    /**
     * Get the top card of the discard stack.
     * 
     * @return The top card of the discard stack.
     */
    public CardModel getTopDiscard() {
        if (discardStack.size() >= 1)
            return discardStack.getLast();
        else
            return null;
    }

    /**
     * Get the 2nd to top card of the discard stack.
     * 
     * @return The 2nd to top card of the discard stack.
     */
    public CardModel get2ndTopDiscard() {
        if (discardStack.size() >= 2)
            return discardStack.get(discardStack.size() - 2);
        else
            return null;
    }

    /**
     * remove the elment at the top of discard
     * 
     * @return the poped element
     */
    public CardModel popDiscard() {
        if (!discardStack.isEmpty()) {
            CardModel cardModel = discardStack.removeLast();
            cardModel.setIsDrawedInDiscard(true);
            return cardModel;
        } else {
            return null;
        }
    }

    /**
     * Get the draw stack size.
     * 
     * @return The size of the draw stack.
     */
    public int getDrawStackSize() {
        return drawStack.size();
    }

    /**
     * Get the discard stack size.
     * 
     * @return The size of the discard stack.
     */
    public int getDiscardStackSize() {
        return discardStack.size();
    }

    /**
     * Check if the draw stack is empty.
     * 
     * @return True if the draw stack is empty, false otherwise.
     */
    public boolean isDrawStackEmpty() {
        return drawStack.isEmpty();
    }

    /**
     * Check if the discard stack is empty.
     * 
     * @return True if the discard stack is empty, false otherwise.
     */
    public boolean isDiscardStackEmpty() {
        return discardStack.isEmpty();
    }

    /**
     * 
     * @return the number of the orginal card game
     */
    public int getSize() {
        return size;
    }

    /**
     * Resets the game by moving all cards from the discard stack back to the draw stack.
     * The discard stack is emptied, and all its cards are added to the draw stack in the order they were discarded.
     * This method is typically used to reinitialize the draw stack when it runs out of cards.
     */
    public void reset() {
        while (!discardStack.isEmpty()) {
            drawStack.add(discardStack.remove());
        }
    }

    /**
     * Adds a single card to the draw stack.
     * This method allows a card to be manually added to the draw stack.
     * 
     * @param card The card to be added to the draw stack. It cannot be null.
     * @throws NullPointerException If the provided card is null.
     */
    public void addDrawStack(CardModel card) {
        drawStack.add(card);
    }
}