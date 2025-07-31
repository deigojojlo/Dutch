package main.java.onlinegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import main.java.ourjcomponent.CycleSelector;
import main.java.style.ViewStyle;
import main.java.util.Pair;

/**
 * Class for the waiting room view.
 */
public class WaitingRoomView extends JPanel {

    public static final String[] DIFFICULTY_OPTIONS = { "Moyen", "Difficile" };
    private static final int MARGIN_SIZE = 15;
    private static final int MAX_PLAYERS = 10;
    private static final int PLAYER_PANEL_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()
            * 0.070);

    private JButton exitButton;
    private JPanel playerPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JButton readyButton;
    private JButton addPlayerButton;
    private JPanel addPlayerPanel;
    private JButton privateRoomButton;
    private JPanel difficultyPanel;
    private CycleSelector difficultySelector;
    private final LinkedList<WaiterBox> waiterBoxes = new LinkedList<>();
    private final LinkedList<Pair<Integer, WaiterBox>> waiterBoxesConnected = new LinkedList<>();
    private final LinkedList<WaiterBox> waiterBoxesAi = new LinkedList<>();
    private final ArrayList<WaiterBox> waiterBoxesAiConnected = new ArrayList<>();

    private JLabel codeLabel;

    private final int idClient;

    /**
     * Constructs the waiting room view.
     * 
     * @param isPlayerReady initial ready status
     * @param isRoomPrivate initial privacy status
     * @param idClient      the client's unique identifier
     */
    public WaitingRoomView(boolean isPlayerReady, boolean isRoomPrivate, int idClient) {
        this.idClient = idClient;
        initializeLayout();
        initializeComponents(isPlayerReady, isRoomPrivate);
    }

    /**
     * Checks if the list contains a given client ID.
     * 
     * @param list list of (ID, WaiterBox) pairs
     * @param id   client ID to look for
     * @return true if found, false otherwise
     */
    public static boolean contains(LinkedList<Pair<Integer, WaiterBox>> list, int id) {
        for (Pair<Integer, WaiterBox> element : list) {
            if (element.getKey() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the pair matching the given client ID.
     * 
     * @param list list of (ID, WaiterBox) pairs
     * @param id   client ID to find
     * @return the matching pair or null
     */
    public static Pair<Integer, WaiterBox> get(LinkedList<Pair<Integer, WaiterBox>> list, int id) {
        for (Pair<Integer, WaiterBox> element : list) {
            if (element.getKey() == id) {
                return element;
            }
        }
        return null;
    }

    /**
     * Removes and returns the pair for a specific client ID.
     * 
     * @param list list of (ID, WaiterBox) pairs
     * @param id   client ID to remove
     * @return the removed pair or null
     */
    public static Pair<Integer, WaiterBox> remove(LinkedList<Pair<Integer, WaiterBox>> list, int id) {
        for (Pair<Integer, WaiterBox> element : list) {
            if (element.getKey() == id) {
                list.remove(element);
                return element;
            }
        }
        return null;
    }

    /**
     * Sets up the main layout and side borders.
     */
    private void initializeLayout() {
        setLayout(new BorderLayout());
        setBackground(ViewStyle.BACKGROUND_COLOR);

        // Create side panels to act as borders
        JPanel leftBorder = new JPanel();
        JPanel rightBorder = new JPanel();
        leftBorder.setBackground(ViewStyle.BACKGROUND_COLOR);
        rightBorder.setBackground(ViewStyle.BACKGROUND_COLOR);

        add(leftBorder, BorderLayout.WEST);
        add(rightBorder, BorderLayout.EAST);
    }

    /**
     * Creates the top panel with exit controls.
     */
    private void createTopPanel() {
        this.topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.topPanel.setBorder(BorderFactory.createEmptyBorder(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE));
        this.topPanel.setBackground(ViewStyle.BACKGROUND_COLOR);

        exitButton = new JButton("Quitter");
        styleButton(new Color(192, 0, 0), exitButton);
        this.topPanel.add(exitButton);
    }

    /**
     * Toggles visibility of difficulty and privacy options.
     * 
     * @param flag true to show, false to hide
     */
    private void setVisility(boolean flag) {
        difficultyPanel.setVisible(flag);
        privateRoomButton.setVisible(flag);

    }

    /**
     * Builds the bottom panel with ready, privacy and difficulty controls.
     * 
     * @param isPlayerReady initial ready state
     * @param isRoomPrivate initial privacy state
     */
    private void createBottomPanel(boolean isPlayerReady, boolean isRoomPrivate) {
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ViewStyle.BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE));

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(ViewStyle.BACKGROUND_COLOR);

        readyButton = new JButton(isPlayerReady ? "Prêt" : "Pas Prêt");
        codeLabel = new JLabel();
        privateRoomButton = new JButton(isRoomPrivate ? "Privé" : "Public");
        difficultyPanel = new JPanel();
        difficultyPanel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                        "Difficulté"));

        difficultySelector = new CycleSelector(DIFFICULTY_OPTIONS, 0);

        styleButton(isPlayerReady ? ViewStyle.READY_COLOR : ViewStyle.NOT_READY_COLOR, readyButton);
        styleButton(isRoomPrivate ? ViewStyle.PRIVATE_ROOM_COLOR : ViewStyle.PUBLIC_ROOM_COLOR,
                privateRoomButton);
        styleButton(Color.DARK_GRAY, difficultySelector.getButtons().toArray(new JButton[0]));
        difficultySelector.getLabel().setPreferredSize(new Dimension(80, 40));

        centerPanel.add(readyButton);
        centerPanel.add(privateRoomButton);
        centerPanel.add(codeLabel);

        bottomPanel.add(centerPanel, BorderLayout.CENTER);
        difficultyPanel.add(difficultySelector);
        bottomPanel.add(difficultyPanel, BorderLayout.EAST);

    }

    /**
     * Applies styling to the given buttons.
     * 
     * @param backgroundColor the background color
     * @param buttons         buttons to style
     */
    private void styleButton(Color backgroundColor, JButton... buttons) {
        for (JButton button : buttons) {
            button.setBackground(backgroundColor);
            button.setForeground(ViewStyle.BUTTON_TEXT_COLOR);
            button.setFont(ViewStyle.BUTTON_FONT);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setPreferredSize(new Dimension(100, 30));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        }
    }

    /**
     * Getter for the exit button.
     *
     * @return the button to exit the waiting room
     */
    public JButton getExitButton() {
        return exitButton;
    }

    /**
     * Getter for the player panel.
     *
     * @return the panel to display the players
     */
    public JPanel getPlayerPanel() {
        return playerPanel;
    }

    public CycleSelector getDifficultySelector() {
        return difficultySelector;
    }

    /**
     * Updates the ready button based on readiness.
     * 
     * @param isReady current ready status
     */
    public void toggleReadyButton(boolean isReady) {
        readyButton.setText(isReady ? "Prêt" : "Pas Prêt");
        readyButton.setBackground(isReady ? ViewStyle.READY_COLOR : ViewStyle.NOT_READY_COLOR);
    }

    /**
     * Updates the privacy button based on room privacy.
     * 
     * @param isRoomPrivate current privacy status
     */
    public void togglePrivacyButton(boolean isRoomPrivate) {
        privateRoomButton.setText(isRoomPrivate ? "Privé" : "Public");
        privateRoomButton.setBackground(isRoomPrivate ? ViewStyle.PRIVATE_ROOM_COLOR : ViewStyle.PUBLIC_ROOM_COLOR);
        privateRoomButton.revalidate();
        privateRoomButton.revalidate();
    }

    /**
     * Adds a player to the waiting room.
     *
     * @param id       the player's ID
     * @param username the player's username
     * @param isReady  the player's ready status
     */
    public void addPlayer(int id, String username, boolean isReady, boolean isClientHost) {
        WaiterBox waiterBox;
        if (!contains(waiterBoxesConnected, id)) {
            waiterBox = waiterBoxes.removeFirst();
            waiterBox.setPlayer(id, username, isReady, isClientHost);
            waiterBoxesConnected.addLast(new Pair<>(id, waiterBox));
            if (id == WebsocketClient.getId())
                waiterBox.setClientStyle();
        } else {
            waiterBox = get(waiterBoxesConnected, id).getValue();
            waiterBox.updatePlayer(username, isReady, isClientHost);
        }
        if (waiterBoxesAiConnected.size() + waiterBoxesConnected.size() > MAX_PLAYERS) {
            waiterBoxesAi.add(waiterBoxesAiConnected.remove(waiterBoxesAiConnected.size() - 1));
        }
        setVisility(isClientHost);
        updateKickButtons(isClientHost);
        updateView(isClientHost);
    }

    /**
     * Adds an AI player if capacity allows.
     * 
     * @param modifiableByHost whether host can modify the AI
     */
    public void addAIPlayer(boolean modifiableByHost) {
        if (waiterBoxesConnected.size() + waiterBoxesAiConnected.size() < MAX_PLAYERS
                && !waiterBoxesAi.isEmpty()) {
            WaiterBox waiterBox = waiterBoxesAi.removeFirst();
            waiterBox.setAI("Ai " + waiterBoxesAiConnected.size(), modifiableByHost);
            waiterBoxesAiConnected.add(waiterBox);
        }
        updateKickButtons(modifiableByHost);
        updateView(modifiableByHost);
    }

    /**
     * Removes a player and replaces with AI if needed.
     * 
     * @param id           client ID to remove
     * @param isClientHost host privilege flag
     */
    public void removePlayer(int id, boolean isClientHost) {
        if (contains(waiterBoxesConnected, id)) {
            WaiterBox removedWB = remove(waiterBoxesConnected, id).getValue();
            removedWB.reset();
            waiterBoxes.add(removedWB);
            addAIPlayer(isClientHost);
        }
        waiterBoxesConnected.forEach(box -> box.getValue().setIsReady(false));
        updateKickButtons(isClientHost);
        updateView(isClientHost);
        styleButton(ViewStyle.NOT_READY_COLOR, readyButton);
    }

    /**
     * Removes an AI player if present.
     * 
     * @param isClientHost host privilege flag
     */
    public void removeAIPlayer(boolean isClientHost) {
        if (!waiterBoxesAiConnected.isEmpty()) {
            waiterBoxesAi.add(waiterBoxesAiConnected.remove(waiterBoxesAiConnected.size() - 1));
        }
        updateKickButtons(isClientHost);
        updateView(isClientHost);
    }

    /**
     * Initializes the container for player slots.
     */
    private void initPlayerPanel() {
        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBackground(Color.WHITE);
        playerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)));

        addPlayerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addPlayerPanel.setBackground(Color.WHITE);
        addPlayerButton = new JButton("+");
        styleButton(ViewStyle.BUTTON_COLOR, addPlayerButton);
        addPlayerPanel.add(addPlayerButton);

        playerPanel.add(addPlayerPanel);
    }

    /**
     * Sets up all components and default slots.
     * 
     * @param isPlayerReady initial ready status
     * @param isRoomPrivate initial privacy status
     */
    private void initializeComponents(boolean isPlayerReady, boolean isRoomPrivate) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            waiterBoxes.add(new WaiterBox("", false));
        }
        for (int i = 0; i < MAX_PLAYERS - 1; i++) {
            waiterBoxesAi.add(new WaiterBox("Ai " + i, true));
        }

        initPlayerPanel();

        createTopPanel();

        createBottomPanel(isPlayerReady, isRoomPrivate);
        setVisility(false);

        add(topPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes the player panel on the Event Dispatch Thread.
     * 
     * @param isClientHost host privilege flag
     */
    public void updateView(boolean isClientHost) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            playerPanel.removeAll();

            List<Pair<Integer, WaiterBox>> connectedCopy = new ArrayList<>(waiterBoxesConnected);
            connectedCopy.forEach(pair -> {
                pair.getValue().setVisible(true);
                pair.getValue()
                        .setPreferredSize(new Dimension(Integer.MAX_VALUE, pair.getValue().getPreferredSize().height));
                if (pair.getKey() == WebsocketClient.getId())
                    pair.getValue().setClientStyle();
                else
                    pair.getValue().setDefaultClientStyle();
                playerPanel.add(pair.getValue());
            });

            List<WaiterBox> waiterBoxesAiConnectedCopy = new ArrayList<>(waiterBoxesAiConnected);
            waiterBoxesAiConnectedCopy.forEach(player -> {
                player.setVisible(true);
                player.setPreferredSize(new Dimension(Integer.MAX_VALUE, player.getPreferredSize().height));
                playerPanel.add(player);
            });

            List<WaiterBox> waiterBoxesCopy = new ArrayList<>(waiterBoxes);
            waiterBoxesCopy.forEach(player -> {
                player.setVisible(false);
                player.setPreferredSize(new Dimension(Integer.MAX_VALUE, player.getPreferredSize().height));
                player.getKickButton().setVisible(isClientHost && idClient != WebsocketClient.getId());
                playerPanel.add(player);
            });

            List<WaiterBox> waiterBoxesAiCopy = new ArrayList<>(waiterBoxesAi);
            waiterBoxesAiCopy.forEach(player -> {
                player.setVisible(false);
                player.setPreferredSize(new Dimension(Integer.MAX_VALUE, player.getPreferredSize().height));
                playerPanel.add(player);
            });

            if (isClientHost && waiterBoxesConnected.size() + waiterBoxesAiConnected.size() < MAX_PLAYERS) {
                playerPanel.add(addPlayerPanel);
            }

            playerPanel.revalidate();
            playerPanel.repaint();
        });
    }

    /**
     * Changes a client's ready status indicator.
     * 
     * @param isReady  new ready status
     * @param id       client ID to update
     * @param isClient if updating local client
     */
    public void setIsReady(boolean isReady, int id, boolean isClient) {
        if (contains(waiterBoxesConnected, id)) {
            get(waiterBoxesConnected, id).getValue().setIsReady(isReady);
        }
        if (isClient) {
            toggleReadyButton(isReady);
        }
    }

    /**
     * Changes room privacy and updates the view.
     * 
     * @param isRoomPrivate new privacy setting
     * @param isClientHost  host privilege flag
     */
    public void changeRoomPrivacy(boolean isRoomPrivate, boolean isClientHost) {
        togglePrivacyButton(isRoomPrivate);
        updateView(isClientHost);
    }

    /**
     * Updates the displayed room code.
     * 
     * @param code room code string
     */
    public void setCode(String code) {
        codeLabel.setText(code);
    }

    /**
     * Sets the AI difficulty in the selector.
     * 
     * @param difficulty index of difficulty option
     */
    public void setAIDifficulty(int difficulty) {
        difficultySelector.modifyIndex(difficulty);
    }

    /**
     * Getter for the ready button.
     *
     * @return the button to toggle player readiness
     */
    public JButton getReadyButton() {
        return readyButton;
    }

    /**
     * Getter for the private room button.
     *
     * @return the button to toggle room privacy
     */
    public JButton getPrivateRoomButton() {
        return privateRoomButton;
    }

    public LinkedList<Pair<Integer, WaiterBox>> getWaiterBoxesConnected() {
        return waiterBoxesConnected;
    }

    /**
     * Returns all player-related WaiterBox components.
     * 
     * @return list of WaiterBox components for players
     */
    public List<WaiterBox> getWaiterPlayersBoxes() {
        List<WaiterBox> allWaiterBoxes = new ArrayList<>();
        for (Pair<Integer, WaiterBox> pair : waiterBoxesConnected) {
            allWaiterBoxes.add(pair.getValue());
        }
        allWaiterBoxes.addAll(waiterBoxes);
        return allWaiterBoxes;
    }

    /**
     * Returns all AI-related WaiterBox components.
     * 
     * @return list of WaiterBox components for AIs
     */
    public List<WaiterBox> getWaiterAIBoxes() {
        List<WaiterBox> allWaiterBoxes = new ArrayList<>();
        waiterBoxesAiConnected.forEach(wb -> allWaiterBoxes.add(wb));
        waiterBoxesAi.forEach(wb -> allWaiterBoxes.add(wb));
        return allWaiterBoxes;
    }

    public JButton getAddPlayerButton() {
        return addPlayerButton;
    }

    /**
     * Clears all player and AI slots back to initial state.
     */
    public void clear() {
        while (!waiterBoxesAiConnected.isEmpty())
            waiterBoxesAi.addLast(waiterBoxesAiConnected.removeFirst());
        while (!waiterBoxesConnected.isEmpty())
            waiterBoxes.addLast(waiterBoxesConnected.removeFirst().getValue());
        waiterBoxes.forEach(wb -> wb.reset());
    }

    /**
     * Reorders usernames so the client appears first.
     * 
     * @return list of usernames in display order
     */
    public ArrayList<String> getUsernames() {
        ArrayList<String> listUsername = new ArrayList<>();
        waiterBoxesConnected.forEach(pair -> listUsername.add(pair.getValue().getUsername()));
        waiterBoxesAiConnected.forEach((waiterBox) -> listUsername.add(waiterBox.getUsername()));

        int clientID = WebsocketClient.getId();
        if (contains(waiterBoxesConnected, clientID)) {
            while (!listUsername.getFirst()
                    .equals(get(waiterBoxesConnected, clientID).getValue().usernameLabel.getText())) {
                listUsername.add(listUsername.removeFirst());
            }
        }

        return listUsername;
    }

    /**
     * Grants or revokes host controls.
     * 
     * @param isHost true to enable host controls
     */
    public void setHost(boolean isHost) {
        setVisility(isHost);
        updateKickButtons(isHost);
    }

    /**
     * Updates visibility of all kick buttons based on host status.
     * 
     * @param isClientHost host privilege flag
     */
    private void updateKickButtons(boolean isClientHost) {
        for (Pair<Integer, WaiterBox> pair : waiterBoxesConnected) {
            pair.getValue().getKickButton().setVisible(isClientHost && pair.getKey() != WebsocketClient.getId());
        }
        for (WaiterBox wb : waiterBoxesAiConnected) {
            wb.getKickButton().setVisible(isClientHost);
        }
    }

    public void setAllReady(boolean isReady) {
        waiterBoxesConnected.forEach(pair -> pair.getValue().updateReadyIcon(isReady));
    }

    /**
     * Slot panel representing a waiting player or AI.
     */
    public class WaiterBox extends JPanel {
        private JLabel usernameLabel;
        private JPanel readyIcon;
        private JButton kickButton;
        private JPanel eastPanel;
        private int id;

        private WaiterBox(String username, boolean isReady) {
            setLayout(new BorderLayout(10, 0));
            setPreferredSize(new Dimension(Integer.MAX_VALUE, PLAYER_PANEL_HEIGHT));
            setMinimumSize(new Dimension(0, PLAYER_PANEL_HEIGHT));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, PLAYER_PANEL_HEIGHT));

            initWBLayout(username);

            add(usernameLabel, BorderLayout.WEST);
            add(eastPanel, BorderLayout.EAST);

            setStyle(isReady);
        }

        private void initWBLayout(String username) {
            usernameLabel = new JLabel(username);
            readyIcon = new JPanel();
            kickButton = new JButton("Expulser");

            eastPanel = new JPanel(new BorderLayout(10, 0));
            eastPanel.setBackground(ViewStyle.PANEL_BACKGROUND);
            eastPanel.add(kickButton, BorderLayout.CENTER);
            eastPanel.add(readyIcon, BorderLayout.EAST);
        }

        private void setStyle(boolean isReady) {
            setBackground(ViewStyle.PANEL_BACKGROUND);
            setBorder(BorderFactory.createEmptyBorder(ViewStyle.PANEL_PADDING, ViewStyle.PANEL_PADDING,
                    ViewStyle.PANEL_PADDING, ViewStyle.PANEL_PADDING));

            usernameLabel.setFont(ViewStyle.USERNAME_FONT);
            usernameLabel.setForeground(ViewStyle.TEXT_COLOR);

            readyIcon.setBackground(isReady ? ViewStyle.READY_COLOR : ViewStyle.NOT_READY_COLOR);
            readyIcon.setPreferredSize(new Dimension(ViewStyle.READY_ICON_SIZE, ViewStyle.READY_ICON_SIZE));
            readyIcon.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

            kickButton.setFocusable(false);
            kickButton.setBackground(ViewStyle.KICK_BUTTON_COLOR);
            kickButton.setForeground(ViewStyle.BUTTON_TEXT_COLOR);
            kickButton.setFont(ViewStyle.BUTTON_FONT);
            kickButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ViewStyle.KICK_BUTTON_COLOR.darker(), 1),
                    BorderFactory.createEmptyBorder(4, 12, 4, 12)));
            kickButton.setOpaque(true);
            kickButton.setBorderPainted(false);
        }

        public void setBorder() {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(ViewStyle.PANEL_PADDING, ViewStyle.PANEL_PADDING,
                            ViewStyle.PANEL_PADDING, ViewStyle.PANEL_PADDING)));
        }

        /**
         * Updates the player view.
         *
         * @param username the username of the player
         * @param isReady  whether the player is ready
         * @param isHost   whether the player is a client (not the host)
         */
        public void updatePlayer(String username, boolean isReady, boolean isHost) {
            usernameLabel.setText(username);
            updateReadyIcon(isReady);
            kickButton.setVisible(isHost && id != WebsocketClient.getId());
            setBorder();
        }

        private void updateReadyIcon(boolean isReady) {
            readyIcon.setBackground(isReady ? ViewStyle.READY_COLOR : ViewStyle.NOT_READY_COLOR);
            readyIcon.setBorder(BorderFactory.createLineBorder(
                    isReady ? ViewStyle.READY_COLOR.darker() : ViewStyle.NOT_READY_COLOR.darker(), 1));
        }

        public JButton getKickButton() {
            return kickButton;
        }

        public void setIsReady(boolean isReady) {
            updateReadyIcon(isReady);
        }

        public void setPlayer(int id, String username, boolean isReady, boolean isHost) {
            usernameLabel.setText(username);
            setIsReady(isReady);
            updateKickButtons(isHost);
            setBorder();
            this.id = id;
        }

        public void reset() {
            usernameLabel.setText("");
            setIsReady(false);
            kickButton.setVisible(false);
            this.id = -1;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return usernameLabel.getText();
        }

        public void setAI(String username, boolean isHost) {
            usernameLabel.setText(username);
            kickButton.setVisible(isHost);
            setBorder();
        }

        public void setClientStyle() {
            setBackground(new Color(150, 150, 150));
            getComponent(1).setBackground(new Color(150, 150, 150));
        }

        public void setDefaultClientStyle() {
            setBackground(ViewStyle.PANEL_BACKGROUND);
            getComponent(1).setBackground(ViewStyle.PANEL_BACKGROUND);
        }
    }
}