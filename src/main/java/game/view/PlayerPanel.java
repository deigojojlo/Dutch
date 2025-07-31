package main.java.game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.java.util.SpriteUtil;

/**
 * A panel representing a player's card area and status in the game UI.
 * It displays the player's deck of cards, rotates based on seating,
 * and can indicate end-game status.
 */
public class PlayerPanel extends JPanel {

    private final int rotation;
    private ImageIcon backCardIcon;
    private final JPanel topPanel;
    private final JLabel label;
    private JPanel endGameIndicator;
    private final JPanel playerDeck = new JPanel(new GridLayout(2, 2, 5, 5));
    private final JButton[] deck = new JButton[4];

    /**
     * Creates a new PlayerPanel.
     * 
     * @param dimension    the size of this panel
     * @param rotation     rotation angle (0, 90, 180, 270) for seating orientation
     * @param backCardIcon the icon shown on the back of each card
     */
    PlayerPanel(Dimension dimension, int rotation, ImageIcon backCardIcon) {
        setLayout(new BorderLayout());
        setSize(dimension);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 2));

        this.rotation = rotation;
        this.backCardIcon = backCardIcon;

        playerDeck.setPreferredSize(getSize());
        playerDeck.setMinimumSize(getSize());
        playerDeck.setMaximumSize(getSize());
        playerDeck.setOpaque(false);

        topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        label = new JLabel("", SwingConstants.CENTER);
        label.setForeground(Color.BLACK);
        topPanel.add(label, BorderLayout.CENTER);

        initializeDeck();
        initializePlayerPanel();
    }

    /**
     * @return true if this panel is at the bottom (rotation 0°)
     */
    public boolean isBottomPlayerPanel() {
        return rotation == 0;
    }

    /**
     * @return true if this panel is on the left (rotation 90°)
     */
    public boolean isLeftPlayerPanel() {
        return rotation == 90;
    }

    /**
     * @return true if this panel is at the top (rotation 180°)
     */
    public boolean isTopPlayerPanel() {
        return rotation == 180;
    }

    /**
     * @return true if this panel is on the right (rotation 270°)
     */
    public boolean isRightPlayerPanel() {
        return rotation == 270;
    }

    /**
     * @return true if this panel is on either side (left or right)
     */
    public boolean isSidePlayerPanel() {
        return isLeftPlayerPanel() || isRightPlayerPanel();
    }

    private void initializeDeck() {
        for (int i = 0; i < 4; i++) {
            deck[i] = new JButton();
        }
        setDeckEnabled(false);
    }

    private void initializePlayerPanel() {
        switch (rotation) {
            case 0 -> bottomPlayerPanel();
            case 90 -> leftPlayerPanel();
            case 180 -> topPlayerPanel();
            case 270 -> rightPlayerPanel();
        }
    }

    private void configureCardButton(JButton card) {
        card.setOpaque(false);
        card.setFocusable(false);
        card.setFocusPainted(false);
        card.setBorderPainted(false);
        card.setContentAreaFilled(false);
        card.setIcon(backCardIcon);
        card.setDisabledIcon(backCardIcon);
        playerDeck.add(card);
    }

    private void bottomPlayerPanel() {
        add(topPanel, BorderLayout.NORTH);
        add(playerDeck, BorderLayout.CENTER);

        for (JButton card : deck) {
            configureCardButton(card);
        }
    }

    private void leftPlayerPanel() {
        setSize(getSize().height, getSize().width);
        playerDeck.setPreferredSize(getSize());
        add(topPanel, BorderLayout.EAST);
        add(playerDeck, BorderLayout.CENTER);

        backCardIcon = SpriteUtil.rotateFrame(backCardIcon, 90);
        for (JButton card : deck) {
            configureCardButton(card);
        }
    }

    private void topPlayerPanel() {
        add(topPanel, BorderLayout.SOUTH);
        add(playerDeck, BorderLayout.CENTER);

        backCardIcon = SpriteUtil.rotateFrame(backCardIcon, 180);
        for (JButton card : deck) {
            configureCardButton(card);
        }
    }

    private void rightPlayerPanel() {
        setSize(getSize().height, getSize().width);
        playerDeck.setPreferredSize(getSize());
        add(topPanel, BorderLayout.WEST);
        add(playerDeck, BorderLayout.CENTER);

        backCardIcon = SpriteUtil.rotateFrame(backCardIcon, 270);
        for (JButton card : deck) {
            configureCardButton(card);
        }
    }

    /**
     * @return the label used for player name or status
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * @return the array of card buttons representing the player's deck
     */
    public JButton[] getDeck() {
        return deck;
    }

    /**
     * Enables or disables the player's deck buttons.
     * 
     * @param flag true to enable clicking, false to disable
     */
    public void setDeckEnabled(boolean flag) {
        for (JButton card : deck) {
            card.setEnabled(flag);
            card.setFocusable(flag);
            card.requestFocus();
        }
    }

    /**
     * Adds an ActionListener to each card button in the deck.
     * 
     * @param actionListener the listener to add
     */
    public void addDeckActionListener(ActionListener actionListener) {
        for (JButton card : deck) {
            card.addActionListener(actionListener);
        }
    }

    /**
     * Finds the index of a card button in the deck.
     * 
     * @param card the button to locate
     * @return index (0–3) or -1 if not found
     */
    public int getIndex(Object card) {
        for (int i = 0; i < deck.length; i++) {
            if (deck[i] == card) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Shows an end-game indicator next to the player's label.
     */
    public void announceEndGame() {
        endGameIndicator = new JPanel();
        endGameIndicator.setOpaque(false);
        ImageIcon endGameIcon = new ImageIcon(SpriteUtil.loadIcon("ENDGAME.png").getImage()
                .getScaledInstance(15, 15, Image.SCALE_SMOOTH));

        JLabel endGameLabel = new JLabel(endGameIcon);
        endGameIndicator.add(endGameLabel);

        topPanel.add(endGameIndicator, BorderLayout.EAST);
        topPanel.revalidate();
        topPanel.repaint();
    }

    /**
     * Removes the end-game indicator if present.
     */
    public void removeEndGame() {
        if (endGameIndicator != null) {
            topPanel.remove(endGameIndicator);
        }
    }

    /**
     * Resets the deck's back-of-card icon to the original orientation.
     */
    public void resetDeckBackIcon() {

    }

    /**
     * @return rotation angle of this panel (0, 90, 180, 270)
     */
    public int getRotation() {
        return rotation;
    }
}