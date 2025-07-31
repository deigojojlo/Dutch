package main.java.game.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.java.game.model.PlayerModel;
import main.java.onlinegame.WebsocketClient;

public class ExitPowerListener implements ActionListener {

    private final GameController gameController;
    private final RevealListener revealListener;

    /**
     * Constructs an ExitPowerListener to handle the action of exiting a revealed
     * power.
     *
     * @param gameController the player's game controller
     * @param revealListener the listener associated with the revealed card
     */
    ExitPowerListener(GameController gameController, RevealListener revealListener) {
        this.gameController = gameController;
        this.revealListener = revealListener;
    }

    /**
     * Invoked when the associated action is performed.
     * If the game model is null (online mode), it sends a message through the
     * WebSocket.
     * Otherwise, it hides the player's card locally.
     *
     * @param e the action event that triggered this method
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int playerId = revealListener.getPlayerId();
        int position = revealListener.getCardPosition();
        gameController.getFrame().getContentPane().remove(0);
        if (gameController.getModel() == null) {
            WebsocketClient.sendByte(
                    (byte) 64,
                    (byte) 6,
                    (byte) gameController.getGameId(),
                    (byte) gameController.getLocalPlayerId((byte) playerId),
                    (byte) playerId,
                    (byte) position);
        } else {
            PlayerModel player = gameController.getModel().getPlayerQueue().get((byte) (((int) playerId +
                    gameController.getGameView().getNbOfPlayer() - 1)
                    % gameController.getGameView().getNbOfPlayer()));
            gameController.hidePlayerCard(player, (byte) playerId, (byte) position, true);
        }

        gameController.getFrame().revalidate();
        gameController.getFrame().repaint();
        gameController.resetAndRepaintRoundedButton();
    }
}