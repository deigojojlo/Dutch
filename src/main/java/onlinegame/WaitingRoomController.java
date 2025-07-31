package main.java.onlinegame;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.java.game.controller.GameController;
import main.java.game.controller.SelectionController;
import main.java.ourjcomponent.CycleSelector;
import main.java.ourjcomponent.LabelOverlay;

/**
 * Controller for the waiting room.
 */
public class WaitingRoomController {

    private final WaitingRoomView view;
    private final JFrame frame;

    private int idClient;
    private boolean isHost;
    private int difficulty;

    ArrayList<Byte> playerOrder;

    /**
     * Constructs a new waiting room controller.
     *
     * @param frame the primary frame of the application
     */
    public WaitingRoomController(JFrame frame) {
        this.view = new WaitingRoomView(false, false, idClient);
        this.frame = frame;
        this.isHost = false;

        initializeControllers();
        setupView();
    }

    /**
     * set the host client id
     * 
     * @param idClient the new id
     */
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    /**
     * Apply the listener
     */
    private void initializeControllers() {
        view.getAddPlayerButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 81);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(view);
        });
        view.getReadyButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 67);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(view);
            frame.revalidate();
            frame.repaint();
        });
        view.getWaiterPlayersBoxes().forEach(waiterBox -> waiterBox.getKickButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 70, (byte) waiterBox.getId());
            frame.getContentPane().removeAll();
            frame.getContentPane().add(view);
            frame.revalidate();
            frame.repaint();
        }));
        view.getPrivateRoomButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 83);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(view);
            frame.revalidate();
            frame.repaint();
        });
        view.getWaiterAIBoxes().forEach(waiterBox -> waiterBox.getKickButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 82);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(view);
            frame.revalidate();
            frame.repaint();
        }));
        ((CycleSelector) view.getDifficultySelector()).setUpdateIndex(() -> {
            WebsocketClient.sendByte((byte) 68,
                    (byte) ((CycleSelector) view.getDifficultySelector()).getSelectedIndex());
            frame.getContentPane().removeAll();
            frame.getContentPane().add(view);
            frame.revalidate();
            frame.repaint();
        });
    }

    private void setupView() {
        view.getExitButton().addActionListener(_ -> exitRoom());
        view.updateView(isHost);
    }

    /**
     * Adds a player to the waiting room.
     *
     * @param id       the player's ID
     * @param username the player's username
     * @param isReady  the player's ready status
     */
    public void addPlayer(int id, String username, boolean isReady, boolean tkt) {
        if (id == idClient) {
            isHost = tkt;
        }
        view.addPlayer(id, username, isReady, isHost);
    }

    /**
     * Adds an AI player to the waiting room.
     *
     * @param username   the AI player's username
     * @param difficulty the AI player's difficulty level
     */
    public void addAIPlayer(String username, int difficulty) {
        this.difficulty = difficulty;
        view.addAIPlayer(isHost);
    }

    /**
     * Removes a player from the waiting room.
     *
     * @param id the player's ID
     */
    public void removePlayer(int id) {
        isHost = view.getWaiterBoxesConnected().get(0).getKey() == idClient;
        setButtonsWREnabled(true);
        if (id == idClient) {
            clear();
        }
        view.removePlayer(id, isHost);

        view.setHost(isHost);
    }

    /**
     * Removes an AI player from the waiting room.
     */
    public void removeAIPlayer() {
        view.removeAIPlayer(isHost);
    }

    /**
     * Display the waiting room
     */
    public void display() {
        view.updateView(isHost);
        view.setAllReady(false);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(view);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Display an error choose by the errorCode
     * 
     * @param errorCode the error code
     */
    public void displayError(byte errorCode) {
        switch (errorCode) {
            case 0 -> JOptionPane.showMessageDialog(frame, "La salle d'attente est pleine ou la partie a déjà commencé",
                    "Erreur de salon", JOptionPane.ERROR_MESSAGE);
            case 1 -> JOptionPane.showMessageDialog(frame, "Le salon demandé n'existe pas ou plus", "Erreur de salon",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void displayGame(GameController gc) {
        this.frame.getContentPane().removeAll();
        this.frame.getContentPane().add(gc.getGameView());
        this.frame.revalidate();
        this.frame.repaint();
    }

    public GameController createGame() {
        GameController gc = new GameController(this.frame, playerOrder, view.getUsernames(), this);
        return gc;
    }

    /**
     * Say if the player is the local host or not
     * 
     * @param id the id of the player's box checked
     * @return
     */
    private boolean isClient(int id) {
        return this.idClient == id;
    }

    /**
     * Exits the waiting room.
     */
    private void exitRoom() {
        WebsocketClient.sendByte((byte) 69);
        setPlayerReady((byte) idClient, false);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new SelectionController(frame).getView());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Set the ai difficulty label
     * 
     * @param difficulty level
     */
    public void setAIDifficulty(byte difficulty) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(view);
        view.setAIDifficulty(difficulty);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Put the client isReady status to true/false and display it
     * 
     * @param clientID the id of the client
     * @param isReady  The status
     */
    public void setPlayerReady(byte clientID, boolean isReady) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(view);
        view.setIsReady(isReady, clientID, isClient(clientID));
        setButtonsWREnabled(true);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Exit properly from a connected to the server page
     * 
     * @param volontaryDisconnecteds if the player click on the exit button or issue
     */
    public void exit(boolean volontaryDisconnecteds) {
        setPlayerReady((byte) idClient, false);
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new SelectionController(frame).getView());
            setButtonsWREnabled(true);
            frame.revalidate();
            frame.repaint();
            if (!volontaryDisconnecteds)
                JOptionPane.showMessageDialog(frame, "la connexion au server est perdu", "connection error",
                        JOptionPane.ERROR_MESSAGE);
        });
    }

    public void setCode(String code) {
        view.setCode(code);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(view);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Clear the waiting room view
     */
    public void clear() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(view);
        frame.revalidate();
        frame.repaint();
        view.clear();
    }

    /**
     * Set the room privacy
     * 
     * @param isRoomPrivate true if the room is private
     */
    public void changeRoomPrivacy(boolean isRoomPrivate) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(view);
        frame.revalidate();
        frame.repaint();
        view.changeRoomPrivacy(isRoomPrivate, isHost);
    }

    /**
     * Display the counter
     * 
     * @param n the number to display
     */
    public void displayCounter(int n) {
        if (frame.getContentPane().getComponents().length >= 2)
            frame.getContentPane().remove(0);
        setButtonsWREnabled(false);
        frame.getContentPane().add(new LabelOverlay(n + "", frame), 0);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Set the buttons enabled or not
     * 
     * @param enabled true if the buttons are enabled
     */
    public void setButtonsWREnabled(boolean enabled) {
        view.getWaiterAIBoxes().forEach(waiterBox -> waiterBox.getKickButton().setEnabled(enabled));
        view.getWaiterBoxesConnected().forEach(waiterBox -> waiterBox.getValue().getKickButton().setEnabled(enabled));
        view.getAddPlayerButton().setEnabled(enabled);
        view.getPrivateRoomButton().setEnabled(enabled);
        view.getDifficultySelector().getButtons().forEach(button -> button.setEnabled(enabled));
        view.getReadyButton().setEnabled(enabled);
        view.getExitButton().setEnabled(enabled);
    }

    public void setPlayerOrder(ArrayList<Byte> order) {
        this.playerOrder = order;
    }

    public int getDifficulty() {
        return this.difficulty;
    }
}