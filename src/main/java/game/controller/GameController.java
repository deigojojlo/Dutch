package main.java.game.controller;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import main.java.game.model.CardGameModel;
import main.java.game.model.CardModel;
import main.java.game.model.GameModel;
import main.java.game.model.PlayerComputerModel;
import main.java.game.model.PlayerModel;
import main.java.game.view.GameView;
import main.java.game.view.PlayerPanel;
import main.java.onlinegame.WaitingRoomController;
import main.java.onlinegame.WebsocketClient;
import main.java.ourjcomponent.LabelOverlay;
import main.java.ourjcomponent.QueryPanel;
import main.java.ourjcomponent.RoundedButton;
import main.java.util.Pair;

public class GameController {
    private final GameView gameView;
    private final JFrame frame;
    private ArrayList<Byte> playerOrder;
    private WaitingRoomController waitingRoomController;

    private GameModel model; // only on the local game

    ActionListener pickStackEvent;
    ActionListener discardStackEvent;
    ActionListener endGameEvent;
    ActionListener[] deckStackEvents;

    private ArrayList<List<CardModel>> revealedCard;

    /*
     * NOTE : si les arguments contiennent des bytes , il faut un équivalent pour la
     * partie local qui utilise le game model
     */
    public GameController(JFrame frame, ArrayList<Byte> playerOrder, ArrayList<String> usernames,
            WaitingRoomController wrc) {
        this.frame = frame;
        this.playerOrder = playerOrder;
        this.gameView = new GameView(frame, playerOrder, usernames);
        this.waitingRoomController = wrc;

        pickStackEvent = createPickStackListener();
        discardStackEvent = createDiscardStackListener();
        deckStackEvents = createDeckStackputCardOnDiscard();
        endGameEvent = createEndGameListener();
        gameView.getEndGameButton().addActionListener(endGameEvent);
        gameView.getExitButton().addActionListener(_ -> {
            WebsocketClient.disconnect();
            frame.getContentPane().removeAll();

            frame.add(new MenuController(frame).getView());

            frame.revalidate();
            frame.repaint();
        });
    }

    /**
     * The solo constructor
     * 
     * @param frame      the frame
     * @param nbOfPlayer the number of real player
     * @param nbOfAI     the number of player computer
     * @param difficulty `null` if no ai else 1 2 or 3
     */
    public GameController(JFrame frame, int nbOfPlayer, int nbOfAI, Integer difficulty) {
        this.frame = frame;
        this.gameView = new GameView(frame, nbOfPlayer + nbOfAI);
        PlayerComputerModel.setNbofPlayer(nbOfPlayer + nbOfAI);
        this.model = new GameModel(104, nbOfPlayer, nbOfAI, difficulty);
        gameView.getExitButton().addActionListener(_ -> {
            frame.getContentPane().removeAll();

            frame.add(new MenuController(frame).getView());

            frame.revalidate();
            frame.repaint();
        });
    }

    /**
     * @return the GameModel linked to the GameController
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * @return the JFrame that's at the top of the component hierarchy
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @return the GameView linked to the GameController
     */
    public GameView getGameView() {
        return gameView;
    }

    /**
     * 
     * @return the ID (a.k.a. the position) of the player inside the waiting room
     *         (the host is 0, next is 1 etc...)
     */
    public int getGameId() {
        return playerOrder.get(0);
    }

    /**
     * 
     * @return the ArratList containing the order of the players as they joined the
     *         waiting room
     */
    public ArrayList<Byte> getPlayerOrder() {
        return playerOrder;
    }

    /**
     * Launch the game in local
     */
    public void play() throws InterruptedException {
        setupGame();

        while (!isEndPlayerTurn()) {
            model.nextPlayer();
            PlayerModel activePlayer = model.getActivePlayer();
            gameView.highlightActivePlayer(activePlayer);

            if (activePlayer instanceof PlayerComputerModel pc) {
                handleComputerTurnWithEndCheck(pc);
            } else {
                handleHumanTurn(activePlayer);
            }

            gameView.unhighlightActivePlayer(activePlayer);
        }

        endTurnSequence();
    }

    /**
     * Initializes the game by distributing cards and registering event
     * putCardOnDiscard.
     */
    private void setupGame() {
        model.distribute();

        pickStackEvent = createPickStackListener();
        discardStackEvent = createDiscardStackListener();
        deckStackEvents = createDeckStackputCardOnDiscard();
        endGameEvent = createEndGameListener();

        gameView.getEndGameButton().addActionListener(endGameEvent);
    }

    /**
     * Checks whether the current player is the one who announced the end of the
     * game.
     *
     * @return true if it's the end player's turn, false otherwise
     */
    private boolean isEndPlayerTurn() {
        return model.getPlayerWhoAnnouncedTheEnd() == model.getPlayerQueue().getFirst();
    }

    /**
     * Handles a computer player's turn and checks if it announces the end of the
     * game.
     *
     * @param pc the computer player
     */
    private void handleComputerTurnWithEndCheck(PlayerComputerModel pc) throws InterruptedException {
        if (pc.finish() && model.getPlayerWhoAnnouncedTheEnd() == null) {
            model.setPlayerWhoAnnoncedTheEnd(model.getActivePlayer());
            gameView.annonceEndGame(model.getNumberOfPlayer() - 1);
            frame.revalidate();
            frame.repaint();
        }
        handleComputerTurn(pc);
    }

