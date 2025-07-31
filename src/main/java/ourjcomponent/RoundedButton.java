package main.java.ourjcomponent;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * This class is a JButton with a draw of a rounded rectangle for his background
 */
public class RoundedButton extends JButton {
    private int radius = 20; // the raduis of the corner
    private final JLabel label; // the text of the button
    private Color buttonBackground;
    private Color absoluteBackground = new Color(0, 0, 0, 0);
    /* Init block */
    {
        label = new JLabel("", SwingConstants.CENTER);
    }

    /**
     * Create a default button
     */
    public RoundedButton() {
        setOpaque(false);
    }

    /**
     * create a default button with a specified raduis
     * 
     * @param radius
     */
    public RoundedButton(int radius) {
        this.radius = radius;
    }

    /**
     * create a button with a text and a specified raduis
     * 
     * @param text
     * @param radius
     */
    public RoundedButton(String text, int radius) {
        label.setText(text);
        add(label, SwingConstants.CENTER);
        this.radius = radius;
        setOpaque(false);
    }

    /**
     * create a default button with a specified text
     * 
     * @param text
     */
    public RoundedButton(String text) {
        label.setText("<html>" + text);
        add(label);
        setOpaque(false);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (label != null)
            label.setForeground(fg);
    }

    @Override
    public void setBackground(Color bg) {
        this.buttonBackground = bg;
    }

    public void setAbsoluteBackground(Color bg) {
        this.absoluteBackground = bg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Activez l'anti-aliasing pour des bords plus lisses
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(this.absoluteBackground);
        g2d.fillRect(-1, -1, getWidth() + 1, getHeight() + 1);
        // Créez une forme de rectangle arrondi
        RoundRectangle2D rectangleArrondi = new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(), radius, radius);

        // Définissez la couleur de fond
        g2d.setColor(buttonBackground);

        // Remplissez le rectangle arrondi avec la couleur de fond
        g2d.fill(rectangleArrondi);

        // Libérez les ressources du contexte graphique
        g2d.dispose();
    }
}