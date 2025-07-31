package main.java.ourjcomponent;

import java.awt.*;
import javax.swing.*;

/**
 * This panel creates a border layout with 5 main regions:
 * - Top panel (NORTH), Bottom panel (SOUTH)
 * - Left panel (WEST), Right panel (EAST)
 * - Middle panel (CENTER)
 */
public class BoardPanel extends JPanel {
    private JPanel leftPanel, rightPanel, centerPanel, topPanel, bottomPanel, middlePanel;
    private JPanel topInnerPanel, bottomInnerPanel, leftInnerPanel, rightInnerPanel, middleInnerPanel;

    // Track the next available grid position for panels
    private int topPanelNextGridX = 0, bottomPanelNextGridX = 0, middlePanelNextGridX = 0;

    /**
     * Constructs a new CustomLayoutExample with the default layout.
     */
    public BoardPanel(Dimension dimension) {
        initialize(dimension);
    }

    /**
     * Initializes the panel structure and layout.
     */
    private void initialize(Dimension dimension) {
        setLayout(new BorderLayout());

        // Create main panels
        leftPanel = new JPanel(new BorderLayout());
        rightPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new BorderLayout());
        topPanel = new JPanel(new BorderLayout());
        bottomPanel = new JPanel(new BorderLayout());
        middlePanel = new JPanel(new BorderLayout());

        // Set background colors for visibility
        leftPanel.setBackground(null);
        rightPanel.setBackground(null);
        centerPanel.setBackground(null);
        topPanel.setBackground(null);
        bottomPanel.setBackground(null);
        middlePanel.setBackground(null);

        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        topPanel.setOpaque(false);
        bottomPanel.setOpaque(false);
        middlePanel.setOpaque(false);

        // Set preferred sizes (initial size)
        leftPanel.setPreferredSize(new Dimension((int) dimension.getWidth() / 4, 0));
        rightPanel.setPreferredSize(new Dimension((int) dimension.getWidth() / 4, 0));
        topPanel.setPreferredSize(new Dimension(0, (int) dimension.getHeight() / 3));
        bottomPanel.setPreferredSize(new Dimension(0, (int) dimension.getHeight() / 3));
        middlePanel.setPreferredSize(new Dimension(0, (int) dimension.getHeight() / 3));