    /**
     * Handles the end-of-turn logic: score calculation, reveal, score display,
     * cleanup, and either restart or finish.
     *
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private void endTurnSequence() throws InterruptedException {
        boolean gameFinished = model.addScore();
        gameView.setAllButtonsEnabled(false);
        revealLocal();

        Thread.sleep(5000);
        SwingUtilities.invokeLater(() -> {
            showScore();
            frame.revalidate();
            frame.repaint();
        });
        Thread.sleep(7000);

        cleanupAfterScore();
        Thread.sleep((long) (6000 / GameView.getAnimSpeed()));

        if (gameFinished) {
            displayWinner();
            gameView.setAllButtonsEnabled(false);
            RoundedButton exitButton = new RoundedButton("Quitter");
            exitButton.addActionListener(_ -> {
                SelectionController selectionController = new SelectionController(frame);

                frame.getContentPane().removeAll();
                frame.add(selectionController.getView());
                frame.revalidate();
                frame.repaint();
            });
            frame.getContentPane().add(exitButton);
            frame.revalidate();
            frame.repaint();
        } else {
            restartGame();
        }
    }

    /**
     * Cleans up the game UI and model state before a restart.
     */
    private void cleanupAfterScore() {
        if (frame.getContentPane().getComponentCount() >= 2) {
            frame.getContentPane().remove(0);
        }

        resetLocalCardIcon();
        model.clearPlayerCard();
        model.setCardGameModel(new CardGameModel(model.getCardGameModel().getSize()));
    }

    /**
     * Resets the game for a new round and calls play() again.
     */
    private void restartGame() {
        for (PlayerPanel playerPanel : gameView.getPlayers()) {
            playerPanel.removeEndGame();
        }

        gameView.setGameButtonsEnabled(true);
        gameView.getEndGameButton().removeActionListener(endGameEvent);
        gameView.resetDiscardPileButton();
        model.restart();

        frame.revalidate();
        frame.repaint();

        try {
            play();
        } catch (InterruptedException e) {
            System.err.println("Error during the play method");
        }
    }

    /**
     * Create the event pick on pickstack.
     * 
     * @return an action listener.
     */
    private ActionListener createPickStackListener() {
        return _ -> {
            if (this.model == null) {
                WebsocketClient.sendByte((byte) 64, (byte) 2);
            } else {
                pickStack();
                PlayerModel activePlayer = model.getActivePlayer();
                activePlayer.giveCardInHand(model.drawCard());

            }
            removeActionListeners();
        };
    }

    /**
     * Create the event pick on discardstack.
     * 
     * @return an action listener.
     */
    private ActionListener createDiscardStackListener() {
        return _ -> {
            if (this.model == null) {
                WebsocketClient.sendByte((byte) 64, (byte) 3);
                removeActionListeners();
            } else {
                boolean empty = model.getOnDiscard() != null;
                if (empty) {
                    pickDiscard(model.getOnDiscard(), model.get2ndOnDiscard());
                    PlayerModel activePlayer = model.getActivePlayer();

                    activePlayer.giveCardInHand(model.getCardGameModel().popDiscard());
                }
                if (empty) {
                    removeActionListeners();
                }
            }
        };
    }

    /**
     * Create the event the switch card in hand and deck.
     * 
     * @return putCardOnDiscard an array of action listener.
     */
    private ActionListener[] createDeckStackputCardOnDiscard() {
        ActionListener[] putCardOnDiscard = new ActionListener[gameView.getPlayers()[0].getDeck().length];
        for (int i = 0; i < putCardOnDiscard.length; i++) {
            putCardOnDiscard[i] = event -> {
                int cardPosition = -1;
                for (int j = 0; j < gameView.getPlayer(0).getDeck().length; j++) {
                    if (event.getSource() == gameView.getCard(0, j)) {
                        cardPosition = j;
                        break;
                    }
                }
                if (cardPosition == -1) {
                    throw new IllegalStateException("Le bouton du Player n'a pas été trouvé");
                }
                if (this.model == null) {
                    if (frame.getContentPane().getComponentCount() >= 2) {
                        frame.getContentPane().remove(0);
                    }
                    frame.revalidate();
                    frame.repaint();
                    WebsocketClient.sendByte((byte) 64, (byte) 4, (byte) cardPosition);
                    resetAndRepaintRoundedButton();
                } else {
                    if (frame.getContentPane().getComponentCount() >= 2) {
                        frame.getContentPane().remove(0);
                    }
                    frame.revalidate();
                    frame.repaint();
                    resetAndRepaintRoundedButton();
                    PlayerModel activePlayer = model.getActivePlayer();
                    putCardOnDiscard(activePlayer, (byte) activePlayer.getGameId(), (byte) cardPosition,
                            model.getOnDiscard());
                    model.getCardGameModel().discardCard(activePlayer.getCardInDeck(cardPosition));
                    activePlayer.setCardInDeck(cardPosition, activePlayer.getCardInHand());
                    activePlayer.giveCardInHand(null);
                    gameView.setDeckEnabled(0, false);
                    gameView.setDecksEnabled(true);
                    gameView.getDiscardPileButton().setEnabled(true);
                    gameView.getPickPileButton().setEnabled(true);
                    removeActionListeners();
                }
            };
        }
        return putCardOnDiscard;
    }

    /**
     * Create the event for announce the end.
     * 
     * @return an action listener.
     */
    private ActionListener createEndGameListener() {
        return _ -> {
            if (this.model == null) {
                WebsocketClient.sendByte((byte) 64, (byte) -1, (byte) playerOrder.get(0));
            } else {
                if (!(model.getActivePlayer() instanceof PlayerComputerModel)
                        && model.getPlayerWhoAnnouncedTheEnd() == null) {
                    announceTheEnd();
                }
            }
            frame.revalidate();
            frame.repaint();
        };
    }

    /**
     * Remove ActionListener for all button in gameview
     */
    public void removeActionListeners() {
        // Remove listeners from the pick pile button
        for (ActionListener listener : gameView.getPickPileButton().getActionListeners()) {
            gameView.getPickPileButton().removeActionListener(listener);
        }

        // Remove listeners from the discard pile button
        for (ActionListener listener : gameView.getDiscardPileButton().getActionListeners()) {
            gameView.getDiscardPileButton().removeActionListener(listener);
        }

        // Remove putCardOnDiscard from each card in the deck
        for (int i = 0; i < gameView.nbOfPlayer; i++) {
            for (JButton deck : gameView.getPlayers()[i].getDeck()) {
                for (ActionListener listener : deck.getActionListeners()) {
                    deck.removeActionListener(listener);
                }
            }
        }
    }

