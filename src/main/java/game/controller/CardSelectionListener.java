package main.java.game.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CardSelectionListener implements ActionListener {
    private GameController gameController;

    /**
     * Constructs a SelectionListener.
     *
     * @param gameController the player's game controller
     */
    CardSelectionListener(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Handles the selection of a card by a player.
     * Enables all decks except the one of the current player and attaches
     * a {@link SwapListener} to each other player's deck for potential swap.
     *
     * @param e the action event triggered by the card selection
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        byte playerId = (byte) gameController.getGameView().indexOfPlayer(e.getSource());
        byte position = (byte) gameController.getGameView().getPlayer(playerId).getIndex(e.getSource());
        gameController.getGameView().setDecksEnabled(true);
        gameController.getGameView().setDeckEnabled(playerId, false);
        gameController.removeActionListeners();
        for (int i = 0; i < gameController.getGameView().nbOfPlayer; i++) {
            if (i != playerId) {
                gameController.getGameView().addDeckActionListener(i,
                        new SwapListener(gameController, playerId, position));
            }
        }
        gameController.getFrame().revalidate();
        gameController.getFrame().repaint();
    }
}
