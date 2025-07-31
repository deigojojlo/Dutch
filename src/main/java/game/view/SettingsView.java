package main.java.game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ItemListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import main.java.storage.Storage;
import main.java.style.ViewStyle;
import main.java.util.SoundUtil;

public class SettingsView extends JPanel {
    private final JFrame frame;
    private JCheckBox alwaysOnTopCheckBox;
    private JCheckBox resizableCheckBox;
    private JCheckBox gameMusicCheckBox;
    private JButton backToMenuButton;
    private JRadioButton windowedButton;
    private JRadioButton borderlessFullscreenButton;
    private JRadioButton exclusiveFullscreenButton;
    private ButtonGroup displayModeGroup;
    private JRadioButton speed3;
    private JRadioButton speed2;
    private JRadioButton speed1;
    private ButtonGroup speedGroup;
    private static int titleBarHeight = 0;
    private static final Path CONFIGPATH = getUserConfigPath("2024-LG1-A-DUTCH");

    /**
     * The constructore of the SettingsView, used to modify the user's preferences
     * 
     * @param frame the root JFrame used to obtain the window size
     * @param onBackToMenu the runnable called on the bactToMenu button to return to the menu
     */
    public SettingsView(JFrame frame, Runnable onBackToMenu) {
        setLayout(new BorderLayout());

        this.frame = frame;
        initComponents(onBackToMenu);
    }

