package main.java.game.model;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.ImageIcon;

import main.java.util.SpriteUtil;

public class CardModel {

    /**
     * 
     * Represents the four possible colors (suits) of a standard playing card.
     */
    public enum CardColor {
        HEART,
        DIAMOND,
        SPADE,
        CLUB;
    }

    /**
     * 
     * Represents the thirteen possible value of a standard playing card.
     */
    public enum CardValue {
        ACE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING;

        @Override
        public String toString() {
            return switch (this) {
                case ACE -> "ACE";
                case TWO -> "2";
                case THREE -> "3";
                case FOUR -> "4";
                case FIVE -> "5";
                case SIX -> "6";
                case SEVEN -> "7";
                case EIGHT -> "8";
                case NINE -> "9";
                case TEN -> "10";
                case JACK -> "JACK";
                case QUEEN -> "QUEEN";
                case KING -> "KING";
                default -> throw new IllegalArgumentException();
            };
        }
    }

    /**
     * The color (suit) of the card.
     */
    private CardColor color;

    /**
     * The value (rank) of the card, such as ACE, TWO, KING, etc.
     */
    private CardValue value;

    /**
     * The number of points this card is worth.
     */
    private int point;

    /**
     * Indicates whether the card has been drawn into the discard pile.
     */
    private boolean isDrawedInDiscard;

    /**
     * A cache of buffered images for each card to avoid reloading from disk.
     */
    private static final Map<CardModel, BufferedImage> BUFFEREDS = initializeBuffered();

    /**
     * A cache of icons for each card to avoid reloading from disk.
     */
    private static final Map<CardModel, ImageIcon> ICONS = initializeIcons();

    /**
     * The shared image representing the back of a card.
     */
    private static final ImageIcon BACK_CARD = initializeBackImage();

    /**
     * The shared image used for an empty or placeholder card.
     */
    private static final ImageIcon BLANK_CARD = initializeBlankImage();

    /**
     * Constructer of CardModel.
     * 
     * @param color Couleur de la carte (HEART, DIAMOND, SPADE, CLUB).
     * @param value Valeur de la carte (ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN,
     *              EIGHT, NINE, TEN, JACK, QUEEN, KING).
     */
    public CardModel(CardColor color, CardValue value) {
        this.color = color;
        this.value = value;
        initializePoint();
    }

    /**
     * Constructer of CardModel.
     * 
     * @param color a int which represents the position in CardColor declaration (0
     *              = HEART, ... , 3 = CLUB).
     * @param value a int which represents the position in CardValue declaration (0
     *              = ACE, ... , 12 = KING).
     */
    public CardModel(int color, int value) {
        if (0 <= color && color < 4 && 0 <= value && value < 13) {
            this.color = CardColor.values()[color];
            this.value = CardValue.values()[value];
            initializePoint();
        } else {
            throw new IllegalArgumentException("color : " + color + " value : " + value);
        }
    }

    /**
     * @return card color.
     */
    public CardColor getColor() {
        return color;
    }

    /**
     * @return card value.
     */
    public CardValue getValue() {
        return value;
    }

    /**
     * @return the number of points this card is worth.
     */
    public int getPoint() {
        return point;
    }

    /**
     * @return true if card has been drawn into the discard pile, false otherwise.
     */
    public boolean getIsDrawedInDiscard() {
        return this.isDrawedInDiscard;
    }

    /**
     * 
     * @return card BufferedTmage.
     */
    public BufferedImage getBufferedImge() {
        return BUFFEREDS.get(this);
    }

    /**
     * 
     * @return card ImageIcon.
     */
    public ImageIcon getImageIcon() {
        return ICONS.get(this);
    }

    /**
     * 
     * @return a back of a card ImageIcon
     */
    public static ImageIcon getBackCard() {
        return BACK_CARD;
    }

    /**
     * 
     * @return empty card ImageIcon
     */
    public static ImageIcon getBlankCard() {
        return BLANK_CARD;
    }

    public CardModel setIsDrawedInDiscard(boolean isDrawedInDiscard) {
        this.isDrawedInDiscard = isDrawedInDiscard;
        return this;
    }

    /**
     * @return true if the card color is a heart, false otherwise.
     */
    public boolean isHeart() {
        return color == CardColor.HEART;
    }