    /**
     * Handle what human player can do betore pick a card.
     * 
     * @param activePlayer a {@link PlayerModel} who is the player who plays.
     */
    @SuppressWarnings("SleepWhileInLoop")
    private void handleHumanTurn(PlayerModel activePlayer) throws InterruptedException {
        gameView.getExitButton().setEnabled(true);
        gameView.getPickPileButton().setBorder(new LineBorder(Color.red));
        gameView.getDiscardPileButton().setBorder(new LineBorder(Color.red));
        gameView.getPickPileButton().addActionListener(pickStackEvent);
        gameView.getPickPileButton().setEnabled(true);
        gameView.getDiscardPileButton().addActionListener(discardStackEvent);
        gameView.getDiscardPileButton().setEnabled(true);

        while (activePlayer.getCardInHand() == null) {
            Thread.sleep(10);
        }

        givePickedCard(activePlayer.getCardInHand(), activePlayer);

        while (activePlayer.getCardInHand() != null) {
            Thread.sleep(10);
        }

        gameView.setDecksEnabled(true);
        gameView.getDiscardPileButton().setEnabled(true);
        gameView.getPickPileButton().setEnabled(true);
        gameView.getExitButton().setEnabled(false);
    }

    /**
     * Handle what computer player can do betore pick a card.
     * 
     * @param pc a {@link PlayerComputerModel}.
     */
    private void handleComputerTurn(PlayerComputerModel pc) throws InterruptedException {
        int action = pc.playComputerTurn_WantDiscard(model.getCardGameModel().getTopDiscard());
        if (action != -1 && model.getCardGameModel().getTopDiscard() != null) {
            System.out.println("a piocher sur la défausse");
            int cardPosition = action;
            pickDiscard(model.getCardGameModel().getTopDiscard(), model.get2ndOnDiscard());
            pc.giveCardInHand(model.getCardGameModel().popDiscard());
            // Thread.sleep(1000);
            model.getCardGameModel().discardCard(pc.getCardInDeck(cardPosition));
            putCardOnDiscard(pc, (byte) pc.getGameId(), (byte) cardPosition, model.get2ndOnDiscard());
            pc.setCardInDeck(cardPosition, pc.getCardInHand());
            pc.giveCardInHand(null);
        } else {
            // Thread.sleep(1000);
            computerPickStackPlay(pc);
        }
    }

    /**
     * Handle what happened when computer player pick a card.
     * 
     * @param pc a {@link PlayerComputerModel}.
     */
    private void computerPickStackPlay(PlayerComputerModel pc) {
        pickStack();
        pc.giveCardInHand(this.model.drawCard());
        int[] action = pc.playComputerTurn_Pick(pc.getCardInHand());
        switch (action[0]) {
            case -1 -> {
                trashDiscard(pc.getCardInHand(), model.getOnDiscard());
                pc.clearCardInHand(model.getCardGameModel());
            }
            case 1 -> {
                model.getCardGameModel().discardCard(pc.getCardInDeck(action[1]));
                putCardOnDiscard(pc, (byte) pc.getGameId(), (byte) action[1], model.get2ndOnDiscard());
                pc.setCardInDeck(action[1], pc.getCardInHand());
                pc.giveCardInHand(null);
            }
            case 2 -> {
                pc.memorizeCard(action[1], pc.getCardInDeck(action[1]));
                pc.clearCardInHand(model.getCardGameModel());
                trashDiscard(model.getOnDiscard(), model.get2ndOnDiscard());
            }
            case 3 -> {
                pc.memorizeCardOfPlayer(
                        action[1],
                        action[2],
                        model.getPlayerQueue().get(action[1]).getCardInDeck(action[2]));
                pc.clearCardInHand(model.getCardGameModel());
                trashDiscard(model.getOnDiscard(), model.get2ndOnDiscard());
            }
            case 4 -> {
                PlayerModel j1 = model.getPlayerQueue().get(action[1]);
                PlayerModel j2 = model.getPlayerQueue().get(action[3]);
                model.swapCard(j1, action[2], j2, action[4]);
                swapCard((byte) j1.getGameId(), (byte) j2.getGameId(), (byte) action[2], (byte) action[4]);
                pc.clearCardInHand(model.getCardGameModel());
                trashDiscard(model.getOnDiscard(), model.get2ndOnDiscard());
            }
            case 5 -> {
                pc.memorizeCard(action[2], pc.getCardInDeck(action[2]));
                if (pc.activeKingEffect(pc.getCardInDeck(action[2]), action)) {
                    PlayerModel j1 = model.getPlayerQueue().get(action[1]);
                    PlayerModel j2 = model.getPlayerQueue().get(action[3]);
                    model.swapCard(j1, action[2], j2, action[4]);
                    swapCard((byte) j1.getGameId(), (byte) j2.getGameId(), (byte) action[2], (byte) action[4]);
                    pc.memorizeCardOfPlayer(action[1], action[2], j1.getCardInDeck(action[2]));
                    pc.memorizeCardOfPlayer(action[3], action[4], j2.getCardInDeck(action[4]));
                }

                pc.clearCardInHand(model.getCardGameModel());
                trashDiscard(model.getOnDiscard(), model.get2ndOnDiscard());
            }
            default -> {
                pc.clearCardInHand(model.getCardGameModel());
                trashDiscard(model.getOnDiscard(), model.get2ndOnDiscard());
            }
        }
    }

    /**
     * Only for online
     */
    public void giveActivePlayer(byte id) {
        SwingUtilities.invokeLater(() -> {
            if (frame.getContentPane().getComponentCount() >= 2) {
                frame.getContentPane().remove(0);
            }
            if (playerOrder.get(0) == id) {
                System.out.println("    c'est moi");
                gameView.getExitButton().setEnabled(true);
                gameView.getPickPileButton().addActionListener(pickStackEvent);
                gameView.getDiscardPileButton().addActionListener(discardStackEvent);
                gameView.getPickPileButton().setEnabled(true);
                gameView.getDiscardPileButton().setEnabled(true);
            }
            gameView.getPlayer(playerOrder.indexOf(id)).setBorder(new LineBorder(Color.black, 10));
            gameView.getPlayer((playerOrder.indexOf(id) - 1 + gameView.getNbOfPlayer()) % gameView.getNbOfPlayer())
                    .setBorder(new LineBorder(Color.black, 2));
            frame.revalidate();
            frame.repaint();
        });
    }

