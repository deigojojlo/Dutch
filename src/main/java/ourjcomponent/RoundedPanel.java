package main.java.ourjcomponent;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * This class is a JPanel with a rounded ractangle draw for his background
 */
public class RoundedPanel extends JPanel {
    private int radius = 20; // the raduis, default 20

    /**
     * Create a default rounded panel
     */
    public RoundedPanel() {
        setOpaque(false);
    }

    /**
     * Create a default rounded panel with a specified raduis
     * 
     * @param raduis the raduis
     */
    public RoundedPanel(int raduis) {
        this();
        this.radius = raduis;
    }

    /**
     * Create a rounded panel with a specified raduis and a specified background
     * color
     * 
     * @param raduis
     * @param color
     */
    public RoundedPanel(int raduis, Color color) {
        this(raduis);
        super.setBackground(color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Activez l'anti-aliasing pour des bords plus lisses
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Créez une forme de rectangle arrondi
        RoundRectangle2D rectangleArrondi = new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(), radius, radius);

        // Définissez la couleur de fond
        g2d.setColor(super.getBackground());

        // Remplissez le rectangle arrondi avec la couleur de fond
        g2d.fill(rectangleArrondi);

        // Libérez les ressources du contexte graphique
        g2d.dispose();
    }
}
