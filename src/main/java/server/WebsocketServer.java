package main.java.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import main.java.util.StringGenerator;

/**
 * WebsocketServer manages the WebSocket server, handling client connections,
 * waiting rooms,
 * and game-related operations. It maintains a list of connected clients and
 * waiting rooms.
 */
public class WebsocketServer {
    protected static final int PORT = 8080;
    protected static final int MAX_CLIENTS = 250; // Maximum number of clients
    protected static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    protected static int clientIdCounter = 0;
    protected static final LinkedList<OnlineGameManager> waitingRoomList = new LinkedList<>();

    @SuppressWarnings("SleepWhileInLoop")
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on 127.0.0.1:" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accepter la connexion

                if (clients.size() < MAX_CLIENTS) {
                    int clientId = clientIdCounter++;
                    clientId %= MAX_CLIENTS;
                    while (clients.containsKey(clientId)) {
                        clientId = (clientId + 1) % MAX_CLIENTS;
                    }
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
                    clients.put(clientId, clientHandler);
                    new Thread(clientHandler).start();
                } else {
                    System.out.println("Max clients reached, rejecting connection.");
                    clientSocket.close();
                    Thread.sleep(1000);
                }
            }
        } catch (IOException | InterruptedException e) {
        }
    }

    /**
     * Sends waiting room information to all players in the waiting room.
     *
     * @param waitingRoomModel The waiting room model containing player information.
     * @param code             The unique code of the waiting room.
     */
    static void sendToWaitingRoom(OnlineGameManager waitingRoomModel, String code) {
        sendToWaitingRoom(waitingRoomModel.getPlayers(),
                waitingRoomModel.getNumberOfAI(),
                waitingRoomModel.getDifficulty(),
                waitingRoomModel.isPrivate() ? 1 : 0,
                waitingRoomModel.getCode());
    }

    /**
     * Sends waiting room information to specified players.
     *
     * @param players    The list of players to send the information to.
     * @param numberOfAI The number of AI players in the waiting room.
     * @param difficulty The difficulty level of the AI players.
     * @param privacy    The privacy status of the room
     * @param code       The unique code of the waiting room.
     */
    static void sendToWaitingRoom(LinkedList<ClientHandler> players, int numberOfAI, int difficulty, int privacy,
            String code) {
        LinkedList<Byte> message = new LinkedList<>();
        message.add((byte) 66); // Header byte indicating waiting room update

        // Add player information
        for (ClientHandler player : players) {
            message.add((byte) (player.isReady() ? 1 : 0)); // Ready status
            message.add((byte) player.getId()); // Player ID
            for (byte b : player.getPseudo().getBytes()) {
                message.add(b); // Player pseudonym
            }
            message.add((byte) -2); // Separator
        }

        // Add AI information
        message.add((byte) -1); // AI part separator
        for (int i = 0; i < numberOfAI; i++) {
            message.add((byte) 1); // AI ready status
            message.add((byte) difficulty); // AI difficulty
            String aiPseudo = "ai " + i;
            for (byte b : aiPseudo.getBytes()) {
                message.add(b); // AI pseudonym
            }
            message.add((byte) -2); // Separator
        }
        message.add((byte) -1); // End of message

        // Add code information
        if (code != null) {
            for (byte b : code.getBytes()) {
                message.add(b);
            }
        }

        message.add((byte) -1);
        message.add((byte) (players.getFirst().getSpeedAnimation() * 10));
        message.add((byte) privacy);

        // Convert message to byte array and send to players
        byte[] messageByte = toByteArray(message);
        for (ClientHandler clientHandler : players) {
            clientHandler.sendByte(messageByte);
        }
    }

    /**
     * Converts a LinkedList of bytes to a byte array.
     *
     * @param message The LinkedList of bytes.
     * @return The byte array.
     */
    private static byte[] toByteArray(LinkedList<Byte> message) {
        byte[] messageByte = new byte[message.size()];
        for (int index = 0; index < messageByte.length; index++) {
            messageByte[index] = message.get(index);
        }
        return messageByte;
    }

    /**
     * Removes a client from the waiting room.
     *
     * @param clientId The ID of the client to remove.
     */
    static void removeClientFromWaitingroom(int clientId) {
        for (OnlineGameManager waitingRoomModel : waitingRoomList) {
            if (waitingRoomModel.getPlayers().contains(clients.get(clientId))) {
                waitingRoomModel.removePlayer(clients.get(clientId));
                clients.get(clientId).setWaitingRoom(null);
                notifyPlayersOfRemoval(waitingRoomModel, clientId);

                if (waitingRoomModel.getNumberOfPlayers() == 0) {
                    waitingRoomList.remove(waitingRoomModel);
                }
                break;
            }
        }
    }

    /**
     * Notifies other players in the waiting room about the removal of a client.
     *
     * @param waitingRoomModel The waiting room model.
     * @param clientId         The ID of the client that was removed.
     */
    private static void notifyPlayersOfRemoval(OnlineGameManager waitingRoomModel, int clientId) {
        for (ClientHandler clientHandler : waitingRoomModel.getPlayers()) {
            clientHandler.sendByte((byte) 69, (byte) clientId);
        }
        clients.get(clientId).setReady(false);
    }

    /**
     * Removes a client from the server.
     *
     * @param clientId The ID of the client to remove.
     */
    static void removeClient(int clientId) {
        removeClientFromWaitingroom(clientId);
        clients.remove(clientId);
        System.out.println("Client " + clientId + " disconnected.");
    }

    /**
     * Removes an AI player from the waiting room.
     *
     * @param clientID The ID of the client requesting the removal.
     */
    static void removeAIPlayer(int clientID) {
        OnlineGameManager waitingRoomModel = clients.get(clientID).getWaitingRoom();
        if (waitingRoomModel.getRequiredPlayers() > 2) {
            waitingRoomModel.removeAIPlayer();
            notifyAIPlayerRemoval(waitingRoomModel);
        }
    }

    /**
     * Notifies players about the removal of an AI player.
     *
     * @param waitingRoomModel The waiting room model.
     */
    private static void notifyAIPlayerRemoval(OnlineGameManager waitingRoomModel) {
        for (ClientHandler player : waitingRoomModel.getPlayers()) {
            player.sendByte((byte) 82);
        }
    }

    /**
     * Adds an AI player to the waiting room.
     *
     * @param clientId The ID of the client requesting the addition.
     */
    static void addAIPlayer(int clientId) {
        OnlineGameManager waitingRoomModel = clients.get(clientId).getWaitingRoom();
        waitingRoomModel.addAIPlayer();
        sendToWaitingRoom(waitingRoomModel, waitingRoomModel.getCode());
    }

    /**
     * Adds a client to a waiting room based on the provided code.
     *
     * @param client The client handler of the client.
     * @param code   The unique code of the waiting room.
     */
    public static void client(ClientHandler client, String code) {
        for (OnlineGameManager waitingRoomModel : waitingRoomList) {
            if (canJoinWaitingRoom(waitingRoomModel, code)) {
                addClientToWaitingRoom(client, waitingRoomModel);
                return;
            } else if (code != null && waitingRoomModel.getCode().equals(code)) {
                client.sendByte((byte) 65, (byte) 0); // Room is full
                return;
            }
        }

        if (code != null) {
            client.sendByte((byte) 65, (byte) 1); // Room not found
            return;
        }

        // Create a new waiting room if none is found
        createWaitingRoom(client.getId(), false);
    }

    /**
     * Checks if a client can join a waiting room.
     *
     * @param waitingRoomModel The waiting room model.
     * @param code             The unique code of the waiting room.
     * @return True if the client can join, false otherwise.
     */
    private static boolean canJoinWaitingRoom(OnlineGameManager waitingRoomModel, String code) {
        return waitingRoomModel.getNumberOfPlayers() != waitingRoomModel.getRequiredPlayers()
                && !waitingRoomModel.isStarted()
                && ((code == null && !waitingRoomModel.isPrivate())
                        || (code != null && code.equals(waitingRoomModel.getCode())));
    }

    /**
     * Adds a client to a waiting room.
     *
     * @param client           The client handler of the client.
     * @param waitingRoomModel The waiting room model.
     */
    private static void addClientToWaitingRoom(ClientHandler client, OnlineGameManager waitingRoomModel) {
        waitingRoomModel.addPlayer(client);
        client.setWaitingRoom(waitingRoomModel);
        client.setReady(false);
        sendToWaitingRoom(waitingRoomModel, waitingRoomModel.getCode());
    }

    /**
     * Creates a new waiting room.
     *
     * @param clientId  The ID of the client creating the room.
     * @param isPrivate True if the room is private, false otherwise.
     */
    public static void createWaitingRoom(int clientId, boolean isPrivate) {
        String code = generateUniqueCode();
        OnlineGameManager waitingRoomModel = new OnlineGameManager(clients.get(clientId), code);
        waitingRoomModel.setPrivate(isPrivate);
        waitingRoomList.add(waitingRoomModel);
        clients.get(clientId).setWaitingRoom(waitingRoomModel);
        sendToWaitingRoom(waitingRoomModel, code);
    }

    /**
     * Generates a unique code for a waiting room.
     *
     * @return A unique code.
     */
    private static String generateUniqueCode() {
        String code;
        do {
            code = StringGenerator.generateRandomString(6, "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
        } while (!isUnique(code));
        return code;
    }

    /**
     * Checks if a code is unique among existing waiting rooms.
     *
     * @param code The code to check.
     * @return True if the code is unique, false otherwise.
     */
    private static boolean isUnique(String code) {
        for (OnlineGameManager waitingRoomModel : waitingRoomList) {
            if (waitingRoomModel.getCode().equals(code)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the ready status of a player.
     *
     * @param clientId The ID of the client.
     */
    public static void setPlayerReady(int clientId) {
        ClientHandler clientHandler = clients.get(clientId);
        clientHandler.setReady(!clientHandler.isReady());
        if (!clientHandler.isReady()) {
            clientHandler.getWaitingRoom().setAllPlayerReady(false);
        }
        notifyReadyStatus(clientHandler);

        if (areAllPlayersReady(clientHandler)) {
            startCountdown(clientHandler);
        }
    }

    /**
     * Notifies other players about the ready status of a player.
     *
     * @param clientHandler The client handler of the client.
     */
    private static void notifyReadyStatus(ClientHandler clientHandler) {
        for (ClientHandler player : clientHandler.getWaitingRoom().getPlayers()) {
            player.sendByte((byte) 67, (byte) clientHandler.getId(), (byte) (clientHandler.isReady() ? 1 : 0));
        }
    }

    /**
     * Checks if all players in the waiting room are ready.
     *
     * @param clientHandler The client handler of the client.
     * @return True if all players are ready, false otherwise.
     */
    private static boolean areAllPlayersReady(ClientHandler clientHandler) {
        for (ClientHandler player : clientHandler.getWaitingRoom().getPlayers()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts the countdown for the game to begin.
     *
     * @param clientHandler The client handler of the client.
     */
    @SuppressWarnings("SleepWhileInLoop")
    private static void startCountdown(ClientHandler clientHandler) {
        clientHandler.getWaitingRoom().setAllPlayerReady(true);
        new Thread(() -> {
            for (int i = 10; i > 0; i--) {
                if (!clientHandler.getWaitingRoom().isAllPlayerReady()) {
                    break;
                }
                sendCountdownUpdate(clientHandler, i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (clientHandler.getWaitingRoom().isAllPlayerReady()) {
                startGame(clientHandler);
            }
        }).start();
    }

    /**
     * Sends countdown updates to all players in the waiting room.
     *
     * @param clientHandler The client handler of the client.
     * @param countdown     The current countdown value.
     */
    private static void sendCountdownUpdate(ClientHandler clientHandler, int countdown) {
        for (ClientHandler player : clientHandler.getWaitingRoom().getPlayers()) {
            player.sendByte((byte) (70 + countdown));
        }
        if (countdown == 5) {
            sendPlayerOrder(clientHandler);
        }
    }

    /**
     * Sends the player order to all players in the waiting room.
     *
     * @param clientHandler The client handler of the client.
     */
    private static void sendPlayerOrder(ClientHandler clientHandler) {
        ArrayList<Byte> pList = new ArrayList<>();
        int nbOfAi = clientHandler.getWaitingRoom().getNumberOfAI();
        int nbOfPlayer = clientHandler.getWaitingRoom().getNumberOfPlayers();
        /* ajout des joueurs */
        for (int a = 0; a < nbOfPlayer; a++) {
            pList.add((byte) a);
            clientHandler.getWaitingRoom().getPlayers().get(a).setGameId(a);
        }
        /* ajout des ia */
        for (int k = nbOfPlayer; k < nbOfPlayer + nbOfAi; k++) {
            pList.add((byte) k);
        }
        /* envoi des permutation des joueurs */
        clientHandler.getWaitingRoom().getPlayers().forEach(player -> {
            // send list
            byte[] message = new byte[pList.size() + 1];
            message[0] = 84;
            for (int a = 1; a < message.length; a++) {
                message[a] = pList.get(a - 1);
            }
            pList.addLast(pList.removeFirst());
            player.sendByte(message);
        });

    }

    /**
     * Starts the game by initializing the game model.
     *
     * @param clientHandler The client handler of the client.
     */
    private static void startGame(ClientHandler clientHandler) {
        for (ClientHandler player : clientHandler.getWaitingRoom().getPlayers()) {
            player.sendByte((byte) 1, (byte) 1, (byte) clientHandler.getWaitingRoom().getPlayers().get(0).getGameId());
        }
        new Thread(() -> clientHandler.getWaitingRoom().initGameModel()).start();
    }

    /**
     * Changes the AI difficulty level for the waiting room.
     *
     * @param clientId   The ID of the client requesting the change.
     * @param difficulty The new difficulty level.
     */
    public static void changeAiDifficulty(int clientId, byte difficulty) {
        ClientHandler clientHandler = clients.get(clientId);
        clientHandler.getWaitingRoom().setDifficulty(difficulty);
        for (ClientHandler player : clientHandler.getWaitingRoom().getPlayers()) {
            player.sendByte((byte) 68, difficulty);
        }
    }

    /**
     * Kicks a player from the waiting room.
     *
     * @param id The ID of the player to kick.
     */
    public static void kickPlayer(byte id) {
        clients.get(Integer.valueOf(id)).sendByte((byte) 126); // Kick player
    }

    /**
     * Sets the privacy status of the waiting room.
     *
     * @param clientId The ID of the client requesting the change.
     */
    public static void setRoomPrivacy(int clientId) {
        ClientHandler clientHandler = clients.get(clientId);
        boolean isPrivate = !clientHandler.getWaitingRoom().isPrivate();
        clientHandler.getWaitingRoom().setPrivate(isPrivate);
        clientHandler.sendByte((byte) 83, (byte) (isPrivate ? 1 : 0));
    }
}