    /**
     * The player save the card and a option box ask the action
     * 
     * @param cardModel    the drawed card
     * @param activePlayer the active player if local
     */
    public void givePickedCard(CardModel cardModel, PlayerModel activePlayer) {
        if (frame.getContentPane().getComponentCount() >= 2) {
            frame.getContentPane().remove(0);
        }

        gameView.setDecksEnabled(false);
        gameView.getDiscardPileButton().setEnabled(false);
        gameView.getPickPileButton().setEnabled(false);
        frame.revalidate();
        frame.repaint();

        QueryPanel qp = new QueryPanel(cardModel, frame);
        this.frame.getContentPane().add(qp, 0);

        ((RoundedButton) this.gameView.getEndGameButton()).setAbsoluteBackground(new Color(0, 0, 0, 128));
        ((RoundedButton) this.gameView.getExitButton()).setAbsoluteBackground(new Color(0, 0, 0, 128));

        qp.getTrashButton().addActionListener(_ -> {
            frame.getContentPane().remove(0);
            frame.revalidate();
            frame.repaint();
            resetAndRepaintRoundedButton();
            if (this.model == null && activePlayer == null) {
                WebsocketClient.sendByte((byte) 64, (byte) 5);
            } else {
                trashDiscard(activePlayer.getCardInHand(), model.getOnDiscard());
                activePlayer.clearCardInHand(model.getCardGameModel());
            }
        });

        qp.getSwitchButton().addActionListener(_ -> {
            if (frame.getContentPane().getComponentCount() >= 2) {
                frame.getContentPane().remove(0);
            }

            LabelOverlay lo = new LabelOverlay("Choississez la carte à échanger", frame);
            gameView.setDeckEnabled(0, true);
            gameView.addDeckActionListener(0, deckStackEvents[0]);
            frame.getContentPane().add(lo, 0);
            frame.revalidate();
            frame.repaint();
            repaintRoundedButton();
        });

        if (qp.getPowerButton() != null) {
            qp.getPowerButton().addActionListener(_ -> {
                if (frame.getContentPane().getComponentCount() >= 2) {
                    frame.getContentPane().remove(0);
                }

                LabelOverlay lo = handlePower(cardModel);
                frame.getContentPane().add(lo, 0);
                frame.revalidate();
                frame.repaint();
                repaintRoundedButton();
            });
        }

        frame.revalidate();
        frame.repaint();
        SwingUtilities.invokeLater(() -> {
            gameView.getEndGameButton().revalidate();
            gameView.getEndGameButton().repaint();
            gameView.getExitButton().revalidate();
            gameView.getExitButton().repaint();
        });
    }

    /**
     * @param card the card player picked.
     * @return a LabelOverlay according to the card's value
     */
    private LabelOverlay handlePower(CardModel card) {
        return switch (card.getValue()) {
            case CardModel.CardValue.SEVEN, CardModel.CardValue.EIGHT -> handleSevenEightPower();
            case CardModel.CardValue.NINE, CardModel.CardValue.TEN -> handleNineTenPower();
            case CardModel.CardValue.JACK, CardModel.CardValue.QUEEN -> handleJackQueenPower();
            case CardModel.CardValue.KING -> handleKingPower();
            default -> null;
        };
    }

    /**
     * Initializes the Seven/Eight power by attaching a {@link RevealListener} to
     * the player's own deck.
     * When a card is selected, a {@link LabelOverlay} with an "OK" button is shown
     * to confirm the action
     * and proceed with the turn logic.
     *
     * @return a {@code LabelOverlay} prompting the player to choose a card from
     *         their own deck to reveal
     */
    private LabelOverlay handleSevenEightPower() {
        RevealListener revealListener = new RevealListener(this);

        ExitPowerListener exitPowerListener = new ExitPowerListener(this, revealListener);
        ActionListener okActionListener = e -> {
            exitPowerListener.actionPerformed(e);
            if (model == null) {
                WebsocketClient.sendByte((byte) 64, (byte) 5);
            } else {
                resetAndRepaintRoundedButton();
                trashDiscard(model.getActivePlayer().getCardInHand(), model.getOnDiscard());
                model.getActivePlayer().clearCardInHand(model.getCardGameModel());
            }
        };

        revealListener.setLabelOverlay(new LabelOverlay("", frame, "ok", okActionListener));

        gameView.addDeckActionListener(0, revealListener);
        LabelOverlay lo = new LabelOverlay("Choisissez une carte de votre deck que vous voulez voir.", frame);
        gameView.setDeckEnabled(0, true);
        repaintRoundedButton();
        return lo;
    }

    /**
     * Initializes the Nine/Ten power by setting up a {@link RevealListener} on all
     * other players' decks.
     * When a card is revealed, a {@link LabelOverlay} with an "OK" button is shown,
     * allowing the player to acknowledge the revealed card before proceeding.
     *
     * @return a {@code LabelOverlay} prompting the player to choose a card to
     *         reveal
     */
    private LabelOverlay handleNineTenPower() {
        RevealListener revealListener = new RevealListener(this);
        ExitPowerListener exitPowerListener = new ExitPowerListener(this, revealListener);
        ActionListener okActionListener = e -> {
            exitPowerListener.actionPerformed(e);
            if (model == null) {
                WebsocketClient.sendByte((byte) 64, (byte) 5);
            } else {
                resetAndRepaintRoundedButton();
                trashDiscard(model.getActivePlayer().getCardInHand(), model.getOnDiscard());
                model.getActivePlayer().clearCardInHand(model.getCardGameModel());
            }
        };

        revealListener.setLabelOverlay(new LabelOverlay("", frame, "ok", okActionListener));

        for (int i = 1; i < gameView.nbOfPlayer; i++) {
            gameView.addDeckActionListener(i, revealListener);
            gameView.setDeckEnabled(i, true);
        }
        gameView.addDeckActionListener(0, revealListener);
        LabelOverlay lo = new LabelOverlay("Choisissez une carte du deck de vos adversaires que vous voulez voir.",
                frame);
        return lo;
    }

