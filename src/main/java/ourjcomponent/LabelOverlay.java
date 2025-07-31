package main.java.ourjcomponent;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LabelOverlay extends JPanel {
    private Color color = new Color(0, 0, 0, 128);
    private final JPanel panel;
    /* Init block for the JPanel */
    {
        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(null);
        this.setOpaque(false);
    }

    /**
     * Constructor of a label overlay with a text and a background color
     * 
     * @param text            the text
     * @param backgroundColor the background color
     * @param frame           the frame for the size
     */
    public LabelOverlay(String text, Color backgroundColor, JFrame frame) {
        this(text, frame);
        this.color = backgroundColor;
    }

    /**
     * Constructor of a label overlay with a text and the default background color
     * 
     * @param text  the text
     * @param frame the color
     */
    public LabelOverlay(String text, JFrame frame) {
        /* Init the JLabel */
        JLabel label = new JLabel(text, SwingConstants.CENTER);

        /* Style */
        label.setFont(new Font("calibri", 0, 30));
        label.setForeground(Color.WHITE);
        label.setBounds(0, 0, ((int) frame.getPreferredSize().getWidth()),
                ((int) (frame.getPreferredSize().getHeight() * 0.8)));

        panel.add(label);

        /* inner panel style */
        panel.setPreferredSize(frame.getPreferredSize());

        /* Objet style */
        setPreferredSize(frame.getPreferredSize());
        setBounds(0, 0, ((int) frame.getPreferredSize().getWidth()), ((int) frame.getPreferredSize().getHeight()));
        add(panel);
    }

    /**
     * A constructor for a label overlay with a text and a button
     * 
     * @param text         the text
     * @param frame        the frame
     * @param buttonText   the text button
     * @param buttonAction the action listner of the button
     */
    public LabelOverlay(String text, JFrame frame, String buttonText, ActionListener buttonAction) {
        this(text, frame);

        JButton button = createButton(buttonText, buttonAction);

        // Positionner le bouton au centre bas
        int buttonX = (int) (frame.getPreferredSize().getWidth() / 2 - button.getPreferredSize().getWidth() / 2);
        int buttonY = (int) (frame.getPreferredSize().getHeight() * 0.6);
        button.setBounds(buttonX, buttonY, button.getPreferredSize().width, button.getPreferredSize().height);

        panel.add(button);
        button.setFocusable(true);
        button.setFocusable(true);
        button.requestFocus();
    }

    /**
     * A constructor with a text and two button with there 2 litstner
     * 
     * @param text          the text
     * @param frame         the frame
     * @param button1Text   the first button
     * @param button1Action the first actionListner
     * @param button2Text   the second button
     * @param button2Action the second actionListner
     */
    public LabelOverlay(String text, JFrame frame,
            String button1Text, ActionListener button1Action,
            String button2Text, ActionListener button2Action) {
        this(text, frame);

        JButton button1 = createButton(button1Text, button1Action);
        JButton button2 = createButton(button2Text, button2Action);

        // Calculer les dimensions de l'écran
        int frameWidth = (int) frame.getPreferredSize().getWidth();
        int frameHeight = (int) frame.getPreferredSize().getHeight();

        // Positionner les boutons côte à côte avec un espace entre eux
        int spacing = 20; // Espace entre les boutons
        int totalWidth = button1.getPreferredSize().width + button2.getPreferredSize().width + spacing;

        int button1X = (frameWidth - totalWidth) / 2;
        int button2X = button1X + button1.getPreferredSize().width + spacing;
        int buttonY = (int) (frameHeight * 0.6);

        button1.setBounds(button1X, buttonY, button1.getPreferredSize().width, button1.getPreferredSize().height);
        button2.setBounds(button2X, buttonY, button2.getPreferredSize().width, button2.getPreferredSize().height);

        panel.add(button1);
        panel.add(button2);
    }

    // Méthode utilitaire pour créer un bouton
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("calibri", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(220, 220, 220));
        button.setPreferredSize(new Dimension(150, 40));
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    /**
     * A setter for the background color
     * 
     * @param color the wanted color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Draw method for the LabelOverlay panel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Appel à la méthode de la superclasse pour effacer l'arrière-plan
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(color);

        // Dessiner le rectangle
        int x = 0; // Position X du rectangle
        int y = 0; // Position Y du rectangle
        int width = 20000; // Largeur du rectangle
        int height = 10000; // Hauteur du rectangle
        g2d.fillRect(x, y, width, height);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(
                () -> {
                    JFrame mainFrame = new JFrame();
                    mainFrame.setMinimumSize(Toolkit.getDefaultToolkit().getScreenSize());
                    mainFrame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
                    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    mainFrame.getContentPane().setBackground(new Color(0, 128, 0));
                    mainFrame.setBackground(new Color(0, 128, 0));
                    LabelOverlay qp = new LabelOverlay("1JIOZHFAUIQ", new Color(100, 0, 0, 129), mainFrame);
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
