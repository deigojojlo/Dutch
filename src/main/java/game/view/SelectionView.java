package main.java.game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import main.java.ourjcomponent.CycleSelector;
import main.java.ourjcomponent.RoundedButton;
import main.java.ourjcomponent.RoundedPanel;
import main.java.storage.Storage;
import main.java.style.ViewStyle;

/**
 * Main view for selecting game mode and configuring solo or multiplayer
 * settings.
 */
public class SelectionView extends JPanel {
    private JPanel modeSelection;
    private JPanel soloPlayerSelection;
    private JPanel multiplayerSelection;
    private JPanel multiplayerJoinSelection;
    private JPanel contentPanel;

    private JButton soloButton;
    private JButton multiplayerButton;
    private JButton startButton;
    private JButton backButton;
    private JButton backToMenu;

    private JButton joinWithCodeButton;
    private JButton newPrivateButton;
    private JButton joinButton;

    private JSlider playerSlider;

    private JLabel playerLabel;
    private JLabel difficultyLabel;

    private CycleSelector difficultyCycle;

    private JTextField codeField;

    /**
     * Initializes the selection view with navigation buttons and content panels.
     * 
     * @param frame the parent JFrame
     */
    public SelectionView(JFrame frame) {
        setLayout(new BorderLayout());

        backButton = new RoundedButton("Retour");
        backToMenu = new RoundedButton("Menu");

        backToMenu.setVisible(true);
        backButton.setVisible(false);

        setButtonStyle(backToMenu);
        setButtonStyle(backButton);

        backButton.setBounds(25, 25, 200, 25);
        backToMenu.setBounds(25, 25, 200, 25);

        this.add(backButton);
        this.add(backToMenu);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        createModeSelection();
        createSoloSelection();
        createMultiplayerSelection();
        createMultiplayerJoinSelection();

        add(contentPanel, BorderLayout.CENTER);

        displayModeSelection();
    }