    /**
     * Initializes the Jack/Queen power by adding a {@link CardSelectionListener} to
     * all players' decks.
     * This allows the player to select two cards they wish to swap.
     * A {@link LabelOverlay} is displayed to guide the player during this
     * interaction.
     *
     * @return a {@code LabelOverlay} prompting the player to choose cards to swap
     */
    private LabelOverlay handleJackQueenPower() {
        CardSelectionListener selectionListener = new CardSelectionListener(this);
        gameView.addDecksActionListener(selectionListener);
        gameView.setDecksEnabled(true);
        repaintRoundedButton();
        LabelOverlay lo = new LabelOverlay("Choisissez les cartes que vous voulez échanger.", frame);
        return lo;
    }

    /**
     * Handles the logic for the King power, allowing the player to reveal an
     * opponent's card
     * and optionally swap it with one from their own deck. Two buttons ("Yes" and
     * "No") are displayed
     * to confirm or cancel the swap.
     *
     * @return a {@code LabelOverlay} prompting the player to choose a card to
     *         reveal and potentially swap
     */
    private LabelOverlay handleKingPower() {
        RevealListener revealListener = new RevealListener(this);
        ExitPowerListener exitPowerListener = new ExitPowerListener(this, revealListener);

        ActionListener noActionListener = createNoActionListener(exitPowerListener);
        ActionListener yesActionListener = createYesActionListener(exitPowerListener, revealListener);

        LabelOverlay confirmationOverlay = new LabelOverlay(
                "Échangez cette carte avec une carte de votre deck ?",
                frame,
                "Oui", yesActionListener,
                "Non", noActionListener);

        revealListener.setLabelOverlay(confirmationOverlay);
        gameView.setDecksEnabled(true);
        gameView.setDeckEnabled(0, false);
        gameView.addDecksActionListener(revealListener);

        return new LabelOverlay(
                "Choisissez une carte que vous voulez voir pour possiblement l'échanger",
                frame);
    }

    /**
     * Creates an {@link ActionListener} for the "No" button, which skips the swap
     * and finishes the current player's turn.
     *
     * @param exitPowerListener the listener used to process the card reveal
     * @return the action listener for the "No" option
     */
    private ActionListener createNoActionListener(ExitPowerListener exitPowerListener) {
        return e -> {
            exitPowerListener.actionPerformed(e);
            if (model == null) {
                WebsocketClient.sendByte((byte) 64, (byte) 5);
                resetAndRepaintRoundedButton();
            } else {
                resetAndRepaintRoundedButton();
                trashDiscard(model.getActivePlayer().getCardInHand(), model.getOnDiscard());
                model.getActivePlayer().clearCardInHand(model.getCardGameModel());
            }
        };
    }

    /**
     * Creates an {@link ActionListener} for the "Yes" button, which initiates a
     * card swap
     * between the revealed opponent card and one from the player's own deck.
     *
     * @param exitPowerListener the listener used to process the card reveal
     * @param revealListener    the listener that stores the selected opponent card
     * @return the action listener for the "Yes" option
     */
    private ActionListener createYesActionListener(ExitPowerListener exitPowerListener, RevealListener revealListener) {
        return e -> {
            exitPowerListener.actionPerformed(e);
            frame.add(new LabelOverlay("Avec quelle carte voulez-vous échanger ?", frame), 0);

            gameView.addDeckActionListener(0,
                    new SwapListener(this, revealListener.getPlayerId(), revealListener.getCardPosition()));
            gameView.setDecksEnabled(false);
            gameView.setDeckEnabled(0, true);
            frame.revalidate();
            frame.repaint();
            resetAndRepaintRoundedButton();
        };
    }

    /**
     * Plays the animation of the new top discard card being placed
     * 
     * @param discardCard the card Model
     */
    public void trashDiscard(CardModel discardCard, CardModel cardBelow) {
        if (discardCard != null) {
            gameView.trashDiscard(discardCard, cardBelow);
        }
    }

    /**
     * Plays the animation of the top discard card being picked.
     * 
     * @param discardCard the card Model.
     */
    public void pickDiscard(CardModel discardCard, CardModel cardBelow) {
        if (discardCard != null) {
            gameView.pickDiscard(discardCard, cardBelow);
        }
    }

    /**
     * Plays the animation of the top stack card being picked.
     */
    public void pickStack() {
        gameView.pickStack();
    }

    /**
     * Plays the animation of a card being reveal.
     * 
     * @param color          a {@code byte} which represents the color of the card
     *                       (00 = HEART, 01 = DIAMOND, 10 = SPADE, 11 = CLUB).
     * @param value          a {@code byte} which represents the value of the card
     *                       (0000 = ACE, ... , 1101 = KING).
     * @param playerPosition a {@code byte} which represents a player position in
     *                       {@code GamView} (0000 = player1 , ... , 1010 =
     *                       player10).
     * @param cardPosition   a a {@code byte} which represents the position of the
     *                       card (00 = first card , ... , 11 = fourth card).
     */
    public void revealPlayerCard(byte color, byte value, byte playerPosition, byte cardPosition, boolean isBlocking) {
        if (color != -1 && value != -1) {
            CardModel card = new CardModel((int) color, (int) value);
            gameView.revealCard(playerPosition, cardPosition, card, isBlocking);
        }
    }

    /**
     * Plays the animation of a card being reveal.
     * 
     * @param player         a {@code PlayerModel} which represents the player
     *                       holding the card.
     * @param playerPosition a {@code byte} which represents a player position in
     *                       {@code GamView} (0000 = player1 , ... , 1010 =
     *                       player10).
     * @param cardPosition   a a {@code byte} which represents the position of the
     *                       card (00 = first card , ... , 11 = fourth card).
     * @param isBlocking     a a {@code boolean} which represents if animation have
     *                       to block the game.
     */
    public void revealPlayerCard(PlayerModel player, byte playerPosition, byte cardPosition, boolean isBlocking) {
        byte color = (byte) player.getCardInDeck(cardPosition).getColor().ordinal();
        byte value = (byte) player.getCardInDeck(cardPosition).getValue().ordinal();
        revealPlayerCard(color, value, playerPosition, cardPosition, isBlocking);
    }