    /**
     * Sets the buttons to allow the user to choose and save their settings
     * 
     * @param onBackToMenu the runnable called on the bactToMenu button to return to the menu
     */
    private void initComponents(Runnable onBackToMenu) {
        setLayout(new BorderLayout());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        Font biggerFont = new Font("SansSerif", Font.PLAIN, 30);

        displayModeGroup = new ButtonGroup();
        exclusiveFullscreenButton = new JRadioButton("Plein Écran Exclusif");
        borderlessFullscreenButton = new JRadioButton("Plein Écran Sans Bordure");
        windowedButton = new JRadioButton("Écran Fenêtré");

        exclusiveFullscreenButton.setOpaque(false);
        exclusiveFullscreenButton.setFont(biggerFont);
        exclusiveFullscreenButton.setForeground(Color.white);
        exclusiveFullscreenButton.setSelected(gd.getFullScreenWindow() != null);
        exclusiveFullscreenButton.setFocusable(false);
        exclusiveFullscreenButton.setBorder(null);

        borderlessFullscreenButton.setOpaque(false);
        borderlessFullscreenButton.setFont(biggerFont);
        borderlessFullscreenButton.setForeground(Color.white);
        borderlessFullscreenButton.setSelected(gd.getFullScreenWindow() == null && frame.isUndecorated());
        borderlessFullscreenButton.setFocusable(false);
        borderlessFullscreenButton.setBorder(null);

        windowedButton.setOpaque(false);
        windowedButton.setFont(biggerFont);
        windowedButton.setForeground(Color.white);
        windowedButton.setSelected(!frame.isUndecorated());
        windowedButton.setFocusable(false);
        windowedButton.setBorder(null);

        displayModeGroup.add(exclusiveFullscreenButton);
        displayModeGroup.add(borderlessFullscreenButton);
        displayModeGroup.add(windowedButton);

        exclusiveFullscreenButton.addActionListener(_ -> {
            frame.dispose();
            setExclusiveFullscreen(frame);

            resizableCheckBox.setSelected(false);
            alwaysOnTopCheckBox.setSelected(false);

            frame.repaint();
            frame.revalidate();

            createSettings();
        });

        borderlessFullscreenButton.addActionListener(_ -> {
            frame.dispose();
            setBorderlessFullscreen(frame);

            resizableCheckBox.setSelected(false);
            alwaysOnTopCheckBox.setSelected(false);

            frame.repaint();
            frame.revalidate();

            createSettings();
        });

        windowedButton.addActionListener(_ -> {
            frame.dispose();
            setWindowedScreen(frame);

            resizableCheckBox.setSelected(false);
            alwaysOnTopCheckBox.setSelected(false);

            frame.repaint();
            frame.revalidate();

            createSettings();
        });

        alwaysOnTopCheckBox = new JCheckBox("Épingler la fenêtre (fenêtré)");
        resizableCheckBox = new JCheckBox("Rendre la fenêtre redimensionnable (fenêtré)");
        gameMusicCheckBox = new JCheckBox("Activer la sonorisation");

        alwaysOnTopCheckBox.setOpaque(false);
        alwaysOnTopCheckBox.setFont(biggerFont);
        alwaysOnTopCheckBox.setForeground(Color.white);
        alwaysOnTopCheckBox.setSelected(frame.isAlwaysOnTop());
        alwaysOnTopCheckBox.setFocusable(false);
        alwaysOnTopCheckBox.setBorder(null);
        alwaysOnTopCheckBox.addActionListener(_ -> {
            frame.setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
            createSettings();
        });

        resizableCheckBox.setOpaque(false);
        resizableCheckBox.setFont(biggerFont);
        resizableCheckBox.setForeground(Color.white);
        resizableCheckBox.setSelected(frame.isResizable());
        resizableCheckBox.setFocusable(false);
        resizableCheckBox.setBorder(null);
        resizableCheckBox.addActionListener(_ -> {
            frame.setResizable(resizableCheckBox.isSelected());
            createSettings();
        });

        gameMusicCheckBox.setOpaque(false);
        gameMusicCheckBox.setFont(biggerFont);
        gameMusicCheckBox.setForeground(Color.white);
        gameMusicCheckBox.setSelected(GameView.getGameMusic());
        gameMusicCheckBox.setFocusable(false);
        gameMusicCheckBox.setBorder(null);
        gameMusicCheckBox.addActionListener(_ -> {
            GameView.setGameMusic(gameMusicCheckBox.isSelected());
            if (gameMusicCheckBox.isSelected()) {
                SoundUtil.playAmbientLoop("GAME_MAIN.wav");
            } else {
                SoundUtil.stopAmbientLoop();
            }
            createSettings();
        });

        ItemListener enableCheckBoxes = _ -> {
            boolean isWindowed = windowedButton.isSelected();
            alwaysOnTopCheckBox.setEnabled(isWindowed);
            resizableCheckBox.setEnabled(isWindowed);
        };

        exclusiveFullscreenButton.addItemListener(enableCheckBoxes);
        borderlessFullscreenButton.addItemListener(enableCheckBoxes);
        windowedButton.addItemListener(enableCheckBoxes);
        enableCheckBoxes.itemStateChanged(null);

        JPanel modePanel = createFixedCardPanel();
        modePanel.add(exclusiveFullscreenButton);
        modePanel.add(Box.createHorizontalStrut(20));
        modePanel.add(borderlessFullscreenButton);
        modePanel.add(Box.createHorizontalStrut(20));
        modePanel.add(windowedButton);

        JPanel checkBoxPanel = createFixedCardPanel();
        checkBoxPanel.add(gameMusicCheckBox);
        checkBoxPanel.add(Box.createHorizontalStrut(20));
        checkBoxPanel.add(alwaysOnTopCheckBox);
        checkBoxPanel.add(Box.createHorizontalStrut(20));
        checkBoxPanel.add(resizableCheckBox);

        JPanel speedPanel = createFixedCardPanel();
        speedGroup = new ButtonGroup();
        speed3 = new JRadioButton("3");
        speed2 = new JRadioButton("2");
        speed1 = new JRadioButton("1,5");
        speed3.setOpaque(false);
        speed2.setOpaque(false);
        speed1.setOpaque(false);
        speed3.setFont(biggerFont);
        speed2.setFont(biggerFont);
        speed1.setFont(biggerFont);
        speed3.setForeground(Color.white);
        speed2.setForeground(Color.white);
        speed1.setForeground(Color.white);
        speedGroup.add(speed3);
        speedGroup.add(speed2);
        speedGroup.add(speed1);
        speed3.setSelected(GameView.getAnimSpeed() == 1);
        speed2.setSelected(GameView.getAnimSpeed() == 1.5);
        speed1.setSelected(GameView.getAnimSpeed() == 2);
        speed1.setFocusable(false);
        speed2.setFocusable(false);
        speed3.setFocusable(false);
        speedPanel.add(speed3);
        speedPanel.add(Box.createHorizontalStrut(10));
        speedPanel.add(speed2);
        speedPanel.add(Box.createHorizontalStrut(10));
        speedPanel.add(speed1);

        speed1.addActionListener(_ -> {
            GameView.setAnimSpeed(2);
            createSettings();
        });

        speed2.addActionListener(_ -> {
            GameView.setAnimSpeed(1.5);
            createSettings();
        });

        speed3.addActionListener(_ -> {
            GameView.setAnimSpeed(1);
            createSettings();
        });

        backToMenuButton = new JButton("Retour au menu");
        backToMenuButton.setOpaque(false);
        backToMenuButton.setBackground(ViewStyle.DARK_BUTTON_COLOR);
        backToMenuButton.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        backToMenuButton.setFont(new Font("Calibri", 0, 40));
        backToMenuButton.setBorder(null);
        backToMenuButton.setFocusable(false);
        backToMenuButton.addActionListener(_ -> onBackToMenu.run());

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(centerComponent(createSectionTitle("Mode d'affichage")));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(centerComponent(modePanel));

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(centerComponent(createSectionTitle("Options sonores et visuelles")));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(centerComponent(checkBoxPanel));

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(centerComponent(createSectionTitle("Vitesse d'animation (en secondes)")));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(centerComponent(speedPanel));

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(centerComponent(backToMenuButton));
        mainPanel.add(Box.createVerticalStrut(30));

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * 
     * @return A decorative panel that encapsulates a group of options
     */
    private JPanel createFixedCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int rectWidth = (int) (getWidth() * 0.9);
                int rectHeight = getHeight();
                int x = (getWidth() - rectWidth) / 2;
                int y = 0;
                g.setColor(new Color(150, 150, 150, 150));
                g.fillRoundRect(x, y, rectWidth, rectHeight, 25, 25);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /**
     * 
     * @param comp the component to wrap
     * @return a JPanel used to wrap another comonent in order to center and stylize it
     */
    private JPanel centerComponent(JComponent comp) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.add(Box.createHorizontalGlue());
        wrapper.add(comp);
        wrapper.add(Box.createHorizontalGlue());
        return wrapper;
    }

