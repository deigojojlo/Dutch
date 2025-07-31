package main.java.onlinegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import main.java.game.controller.GameController;
import main.java.game.model.CardModel;
import main.java.game.view.GameView;
import main.java.storage.Storage;
import main.java.util.Debug;

/**
 * WebsocketClient handles the WebSocket connection to the server, manages
 * message sending and receiving,
 * and processes game and waiting room updates.
 */
public class WebsocketClient {
    private static int clientId;
    private static OutputStream out;
    private static boolean alreadyConnected = false;
    private static WaitingRoomController controller;
    private static GameController gameController;
    private static Socket socket;
    private static boolean volontaryDisconnected = false;

    /**
     * Establishes a connection to the WebSocket server and handles communication.
     *
     * @param wController The WaitingRoomController instance.
     * @return True if the connection is successful, false otherwise.
     */
    public static boolean connection(WaitingRoomController wController) {
        try {
            controller = wController;
            socket = new Socket(Storage.SERVER_ADDRESS, Storage.SERVER_PORT);
            InputStream in = socket.getInputStream();
            out = socket.getOutputStream();
            System.out.println("Connected to WebSocket server...");

            // Send WebSocket handshake request
            String request = generateRequest(Storage.SERVER_ADDRESS, Storage.SERVER_PORT);
            out.write(request.getBytes());
            out.flush();

            // Read server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                if (line.equals("Rejected server is full"))
                    throw new ConnectException();
                System.out.println(line);
            }
            System.out.println("WebSocket handshake successful!");
            alreadyConnected = true;

            // Start a new thread to handle message receiving
            new Thread(() -> {
                try {
                    while (true) {
                        int firstByte = in.read();
                        if (firstByte == -1)
                            break;

                        int payloadLength = in.read() & 127;
                        byte[] payload = new byte[payloadLength];
                        in.read(payload, 0, payloadLength);

                        if (firstByte == 129) {
                            System.out.println("Text message received, skipping...");
                            continue;
                        }

                        // Process the message based on the first byte
                        processMessage(payload);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                    controller.exit(volontaryDisconnected);
                    closeResources(socket, in, out);
                    alreadyConnected = false;
                }
            }).start();
            return true;
        } catch (IOException e) {
            System.err.println("Error in the socket connection to server ");
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Generates a random WebSocket key encoded in Base64.
     *
     * @return A Base64 encoded WebSocket key.
     */
    public static String generateWebSocketKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    /**
     * Generates the WebSocket handshake request string.
     *
     * @param serverAddress The server address.
     * @param port          The server port.
     * @return The WebSocket handshake request string.
     */
    public static String generateRequest(String serverAddress, int port) {
        return String.format("""
                GET / HTTP/1.1\r
                Host: %s:%d\r
                Upgrade: websocket\r
                Connection: Upgrade\r
                Sec-WebSocket-Key: %s\r
                Sec-WebSocket-Version: 13\r
                \r
                """,
                serverAddress, port, generateWebSocketKey());
    }

    /**
     * Processes incoming messages from the server.
     *
     * @param message The message payload received from the server.
     */
    private static void processMessage(byte[] message) {
        if (message[0] == 0 && message.length == 2) {
            clientId = message[1];
            controller.setIdClient(clientId);
        } else if (message[0] > 64 && message[0] < 127) {
            new Thread(() -> receivedFromWaitingRoom(message, message.length, controller)).start();
        } else if (message[0] == 1) {
            new Thread(() -> receivedGameInformation(message)).start();
        }

        // Debug: Print the message content
        System.out.print("Message received from server: ");
        Debug.printByteString(message);
    }

    /**
     * Handles messages related to the waiting room.
     *
     * @param message       The message payload.
     * @param messageLength The length of the message.
     * @param controller    The WaitingRoomController instance.
     */
    private static void receivedFromWaitingRoom(byte[] message, int messageLength, WaitingRoomController controller) {
        System.out.println("Received from waiting room");

        switch (message[0]) {
            case 126 -> controller.exit(volontaryDisconnected);
            case 65 -> {
                if (messageLength == 2) {
                    controller.displayError(message[1]);
                }
            }
            case 66 -> updateWaitingRoom(message, messageLength, controller);
            case 67 -> controller.setPlayerReady(message[1], message[2] == 1);
            case 68 -> controller.setAIDifficulty(message[1]);
            case 69 -> controller.removePlayer(message[1]);
            case 72, 73, 74, 75, 76, 77, 78, 79, 80 -> {
                controller.displayCounter(message[0] % 71);
                if (message[0] == 73) {
                    gameController = controller.createGame();
                }
            }
            case 71 -> controller.displayGame(gameController);
            case 82 -> controller.removeAIPlayer();
            case 83 -> controller.changeRoomPrivacy(message[1] == 1);
            case 84 -> setPlayerOrder(message);
            case 85 -> gameController.displayWRController();
        }
    }

    /**
     * Handles game-related messages.
     *
     * @param message The message payload.
     */
    private static void receivedGameInformation(byte[] message) {
        switch (message[1]) {
            case 1 -> {
                gameController.giveActivePlayer(message[2]); // OK DRAW BORDER AROUND PLAYER
                gameController.getGameView().getExitButton().setEnabled(false);
            }
            case 2 -> gameController.givePickedCard(
                    new CardModel(message[2], message[3]).setIsDrawedInDiscard(message.length == 5), null); //OK GIVES CARD IN HAND
            case 3 -> gameController.pickStack(); // OK IN ACTION (ONLINEGAMEMANGER)
            case 4 -> gameController.putCardOnDiscard((byte) gameController.getPlayerOrder().indexOf(message[2]),
                    message[3], new CardModel(message[4], message[5]), message[6], message[7]);
            case 6 -> gameController.hidePlayerCard(message[2], message[3],
                    (byte) gameController.getPlayerOrder().indexOf(message[4]), message[5], true);
            case 7 -> gameController.revealPlayerCard(message[2], message[3],
                    (byte) gameController.getPlayerOrder().indexOf(message[4]), message[5], true);
            // ACTION
            // (ONLINEGAMEMANGER)
            case 25 -> handleRevealDiscard(message); // OK TRASH DISCARD NOTIFY
            case 26 -> handlePickDiscard(message); // OK PICK IN DISCARD NOTIFY
            case 50 -> gameController.swapCard((byte) gameController.getPlayerOrder().indexOf(message[2]),
                    (byte) gameController.getPlayerOrder().indexOf(message[3]), message[4], message[5]); // OK SWAP CARDS
                                                                                                     // NOTIFY

            case -1 -> gameController.announceTheEnd(message[2]);
            case -2 -> gameController.revealOnline(message);
            case -3 -> gameController.displayRoundScore(message);
            case -4 -> gameController.displayGameScore(message[2], message[3], message);
            case -5 -> gameController.newRound();
            case -6 -> gameController.endOfTheGame();
        }
    }

    /**
     * Updates the waiting room with player and AI information.
     *
     * @param message       The message payload.
     * @param messageLength The length of the message.
     * @param controller    The WaitingRoomController instance.
     */
    private static void updateWaitingRoom(byte[] message, int messageLength, WaitingRoomController controller) {
        int byteIndex = 1;
        controller.clear();
        int countPlayer = 0;
        // player
        while (message[byteIndex] != -1) {
            boolean isReady = message[byteIndex++] == 1;
            int id = message[byteIndex++];
            String pseudo = extractPseudo(message, byteIndex);
            controller.addPlayer(id, pseudo, isReady, countPlayer == 0);
            countPlayer++;
            byteIndex = skipToNextPart(message, byteIndex);
        }

        byteIndex++;
        // ai
        while (message[byteIndex] != -1) {
            int difficulty = message[byteIndex++];
            String pseudo = extractPseudo(message, byteIndex);
            controller.addAIPlayer(pseudo, difficulty);
            byteIndex = skipToNextPart(message, byteIndex);
        }

        byteIndex++;
        StringBuilder code = new StringBuilder();
        // code
        while (message[byteIndex] != -1) {
            code.append((char) message[byteIndex++]);
        }
        controller.setCode(code.toString());
        byteIndex++;

        // speed anim
        GameView.setAnimSpeed(((double) message[byteIndex++]) / 10.0);
        // room privacy
        controller.changeRoomPrivacy(message[byteIndex] != 0);

        controller.display();
    }

    /**
     * Extracts the pseudo from the message payload.
     *
     * @param message   The message payload.
     * @param byteIndex The current byte index.
     * @return The extracted pseudo string.
     */
    private static String extractPseudo(byte[] message, int byteIndex) {
        StringBuilder pseudo = new StringBuilder();
        while (byteIndex < message.length && message[byteIndex] != -2) {
            pseudo.append((char) message[byteIndex++]);
        }
        return pseudo.toString();
    }

    /**
     * Skips to the next part of the message payload.
     *
     * @param message   The message payload.
     * @param byteIndex The current byte index.
     * @return The updated byte index.
     */
    private static int skipToNextPart(byte[] message, int byteIndex) {
        while (message[byteIndex] != -2) {
            byteIndex++;
        }
        return byteIndex + 1;
    }

    /**
     * Sets the player order based on the message payload.
     *
     * @param message The message payload.
     */
    private static void setPlayerOrder(byte[] message) {
        ArrayList<Byte> order = new ArrayList<>();
        for (int i = 1; i < message.length; i++) {
            order.add(message[i]);
        }
        controller.setPlayerOrder(order);
    }

    /**
     * Handles the reveal discard action based on the message payload.
     *
     * @param message The message payload.
     */
    private static void handleRevealDiscard(byte[] message) {
        if (message[2] == -1 && message[3] == -1) {
        } else if (message[4] == -1 && message[5] == -1) {
            gameController.trashDiscard(new CardModel(message[2], message[3]), null);
        } else {
            gameController.trashDiscard(new CardModel(message[2], message[3]),
                    new CardModel(message[4], message[5]));
        }
    }

    /**
     * Handles the pick discard action based on the message payload.
     *
     * @param message The message payload.
     */
    private static void handlePickDiscard(byte[] message) {
        if (message[2] == -1 && message[3] == -1) {
        } else if (message[4] == -1 && message[5] == -1) {
            gameController.pickDiscard(new CardModel(message[2], message[3]), null);
        } else {
            gameController.pickDiscard(new CardModel(message[2], message[3]),
                    new CardModel(message[4], message[5]));
        }
    }

    /**
     * Sends a byte message to the server with the appropriate WebSocket framing.
     *
     * @param message The message as a byte array.
     */
    public static void sendByte(byte... message) {
        try {
            byte[] mask = new byte[4];
            new SecureRandom().nextBytes(mask);

            int length = message.length;
            out.write(130);
            if (length <= 125) {
                out.write(128 | length);
            } else if (length <= 65535) {
                out.write(128 | 126);
                out.write((length >> 8) & 255);
                out.write(length & 255);
            } else {
                out.write(128 | 127);
                for (int i = 7; i >= 0; i--) {
                    out.write((length >> (i * 8)) & 255);
                }
            }

            for (int i = 0; i < length; i++) {
                message[i] ^= mask[i % 4];
            }

            out.write(mask);
            out.write(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending byte message.");
        }
    }

    /**
     * Closes the socket and streams to free resources.
     *
     * @param socket The socket to close.
     * @param in     The input stream to close.
     * @param out    The output stream to close.
     */
    private static void closeResources(Socket socket, InputStream in, OutputStream out) {
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            System.out.println("Error closing resources.");
        }
    }

    public static boolean isAlreadyConnected() {
        return alreadyConnected;
    }

    public static int getId() {
        return clientId;
    }

    public static void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error while closing socket");
        }
        volontaryDisconnected = true;
    }
}