    /**
     * Plays the animation of a card being hide.
     * 
     * @param color          a {@code byte} which represents the color of the card
     *                       (00 = HEART, 01 = DIAMOND, 10 = SPADE, 11 = CLUB).
     * @param value          a {@code byte} which represents the value of the card
     *                       (0000 = ACE, ... , 1101 = KING).
     * @param playerPosition a {@code byte} which represents a player position in
     *                       {@code GamView} (0000 = player1 , ... , 1010 =
     *                       player10).
     * @param cardPosition   a a {@code byte} which represents the position of the
     *                       card (00 = first card , ... , 11 = fourth card).
     * @param isBlocking     a a {@code boolean} which represents if animation have
     *                       to block the game ({@code true} to block the game,
     *                       {@code false} otherwise).
     */
    public void hidePlayerCard(byte color, byte value, byte playerPosition, byte cardPosition, boolean isBlocking) {
        if (color != -1 && value != -1) {
            CardModel card = new CardModel((int) color, (int) value);
            gameView.hideCard(playerPosition, cardPosition, card, isBlocking);
        }
    }

    /**
     * Plays the animation of a card being hide.
     * 
     * @param player         a {@code PlayerModel} which represents the player
     *                       holding the card.
     * @param playerPosition a {@code byte} which represents a player position in
     *                       {@code GamView} (0000 = player1 , ... , 1010 =
     *                       player10).
     * @param cardPosition   a a {@code byte} which represents the position of the
     *                       card (00 = first card , ... , 11 = fourth card).
     * @param isBlocking     a a {@code boolean} which represents if animation have
     *                       to block the game ({@code true} to block the game,
     *                       {@code false} otherwise).
     */
    public void hidePlayerCard(PlayerModel player, byte playerPosition, byte cardPosition, boolean isBlocking) {
        byte color = (byte) player.getCardInDeck(cardPosition).getColor().ordinal();
        byte value = (byte) player.getCardInDeck(cardPosition).getValue().ordinal();
        hidePlayerCard(color, value, playerPosition, cardPosition, isBlocking);
    }

    /**
     * Plays the animation of a card being hide.
     * 
     * @param card           {@code CardModel} which represents the card that will
     *                       be
     *                       revealed.
     * @param playerPosition a {@code byte} which represents a player position in
     *                       {@code GamView} (0000 = player1 , ... , 1010 =
     *                       player10).
     * @param cardPosition   a a {@code byte} which represents the position of the
     *                       card (00 = first card , ... , 11 = fourth card).
     * @param isBlocking     a a {@code boolean} which represents if animation have
     *                       to block the game ({@code true} to block the game,
     *                       {@code false} otherwise).
     */
    public void hidePlayerCard(CardModel card, byte playerPosition, byte cardPosition, boolean isBlocking) {
        byte color = (byte) card.getColor().ordinal();
        byte value = (byte) card.getValue().ordinal();
        hidePlayerCard(color, value, playerPosition, cardPosition, isBlocking);
    }

    /**
     * Plays the animation for two card in different decks being swap.
     * 
     * @param player1Position a {@code byte} which represents the position of the
     *                        first player that initiated the switch.
     * @param player2Position a {@code byte} which represents the position of the
     *                        second player.
     * @param card1Position   a {@code byte} which represents the position of the
     *                        card of player 1.
     * @param card2Position   a {@code byte} which represents the position of the
     *                        card of player 2.
     */
    public void swapCard(byte player1Position, byte player2Position, byte card1Position, byte card2Position) {
        gameView.swapCard(player1Position, card1Position, player2Position, card2Position);
    }

    /**
     * Display animation for put discars card in player hand.
     * 
     * @param playerPosition a {@code byte} which represents the position of the
     *                       player.
     * @param card           a {@code byte} which represents the position of the
     *                       card.
     * @param cardOnTop      a {@code CardModel} which represents the top card.
     * @param colorBelow     a {@code byte} which represents the color of the below
     *                       card can be -1 positionif and only if null card.
     * @param valueBelow     a {@code byte} which represents the value of the below
     *                       card can be -1 positionif and only if null card.
     */
    public void putCardOnDiscard(byte playerPosition, byte card, CardModel cardOnTop, byte colorBelow,
            byte valueBelow) {
        if (colorBelow == -1 && valueBelow == -1)
            gameView.putCardOnDiscard(playerPosition, card, cardOnTop, null);
        else
            gameView.putCardOnDiscard(playerPosition, card, cardOnTop, new CardModel(colorBelow, valueBelow));
    }

    /**
     * Display animation for put discars card in player hand.
     * 
     * @param player         a {@code PlayerModel} which represents the player
     *                       holding the card.
     * @param playerPosition a {@code byte} which represents the position of the
     *                       player.
     * @param cardPosition   a {@code byte} which represents the position of the
     *                       card.
     * @param cardBelow      a {@code CardModel} which represents the below card can
     *                       be -1 positionif and only if null card.
     */
    public void putCardOnDiscard(PlayerModel player, byte playerPosition, byte cardPosition, CardModel cardBelow) {
        CardModel playerCardModel = new CardModel(
                player.getCardInDeck(cardPosition).getColor().ordinal(),
                player.getCardInDeck(cardPosition).getValue().ordinal());
        gameView.putCardOnDiscard(playerPosition, cardPosition, playerCardModel, cardBelow);
    }

    /**
     * Reveal the card base on the game model.
     */
    public void revealLocal() {
        for (byte playerPosition = 0; playerPosition < this.model.getNumberOfPlayer(); playerPosition++) {
            PlayerModel playerModel = model.getPlayerQueue().get(playerPosition);
            for (byte i = 0; i < playerModel.getCardInDeck().size(); i++) {
                revealPlayerCard(((byte) playerModel.getCardInDeck().get(i).getColor().ordinal()),
                        ((byte) playerModel.getCardInDeck().get(i).getValue().ordinal()), playerPosition, i, false);
            }
        }
    }

