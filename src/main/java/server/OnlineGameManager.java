package main.java.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import main.java.game.model.CardGameModel;
import main.java.game.model.CardModel;
import main.java.game.model.GameModel;
import main.java.game.model.PlayerComputerModel;
import main.java.game.model.PlayerModel;
import main.java.game.view.GameView;
import main.java.util.Pair;

/**
 * This class allows you to interpret received messages and organize multiplayer
 * games by translating instructions on a game into a request to the game model.
 */
public class OnlineGameManager {
    /**
     * List of players in the waiting room.
     */
    private final LinkedList<ClientHandler> players;

    /**
     * Number of players currently in the waiting room.
     */
    private int numberOfPlayers;

    /**
     * Number of required players to start the game.
     */
    private int requiredPlayers;

    /**
     * Unique code for the waiting room.
     */
    private final String code;

    /**
     * Indicates if the waiting room is private.
     */
    private boolean isPrivate;

    /**
     * Indicates if all players are ready.
     */
    private boolean allPlayersReady;

    /**
     * Indicates if the game has started.
     */
    private boolean isStarted;

    /**
     * Difficulty level of the game.
     */
    private int difficulty;

    /**
     * The game model associated with this waiting room.
     */
    private GameModel gameModel;

    /**
     * Constructor for the waiting room model.
     *
     * @param hostPlayer The host player of the waiting room.
     * @param code       The unique code for the waiting room.
     */
    public OnlineGameManager(ClientHandler hostPlayer, String code) {
        this.requiredPlayers = 2; // Minimum number of players required
        this.code = code;
        this.isStarted = false;
        this.players = new LinkedList<>();
        this.players.add(hostPlayer);
        this.numberOfPlayers = 1;
    }

    /**
     * Gets the list of players in the waiting room.
     *
     * @return The list of players.
     */
    public LinkedList<ClientHandler> getPlayers() {
        return players;
    }

    /**
     * Gets the number of players in the waiting room.
     *
     * @return The number of players.
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Gets the number of required players to start the game.
     *
     * @return The number of required players.
     */
    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    /**
     * Gets the number of AI players in the waiting room.
     *
     * @return The number of AI players.
     */
    public int getNumberOfAI() {
        return requiredPlayers - numberOfPlayers;
    }

    /**
     * Gets the difficulty level of the game.
     *
     * @return The difficulty level.
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty level of the game.
     *
     * @param difficulty The difficulty level to set.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Adds a player to the waiting room.
     *
     * @param player The player to be added.
     * @return True if the player is added, false otherwise.
     */
    public boolean addPlayer(ClientHandler player) {
        if (numberOfPlayers < requiredPlayers) {
            players.add(player);
            numberOfPlayers++;
            allPlayersReady = false;
            return true;
        }
        return false;
    }

    /**
     * Removes a player from the waiting room.
     *
     * @param player The player to be removed.
     * @return True if the player is removed, false otherwise.
     */
    public boolean removePlayer(ClientHandler player) {
        System.out.println("Remove player");
        players.forEach(p -> p.setReady(false));
        if (players.contains(player)) {
            System.out.println("Removing player");
            players.remove(player);
            numberOfPlayers--;
            return true;
        }
        return false;
    }

    /**
     * Adds an AI player to the waiting room.
     */
    public void addAIPlayer() {
        requiredPlayers++;
    }

    /**
     * Removes an AI player from the waiting room.
     */
    public void removeAIPlayer() {
        requiredPlayers--;
    }

    /**
     * Resets the ready status of all players in the waiting room.
     */
    public void resetPlayersReady() {
        for (ClientHandler player : players) {
            player.setReady(false);
        }
    }

    /**
     * Gets the unique code of the waiting room.
     *
     * @return The unique code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the privacy status of the waiting room.
     *
     * @param isPrivate True if the waiting room is private, false otherwise.
     */
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * Gets the privacy status of the waiting room.
     *
     * @return True if the waiting room is private, false otherwise.
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Sets the ready status of all players.
     *
     * @param allPlayersReady True if all players are ready, false otherwise.
     */
    public void setAllPlayersReady(boolean allPlayersReady) {
        this.allPlayersReady = allPlayersReady;
    }

    /**
     * Gets the ready status of all players.
     *
     * @return True if all players are ready, false otherwise.
     */
    public boolean areAllPlayersReady() {
        return allPlayersReady;
    }

