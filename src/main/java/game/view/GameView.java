package main.java.game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import main.java.game.model.CardModel;
import main.java.game.model.GameModel;
import main.java.game.model.PlayerModel;
import main.java.ourjcomponent.BoardPanel;
import main.java.ourjcomponent.RoundedButton;
import main.java.storage.Storage;
import main.java.style.ViewStyle;
import main.java.util.SoundUtil;
import main.java.util.SpriteUtil;

public class GameView extends JPanel {
    private GameModel gameModel;
    public BoardPanel boardPanel;
    public int nbOfPlayer;
    private PlayerPanel[] players;
    private RoundedButton exitButton;
    private RoundedButton endGameButton;
    private JButton pickPileButton;
    private JButton discardPileButton;
    private JFrame frame;
    private int panelWidth;
    private int panelHeight;
    private static double AnimSpeed = 1.5;
    private static boolean GameMusic = true;
    private ImageIcon cardBackIcon;
    private ImageIcon cardBlankIcon;

    /**
     * The online constructor of the GameView setting the visuals of the gameboard
     * 
     * @param frame       the root JFrame used to set the view in and get the window
     *                    size
     * @param playerOrder the order in which the players have joined the waiting
     *                    room
     * @param usernames   the list of usernames corresponding to the players
     */
    public GameView(JFrame frame, ArrayList<Byte> playerOrder, ArrayList<String> usernames) {
        if (playerOrder.size() < 2 || playerOrder.size() > 10) {
            throw new IllegalArgumentException("Nombre de joueur invalide.");
        }

        this.frame = frame;
        panelWidth = frame.getWidth() / 6;
        panelHeight = 16 * (frame.getHeight() - SettingsView.getTitleBarHeight()) / 60;

        this.setLayout(new BorderLayout());

        cardBackIcon = new ImageIcon(CardModel.getBackCard().getImage().getScaledInstance(panelWidth / 3 - 5,
                panelHeight / 2 - 5, Image.SCALE_SMOOTH));

        cardBlankIcon = new ImageIcon(CardModel.getBlankCard().getImage().getScaledInstance(panelWidth / 3 - 5,
                panelHeight / 2 - 5, Image.SCALE_SMOOTH));

        this.boardPanel = new BoardPanel(
                new Dimension(frame.getWidth(), frame.getHeight() - SettingsView.getTitleBarHeight()));
        this.boardPanel.setPreferredSize(
                new Dimension(frame.getWidth(), frame.getHeight() - SettingsView.getTitleBarHeight()));

        this.nbOfPlayer = playerOrder.size();
        this.players = new PlayerPanel[nbOfPlayer];

        exitButton = new RoundedButton("Quitter (Attendez Votre Tour)");
        exitButton.setEnabled(false);

        styleButton(exitButton);
        exitButton.setBounds(25, 25, 200, 25);

        this.add(exitButton);

        initializePlayer(playerOrder, usernames);
        initializeGameButtons();
        addToBoard();

        if (playerOrder.get(0) == 0) {
            System.out.println(playerOrder);
            getPlayer(playerOrder.get(0)).setBorder(new LineBorder(Color.black, 10));
        }

        add(boardPanel);
        boardPanel.setOpaque(false);

        revalidate();
        repaint();
    }

    /**
     * The online constructor of the GameView setting the visuals of the gameboard
     * 
     * @param frame      the root JFrame used to set the view in and get the window
     *                   size
     * @param nbOfPlayer the total number of players, AIs included
     */
    public GameView(JFrame frame, int nbOfPlayer) {
        if (nbOfPlayer < 2 || nbOfPlayer > 10) {
            throw new IllegalArgumentException("Nombre de joueur invalide.");
        }

        /* frame size */
        this.frame = frame;
        panelWidth = frame.getWidth() / 6;
        panelHeight = 16 * (frame.getHeight() - SettingsView.getTitleBarHeight()) / 60;

        this.setLayout(new BorderLayout());

        cardBackIcon = new ImageIcon(CardModel.getBackCard().getImage().getScaledInstance(panelWidth / 3 - 5,
                panelHeight / 2 - 5, Image.SCALE_SMOOTH));

        cardBlankIcon = new ImageIcon(CardModel.getBlankCard().getImage().getScaledInstance(panelWidth / 3 - 5,
                panelHeight / 2 - 5, Image.SCALE_SMOOTH));

        this.boardPanel = new BoardPanel(
                new Dimension(frame.getWidth(), frame.getHeight() - SettingsView.getTitleBarHeight()));
        this.boardPanel.setPreferredSize(
                new Dimension(frame.getWidth(), frame.getHeight() - SettingsView.getTitleBarHeight()));

        /* value initialisation */
        this.nbOfPlayer = nbOfPlayer;
        this.players = new PlayerPanel[nbOfPlayer];

        exitButton = new RoundedButton("Quitter (Attendez Votre Tour)");
        exitButton.setEnabled(false);

        styleButton(exitButton);
        exitButton.setBounds(25, 25, 200, 25);

        this.add(exitButton);

        initializePlayer();
        initializeGameButtons();
        addToBoard();

        add(boardPanel);
        boardPanel.setOpaque(false);

        revalidate();
        repaint();
    }