        // Setup and assemble panels
        setupInnerPanels();
        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.EAST);
        this.add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        centerPanel.add(middlePanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Sets up the inner panels where content can be placed.
     */
    private void setupInnerPanels() {
        // Create all inner panels with GridBagLayout
        topInnerPanel = new JPanel(new GridBagLayout());
        bottomInnerPanel = new JPanel(new GridBagLayout());
        leftInnerPanel = new JPanel(new GridBagLayout());
        rightInnerPanel = new JPanel(new GridBagLayout());
        middleInnerPanel = new JPanel(new GridBagLayout());

        // Set background colors for inner panels
        topInnerPanel.setBackground(null);
        bottomInnerPanel.setBackground(null);
        leftInnerPanel.setBackground(null);
        rightInnerPanel.setBackground(null);
        middleInnerPanel.setBackground(null);

        topInnerPanel.setOpaque(false);
        bottomInnerPanel.setOpaque(false);
        leftInnerPanel.setOpaque(false);
        rightInnerPanel.setOpaque(false);
        middleInnerPanel.setOpaque(false);
        setOpaque(false);

        // Add inner panels to their containers
        topPanel.add(topInnerPanel, BorderLayout.CENTER);
        bottomPanel.add(bottomInnerPanel, BorderLayout.CENTER);
        leftPanel.add(leftInnerPanel, BorderLayout.CENTER);
        rightPanel.add(rightInnerPanel, BorderLayout.CENTER);
        middlePanel.add(middleInnerPanel, BorderLayout.CENTER);
    }

    /**
     * Adds a component to a panel with GridBagLayout, ensuring it's centered.
     * 
     * @param panel     The panel to add the component to
     * @param component The component to add
     */
    public void addCenteredComponent(JPanel panel, Component component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(component, gbc);
    }

    /**
     * Adds a component to a panel with horizontal spacing.
     * 
     * @param panel     The panel to add the component to
     * @param component The component to add
     * @param gridx     The horizontal position in the grid
     */
    public void addHorizontalComponent(JPanel panel, Component component, int gridx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 5, 15);

        panel.add(component, gbc);
    }

    /**
     * Adds a component to the top panel.
     * Components are automatically positioned from left to right.
     * 
     * @param component The component to add
     */
    public void addToTopPanel(Component component) {
        addHorizontalComponent(topInnerPanel, component, topPanelNextGridX++);
    }

    /**
     * Adds a component to the bottom panel.
     * Components are automatically positioned from right to left.
     * 
     * @param component The component to add
     */
    public void addToBottomPanel(Component component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = bottomPanelNextGridX++;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST; // Right alignment
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 5, 15);

        bottomInnerPanel.add(component, gbc);
        // Reorder components from right to left
        reorderComponentsRightToLeft(bottomInnerPanel);
    }

    /**
     * Reorders components in a panel from right to left.
     * 
     * @param panel The panel whose components need to be reordered
     */
    private void reorderComponentsRightToLeft(JPanel panel) {
        Component[] components = panel.getComponents();
        panel.removeAll();

        for (int i = 0; i < components.length; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = components.length - i - 1; // Reverse order
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(5, 15, 5, 15);

            panel.add(components[i], gbc);
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Adds a component to the left panel.
     * Components are stacked vertically from bottom to top.
     * 
     * @param component The component to add
     */
    public void addToLeftPanel(Component component) {
        // Get existing components
        Component[] components = leftInnerPanel.getComponents();
        leftInnerPanel.removeAll();

        // Add the new component first
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 5, 5);
        leftInnerPanel.add(component, gbc);

        // Add existing components below the new one
        for (int i = 0; i < components.length; i++) {
            gbc.gridy = i + 1;
            leftInnerPanel.add(components[i], gbc);
        }

        leftInnerPanel.revalidate();
        leftInnerPanel.repaint();
    }

    /**
     * Adds a component to the right panel.
     * Components are stacked vertically and centered.
     * 
     * @param component The component to add
     */
    public void addToRightPanel(Component component) {
        addCenteredComponent(rightInnerPanel, component);
    }

    /**
     * Adds a component to the middle panel.
     * Components are automatically positioned from left to right in a single row.
     * 
     * @param component The component to add
     */
    public void addToMiddlePanel(Component component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = middlePanelNextGridX++;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        middleInnerPanel.add(component, gbc);
    }

    /**
     * Clears all components from the top panel and resets gridx counter.
     */
    public void clearTopPanel() {
        topInnerPanel.removeAll();
        topPanelNextGridX = 0;
        revalidate();
        repaint();
    }

    /**
     * Clears all components from the bottom panel and resets gridx counter.
     */
    public void clearBottomPanel() {
        bottomInnerPanel.removeAll();
        bottomPanelNextGridX = 0;
        revalidate();
        repaint();
    }

    /**
     * Clears all components from the left panel.
     */
    public void clearLeftPanel() {
        leftInnerPanel.removeAll();
        revalidate();
        repaint();
    }

    /**
     * Clears all components from the right panel.
     */
    public void clearRightPanel() {
        rightInnerPanel.removeAll();
        revalidate();
        repaint();
    }

    /**
     * Clears all components from the middle panel and resets gridx counter.
     */
    public void clearMiddlePanel() {
        middleInnerPanel.removeAll();
        middlePanelNextGridX = 0;
        revalidate();
        repaint();
    }

    /**
     * Clears all components from all panels.
     */
    public void clearAllPanels() {
        clearTopPanel();
        clearBottomPanel();
        clearLeftPanel();
        clearRightPanel();
        clearMiddlePanel();
    }

    /**
     * Gets the top inner panel for content placement.
     * 
     * @return The top inner panel
     */
    public JPanel getTopInnerPanel() {
        return topInnerPanel;
    }

    /**
     * Gets the bottom inner panel for content placement.
     * 
     * @return The bottom inner panel
     */
    public JPanel getBottomInnerPanel() {
        return bottomInnerPanel;
    }

    /**
     * Gets the left inner panel for content placement.
     * 
     * @return The left inner panel
     */
    public JPanel getLeftInnerPanel() {
        return leftInnerPanel;
    }

    /**
     * Gets the right inner panel for content placement.
     * 
     * @return The right inner panel
     */
    public JPanel getRightInnerPanel() {
        return rightInnerPanel;
    }

    /**
     * Gets the middle inner panel for content placement.
     * 
     * @return The middle inner panel
     */
    public JPanel getMiddleInnerPanel() {
        return middleInnerPanel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Custom Layout Example");
        BoardPanel boardPanel = new BoardPanel(frame.getPreferredSize());

        // Add colors and borders to panels for better visualization
        boardPanel.getTopInnerPanel().setBackground(new Color(255, 200, 200));
        boardPanel.getBottomInnerPanel().setBackground(new Color(200, 200, 255));
        boardPanel.getLeftInnerPanel().setBackground(new Color(200, 255, 200));
        boardPanel.getRightInnerPanel().setBackground(new Color(255, 255, 200));
        boardPanel.getMiddleInnerPanel().setBackground(new Color(255, 200, 255));

        // Add borders to clearly show panel boundaries
        boardPanel.getTopInnerPanel().setBorder(BorderFactory.createTitledBorder("Top Panel"));
        boardPanel.getBottomInnerPanel().setBorder(BorderFactory.createTitledBorder("Bottom Panel"));
        boardPanel.getLeftInnerPanel().setBorder(BorderFactory.createTitledBorder("Left Panel"));
        boardPanel.getRightInnerPanel().setBorder(BorderFactory.createTitledBorder("Right Panel"));
        boardPanel.getMiddleInnerPanel().setBorder(BorderFactory.createTitledBorder("Middle Panel"));

        // Add some sample components to the panels
        boardPanel.addToTopPanel(new JButton("Top Button 1"));
        boardPanel.addToTopPanel(new JButton("Top Button 2"));
        boardPanel.addToBottomPanel(new JButton("Bottom Button 1"));
        boardPanel.addToLeftPanel(new JButton("Left Button 1"));
        boardPanel.addToRightPanel(new JButton("Right Button 1"));

        // Add more elements to the middle panel
        boardPanel.addToMiddlePanel(new JButton("Middle Button 1"));
        boardPanel.addToMiddlePanel(new JButton("Middle Button 2"));
        boardPanel.addToMiddlePanel(new JButton("Middle Button 3"));
        boardPanel.addToMiddlePanel(new JLabel("Label 1"));
        boardPanel.addToMiddlePanel(new JCheckBox("Option 1"));

        // Create a panel with some components to add as a group
        JPanel compositePanel = new JPanel();
        compositePanel.add(new JLabel("Group:"));
        compositePanel.add(new JTextField(10));
        boardPanel.addToMiddlePanel(compositePanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(boardPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