    /**
     * Sets the ready status of all players in the waiting room.
     *
     * @param isReady True if players are ready, false otherwise.
     */
    public void setPlayersReady(boolean isReady) {
        for (ClientHandler clientHandler : players) {
            clientHandler.setReady(isReady);
            getPlayers().forEach(
                    player -> player.sendByte((byte) 67, (byte) clientHandler.getGameId(), (byte) 0));
        }
    }

    /**
     * Notifies all players about a switch between hand and deck.
     *
     * @param playerId The ID of the player.
     * @param position The position of the card.
     * @param top      The top card.
     * @param belowTop The card below the top.
     */
    private void notifySwitchHandAndDeck(byte playerId, byte position, CardModel top, CardModel belowTop) {
        if (belowTop == null) {
            players.forEach(p -> p.sendByte((byte) 1, (byte) 4, playerId, position,
                    (byte) top.getColor().ordinal(),
                    (byte) top.getValue().ordinal(),
                    (byte) -1,
                    (byte) -1));
        } else {
            players.forEach(p -> p.sendByte((byte) 1, (byte) 4, playerId, position,
                    (byte) top.getColor().ordinal(),
                    (byte) top.getValue().ordinal(),
                    (byte) belowTop.getColor().ordinal(),
                    (byte) belowTop.getValue().ordinal()));
        }
        try {
            Thread.sleep((int) Math.round(6000 / GameView.getAnimSpeed()));
        } catch (InterruptedException e) {
            System.err.println("An error has occurred in a thread sleep");
        }
    }

    /**
     * Notifies all players about the top discard card.
     *
     * @param cardModel The top discard card.
     * @param cardBelow The card below the top discard card.
     */
    private void notifyTrashDiscard(CardModel cardModel, CardModel cardBelow) {
        System.out.println(cardModel);
        if (cardBelow == null) {
            players.forEach(player -> {
                player.sendByte((byte) 1, (byte) 25,
                        (byte) cardModel.getColor().ordinal(),
                        (byte) cardModel.getValue().ordinal(),
                        (byte) -1,
                        (byte) -1);
            });
        } else {
            players.forEach(player -> {
                player.sendByte((byte) 1, (byte) 25,
                        (byte) cardModel.getColor().ordinal(),
                        (byte) cardModel.getValue().ordinal(),
                        (byte) cardBelow.getColor().ordinal(),
                        (byte) cardBelow.getValue().ordinal());
            });
        }
        try {
            Thread.sleep((int) Math.round(3000 / GameView.getAnimSpeed()));
        } catch (InterruptedException e) {
            System.err.println("An error has occurred in a thread sleep");
        }
    }

    /**
     * Notifies all players about a discard action.
     *
     * @param cardModel The card to be discarded.
     * @param cardBelow The card below the discarded card.
     */
    private void notifyWantDiscard(CardModel cardModel, CardModel cardBelow) {
        System.out.println(cardModel);
        if (cardModel != null) {
            if (cardBelow == null)
                players.forEach(player -> player.sendByte((byte) 1, (byte) 26,
                        (byte) cardModel.getColor().ordinal(),
                        (byte) cardModel.getValue().ordinal(),
                        (byte) -1,
                        (byte) -1));
            else
                players.forEach(player -> {
                    player.sendByte((byte) 1, (byte) 26,
                            (byte) cardModel.getColor().ordinal(),
                            (byte) cardModel.getValue().ordinal(),
                            (byte) cardBelow.getColor().ordinal(),
                            (byte) cardBelow.getValue().ordinal());
                });
            try {
                Thread.sleep((int) Math.round(3000 / GameView.getAnimSpeed()));
            } catch (InterruptedException e) {
                System.err.println("An error has occurred in a thread sleep");
            }
        }
    }

    /**
     * Notifies all players about a switch between two cards.
     *
     * @param playerId1 The ID of the first player.
     * @param playerId2 The ID of the second player.
     * @param card1     The first card.
     * @param card2     The second card.
     */
    private void notifySwapCards(byte playerId1, byte playerId2, byte card1, byte card2) {
        players.forEach(player -> player.sendByte((byte) 1, (byte) 50, playerId1, playerId2, card1, card2));
        try {
            Thread.sleep((int) Math.round(6000 / GameView.getAnimSpeed() + 750));
        } catch (InterruptedException e) {
            System.err.println("An error has occurred in a thread sleep");
        }
    }

