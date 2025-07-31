package main.java.game.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.Timer;

import main.java.game.model.PlayerModel;
import main.java.game.view.GameView;
import main.java.onlinegame.WebsocketClient;
import main.java.ourjcomponent.LabelOverlay;

public class RevealListener implements ActionListener {

    private GameController gameController;
    private LabelOverlay labelOverlay;
    private byte playerId;
    private byte cardPosition;

    /**
     * Constructs a RevealListener.
     *
     * @param gameController the player's game controller
     */
    public RevealListener(GameController gameController) {
        this.gameController = gameController;
        labelOverlay = new LabelOverlay("", gameController.getFrame());
    }

    /**
     * Handles the reveal power.
     * Retrieves the player and card cardPosition, and either sends a WebSocket
     * message
     * or reveals the card directly depending on the game mode (online or local).
     *
     * @param e the action event triggered by the user
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        playerId = (byte) gameController.getGameView().indexOfPlayer(e.getSource());
        cardPosition = (byte) gameController.getGameView().getPlayer(playerId).getIndex(e.getSource());
        gameController.repaintRoundedButton();
        if (gameController.getModel() == null) {
            final JDialog blocker = new JDialog(gameController.getFrame(), true);
            blocker.setModal(true);
            blocker.setSize(0, 0);
            blocker.setLocationRelativeTo(null);
            blocker.setUndecorated(true);
            blocker.setFocusableWindowState(false);
            Timer timer = new Timer((int) Math.round(6000 / GameView.getAnimSpeed()), _ -> {
                gameController.getFrame().revalidate();
                gameController.getFrame().repaint();
                blocker.dispose();
            });

            WebsocketClient.sendByte(
                    (byte) 64,
                    (byte) 7,
                    (byte) gameController.getGameId(),
                    (byte) gameController.getLocalPlayerId(playerId),
                    playerId,
                    cardPosition);

            timer.setRepeats(false);
            timer.start();
            blocker.setVisible(true);

        } else {
            PlayerModel player = gameController.getModel().getPlayerQueue()
                    .get((byte) (((int) playerId + gameController.getGameView().getNbOfPlayer() - 1)
                            % gameController.getGameView().getNbOfPlayer()));
            gameController.revealPlayerCard(player, playerId, cardPosition, true);
        }

        gameController.getFrame().getContentPane().remove(0);
        gameController.removeActionListeners();
        gameController.getGameView().setDecksEnabled(false);

        gameController.getGameView().getCard(playerId, cardPosition).setEnabled(true);
        gameController.getGameView().getCard(playerId, cardPosition).setFocusable(true);
        gameController.getGameView().getCard(playerId, cardPosition).requestFocus();

        gameController.getFrame().getContentPane().add(labelOverlay, 0);
        gameController.getFrame().revalidate();
        gameController.getFrame().repaint();
        gameController.repaintRoundedButton();
    }

    /**
     * Sets a new LabelOverlay to be displayed after revealing a card.
     *
     * @param labelOverlay the label overlay to set
     */
    public void setLabelOverlay(LabelOverlay labelOverlay) {
        this.labelOverlay = labelOverlay;
    }

    /**
     * Returns the ID of the player who triggered the reveal action.
     *
     * @return the player's ID
     */
    public byte getPlayerId() {
        return playerId;
    }

    /**
     * Returns the cardPosition of the card that was revealed.
     *
     * @return the card cardPosition
     */
    public byte getCardPosition() {
        return cardPosition;
    }
}