    /**
     * 
     * @param text the custom text to add to the comonent
     * @return the JComponent used to decorate a section of the option
     */
    private JComponent createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 30));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Storage.SETTINGS_BG != null) {
            g.drawImage(Storage.SETTINGS_BG, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * 
     * @param appName the application name
     * @return the path of where to store user settings depending on their OS
     */
    private static Path getUserConfigPath(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        Path configPath;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            configPath = Path.of(appData, appName);
        } else if (os.contains("mac")) {
            configPath = Path.of(userHome, "Library", "Application Support", appName);
        } else {
            configPath = Path.of(userHome, ".config", appName);
        }

        return configPath;
    }

    /**
     * Creates the file used to store preffered game settings.
     */
    public void createSettings() {
        try {
            Path filePath = Path.of(CONFIGPATH.toString(), "userConfig.json");
            Files.createDirectories(filePath.getParent());

            try (FileWriter writer = new FileWriter(filePath.toString(), false)) {
                StringBuilder sb = new StringBuilder();

                if (exclusiveFullscreenButton.isSelected()) {
                    sb.append("windowType=exclusive\n");
                } else if (borderlessFullscreenButton.isSelected()) {
                    sb.append("windowType=fullscreen\n");
                } else {
                    sb.append("windowType=windowed\n");
                }

                sb.append("windowOnTop=").append(alwaysOnTopCheckBox.isSelected()).append("\n");
                sb.append("windowResizable=").append(resizableCheckBox.isSelected()).append("\n");

                if (speed3.isSelected()) {
                    sb.append("animSpeed=1\n");
                } else if (speed2.isSelected()) {
                    sb.append("animSpeed=1.5\n");
                } else {
                    sb.append("animSpeed=2\n");
                }

                sb.append("gameMusic=").append(gameMusicCheckBox.isSelected()).append("\n");

                writer.write(sb.toString());
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the creation of the config file.");
            System.out.println("Tried to create directory at: " + Path.of(CONFIGPATH.toString(), "userConfig.json").getParent());
        }
    }

    /**
     * Reads the file to get the values stocked in it.
     * 
     * @return an ArrayList that contains one line of the file per index.
     */
    public static ArrayList<String> readSettings() {
        ArrayList<String> parameters = new ArrayList<>();
        try (FileReader fileReader = new FileReader(Path.of(CONFIGPATH.toString(), "userConfig.json").toString())) {
            int character = fileReader.read();
            String line = "";
            while (character != -1) {
                if (character == '\n') {
                    parameters.add(line);
                    line = "";
                } else {
                    if (character != '\r') {
                        line += (char) character;
                    }
                }
                character = fileReader.read();
            }
            if (!line.isEmpty()) {
                parameters.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            System.err.println("Error while reading the settings");
        }
        return parameters;
    }

    /**
     * Lodas the settings and modifies the frame accordingly
     * 
     * @param frame the JFrame to be modified
     */
    public static void loadSettings(JFrame frame) {
        ArrayList<String> lines = readSettings();
        boolean isWindowed = false;

        for (String line : lines) {
            if (line.startsWith("windowType=")) {
                String mode = line.split("=")[1].trim();
                switch (mode) {
                    case "exclusive" -> setExclusiveFullscreen(frame);
                    case "fullscreen" -> setBorderlessFullscreen(frame);
                    case "windowed" -> 
                        {
                            setWindowedScreen(frame);
                            isWindowed = true;
                        }
                    default -> setBorderlessFullscreen(frame);
                }
            }
        }

        for (String line : lines) {
            if (line.startsWith("windowOnTop=") && isWindowed) {
                frame.setAlwaysOnTop(Boolean.parseBoolean(line.split("=")[1].trim()));
            } else if (line.startsWith("windowResizable=") && isWindowed) {
                frame.setResizable(Boolean.parseBoolean(line.split("=")[1].trim()));
            } else if (line.startsWith("animSpeed=")) {
                String val = line.split("=")[1].trim();
                switch (val) {
                    case "1" -> GameView.setAnimSpeed(1);
                    case "1.5" -> GameView.setAnimSpeed(1.5);
                    case "2" -> GameView.setAnimSpeed(2);
                    default -> GameView.setAnimSpeed(1.5);
                }
            } else if (line.startsWith("gameMusic=")) {
                GameView.setGameMusic(Boolean.parseBoolean(line.split("=")[1].trim()));
                if (Boolean.parseBoolean(line.split("=")[1].trim())) {
                    SoundUtil.playSound("GAME_BOOT.wav");
                    SoundUtil.playAmbientLoop("GAME_MAIN.wav");
                } else {
                    SoundUtil.stopAmbientLoop();
                }
            }
        }
    }

    /**
     * Sets Exclusive FullScreen Mode to the frame
     * 
     * @param frame the JFrame to modify
     */
    public static void setExclusiveFullscreen(JFrame frame) {
        frame.setVisible(false);

        // Définir la frame en plein écran exclusif
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        frame.setResizable(false);
        frame.setUndecorated(true);
        gc.getDevice().setFullScreenWindow(frame);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Définir la taille préférée, minimale et maximale de la frame

        titleBarHeight = 0;

        Dimension frameSize = new Dimension(
                (int) gc.getBounds().getWidth(),
                (int) gc.getBounds().getHeight());

        frame.setPreferredSize(frameSize);
        frame.setMinimumSize(frameSize);
        frame.setMaximumSize(frameSize);
        frame.setSize(frameSize);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Storage.APP_ICON);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Dutch");

        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(false);
        frame.createBufferStrategy(2);

        frame.revalidate();
        frame.repaint();
        frame.toFront();
        frame.requestFocus();
    }

    /**
     * Sets Borderless FullScreen Mode to the frame
     * 
     * @param frame the JFrame to modify
     */
    public static void setBorderlessFullscreen(JFrame frame) {
        frame.setVisible(false);

        // Définir la frame en plein écran
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        frame.setResizable(false);
        frame.setUndecorated(true);
        gc.getDevice().setFullScreenWindow(null);
        frame.setExtendedState(JFrame.NORMAL);

        // Définir la taille préférée, minimale et maximale de la frame

        titleBarHeight = 0;

        Dimension frameSize = new Dimension(
                (int) gc.getBounds().getWidth(),
                (int) gc.getBounds().getHeight() - Toolkit.getDefaultToolkit().getScreenInsets(gc).bottom);

        frame.setPreferredSize(frameSize);
        frame.setMinimumSize(frameSize);
        frame.setMaximumSize(frameSize);
        frame.setSize(frameSize);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Storage.APP_ICON);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Dutch");

        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(false);
        frame.createBufferStrategy(2);

        frame.revalidate();
        frame.repaint();
        frame.toFront();
        frame.requestFocus();
    }

    /**
     * Sets Windowed Mode to the frame
     * 
     * @param frame the JFrame to modify
     */
    public static void setWindowedScreen(JFrame frame) {
        frame.setVisible(false);

        // Définir la frame en fenêtré
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        frame.setResizable(true);
        frame.setUndecorated(false);
        gc.getDevice().setFullScreenWindow(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Définir la taille préférée, minimale et maximale de la frame

        frame.addNotify();
        Insets insets = frame.getInsets();
        titleBarHeight = insets.top;
        frame.removeNotify();

        Dimension frameSize = new Dimension(
                (int) gc.getBounds().getWidth(),
                (int) gc.getBounds().getHeight() - titleBarHeight);

        frame.setPreferredSize(frameSize);
        frame.setMinimumSize(new Dimension(1728, 972));
        frame.setMaximumSize(frameSize);
        frame.setSize(frameSize);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Storage.APP_ICON);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Dutch");

        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(false);
        frame.createBufferStrategy(2);

        frame.revalidate();
        frame.repaint();
        frame.toFront();
        frame.requestFocus();
    }

    public static int getTitleBarHeight() {
        return titleBarHeight;
    }

    public static Path getConfigpath() {
        return CONFIGPATH;
    }
}