    /**
     * Handles the computer player's turn.
     *
     * @param pc   The computer player.
     * @param card The card to play.
     */
    private void computerPlay(PlayerComputerModel pc, CardModel card) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.err.println("An error has occurred in a thread sleep");
        }
        pc.giveCardInHand(card);
        int[] action = pc.playComputerTurn_Pick(pc.getCardInHand());
        switch (action[0]) {
            case -1 -> {
                pc.clearCardInHand(gameModel.getCardGameModel());
                notifyTrashDiscard(gameModel.getOnDiscard(), gameModel.get2ndOnDiscard());
            }
            case 1 -> {
                // Swap hand and deck
                System.out.println("Swap hand and deck");
                gameModel.getCardGameModel().discardCard(pc.getCardInDeck(action[1]));
                notifySwitchHandAndDeck((byte) pc.getGameId(), (byte) action[1], gameModel.getOnDiscard(),
                        gameModel.get2ndOnDiscard());
                pc.setCardInDeck(action[1], pc.getCardInHand());
                pc.giveCardInHand(null); // Remove the card. Caution do not discard it
            }
            case 2 -> {
                // Power
                pc.memorizeCard(action[1], pc.getCardInDeck(action[1]));
                pc.clearCardInHand(gameModel.getCardGameModel());
                notifyTrashDiscard(gameModel.getOnDiscard(), gameModel.get2ndOnDiscard());
            }
            case 3 -> {
                // Watch action[1] of his card
                pc.memorizeCardOfPlayer(action[1], action[2],
                        gameModel.getPlayerQueue().get(action[1]).getCardInDeck(action[2]));
                pc.clearCardInHand(gameModel.getCardGameModel());
                notifyTrashDiscard(gameModel.getOnDiscard(), gameModel.get2ndOnDiscard());
            }
            case 4 -> {
                // Watch action[2] action[1] player's card
                PlayerModel j1 = gameModel.getPlayerQueue().get(action[1]);
                PlayerModel j2 = gameModel.getPlayerQueue().get(action[3]);
                int p1position = (action[1] + 1) % gameModel.getNumberOfPlayer();
                int p2position = (action[3] + 1) % gameModel.getNumberOfPlayer();
                gameModel.swapCard(j1, action[2], j2, action[4]);
                notifySwapCards((byte) p1position, (byte) p2position, (byte) action[2], (byte) action[4]);
                pc.clearCardInHand(gameModel.getCardGameModel());
                notifyTrashDiscard(gameModel.getOnDiscard(), gameModel.get2ndOnDiscard());
            }
            case 5 -> {
                pc.memorizeCard(action[2], pc.getCardInDeck(action[2]));
                if (pc.activeKingEffect(pc.getCardInDeck(action[2]), action)) {
                    PlayerModel j1 = this.gameModel.getPlayerQueue().get(action[1]);
                    PlayerModel j2 = this.gameModel.getPlayerQueue().get(action[3]);
                    int p1position = (action[1] + 1) % gameModel.getNumberOfPlayer();
                    int p2position = (action[3] + 1) % gameModel.getNumberOfPlayer();
                    this.gameModel.swapCard(j1, action[2], j2, action[4]);
                    pc.memorizeCardOfPlayer(action[1], action[2], j1.getCardInDeck(action[2]));
                    pc.memorizeCardOfPlayer(action[3], action[4], j2.getCardInDeck(action[4]));
                    notifySwapCards((byte) p1position, (byte) p2position, (byte) action[2], (byte) action[4]);
                }
                pc.clearCardInHand(gameModel.getCardGameModel());
                notifyTrashDiscard(gameModel.getOnDiscard(), gameModel.get2ndOnDiscard());
            }
        }
    }

    /**
     * Handles the next turn in the game.
     */
    private void nextTurn() {
        try {
            if (gameModel.getPlayerWhoAnnouncedTheEnd() != gameModel.getPlayerQueue().getFirst()) {
                gameModel.nextPlayer();
                players.forEach(player -> {
                    player.sendByte((byte) 1, (byte) 1, (byte) gameModel.getActivePlayer().getGameId());
                });
                if (gameModel.getActivePlayer() instanceof PlayerComputerModel pc) {
                    System.out.println("    AI is playing");
                    int action = pc.playComputerTurn_WantDiscard(gameModel.getCardGameModel().getTopDiscard());
                    if (action != -1 && gameModel.getCardGameModel().getDiscardStackSize() != 0) {
                        System.out.println("    Takes from the discard");
                        Thread.sleep(1000);
                        notifyWantDiscard(gameModel.getOnDiscard(), gameModel.get2ndOnDiscard());
                        pc.giveCardInHand(gameModel.getCardGameModel().popDiscard());
                        gameModel.getCardGameModel().discardCard(pc.getCardInDeck(action));
                        notifySwitchHandAndDeck((byte) pc.getGameId(), (byte) action, gameModel.getOnDiscard(),
                                gameModel.get2ndOnDiscard());
                        pc.setCardInDeck(action, pc.getCardInHand());
                        pc.giveCardInHand(null);
                        System.out.println("    Finishes turn");
                    } else {
                        System.out.println("    Draws from the deck");
                        players.forEach(player -> {
                            player.sendByte((byte) 1, (byte) 3);
                        });
                        Thread.sleep(1000);
                        computerPlay(pc, gameModel.drawCard());
                        Thread.sleep(1000);
                        System.out.println("    Finishes turn");
                    }
                    if (pc.finish() && gameModel.getPlayerWhoAnnouncedTheEnd() == null) {
                        this.gameModel.setPlayerWhoAnnoncedTheEnd(pc);
                        players.forEach(player -> {
                            player.sendByte((byte) 1, (byte) -1,
                                    (byte) gameModel.getPlayerWhoAnnouncedTheEnd().getGameId());
                        });
                    }
                    nextTurn();
                }
            } else {
                // End of the round
                // Send reveal
                Thread.sleep(2000);
                ArrayList<Byte> message = new ArrayList<>();
                message.add((byte) 1);
                message.add((byte) -2);
                for (Pair<Integer, ArrayList<CardModel>> deck : gameModel.getDecks()) {
                    message.add(deck.getKey().byteValue());
                    for (CardModel cardModel : deck.getValue()) {
                        message.add((byte) cardModel.getColor().ordinal());
                        message.add((byte) cardModel.getValue().ordinal());
                    }
                    message.add((byte) -2); // Player separation
                }
                message.add((byte) -1);
                byte[] revealMessage = new byte[message.size()];
                for (int i = 0; i < message.size(); i++)
                    revealMessage[i] = message.get(i);
                players.forEach(player -> player.sendByte(revealMessage));
                Thread.sleep(3000 * gameModel.getPlayerQueue().size());
                // Send scoreboard
                message = new ArrayList<>();
                gameModel.addScore();
                message.add((byte) 1);
                message.add((byte) -3);
                for (Pair<Integer, Integer> score : gameModel.getScoreboard()) {
                    message.add((byte) score.getKey().byteValue());
                    message.add((byte) score.getValue().byteValue());
                }
                message.add((byte) -1);
                byte[] scoreboardMessage = new byte[message.size()];
                for (int i = 0; i < message.size(); i++)
                    scoreboardMessage[i] = message.get(i);
                players.forEach(player -> player.sendByte(scoreboardMessage));
                Thread.sleep(5000);
                if (gameModel.isGameFinished()) {
                    byte winnerId = (byte) gameModel.getWinner();
                    byte[] winnerMessage = new byte[3 + scoreboardMessage.length - 1];
                    winnerMessage[0] = (byte) 1;
                    winnerMessage[1] = (byte) -4;
                    winnerMessage[2] = (byte) (isHostLeft() ? 1 : 0);
                    winnerMessage[3] = winnerId;
                    int numPlayers = gameModel.getPlayerQueue().size();
                    System.arraycopy(scoreboardMessage, 2, winnerMessage, 4, numPlayers * 2);
                    winnerMessage[winnerMessage.length - 1] = -1;
                    players.forEach(player -> player.sendByte(winnerMessage));
                    gameModel.clearPlayerCard();
                    gameModel.setCardGameModel(new CardGameModel(gameModel.getCardGameModel().getSize()));
                    gameModel.restart();
                } else {
                    players.forEach(player -> player.sendByte((byte) 1, (byte) -5));
                    gameModel.clearPlayerCard();
                    gameModel.setCardGameModel(new CardGameModel(gameModel.getCardGameModel().getSize()));
                    gameModel.restart();
                    gameModel.distribute();
                    nextTurn();
                }
            }
        } catch (InterruptedException e) {
            System.err.println("An error has occurred in a thread sleep");
        }
    }

    /**
     * Handles actions based on the message received.
     *
     * @param message  The message received.
     * @param clientId The ID of the client.
     * @param gameId   The ID of the game.
     */
    public void action(byte[] message, int clientId, int gameId) {
        byte[] response;
        ArrayList<Byte> aResponse;
        if (gameModel == null || gameId != gameModel.getActivePlayer().getGameId()) {
            System.out.println("    Not processed: model null " + gameModel == null);
            return;
        }
        System.out.println(gameModel.getPlayerQueue());
        switch (message[1]) {
            case 2 -> {
                // Draw from the deck
                System.out.println("    Message received: draw from the deck");
                gameModel.getActivePlayer().giveCardInHand(gameModel.drawCard());
                players.forEach(player -> {
                    player.sendByte((byte) 1, (byte) 3);
                });
                try {
                    Thread.sleep(3000 / (int) GameView.getAnimSpeed() + 500);
                } catch (InterruptedException e) {
                    System.err.println("An error has occurred in a thread sleep");
                }
                players.forEach(p -> {
                    if (p.getGameId() == gameId)
                        p.sendByte((byte) 1, (byte) 2,
                                (byte) gameModel.getActivePlayer().getCardInHand().getColor().ordinal(),
                                (byte) gameModel.getActivePlayer().getCardInHand().getValue().ordinal());
                });
                break;
            }
            case 3 -> {
                // Draw from the discard
                System.out.println("    Message received: draw from the discard");
                if (gameModel.getOnDiscard() == null) {
                    players.forEach(player -> {
                        player.sendByte((byte) 1, (byte) 1, (byte) gameModel.getActivePlayer().getGameId());
                    });
                    return;
                }
                gameModel.getActivePlayer().giveCardInHand(gameModel.getCardGameModel().popDiscard());
                notifyWantDiscard(gameModel.getActivePlayer().getCardInHand(),
                        gameModel.getCardGameModel().getTopDiscard());
                players.get(clientId).sendByte((byte) 1, (byte) 2,
                        (byte) gameModel.getActivePlayer().getCardInHand().getColor().ordinal(),
                        (byte) gameModel.getActivePlayer().getCardInHand().getValue().ordinal(),
                        (byte) 0);
                break;
            }
            case 4 -> { // Swap hand and deck
                System.out.println("    Message received: swap hand and deck");
                CardModel cardModel = gameModel.getActivePlayer().getCardInDeck(message[2]);
                gameModel.getCardGameModel().discardCard(cardModel);

                // Set the card to the correct position
                gameModel.getActivePlayer().setCardInDeck(message[2], gameModel.getActivePlayer().getCardInHand());
                gameModel.getActivePlayer().giveCardInHand(null);

                // Notify all players
                notifySwitchHandAndDeck((byte) gameModel.getActivePlayer().getGameId(), message[2],
                        gameModel.getOnDiscard(),
                        gameModel.get2ndOnDiscard());
                // End of the turn
                nextTurn();
            }
            case 5 -> {
                // Discard the card
                System.out.println("    Message received: discard card");
                // Notify discard
                notifyTrashDiscard(gameModel.getActivePlayer().getCardInHand(),
                        gameModel.getCardGameModel().getTopDiscard());
                // Clear the hand
                gameModel.getActivePlayer().clearCardInHand(gameModel.getCardGameModel());
                // Next player
                nextTurn();
            }
            case 6 -> {
                // Hides the card of the player
                CardModel card = gameModel.getCardOf(message[3], message[5]);
                players.get(message[2]).sendByte((byte) 1, (byte) 6, (byte) card.getColor().ordinal(),
                        (byte) card.getValue().ordinal(), message[4], message[5]);
                try {
                    Thread.sleep((int) Math.round(6000 / GameView.getAnimSpeed()));
                } catch (InterruptedException e) {
                    System.err.println("An error has occurred in a thread sleep");
                }
            }
            case 7 -> {
                // Reveals the card of the player
                CardModel card = gameModel.getCardOf(message[3], message[5]);
                players.get(message[2]).sendByte((byte) 1, (byte) 7, (byte) card.getColor().ordinal(),
                        (byte) card.getValue().ordinal(), message[4], message[5]);
                try {
                    Thread.sleep((int) Math.round(6000 / GameView.getAnimSpeed()));
                } catch (InterruptedException e) {
                    System.err.println("An error has occurred in a thread sleep");
                }
            }
            case 8 -> {
                // Swap the cards between players
                System.out.println("    Message received: switch card");
                notifySwapCards(message[6], message[7], message[4], message[5]);
                gameModel.swapCard(gameModel.getPlayerQueue().get(message[2]), message[4],
                        gameModel.getPlayerQueue().get(message[3]), message[5]);
            }
            case -1 -> {
                if (gameModel.getPlayerWhoAnnouncedTheEnd() == null
                        && gameModel.getActivePlayer().getGameId() == message[2]) {
                    // Announce the end
                    gameModel.setPlayerWhoAnnouncedTheEnd(gameModel.getActivePlayer());
                    // Broadcast message
                    players.forEach(player -> {
                        player.sendByte((byte) 1, (byte) -1,
                                (byte) gameModel.getPlayerWhoAnnouncedTheEnd().getGameId());
                    });
                }
            }
            case -2 -> {
                // Send deck
                LinkedList<Pair<Integer, ArrayList<CardModel>>> decks = gameModel.getDecks();
                aResponse = new ArrayList<>();
                aResponse.add((byte) 1);
                aResponse.add((byte) -2);
                for (int i = 0; i < decks.size(); i++) {
                    Pair<Integer, ArrayList<CardModel>> pair = decks.get(i);
                    aResponse.add(pair.getKey().byteValue());
                    for (int j = 0; j < pair.getValue().size(); j++) {
                        aResponse.add((byte) pair.getValue().get(j).getValue().ordinal());
                        aResponse.add((byte) pair.getValue().get(j).getColor().ordinal());
                    }
                }

                byte[] byteArray = new byte[aResponse.size()];
                for (int i = 0; i < aResponse.size(); i++) {
                    byteArray[i] = aResponse.get(i);
                }
                players.forEach(player -> {
                    player.sendByte(byteArray);
                });
            }
            case -3 -> {
                // Send round score
                List<Pair<Integer, Integer>> scoreboard = gameModel.getScoreboard();
                response = new byte[scoreboard.size() * 2 + 2];
                response[0] = 1;
                response[1] = -2;
                for (int i = 2; i < response.length; i += 2) {
                    Pair<Integer, Integer> pair = scoreboard.get(((i - 2) / 2));
                    response[i] = (byte) pair.getKey().byteValue();
                    response[i + 1] = (byte) pair.getValue().byteValue();
                }

                players.forEach(player -> {
                    player.sendByte(response);
                });
            }
            case -4 -> {
            }
            case -5 -> {
            }
            case -6 -> {
            }
            case -7 -> {
            }
        }

        // Send game score
        // New round
        // End of the game
        // Display leader board
    }

    /**
     * Initializes the game model.
     */
    public void initGameModel() {
        PlayerComputerModel.setNbofPlayer(requiredPlayers);
        this.gameModel = new GameModel(104, players, requiredPlayers - numberOfPlayers, difficulty);
        this.gameModel.distribute();
        this.isStarted = true;
    }

    /**
     * Checks if the game has started.
     *
     * @return True if the game has started, false otherwise.
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Check if all player had the ready status
     */
    public boolean isAllPlayerReady() {
        boolean result = true;
        for (ClientHandler player : players) {
            result &= player.isReady();
        }
        return result;
    }

    /**
     * Set all player ready
     */
    public void setAllPlayerReady(boolean status) {
        for (ClientHandler player : players) {
            player.setReady(status);
        }
    }

    /**
     * Replace a player by ai when player exit
     * 
     * @param gameId the game id of the leaving player
     */
    public void replacePlayerByAi(int gameId, PlayerComputerModel ai) {
        int index = 0;
        while (gameModel.getPlayerQueue().get(index).getGameId() != gameId) {
            index++;
        }

        PlayerModel exPlayer = gameModel.getPlayerQueue().get(index);

        /* deep copy */
        for (CardModel card : exPlayer.getCardInDeck()) {
            ai.giveCardInDeck(card);
        }
        ai.addScore(exPlayer.getScore());
        ai.giveCardInHand(exPlayer.getCardInHand());
        if (gameModel.getPlayerWhoAnnouncedTheEnd() == exPlayer)
            gameModel.setPlayerWhoAnnoncedTheEnd(ai);
        gameModel.getPlayerQueue().set(index, ai);

        /* restart the turn if it was the explayer turn */
        if (gameModel.getActivePlayer().getGameId() == gameId && ai.getCardInHand() != null) {
            computerPlay(ai, ai.getCardInHand());
            nextTurn();
        } else if (gameModel.getActivePlayer().getGameId() == gameId) {
            players.forEach(p -> p.sendByte((byte) 1, (byte) 3));
            computerPlay(ai, gameModel.drawCard());
            nextTurn();
        }
    }

    public boolean isHostLeft() {
        for (ClientHandler clientHandler : players) {
            if (clientHandler.getGameId() == 0) {
                return false;
            }
        }
        return true;
    }
}