    /**
     * Styles the button to specify it's visuals
     * 
     * @param button the button on which to apply the visuals
     */
    private void styleButton(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setBorder(null);
        button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
        button.setForeground(ViewStyle.LIGHT_TEXT_COLOR);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(Color.DARK_GRAY);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
                    button.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
                }
            }
        });
        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ViewStyle.TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
                }
            }
        });
    }

    /**
     * Initializes the player panels inside the gameboard for online mode
     * 
     * @param playerOrder the order in which the players have joined the waiting
     *                    room
     * @param usernames   the list of usernames corresponding to the players
     */
    private void initializePlayer(ArrayList<Byte> playerOrder, ArrayList<String> usernames) {
        for (int i = 0; i < nbOfPlayer; i++) {
            String playerName = usernames.get(i);
            players[i] = new PlayerPanel(new Dimension(panelWidth, panelHeight), getPlayerRotation(i), cardBackIcon);
            players[i].getLabel().setText(playerName);
        }
    }

    /**
     * Initializes the player panels inside the gameboard for offline mode
     */
    private void initializePlayer() {
        this.players[0] = new PlayerPanel(new Dimension(panelWidth, panelHeight), getPlayerRotation(0), cardBackIcon);
        this.players[0].getLabel().setText("joueur 1");
        for (int i = 1; i < nbOfPlayer; i++) {
            this.players[i] = new PlayerPanel(new Dimension(panelWidth, panelHeight), getPlayerRotation(i),
                    cardBackIcon);
            this.players[i].getLabel().setText("IA " + i);
        }
    }

    /**
     * 
     * @param playerIndex the player ID from the gameView
     * @return the angle of the player panel depending on his ID/location
     */
    private int getPlayerRotation(int playerIndex) {
        return switch (nbOfPlayer) {
            case 2, 3 -> playerIndex == 0 ? 0 : (playerIndex + 1) * 90;
            case 4 -> playerIndex * 90;
            case 5 -> playerIndex == 0 ? 0 : playerIndex < 2 ? 90 : playerIndex < 4 ? 180 : 270;
            case 6 -> playerIndex < 2 ? 0 : playerIndex < 3 ? 90 : playerIndex < 5 ? 180 : 270;
            case 7 -> playerIndex < 2 ? 0 : playerIndex < 3 ? 90 : playerIndex < 6 ? 180 : 270;
            case 8 -> (playerIndex / 2) * 90;
            case 9 -> playerIndex < 2 ? 0 : playerIndex < 4 ? 90 : playerIndex < 7 ? 180 : 270;
            case 10 -> playerIndex < 3 ? 0 : playerIndex < 5 ? 90 : playerIndex < 8 ? 180 : 270;
            default -> 0;
        };
    }

    /**
     * Initializes the in game buttons, the pick pile, the discard pile and the
     * announce the end buttons
     */
    private void initializeGameButtons() {
        this.endGameButton = new RoundedButton("Fin de la partie");
        this.endGameButton.setPreferredSize(new Dimension(panelWidth * 3 / 4, panelHeight / 10));
        styleButton(this.endGameButton);

        this.pickPileButton = new JButton();
        this.pickPileButton.setIcon(cardBackIcon);
        this.pickPileButton.setDisabledIcon(cardBackIcon);
        this.pickPileButton.setPreferredSize(new Dimension(panelWidth / 3, panelHeight / 2));
        this.pickPileButton.setOpaque(false);
        this.pickPileButton.setFocusPainted(false);
        this.pickPileButton.setBorderPainted(false);
        this.pickPileButton.setContentAreaFilled(false);

        this.discardPileButton = new JButton();
        this.discardPileButton.setIcon(cardBlankIcon);
        this.discardPileButton.setDisabledIcon(cardBlankIcon);
        this.discardPileButton.setPreferredSize(new Dimension(panelWidth / 3, panelHeight / 2));
        this.discardPileButton.setOpaque(false);
        this.discardPileButton.setFocusPainted(false);
        this.discardPileButton.setBorderPainted(false);
        this.discardPileButton.setContentAreaFilled(false);
    }

    /**
     * Adds the player panels to the correct side panel depending on the player's
     * rotation
     */
    private void addToBoard() {
        for (int i = 0; i < nbOfPlayer; i++) {
            int rotation = (int) getPlayerRotation(i);
            switch (rotation) {
                case 0 -> boardPanel.addToBottomPanel(players[i]);
                case 90 -> boardPanel.addToLeftPanel(players[i]);
                case 180 -> boardPanel.addToTopPanel(players[i]);
                case 270 -> boardPanel.addToRightPanel(players[i]);
            }
        }

        boardPanel.addToMiddlePanel(pickPileButton);
        boardPanel.addToMiddlePanel(discardPileButton);
        boardPanel.addToMiddlePanel(endGameButton);
    }

    /**
     * Triggers the animation that reveals a player card
     * 
     * @param playerId   the player that possesses the card to reveal
     * @param cardPos    the position of the card to reveal
     * @param card       the visual information of the card to reveal
     * @param isBlocking wether or not the animation should hang the EDT
     */
    public void revealCard(byte playerId, byte cardPos, CardModel card, boolean isBlocking) {
        String folder = card.getColor().toString() + 'S';
        String file = folder + '_' + card.getValue();

        ImageIcon intro = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[playerId].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_PICKED.gif");
        ImageIcon outro = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[playerId].getRotation(),
                folder, file + "_REVEAL.gif");
        ImageIcon endCard = new ImageIcon(card.getImageIcon().getImage().getScaledInstance(panelWidth / 3 - 5,
                panelHeight / 2 - 5, Image.SCALE_SMOOTH));
        ImageIcon rotatedEndCard = SpriteUtil.rotateFrame(endCard, players[playerId].getRotation());

        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        SwingUtilities.invokeLater(() -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(intro);
            players[playerId].getDeck()[cardPos].setIcon(intro);
            frame.revalidate();
            frame.repaint();
        });

        Timer firstTimer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(outro);
            players[playerId].getDeck()[cardPos].setIcon(outro);
            frame.revalidate();
            frame.repaint();
        });

        Timer secondTimer = new Timer((int) Math.round(6000 / AnimSpeed), _ -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(rotatedEndCard);
            players[playerId].getDeck()[cardPos].setIcon(rotatedEndCard);
            frame.revalidate();
            frame.repaint();

            blocker.dispose();
        });

        //Create Timer for sound
        Timer soundTimer = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();

        Timer soundTimer2 = new Timer((int) Math.round(3000 / AnimSpeed) + (int) Math.round(1 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer2.setRepeats(false);
        soundTimer2.start();

        firstTimer.setRepeats(false);
        secondTimer.setRepeats(false);
        firstTimer.start();
        secondTimer.start();

        if (isBlocking) {
            blocker.setVisible(true);
        }
    }

    /**
     * Triggers the animation that hides a player card
     * 
     * @param playerId   the player that possesses the card to hide
     * @param cardPos    the position of the card to hide
     * @param card       the visual information of the card to hide
     * @param isBlocking wether or not the animation should hang the EDT
     */
    public void hideCard(byte playerId, byte cardPos, CardModel card, boolean isBlocking) {
        String folder = card.getColor().toString() + 'S';
        String file = folder + '_' + card.getValue();

        ImageIcon outro = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[playerId].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_REVEAL.gif");
        ImageIcon intro = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[playerId].getRotation(),
                folder, file + "_PICKED.gif");

        ImageIcon endCard = cardBackIcon;
        ImageIcon rotatedEndCard = SpriteUtil.rotateFrame(endCard, players[playerId].getRotation());

        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        SwingUtilities.invokeLater(() -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(intro);
            players[playerId].getDeck()[cardPos].setIcon(intro);
            frame.revalidate();
            frame.repaint();
        });

        Timer firstTimer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(outro);
            players[playerId].getDeck()[cardPos].setIcon(outro);
            frame.revalidate();
            frame.repaint();
        });

        Timer secondTimer = new Timer((int) Math.round(6000 / AnimSpeed), _ -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(rotatedEndCard);
            players[playerId].getDeck()[cardPos].setIcon(rotatedEndCard);
            frame.revalidate();
            frame.repaint();

            blocker.dispose();
        });

        //Create Timer for sound
        Timer soundTimer = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();

        Timer soundTimer2 = new Timer((int) Math.round(3000 / AnimSpeed) + (int) Math.round(1 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer2.setRepeats(false);
        soundTimer2.start();

        firstTimer.setRepeats(false);
        secondTimer.setRepeats(false);
        firstTimer.start();
        secondTimer.start();

        if (isBlocking) {
            blocker.setVisible(true);
        }
    }

    /**
     * Triggers the animation of the top pick pile card being drawed
     */
    public void pickStack() {
        ImageIcon cardGif = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, 0, "CARD_BACK",
                "RED_CARD_BACK_PICKED.gif");

        ImageIcon endCard = cardBackIcon;

        Point relativeLocation = SwingUtilities.convertPoint(
                pickPileButton.getParent(),
                pickPileButton.getLocation(),
                frame.getLayeredPane());

        // Add animated GIF
        final JLabel gifLabel = new JLabel(cardGif);
        gifLabel.setBounds(relativeLocation.x, relativeLocation.y, pickPileButton.getWidth(),
                pickPileButton.getHeight());
        frame.getLayeredPane().add(gifLabel, JLayeredPane.PALETTE_LAYER);

        // Add static background
        final JLabel backgroundLabel = new JLabel(endCard);
        backgroundLabel.setBounds(relativeLocation.x, relativeLocation.y, pickPileButton.getWidth(),
                pickPileButton.getHeight());
        frame.getLayeredPane().add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Unsets the button icon to make room for the labels
        pickPileButton.setDisabledIcon(null);
        pickPileButton.setIcon(null);
        pickPileButton.setEnabled(false);
        discardPileButton.setEnabled(false);
        frame.revalidate();
        frame.repaint();

        frame.getLayeredPane().revalidate();
        frame.getLayeredPane().repaint();

        // Invisible modal dialog to halt the code without stopping the EDT
        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        Timer timer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            pickPileButton.setIcon(endCard);
            pickPileButton.setDisabledIcon(endCard);
            frame.revalidate();
            frame.repaint();

            frame.getLayeredPane().remove(gifLabel);
            frame.getLayeredPane().remove(backgroundLabel);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();

            blocker.dispose(); // Releases the block
        });

        //Create Timer for sound
        Timer soundTimer = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();

        timer.setRepeats(false);
        timer.start();

        blocker.setVisible(true);
    }

    /**
     * Triggers the animation of the top discard pile card being drawed
     * 
     * @param card      the visuals of the card to be drawed
     * @param cardBelow the visuals of the card below the card to draw
     */
    public void pickDiscard(CardModel card, CardModel cardBelow) {
        String folder = card.getColor().toString() + 'S';
        String file = folder + '_' + card.getValue();

        ImageIcon cardGif = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, 0, folder,
                file + "_PICKED.gif");

        final ImageIcon belowCard;
        if (cardBelow != null) {
            belowCard = new ImageIcon(cardBelow.getImageIcon().getImage().getScaledInstance(panelWidth / 3 - 5,
                    panelHeight / 2 - 5, Image.SCALE_SMOOTH));
        } else {
            belowCard = cardBlankIcon;
        }

        Point relativeLocation = SwingUtilities.convertPoint(
                discardPileButton.getParent(),
                discardPileButton.getLocation(),
                frame.getLayeredPane());

        // Add animated GIF
        final JLabel gifLabel = new JLabel(cardGif);
        gifLabel.setBounds(relativeLocation.x, relativeLocation.y, discardPileButton.getWidth(),
                discardPileButton.getHeight());
        frame.getLayeredPane().add(gifLabel, JLayeredPane.PALETTE_LAYER);

        // Add static background
        final JLabel backgroundLabel = new JLabel(belowCard);
        backgroundLabel.setBounds(relativeLocation.x, relativeLocation.y, discardPileButton.getWidth(),
                discardPileButton.getHeight());
        frame.getLayeredPane().add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Unsets the button icon to make room for the labels
        discardPileButton.setDisabledIcon(null);
        discardPileButton.setIcon(null);

        frame.getLayeredPane().revalidate();
        frame.getLayeredPane().repaint();

        // Invisible modal dialog to halt the code without stopping the EDT
        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        Timer timer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            discardPileButton.setIcon(belowCard);
            discardPileButton.setDisabledIcon(belowCard);
            frame.revalidate();
            frame.repaint();

            frame.getLayeredPane().remove(gifLabel);
            frame.getLayeredPane().remove(backgroundLabel);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();

            blocker.dispose(); // Releases the block
        });

        //Create Timer for sound
        Timer soundTimer = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();

        timer.setRepeats(false);
        timer.start();

        blocker.setVisible(true);
    }

    /**
     * Triggers the animation to discard a card
     * 
     * @param card      the card being discarded
     * @param cardBelow the card that was previously on top of the discard pile
     */
    public void trashDiscard(CardModel card, CardModel cardBelow) {
        String folder = card.getColor().toString() + 'S';
        String file = folder + '_' + card.getValue();

        ImageIcon cardGif = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, 0, folder,
                file + "_REVEAL.gif");
        ImageIcon endCard = new ImageIcon(card.getImageIcon().getImage().getScaledInstance(panelWidth / 3 - 5,
                panelHeight / 2 - 5, Image.SCALE_SMOOTH));

        final ImageIcon belowCard;
        if (cardBelow != null) {
            belowCard = new ImageIcon(cardBelow.getImageIcon().getImage().getScaledInstance(panelWidth / 3 - 5,
                    panelHeight / 2 - 5, Image.SCALE_SMOOTH));
        } else {
            belowCard = cardBlankIcon;
        }

        Point relativeLocation = SwingUtilities.convertPoint(
                discardPileButton.getParent(),
                discardPileButton.getLocation(),
                frame.getLayeredPane());

        // Add static below card
        final JLabel belowLabel = new JLabel(belowCard);
        belowLabel.setBounds(relativeLocation.x, relativeLocation.y, discardPileButton.getWidth(),
                discardPileButton.getHeight());
        frame.getLayeredPane().add(belowLabel, JLayeredPane.DEFAULT_LAYER);

        // Add animated GIF
        final JLabel gifLabel = new JLabel(cardGif);
        gifLabel.setBounds(relativeLocation.x, relativeLocation.y, discardPileButton.getWidth(),
                discardPileButton.getHeight());
        frame.getLayeredPane().add(gifLabel, JLayeredPane.PALETTE_LAYER);

        // Unsets the button icon to make room for the labels
        discardPileButton.setDisabledIcon(null);
        discardPileButton.setIcon(null);

        frame.getLayeredPane().revalidate();
        frame.getLayeredPane().repaint();

        // Invisible modal dialog to halt the code without stopping the EDT
        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        Timer timer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            discardPileButton.setIcon(endCard);
            discardPileButton.setDisabledIcon(endCard);
            frame.revalidate();
            frame.repaint();

            frame.getLayeredPane().remove(belowLabel);
            frame.getLayeredPane().remove(gifLabel);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();

            blocker.dispose(); // Releases the block
        });

        //Create Timer for sound
        Timer soundTimer = new Timer((int) Math.round(1 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();
        
        timer.setRepeats(false);
        timer.start();

        blocker.setVisible(true);
    }

    /**
     * Triggers the animation of two player cards being swapped
     * 
     * @param player1 the ID of the first player
     * @param card1   the position of the first player's card to swap
     * @param player2 the ID of the second player
     * @param card2   the position of the second player's card to swap
     */
    public void swapCard(byte player1, byte card1, byte player2, byte card2) {
        ImageIcon baseCard1 = (ImageIcon) players[player1].getDeck()[card1].getDisabledIcon();
        ImageIcon baseCard2 = (ImageIcon) players[player2].getDeck()[card2].getDisabledIcon();
        ImageIcon rotatedBaseCard1 = SpriteUtil.rotateFrame(baseCard1,
                (players[player2].getRotation() - players[player1].getRotation() + 360) % 360);
        ImageIcon rotatedBaseCard2 = SpriteUtil.rotateFrame(baseCard2,
                (players[player1].getRotation() - players[player2].getRotation() + 360) % 360);

        ImageIcon intro1 = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[player1].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_PICKED.gif");
        ImageIcon intro2 = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[player2].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_PICKED.gif");
        ImageIcon outro1 = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[player1].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_REVEAL.gif");
        ImageIcon outro2 = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[player2].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_REVEAL.gif");

        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        SwingUtilities.invokeLater(() -> {
            players[player1].getDeck()[card1].setDisabledIcon(intro1);
            players[player1].getDeck()[card1].setIcon(intro1);
            frame.revalidate();
            frame.repaint();
        });

        Timer firstTimer = new Timer(750, _ -> {
            players[player2].getDeck()[card2].setDisabledIcon(intro2);
            players[player2].getDeck()[card2].setIcon(intro2);
            frame.revalidate();
            frame.repaint();
        });

        Timer secondTimer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            players[player1].getDeck()[card1].setDisabledIcon(outro1);
            players[player1].getDeck()[card1].setIcon(outro1);
            frame.revalidate();
            frame.repaint();
        });

        Timer thirdTimer = new Timer((int) Math.round(3000 / AnimSpeed) + 750, _ -> {
            players[player2].getDeck()[card2].setDisabledIcon(outro2);
            players[player2].getDeck()[card2].setIcon(outro2);
            frame.revalidate();
            frame.repaint();
        });

        Timer fourthTimer = new Timer((int) Math.round(6000 / AnimSpeed), _ -> {
            players[player1].getDeck()[card1].setDisabledIcon(rotatedBaseCard2);
            players[player1].getDeck()[card1].setIcon(rotatedBaseCard2);
            frame.revalidate();
            frame.repaint();
        });

        Timer fifthTimer = new Timer((int) Math.round(6000 / AnimSpeed) + 750, _ -> {
            players[player2].getDeck()[card2].setDisabledIcon(rotatedBaseCard1);
            players[player2].getDeck()[card2].setIcon(rotatedBaseCard1);
            frame.revalidate();
            frame.repaint();

            blocker.dispose();
        });

        //Create Timer for sound
        Timer soundTimer = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();

        Timer soundTimer2 = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25) + 750, _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer2.setRepeats(false);
        soundTimer2.start();

        Timer soundTimer3 = new Timer((int) Math.round(3000 / AnimSpeed) + (int) Math.round(1 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer3.setRepeats(false);
        soundTimer3.start();

        Timer soundTimer4 = new Timer((int) Math.round(3000 / AnimSpeed) + (int) Math.round(1 * 1500 / AnimSpeed / 25) + 750, _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer4.setRepeats(false);
        soundTimer4.start();

        firstTimer.setRepeats(false);
        secondTimer.setRepeats(false);
        thirdTimer.setRepeats(false);
        fourthTimer.setRepeats(false);
        fifthTimer.setRepeats(false);

        firstTimer.start();
        secondTimer.start();
        thirdTimer.start();
        fourthTimer.start();
        fifthTimer.start();

        blocker.setVisible(true);
    }

    /**
     * Triggers the animation of the player swapping the card in his hand with a
     * card in his deck (which is sended on the discard pile)
     * 
     * @param playerId        the ID of the player making the swap
     * @param cardPos         the position of the card to be discarded from the
     *                        player's deck
     * @param playerCardModel the visual of the card to be discarded
     * @param cardBelow       the visual information of the card that was on top of
     *                        the discard pile
     */
    public void putCardOnDiscard(byte playerId, byte cardPos, CardModel playerCardModel, CardModel cardBelow) {
        String folder = playerCardModel.getColor().toString() + 'S';
        String file = folder + '_' + playerCardModel.getValue();

        ImageIcon playerCard = new ImageIcon(playerCardModel.getImageIcon().getImage()
                .getScaledInstance(panelWidth / 3 - 5, panelHeight / 2 - 5, Image.SCALE_SMOOTH));
        ImageIcon rotatedPlayerCard = SpriteUtil.rotateFrame(playerCard, 0);

        ImageIcon endCard = cardBackIcon;
        ImageIcon rotatedEndCard = SpriteUtil.rotateFrame(endCard, players[playerId].getRotation());

        ImageIcon intro = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, players[playerId].getRotation(),
                "CARD_BACK", "RED_CARD_BACK_PICKED.gif");
        ImageIcon outro1 = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5, 0, folder,
                file + "_REVEAL.gif");
        ImageIcon outro2 = SpriteUtil.resizeGif(panelWidth / 3 - 5, panelHeight / 2 - 5,
                players[playerId].getRotation(), "CARD_BACK", "RED_CARD_BACK_REVEAL.gif");

        final ImageIcon belowCard;
        if (cardBelow != null) {
            belowCard = new ImageIcon(cardBelow.getImageIcon().getImage().getScaledInstance(panelWidth / 3 - 5,
                    panelHeight / 2 - 5, Image.SCALE_SMOOTH));
        } else {
            belowCard = cardBlankIcon;
        }

        Point relativeLocation = SwingUtilities.convertPoint(
                discardPileButton.getParent(),
                discardPileButton.getLocation(),
                frame.getLayeredPane());

        // Create static below card
        final JLabel belowLabel = new JLabel(belowCard);

        // Create animated outro GIF
        final JLabel gifOutroLabel = new JLabel(outro1);

        final JDialog blocker = new JDialog(frame, true);
        blocker.setModal(true);
        blocker.setSize(0, 0);
        blocker.setLocationRelativeTo(null);
        blocker.setUndecorated(true);
        blocker.setFocusableWindowState(false);

        SwingUtilities.invokeLater(() -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(intro);
            players[playerId].getDeck()[cardPos].setIcon(intro);
            frame.revalidate();
            frame.repaint();
        });

        Timer firstTimer = new Timer((int) Math.round(3000 / AnimSpeed) - 750, _ -> {
            belowLabel.setBounds(relativeLocation.x, relativeLocation.y, discardPileButton.getWidth(),
                    discardPileButton.getHeight());
            frame.getLayeredPane().add(belowLabel, JLayeredPane.DEFAULT_LAYER);

            gifOutroLabel.setBounds(relativeLocation.x, relativeLocation.y, discardPileButton.getWidth(),
                    discardPileButton.getHeight());
            frame.getLayeredPane().add(gifOutroLabel, JLayeredPane.PALETTE_LAYER);

            // Unsets the button icon to make room for the labels
            discardPileButton.setDisabledIcon(null);
            discardPileButton.setIcon(null);

            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
        });

        Timer secondTimer = new Timer((int) Math.round(3000 / AnimSpeed), _ -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(outro2);
            players[playerId].getDeck()[cardPos].setIcon(outro2);
            frame.revalidate();
            frame.repaint();
        });

        Timer thirdTimer = new Timer((int) Math.round(6000 / AnimSpeed) - 750, _ -> {
            discardPileButton.setDisabledIcon(rotatedPlayerCard);
            discardPileButton.setIcon(rotatedPlayerCard);
            frame.revalidate();
            frame.repaint();

            frame.getLayeredPane().remove(belowLabel);
            frame.getLayeredPane().remove(gifOutroLabel);
            frame.getLayeredPane().revalidate();
            frame.getLayeredPane().repaint();
        });

        Timer fourthTimer = new Timer((int) Math.round(6000 / AnimSpeed), _ -> {
            players[playerId].getDeck()[cardPos].setDisabledIcon(rotatedEndCard);
            players[playerId].getDeck()[cardPos].setIcon(rotatedEndCard);
            frame.revalidate();
            frame.repaint();

            blocker.dispose();
        });

        Timer soundTimer = new Timer((int) Math.round(24 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer.setRepeats(false);
        soundTimer.start();

        Timer soundTimer2 = new Timer((int) Math.round(3000 / AnimSpeed) + (int) Math.round(1 * 1500 / AnimSpeed / 25) - 750, _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer2.setRepeats(false);
        soundTimer2.start();

        Timer soundTimer3 = new Timer((int) Math.round(3000 / AnimSpeed) + (int) Math.round(1 * 1500 / AnimSpeed / 25), _ -> {
                SoundUtil.playRandomCardFlip();
        });

        soundTimer3.setRepeats(false);
        soundTimer3.start();

        firstTimer.setRepeats(false);
        secondTimer.setRepeats(false);
        thirdTimer.setRepeats(false);
        fourthTimer.setRepeats(false);

        firstTimer.start();
        secondTimer.start();
        thirdTimer.start();
        fourthTimer.start();

        blocker.setVisible(true);
    }

    /**
     * 
     * @return the GameModel associated with the Gameview
     */
    public GameModel getGameModel() {
        return this.gameModel;
    }

    /**
     * 
     * @return the JButton used to declare the end of the round
     */
    public JButton getEndGameButton() {
        return endGameButton;
    }

    /**
     * 
     * @return the JButton used as the discard pile
     */
    public JButton getDiscardPileButton() {
        return discardPileButton;
    }

    /**
     * 
     * @return the JButton used as the pick pile
     */
    public JButton getPickPileButton() {
        return pickPileButton;
    }

    /**
     * 
     * @return the array of PlayerPanels composed of every players' panel
     */
    public PlayerPanel[] getPlayers() {
        return players;
    }

    /**
     * 
     * @param i the ID of the player
     * @return the PlayerPanel associated to the player ID
     */
    public PlayerPanel getPlayer(int i) {
        return players[i];
    }

    /**
     * 
     * @param id       the ID of the player
     * @param position the ID/position of the card
     * @return the JButton used as the xth card of the yth player
     */
    public JButton getCard(int id, int position) {
        return players[id].getDeck()[position];
    }

    /**
     * 
     * @return the number of players currently in the game (AIs included)
     */
    public int getNbOfPlayer() {
        return nbOfPlayer;
    }

    /**
     * 
     * @return the JButton used for returning to the menu
     */
    public JButton getExitButton() {
        return exitButton;
    }

    /**
     * Triggers the appearance of the small flag indicating the player has announced
     * the end of the round
     * 
     * @param playerId the ID of the player announcing the end of the round
     */
    public void annonceEndGame(int playerId) {
        players[playerId].announceEndGame();
    }

    /**
     * Enables all the decks on the gameboard
     * 
     * @param flag wether or not to enable the deck
     */
    public void setDecksEnabled(boolean flag) {
        for (PlayerPanel playerPanel : players) {
            playerPanel.setDeckEnabled(flag);
        }
    }

    /**
     * Enables the deck of the specified player
     * 
     * @param id   the ID of the player
     * @param flag wether or not to enable the deck
     */
    public void setDeckEnabled(int id, boolean flag) {
        try {
            players[id].setDeckEnabled(flag);
        } catch (Exception e) {
            System.err.println("Player's deck not found : " + e.getMessage());
        }
    }

    /**
     * Enables the game buttons
     * 
     * @param flag wether or not to enable the buttons
     */
    public void setGameButtonsEnabled(boolean flag) {
        endGameButton.setEnabled(flag);
        pickPileButton.setEnabled(flag);
        discardPileButton.setEnabled(flag);
    }

    /**
     * Enables all the buttons on the gameboard (except the Exit button)
     * 
     * @param flag wether or not to enable the buttons
     */
    public void setAllButtonsEnabled(boolean flag) {
        setDecksEnabled(flag);
        setGameButtonsEnabled(flag);
    }

    /**
     * Adds the ActionListener to all PlayerPanels
     * 
     * @param actionListener the ActionListener to add
     */
    public void addDecksActionListener(ActionListener actionListener) {
        for (PlayerPanel playerPanel : players) {
            playerPanel.addDeckActionListener(actionListener);
        }
    }

    /**
     * Adds the ActionListener to a specific PlayerPanel
     * 
     * @param id             the ID of the player associated with the PlayerPanel
     * @param actionListener the ActionListener to add
     */
    public void addDeckActionListener(int id, ActionListener actionListener) {
        players[id].addDeckActionListener(actionListener);
    }

    /**
     * 
     * @param card the card to check ownership of
     * @return the ID of the player possessing the card
     */
    public int indexOfPlayer(Object card) {
        for (int i = 0; i < nbOfPlayer; i++) {
            for (JButton cards : players[i].getDeck()) {
                if (cards == card) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Resets the icon of the discard pile when it's emptied
     */
    public void resetDiscardPileButton() {
        discardPileButton.setIcon(cardBlankIcon);
        discardPileButton.setDisabledIcon(cardBlankIcon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Storage.GAME_BG != null) {
            g.drawImage(Storage.GAME_BG, 0, 0, frame.getWidth(), frame.getHeight(), this);
        }
    }

    /**
     * 
     * @return the animation speed in milliseconds
     */
    public static double getAnimSpeed() {
        return AnimSpeed;
    }

    /**
     * Sets the animation speed to the new value
     * 
     * @param animSpeed the new value of the animation speed in milliseconds
     */
    public static void setAnimSpeed(double animSpeed) {
        AnimSpeed = animSpeed;
    }

    /**
     * 
     * @return the current state of the music being played
     */
    public static boolean getGameMusic() {
        return GameMusic;
    }

    /**
     * Sets the state of the music being played
     * 
     * @param gameMusic the new value of the state of the music
     */
    public static void setGameMusic(boolean gameMusic) {
        GameMusic = gameMusic;
    }

    /**
     * Highlights the active player's UI panel with a black border.
     *
     * @param player the active player
     */
    public void highlightActivePlayer(PlayerModel player) {
        players[player.getGameId()].setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 10));
    }

    /**
     * Removes the highlight from the active player's UI panel.
     *
     * @param player the active player
     */
    public void unhighlightActivePlayer(PlayerModel player) {
        players[player.getGameId()].setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 2));
    }
}