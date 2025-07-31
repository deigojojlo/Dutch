package main.java.game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.java.storage.Storage;

public class MenuView extends JPanel {
    private JButton exitButton;
    private JButton newGameButton;
    private JButton settingsButton;
    private JButton ruleButton;
    private JLabel title;

    /**
     * The constructor of the MenuView, used as the hub of the application
     * 
     * @param frame the root JFrame used as an anchor and get the window size
     */
    public MenuView(JFrame frame) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        title = new JLabel("DUTCH", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("TimesRoman", Font.BOLD | Font.ITALIC, 70));
        title.setBorder(javax.swing.BorderFactory.createEmptyBorder(100, 0, 0, 0));
        titlePanel.add(title, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.NORTH);

        JPanel buttonsPanelWrapper = new JPanel(new BorderLayout());
        buttonsPanelWrapper.setOpaque(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 50));
        bottomPanel.setOpaque(false);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        newGameButton = createMenuButton("Nouvelle partie");
        settingsButton = createMenuButton("Paramètres");
        ruleButton = createMenuButton("Règles");
        exitButton = createMenuButton("Quitter");

        newGameButton.setAlignmentX(CENTER_ALIGNMENT);
        settingsButton.setAlignmentX(CENTER_ALIGNMENT);
        ruleButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton.setAlignmentX(CENTER_ALIGNMENT);

        buttonsPanel.add(newGameButton);
        buttonsPanel.add(Box.createVerticalStrut(25));
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(Box.createVerticalStrut(25));
        buttonsPanel.add(ruleButton);
        buttonsPanel.add(Box.createVerticalStrut(25));
        buttonsPanel.add(exitButton);

        bottomPanel.add(buttonsPanel);
        buttonsPanelWrapper.add(bottomPanel, BorderLayout.SOUTH);

        add(buttonsPanelWrapper, BorderLayout.CENTER);
    }

    /**
     * 
     * @param text the text to add to the button
     * @return a JButton stylized with a custom text
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setOpaque(false);
        button.setFocusable(false);
        button.setBorder(null);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Calibri", Font.PLAIN, 20));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Storage.HOME_BG != null) {
            g.drawImage(Storage.HOME_BG, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }

    public JButton getRuleButton() {
        return ruleButton;
    }
}