package main.java.ourjcomponent;

import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * CycleSelector is a custom JPanel that allows cycling through a list of
 * options.
 * It provides buttons to navigate through the options and displays the current
 * option.
 */
public class CycleSelector extends JPanel {
    private int currentIndex;
    private final String[] options;
    private Runnable updateIndex;
    private final JLabel label;
    private final JButton leftButton;
    private final JButton rightButton;

    /**
     * Constructor for CycleSelector.
     *
     * @param options      The list of options to cycle through.
     * @param currentIndex The initial index of the selected option.
     */
    public CycleSelector(String[] options, int currentIndex) {
        this.options = options;
        this.currentIndex = currentIndex;

        setBackground(null);
        setVisible(true);

        label = new JLabel(getCurrentOption());
        label.setHorizontalAlignment(JLabel.CENTER);

        leftButton = new JButton("<");
        rightButton = new JButton(">");
        leftButton.setFocusable(false);
        rightButton.setFocusable(false);

        add(leftButton);
        add(label);
        add(rightButton);

        addActionListener();
    }

    /**
     * Sets the Runnable to be executed when the index is updated.
     *
     * @param updateIndex The Runnable to be executed on index update.
     */
    public void setUpdateIndex(Runnable updateIndex) {
        this.updateIndex = updateIndex;
    }

    /**
     * Changes the current index and updates the label.
     *
     * @param index The new index to set.
     */
    public void changeIndex(int index) {
        currentIndex = index;
        updateLabel(currentIndex);
        if (updateIndex != null) {
            updateIndex.run();
        }
    }

    /**
     * Modifies the current index and updates the label (called by the server).
     *
     * @param index The new index to set.
     */
    public void modifyIndex(int index) {
        currentIndex = index;
        updateLabel(index);
    }

    /**
     * Gets the list of options.
     *
     * @return The list of options.
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Gets the currently selected index.
     *
     * @return The currently selected index.
     */
    public int getSelectedIndex() {
        return currentIndex;
    }

    /**
     * Gets the current option based on the current index.
     *
     * @return The current option.
     */
    public String getCurrentOption() {
        return options[currentIndex];
    }

    /**
     * Gets the label displaying the current option.
     *
     * @return The label displaying the current option.
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * Adds action listeners to the left and right buttons for cycling through
     * options.
     */
    public void addActionListener() {
        leftButton.addActionListener(
                _ -> changeIndex((currentIndex - 1 + getOptions().length) % getOptions().length));
        rightButton.addActionListener(
                _ -> changeIndex((currentIndex + 1 + getOptions().length) % getOptions().length));
    }

    /**
     * Updates the label to display the current option based on the index.
     *
     * @param index The index of the option to display.
     */
    public void updateLabel(int index) {
        label.setText(getOptions()[index]);
    }

    /**
     * Gets the list of buttons in the CycleSelector.
     *
     * @return The list of buttons.
     */
    public List<JButton> getButtons() {
        return Arrays.asList(leftButton, rightButton);
    }
}
