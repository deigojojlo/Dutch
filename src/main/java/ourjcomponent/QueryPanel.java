package main.java.ourjcomponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.java.game.model.CardModel;

public class QueryPanel extends JPanel {
    private final BufferedImage image;
    private Color backgroundColor = new Color(0, 0, 0, 128);
    private final JButton trashButton;
    private final JButton switchButton;
    private final JButton powerButton;
    private final JPanel buttonPanel;

    {
        buttonPanel = new JPanel();
        trashButton = new JButton("Jeter la carte");
        switchButton = new JButton("Echanger");
        powerButton = new JButton("Pouvoir");

        /* style */
        buttonPanel.setOpaque(false);
        trashButton.setBackground(new Color(0, 0, 0, 0));
        trashButton.setForeground(Color.white);
        trashButton.setBorderPainted(false);
        trashButton.setFont(new Font("Calibri", 0, 20));
        trashButton.setFocusable(false);
        trashButton.setOpaque(false);
        switchButton.setBackground(new Color(0, 0, 0, 0));
        switchButton.setForeground(Color.white);
        switchButton.setBorderPainted(false);
        switchButton.setFont(new Font("Calibri", 0, 20));
        switchButton.setFocusable(false);
        switchButton.setOpaque(false);
        powerButton.setBackground(new Color(0, 0, 0, 0));
        powerButton.setForeground(Color.white);
        powerButton.setBorderPainted(false);
        powerButton.setFont(new Font("Calibri", 0, 20));
        powerButton.setFocusable(false);
        powerButton.setOpaque(false);
    }

    /**
     * Create a JPanel who displayed a card and some possible action
     * 
     * @param cardModel       the card wich will be draw
     * @param backgroundColor the background color for the panel
     * @param frame           the frame to get the dimension
     */
    public QueryPanel(CardModel cardModel, Color backgroundColor, JFrame frame) {
        this(cardModel, frame);
        this.backgroundColor = backgroundColor;
    }

    /**
     * Create a JPanel who displayed a card and some possible action
     * 
     * @param cardModel the card wich will be draw
     * @param frame     the frame to get the dimension
     */
    public QueryPanel(CardModel cardModel, JFrame frame) {
        image = cardModel.getBufferedImge();

        setPreferredSize(frame.getPreferredSize());
        setBounds(0, 0, frame.getPreferredSize().width, frame.getPreferredSize().height);
        setLayout(null);
        setOpaque(false);
        add(buttonPanel);

        trashButton.setPreferredSize(new Dimension(image.getWidth(), 20));
        switchButton.setPreferredSize(new Dimension(image.getWidth(), 20));

        buttonPanel.add(trashButton);
        buttonPanel.add(switchButton);
        if (cardModel.hasPower()) {
            buttonPanel.add(powerButton);
        }
        buttonPanel.setBounds((int) getPreferredSize().getWidth() / 2 + 100, (int) getPreferredSize().getHeight() / 3,
                image.getWidth(), image.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(backgroundColor);

        // Dessiner le rectangle
        int x = 0; // Position X du rectangle
        int y = 0; // Position Y du rectangle
        int width = 20000; // Largeur du rectangle
        int height = 10000; // Hauteur du rectangle
        g2d.fillRect(x, y, width, height);
        g2d.drawImage(image, (int) super.getPreferredSize().getWidth() / 2 - image.getWidth() - 100,
                (int) super.getPreferredSize().getHeight() / 3, this);
    }

    public JButton getTrashButton() {
        return trashButton;
    }

    public JButton getPowerButton() {
        return powerButton;
    }

    public JButton getSwitchButton() {
        return switchButton;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame mainFrame = new JFrame();
            mainFrame.setMinimumSize(Toolkit.getDefaultToolkit().getScreenSize());
            mainFrame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.getContentPane().setBackground(new Color(0, 128, 0));
            mainFrame.setBackground(new Color(0, 128, 0));
            QueryPanel qp = new QueryPanel(new CardModel(1, 1), mainFrame);
            // mainFrame.setAlwaysOnTop(true);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setTitle("Dutch");
            mainFrame.getContentPane().add(qp);
            mainFrame.revalidate();
            mainFrame.repaint();

            mainFrame.setVisible(true);
            mainFrame.setResizable(false);
        });
    }
}