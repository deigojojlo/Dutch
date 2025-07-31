package main.java.game.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.java.game.model.PlayerModel;
import main.java.onlinegame.WebsocketClient;

public class SwapListener implements ActionListener {
    private GameController gameController;
    private int playerId;
    private int cardPosition;

    /**
     * Constructs a SwapListener to handle the swapping of cards.
     *
     * @param gameController the main game controller
     * @param playerId       the ID of the player initiating the swap
     * @param cardPosition   the cardPosition of the initiating player's selected
     *                       card
     */
    SwapListener(GameController gameController, int playerId, int cardPosition) {
        this.gameController = gameController;
        this.playerId = playerId;
        this.cardPosition = cardPosition;
    }

    /**
     * Called when the user selects a card with SelectListener to complete the swap.
     * Executes the swap either locally or sends the command through WebSocket
     * if the game is being played online.
     *
     * @param e the action event triggered by selecting the target card
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        gameController.getFrame().getContentPane().remove(0);
        byte player2Id = (byte) gameController.getGameView().indexOfPlayer(e.getSource());
        byte card2Position = (byte) gameController.getGameView().getPlayer(player2Id).getIndex(e.getSource());
        gameController.resetAndRepaintRoundedButton();

        if (gameController.getModel() == null) {
            WebsocketClient.sendByte(
                    (byte) 64,
                    (byte) 8,
                    (byte) gameController.getLocalPlayerId((byte) playerId),
                    (byte) gameController.getLocalPlayerId(player2Id),
                    (byte) cardPosition,
                    card2Position,
                    (byte) playerId,
                    player2Id);
            WebsocketClient.sendByte((byte) 64, (byte) 5);
        } else {
            PlayerModel player1 = gameController.getModel().getPlayerQueue()
                    .get(((int) playerId + gameController.getModel().getNumberOfPlayer() - 1)
                            % gameController.getModel().getNumberOfPlayer());
            PlayerModel player2 = gameController.getModel().getPlayerQueue()
                    .get(((int) player2Id + gameController.getModel().getNumberOfPlayer() - 1)
                            % gameController.getModel().getNumberOfPlayer());

            gameController.swapCard((byte) playerId, player2Id, (byte) cardPosition, card2Position);
            gameController.getModel().swapCard(player1, (int) cardPosition, player2, (int) card2Position);
            gameController.trashDiscard(gameController.getModel().getActivePlayer().getCardInHand(),
                    gameController.getModel().getOnDiscard());
            gameController.getModel().getActivePlayer().clearCardInHand(gameController.getModel().getCardGameModel());
        }

        gameController.removeActionListeners();
        gameController.getFrame().revalidate();
        gameController.getFrame().repaint();
    }
}