    /**
     * @return true if the card color is a diamond, false otherwise.
     */
    public boolean isDiamond() {
        return color == CardColor.DIAMOND;
    }

    /**
     * @return true if the card color is a spade, false otherwise.
     */
    public boolean isSpade() {
        return color == CardColor.SPADE;
    }

    /**
     * @return true if the card color is a club, false otherwise.
     */
    public boolean isCLUB() {
        return color == CardColor.CLUB;
    }

    /**
     * @return true if the card color is red (heart or diamond), false otherwise.
     */
    public boolean isRed() {
        return isHeart() || isDiamond();
    }

    /**
     * Initialize the card's point value based on its value / rank.
     */
    private void initializePoint() {
        switch (value) {
            case ACE -> point = 1;
            case TWO -> point = 2;
            case THREE -> point = 3;
            case FOUR -> point = 4;
            case FIVE -> point = 5;
            case SIX -> point = 6;
            case SEVEN -> point = 7;
            case EIGHT -> point = 8;
            case NINE -> point = 9;
            case TEN -> point = 10;
            case JACK -> point = 10;
            case QUEEN -> point = 10;
            case KING -> {
                if (isRed()) {
                    point = 0;
                } else {
                    point = 15;
                }
            }
            default -> throw new IllegalArgumentException();
        }
    }

    /**
     * Initializes a map containing a buffered image for each unique card.
     * The image is loaded using the card's color and value as part of the filename.
     *
     * @return a map associating each {@code CardModel} to its corresponding
     *         {@code BufferedImage}.
     */
    private static HashMap<CardModel, BufferedImage> initializeBuffered() {
        HashMap<CardModel, BufferedImage> buffereds = new HashMap<>();
        CardModel card;
        for (CardColor color : CardColor.values()) {
            for (CardValue value : CardValue.values()) {
                card = new CardModel(color, value);
                buffereds.put(card, SpriteUtil.loadBufferedImage(color.toString() + "S", card.nameOfImage()));
            }
        }
        return buffereds;
    }

    /**
     * Initializes a map containing an image icon for each unique card.
     * Icons are created from the previously loaded buffered images in
     * {@code BUFFEREDS}.
     *
     * @return a map associating each {@code CardModel} to its corresponding
     *         {@code ImageIcon}.
     */
    private static HashMap<CardModel, ImageIcon> initializeIcons() {
        HashMap<CardModel, ImageIcon> icons = new HashMap<>();
        CardModel card;
        for (CardColor color : CardColor.values()) {
            for (CardValue value : CardValue.values()) {
                card = new CardModel(color, value);
                icons.put(card, new ImageIcon(BUFFEREDS.get(card)));
            }
        }
        return icons;
    }

    /**
     * Loads the default back-side image used for cards.
     *
     * @return an {@code ImageIcon} representing the back of a card.
     */
    private static ImageIcon initializeBackImage() {
        return new ImageIcon(SpriteUtil.loadIcon("CARD_BACK", "RED_CARD_BACK.png").getImage());
    }

    /**
     * Loads a blank image used as a placeholder or for empty card slots.
     *
     * @return an {@code ImageIcon} representing a blank card.
     */
    private static ImageIcon initializeBlankImage() {
        return new ImageIcon(SpriteUtil.loadIcon("CARD_BACK", "BLANK_CARD.png").getImage());
    }

    /**
     * 
     * @return {true if the card has power and has not been drawn into the discard
     *         pile false otherwise
     */
    public boolean hasPower() {
        return switch (this.value) {
            case SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING -> !isDrawedInDiscard;
            default -> false;
        };
    }

    /**
     * @return the name of the image associated with card
     */
    public String nameOfImage() {
        return color.toString() + "S_" + value.toString() + ".png";
    }

    @Override
    public String toString() {
        return "Couleur : " + color + "\nValeur : " + value + "\nPoint : " + point;
    }

    /**
     * Checks whether this card is equal to another object.
     * Two cards are considered equal if they have the same color and value.
     *
     * @param obj the object to compare with.
     * @return true if the given object is a CardModel with the same color and
     *         value; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardModel) {
            CardModel card2 = (CardModel) obj;
            return this.color == card2.color && this.value == card2.value;
        }
        return false;
    }

    /**
     * Returns a hash code based on the card's color and value.
     *
     * @return the hash code of this card.
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }
}