    /**
     * Applies consistent style and behavior to buttons.
     * 
     * @param button the JButton to style
     */
    private void setButtonStyle(JButton button) {
        button.setBorder(null);
        button.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
                button.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
            }
        });
        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                button.setBackground(new Color(50, 50, 50));
            }

            @Override
            public void focusLost(FocusEvent e) {
                button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
            }
        });
    }

    /**
     * Creates the panel for choosing SOLO or MULTIJOUEUR mode.
     */
    private void createModeSelection() {
        soloButton = new RoundedButton("SOLO");
        multiplayerButton = new RoundedButton("MULTIJOUEUR");
        modeSelection = new RoundedPanel(20, ViewStyle.DARK_BACKGROUND_COLOR);

        modeSelection.setLayout(null);
        modeSelection.setPreferredSize(new Dimension(400, 200));
        soloButton.setBounds(50, 75, 100, 50);
        multiplayerButton.setBounds(250, 75, 100, 50);

        setButtonStyle(soloButton);
        setButtonStyle(multiplayerButton);

        modeSelection.add(soloButton);
        modeSelection.add(multiplayerButton);
    }

    /**
     * Creates a placeholder panel for multiplayer options.
     */
    private void createMultiplayerSelection() {
        multiplayerSelection = new RoundedPanel(20, ViewStyle.DARK_BACKGROUND_COLOR);
        multiplayerSelection.setPreferredSize(new Dimension(400, 500));
        multiplayerSelection.setBackground(ViewStyle.DARK_BACKGROUND_COLOR);
    }

    /**
     * Creates the panel for solo mode: player count slider and difficulty selector.
     */
    private void createSoloSelection() {
        soloPlayerSelection = new RoundedPanel(20, ViewStyle.DARK_BACKGROUND_COLOR);
        soloPlayerSelection.setPreferredSize(new Dimension(400, 400));
        soloPlayerSelection.setLayout(null);

        playerSlider = new JSlider(2, 10);
        playerLabel = new JLabel(playerSlider.getValue() + "");
        startButton = new RoundedButton("DÉMARRER");
        difficultyLabel = new JLabel("Difficulté des IAs");
        difficultyCycle = new CycleSelector(new String[] { "Moyen", "Difficle" }, 0);
        JLabel playerTextLabel = new JLabel("Nombre de joueurs séléctionés : ");
        JLabel title = new JLabel("SOLO");

        title.setBounds(50, 50, 300, 50);
        title.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        title.setFont(new Font("calibri", 0, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        playerTextLabel.setBounds(50, 150, 350, 30);
        playerTextLabel.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        playerLabel.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        playerLabel.setBounds(275, 150, 180, 30);

        playerSlider.setBounds(45, 200, 300, 30);
        playerSlider.setOpaque(false);

        difficultyLabel.setBounds(45, 250, 300, 30);
        difficultyLabel.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyCycle.setBounds(45, 290, 300, 30);
        for (JButton button : difficultyCycle.getButtons()) {
            setButtonStyle(button);
            button.setPreferredSize(new Dimension(40, 30));
        }
        difficultyCycle.getLabel().setForeground(Color.WHITE);

        startButton.setBounds(25, 340, 350, 30);
        setButtonStyle(startButton);

        soloPlayerSelection.add(title);
        soloPlayerSelection.add(playerTextLabel);
        soloPlayerSelection.add(playerLabel);
        soloPlayerSelection.add(playerSlider);
        soloPlayerSelection.add(difficultyLabel);
        soloPlayerSelection.add(difficultyCycle);
        soloPlayerSelection.add(startButton);
    }

    /**
     * Creates the panel for joining or creating multiplayer games.
     */
    private void createMultiplayerJoinSelection() {
        multiplayerJoinSelection = new RoundedPanel(20, ViewStyle.DARK_BACKGROUND_COLOR);
        multiplayerJoinSelection.setLayout(null);
        multiplayerJoinSelection.setPreferredSize(new Dimension(400, 500));

        joinButton = new RoundedButton("<html><h2>Rejoindre une partie publique</h2>");
        joinWithCodeButton = new RoundedButton("<html><h2>Rejoindre avec le code</h2>");
        newPrivateButton = new RoundedButton("<html><h2>Créer une nouvelle partie privée</h2>");
        codeField = new JTextField();
        JLabel title = new JLabel("<html><h1>Multijoueur</h1>");

        setButtonStyle(joinWithCodeButton);
        setButtonStyle(joinButton);
        setButtonStyle(newPrivateButton);

        title.setBounds(25, 25, 350, 50);
        joinButton.setBounds(25, 150, 350, 50);
        codeField.setBounds(50, 275, 300, 30);
        joinWithCodeButton.setBounds(25, 325, 350, 50);
        newPrivateButton.setBounds(25, 425, 350, 50);

        title.setForeground(ViewStyle.LIGHT_TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("CALIBRI", 0, 40));

        multiplayerJoinSelection.add(title);
        multiplayerJoinSelection.add(joinButton);
        multiplayerJoinSelection.add(codeField);
        multiplayerJoinSelection.add(joinWithCodeButton);
        multiplayerJoinSelection.add(newPrivateButton);
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getSoloButton() {
        return soloButton;
    }

    public JButton getMultiplayerButton() {
        return multiplayerButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JSlider getPlayerSlider() {
        return playerSlider;
    }

    public JLabel getPlayerLabel() {
        return playerLabel;
    }

    public JButton getBackToMenuButton() {
        return backToMenu;
    }

    public JButton getJoinWithCodeButton() {
        return joinWithCodeButton;
    }

    public JButton getNewPrivateButton() {
        return newPrivateButton;
    }

    public JButton getJoinButton() {
        return joinButton;
    }

    public JTextField getCodeField() {
        return codeField;
    }

    /**
     * Displays the solo configuration panel.
     */
    public void displaySolo() {
        contentPanel.removeAll();
        contentPanel.add(soloPlayerSelection, new GridBagConstraints());
        backToMenu.setVisible(false);
        backButton.setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Displays the main mode selection panel.
     */
    public void displayModeSelection() {
        contentPanel.removeAll();
        contentPanel.add(modeSelection, new GridBagConstraints());
        backToMenu.setVisible(true);
        backButton.setVisible(false);
        revalidate();
        repaint();
    }

    /**
     * Displays the multiplayer join/create panel.
     */
    public void displayMultiplayerSelection() {
        contentPanel.removeAll();
        contentPanel.add(multiplayerJoinSelection, new GridBagConstraints());
        backToMenu.setVisible(false);
        backButton.setVisible(true);
        revalidate();
        repaint();
    }

    /**
     * Paints the stored background image if available.
     * 
     * @param grphcs the Graphics context
     */
    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        if (Storage.SELECT_BG != null) {
            grphcs.drawImage(Storage.SELECT_BG, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Retrieves the selected AI difficulty index.
     * 
     * @return 0 for Moyen, 1 for Difficile
     */
    public int getDifficulty() {
        return difficultyCycle.getSelectedIndex();
    }
}