    /**
     * Hide all cards in local mode.
     */
    public void resetLocalCardIcon() {
        for (byte playerPosition = 0; playerPosition < this.gameView.getPlayers().length; playerPosition++) {
            byte cardPosition = 0;
            for (int i = 0; i < 4; i++) {
                hidePlayerCard(model.getPlayerQueue().get(playerPosition), playerPosition, cardPosition, false);
                cardPosition++;
            }
        }
    }

    /**
     * Reveal the deck base on a message of type byte[]
     * 
     * @param message the message
     */
    public void revealOnline(byte[] message) {
        int i = 2;
        revealedCard = new ArrayList<>();
        while (message[i] != -1) { // end of the message
            byte id = message[i++];
            byte position = 0;
            System.out.println("id : " + id + " position : " + position);
            List<CardModel> deck = new ArrayList<>();
            while (message[i] != -2) { // same player
                revealPlayerCard(
                        message[i],
                        message[i + 1],
                        (byte) playerOrder.indexOf(id),
                        position,
                        false);
                deck.add(new CardModel(message[i], message[i + 1]));
                position++;
                i += 2;
            }
            revealedCard.add(deck);
            i++;
        }
        frame.validate();
        frame.repaint();
    }

    /**
     * Hide all cards in online mode.
     */
    public void resetOnlineCardIcon() {
        for (byte playerPosition = 0; playerPosition < this.gameView.getPlayers().length; playerPosition++) {
            byte cardPosition = 0;
            for (int i = 0; i < 4; i++) {
                hidePlayerCard(
                        revealedCard.get(playerPosition).get(i),
                        playerPosition,
                        cardPosition,
                        false);
                cardPosition++;
            }
        }
    }

    public int getLocalPlayerId(byte playerId) {
        return (playerOrder.indexOf(playerId) - playerOrder.indexOf((byte) WebsocketClient.getId())
                + playerOrder.size()) % playerOrder.size();
    }

