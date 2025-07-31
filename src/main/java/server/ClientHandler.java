package main.java.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.java.game.model.PlayerComputerModel;
import main.java.storage.Storage;

/**
 * ClientHandler is a class that represents the link between the server and the
 * client.
 * It handles WebSocket communication, processes client messages, and manages
 * client state.
 */
public class ClientHandler implements Runnable {
    private final Socket socket; // The WebSocket connection
    private final int clientId; // The client ID associated with the WebSocket
    private int gameId; // The game ID for the client
    private InputStream in; // Input stream for reading data from the client
    private OutputStream out; // Output stream for sending data to the client
    private boolean isReady = false; // Indicates if the client is ready in a waiting room
    private String pseudo; // The client's pseudonym
    private OnlineGameManager waitingRoom; // The waiting room where the player is located
    private double animationSpeed = 1.5; // The animationSpeed of the player

    /**
     * Constructor for clients with a socket connection.
     *
     * @param socket   The socket connection for the client.
     * @param clientId The unique client ID.
     */
    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
        this.pseudo = Storage.pseudos[clientId];
    }

    /**
     * Constructor for simulated clients (without a socket).
     *
     * @param clientId The unique client ID.
     */
    public ClientHandler(int clientId) {
        this(null, clientId);
    }

    @Override
    @SuppressWarnings("resource")
    public void run() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();

            // WebSocket handshake
            Scanner scanner = new Scanner(in, "UTF-8");
            StringBuilder request = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                request.append(line).append("\r\n");
                if (line.isEmpty())
                    break;
            }

            // Extract WebSocket key
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(request.toString());
            if (!match.find()) {
                System.out.println("No Sec-WebSocket-Key found, closing connection.");
                return;
            }

            // Prepare the accept key
            String key = match.group(1).trim();
            String acceptKey = Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-1")
                            .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));

            // Send connection validation response
            String response = """
                    HTTP/1.1 101 Switching Protocols\r
                    Upgrade: websocket\r
                    Connection: Upgrade\r
                    Sec-WebSocket-Accept: """ + acceptKey + "\r\n\r\n";
            out.write(response.getBytes("UTF-8"));
            out.flush();
            System.out.println("Client " + clientId + " connected.");

            // Send a welcome message
            sendByte(new byte[] { 0, (byte) clientId });

            // Read and broadcast client messages
            while (true) {
                int firstByte = in.read();
                if (firstByte == -1) {
                    System.out.println("Le premier byte étais -1");
                    break;
                }

                int secondByte = in.read();
                int payloadLength = secondByte & 127;
                byte[] payload = new byte[payloadLength];
                byte[] mask = new byte[4];
                boolean masked = (secondByte & 128) != 0;

                // Read mask if the message is masked
                if (masked) {
                    in.read(mask);
                }

                // Read the message payload
                in.read(payload);

                // Unmask the payload if necessary
                if (masked) {
                    for (int i = 0; i < payloadLength; i++) {
                        payload[i] ^= mask[i % 4];
                    }
                }

                // Process the message based on the first byte
                if (firstByte == 130) {
                    processMessage(payload);
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Error client handler : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("MANGE TES MORTS");
            e.printStackTrace();
        } finally {
            System.out.println("websocket closing");
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error in the socket close");
            }
            if (this.waitingRoom != null && this.waitingRoom.isStarted()) {
                PlayerComputerModel replaceAi = new PlayerComputerModel(this.gameId,
                        this.waitingRoom.getDifficulty());
                this.waitingRoom.getPlayers().remove(this);
                this.waitingRoom.replacePlayerByAi(this.gameId, replaceAi);
            }
            WebsocketServer.removeClient(clientId);
        }
    }

    /**
     * Processes the incoming message payload and performs the necessary actions.
     *
     * @param payload The message payload received from the client.
     */
    private void processMessage(byte[] payload) {
        if (payload.length > 0) {
            StringBuilder message = new StringBuilder();
            for (byte b : payload) {
                message.append(b).append(" ");
            }
            System.out.println("Client " + clientId + " says: " + message.toString());

            switch (payload[0]) {
                case 63 -> this.animationSpeed = (byte) (((double) payload[1]) / 10);
                case 64 -> waitingRoom.action(payload, clientId, gameId);
                case 65 -> handleConnectionRequest(payload);
                case 66 -> WebsocketServer.createWaitingRoom(clientId, true);
                case 67 -> WebsocketServer.setPlayerReady(clientId);
                case 68 -> WebsocketServer.changeAiDifficulty(clientId, payload[1]);
                case 69 -> WebsocketServer.removeClientFromWaitingroom(clientId);
                case 70 -> {
                    WebsocketServer.removeClientFromWaitingroom(payload[1]);
                    WebsocketServer.kickPlayer(payload[1]);
                }
                case 81 -> WebsocketServer.addAIPlayer(clientId);
                case 82 -> WebsocketServer.removeAIPlayer(clientId);
                case 83 -> WebsocketServer.setRoomPrivacy(clientId);
                case 85 -> handleGoToWaitingRoom();
            }
        }
    }

    /**
     * Handles connection requests based on the payload content.
     *
     * @param payload The message payload received from the client.
     */
    private void handleConnectionRequest(byte[] payload) {
        if (payload.length == 1) {
            WebsocketServer.client(this, null);
        } else {
            System.out.println("    connection with code");
            StringBuilder code = new StringBuilder();
            for (int j = 1; j < payload.length; j++) {
                code.append((char) payload[j]);
            }
            WebsocketServer.client(this, code.toString());
        }
    }

    /**
     * Handles the action of going to the waiting room.
     */
    private void handleGoToWaitingRoom() {
        System.out.println("Go to waiting room");
        waitingRoom.setPlayersReady(false);
        waitingRoom.getPlayers().forEach(player -> player.sendByte((byte) 1, (byte) 85));
    }

    /**
     * Sends a byte message to the client with the appropriate WebSocket header.
     *
     * @param message The message as a byte array.
     */
    public void sendByte(byte... message) {
        try {
            int length = message.length;
            out.write(130);
            if (length <= 125) {
                out.write(length);
            } else if (length <= 65535) {
                out.write(126);
                out.write((length >> 8) & 255);
                out.write(length & 255);
            } else {
                out.write(127);
                for (int i = 7; i >= 0; i--) {
                    out.write((length >> (i * 8)) & 255);
                }
            }
            out.write(message);
            out.flush();

            System.out.print("  Response: ");
            for (byte b : message) {
                System.out.print(b + " ");
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error sending byte message.");
        }
    }

    // Getters and Setters

    public int getId() {
        return clientId;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setWaitingRoom(OnlineGameManager waitingRoomModel) {
        waitingRoom = waitingRoomModel;
    }

    public OnlineGameManager getWaitingRoom() {
        return waitingRoom;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int id) {
        this.gameId = id;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public double getSpeedAnimation() {
        return this.animationSpeed;
    }
}
