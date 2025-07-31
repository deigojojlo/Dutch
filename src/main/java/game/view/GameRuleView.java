package main.java.game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.MouseEvent;
import main.java.ourjcomponent.RoundedButton;
import main.java.storage.Storage;
import main.java.style.ViewStyle;

public class GameRuleView extends JPanel {
    private JLabel label;
    private JButton backButton;

    /**
     * Constructor of the JPanel showing the rules of the game
     */
    public GameRuleView() {
        setLayout(new BorderLayout());
        setBackground(ViewStyle.BACKGROUND_COLOR);

        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("rules.html"))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        label = new JLabel("<html>" + text + "</html>");
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(ViewStyle.LIGHT_TEXT_COLOR);

        backButton = new RoundedButton("Retour");
        styleButton(backButton);

        backButton.setBounds(25, 25, 200, 25);
        
        this.add(backButton);
        this.add(label, BorderLayout.CENTER);
    }

    /**
     * Styles the button to specify it's visuals
     * 
     * @param button the button on which to apply the visuals
     */
    private void styleButton(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setBorder(null);
        button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
        button.setForeground(ViewStyle.LIGHT_TEXT_COLOR);

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
                button.setBackground(ViewStyle.TEXT_COLOR);
            }

            @Override
            public void focusLost(FocusEvent e) {
                button.setBackground(ViewStyle.DARK_BUTTON_COLOR);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        if (Storage.RULES_BG != null) {
            grphcs.drawImage(Storage.RULES_BG, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * 
     * @return the JButton allowing returning to the menu
     */
    public JButton getBackButton() {
        return this.backButton;
    }
}