    /**
     * Announces the end of the game for the current active player.
     * Sets the current player as the one who triggered the end and updates the view
     * accordingly.
     */
    public void announceTheEnd() {
        model.setPlayerWhoAnnoncedTheEnd(model.getActivePlayer());
        gameView.annonceEndGame(0);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Announces the end of the game for a remote player (used in client-side
     * multiplayer).
     * If the model is not initialized (client-only), it updates the view to show
     * that the player
     * with the given ID has ended the game.
     *
     * @param playerId the ID of the player who announced the end
     * @throws IllegalStateException if the model is initialized, indicating this
     *                               method should not be used server-side
     */
    public void announceTheEnd(byte playerId) {
        if (model == null) {
            gameView.annonceEndGame(playerOrder.indexOf(playerId));
            frame.revalidate();
            frame.repaint();
        } else {
            throw new IllegalStateException("model is null when endGame announce. serveur side.");
        }
    }

    /**
     * Displays the round score in a styled HTML table overlay.
     * Parses the received message to extract player scores, sorts them in
     * descending order,
     * and shows the result in a LabelOverlay on the game frame.
     *
     * @param message the byte array containing player IDs and their scores
     */
    public void displayRoundScore(byte[] message) {
        List<Pair<String, Integer>> playerScores = parsePlayerScores(message);
        playerScores.sort(Comparator.comparingInt((Pair<String, Integer> ps) -> ps.getValue()).reversed());

        String htmlContent = generateScoreTableHTML(playerScores, "");

        if (frame.getContentPane().getComponentCount() >= 2) {
            frame.getContentPane().remove(0);
        }

        LabelOverlay overlay = new LabelOverlay(htmlContent, frame);
        frame.getContentPane().add(overlay, 0);
        gameView.getEndGameButton().setEnabled(false);
        ((RoundedButton) this.gameView.getExitButton()).setAbsoluteBackground(new Color(0, 0, 0, 128));
        frame.revalidate();
        frame.repaint();
        gameView.getExitButton().revalidate();
        gameView.getExitButton().repaint();
    }

    /**
     * Parses the byte message to extract a list of player usernames and scores.
     *
     * @param message the byte array containing player-score pairs
     * @return a list of pairs where each pair holds a player name and their score
     */
    private List<Pair<String, Integer>> parsePlayerScores(byte[] message) {
        List<Pair<String, Integer>> scores = new ArrayList<>();
        int i = 2;
        while (message[i] != -1) {
            String name = gameView.getPlayer(playerOrder.indexOf(message[i])).getLabel().getText();
            int score = message[i + 1];
            scores.add(new Pair<>(name, score));
            i += 2;
        }
        return scores;
    }

    /**
     * Generates an HTML table from the list of player scores.
     *
     * @param scores          the sorted list of player names and scores
     * @param textBeforeTable text to add before the table
     * @return a string containing the HTML table
     */
    private String generateScoreTableHTML(List<Pair<String, Integer>> scores, String textBeforeTable) {
        scores.sort(Comparator.comparingInt((
                Pair<String, Integer> p) -> p.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 2px solid #ffffff; padding: 8px; text-align: left; color: white; }")
                .append("tr:nth-child(even) { background-color: #f2f2f2; }")
                .append("th { font-size: 20px; color: white; background-color: #4CAF50; }")
                .append("</style></head><body>")
                .append(textBeforeTable)
                .append("<table><thead><tr><th colspan='2'>Score de la manche</th></tr></thead>");

        for (Pair<String, Integer> score : scores) {
            sb.append("<tr><td>").append(score.getKey()).append("</td><td>").append(score.getValue())
                    .append("</td></tr>");
        }

        sb.append("</table></body></html>");
        return sb.toString();
    }

    /**
     * Displays the final game score in an HTML-styled table within a LabelOverlay.
     * Highlights the winner and presents each player's total score.
     * 
     * @param winnerId the ID of the winning player
     * @param message  the message byte array containing player IDs and scores
     */
    public void displayGameScore(byte hostLeft, byte winnerId, byte[] message) {
        String winnerName = gameView.getPlayer(playerOrder.indexOf(winnerId)).getLabel().getText();

        List<Pair<String, Integer>> playerScores = new ArrayList<>();
        for (int i = 4; message[i] != -1; i += 2) {
            byte playerId = message[i];
            int score = message[i + 1];
            int playerIndex = playerOrder.indexOf(playerId);
            String playerName;
            if (playerIndex != -1) {
                playerName = gameView.getPlayer(playerIndex).getLabel().getText();
            } else {
                playerName = "Inconnu";
            }
            playerScores.add(new Pair<>(playerName, score));
        }

        String htmlContent = generateScoreTableHTML(playerScores,
                "<h1 style=\"color: white;\">Le vainqueur est : " + winnerName + "</h1>");

        if (frame.getContentPane().getComponentCount() >= 2) {
            frame.getContentPane().remove(0);
        }

        LabelOverlay lo;
        if (playerOrder.get(0) == 0) { // isHost
            lo = new LabelOverlay(htmlContent, frame, "Quitter",
                    _ -> WebsocketClient.sendByte((byte) 85));
        } else {
            lo = new LabelOverlay(htmlContent, frame);
        }

        gameView.setAllButtonsEnabled(false);
        frame.getContentPane().add(lo, 0);
        frame.revalidate();
        frame.repaint();

        if (hostLeft == 1) {
            try {
                Thread.sleep(5000);
                if (frame.getContentPane().getComponentCount() >= 2) {
                    frame.getContentPane().remove(0);
                }
                waitingRoomController.display();
                frame.revalidate();
                frame.repaint();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Prepares a new round in the online game mode.
     * Resets all card icons, removes end-of-game indicators, re-enables game
     * buttons,
     * and refreshes the game interface.
     * 
     */
    public void newRound() {
        SwingUtilities.invokeLater(() -> {
            resetOnlineCardIcon();
            for (PlayerPanel playerPanel : gameView.getPlayers()) {
                playerPanel.removeEndGame();
                playerPanel.setBorder(new LineBorder(Color.black, 2));
            }
            gameView.setGameButtonsEnabled(true);
            ((RoundedButton) this.gameView.getEndGameButton()).setAbsoluteBackground(new Color(0, 0, 0, 0));
            gameView.resetDiscardPileButton();
            frame.revalidate();
            frame.repaint();
        });
    }

    /**
     * Handles the end-of-game logic for online mode by displaying the win/lose/draw
     * screen.
     * Catches and logs any exception that might occur during the display.
     */
    public void endOfTheGame() {
        try {
            displayWRController();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Shows the scoreboard using HTML code to show this table.
     */
    private void showScore() {
        List<Pair<Integer, Integer>> scoreboard = model.getScoreboard();
        List<Pair<String, Integer>> scoreList = new ArrayList<>();
        int i = 0;
        for (Pair<Integer, Integer> score : scoreboard) {
            scoreList.add(i, new Pair<String, Integer>(gameView.getPlayer(score.getKey()).getLabel().getText(),
                    score.getValue()));
        }
        String scoreboardTab = generateScoreTableHTML(scoreList, "");

        if (frame.getContentPane().getComponentCount() >= 2)
            frame.getContentPane().remove(0);
        LabelOverlay labelOverlay = new LabelOverlay(scoreboardTab, this.frame);
        this.frame.getContentPane().add(labelOverlay, 0);
    }

    /**
     * Displays the winner using HTML code to show the winner.
     */
    private void displayWinner() {
        String winner = getWinner();
        if (frame.getContentPane().getComponentCount() >= 2) {
            frame.getContentPane().remove(0);
        }
        List<Pair<Integer, Integer>> scoreboard = model.getScoreboard();
        List<Pair<String, Integer>> scoreList = new ArrayList<>();
        int i = 0;
        for (Pair<Integer, Integer> score : scoreboard) {
            scoreList.add(i, new Pair<String, Integer>(gameView.getPlayer(score.getKey()).getLabel().getText(),
                    score.getValue()));
        }
        LabelOverlay labelOverlay = new LabelOverlay(
                generateScoreTableHTML((scoreList),
                        "<h1 style=\"color: white;\">Le vainqueur est : " + winner + "</h1>"),
                frame);
        this.frame.getContentPane().add(labelOverlay, 0);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Determines the winner of the game based on the scoreboard.
     *
     * @return the name of the player with the lowest score
     */
    private String getWinner() {
        List<Pair<Integer, Integer>> scoreboard = model.getScoreboard();
        int min = scoreboard.get(0).getValue();
        String name = gameView.getPlayer(scoreboard.get(0).getKey()).getLabel().getText();
        for (Pair<Integer, Integer> pair : scoreboard) {
            if (min > pair.getValue()) {
                min = pair.getValue();
                name = gameView.getPlayer(pair.getKey()).getLabel().getText();
            }
        }
        return name;
    }

    /**
     * Back to Waiting room
     */
    public void displayWRController() {
        frame.getContentPane().removeAll();
        waitingRoomController.display();
    }

    /**
     * Redraw with default parameter
     */
    public void resetAndRepaintRoundedButton() {
        SwingUtilities.invokeLater(() -> {
            ((RoundedButton) this.gameView.getEndGameButton()).setAbsoluteBackground(new Color(0, 0, 0, 0));
            ((RoundedButton) this.gameView.getExitButton()).setAbsoluteBackground(new Color(0, 0, 0, 0));
            gameView.getEndGameButton().revalidate();
            gameView.getEndGameButton().repaint();
            gameView.getExitButton().revalidate();
            gameView.getExitButton().repaint();
        });
    }

    /**
     * repaint rounded button
     */
    public void repaintRoundedButton() {
        SwingUtilities.invokeLater(() -> {
            gameView.getEndGameButton().revalidate();
            gameView.getEndGameButton().repaint();
            gameView.getExitButton().revalidate();
            gameView.getExitButton().repaint();
        });
    }
}
